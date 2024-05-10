package com.august.gulimall.search.service.impl;

import cn.hutool.json.JSONNull;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.august.gulimall.common.to.esmodel.SkuEsModel;
import com.august.gulimall.search.constant.EsConstant;
import com.august.gulimall.search.service.MallSearchService;
import com.august.gulimall.search.vo.SearchParam;
import com.august.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * es商品检索服务
 *
 * @author xzy
 */
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    ElasticsearchClient elasticsearchClient;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        System.out.println(param);
        SearchRequest searchRequest = buildSearchRequest(param);
        result = buildSearchResult(searchRequest, param);
        return result;
    }

    /**
     * 构建检索结果
     *
     * @param searchRequest
     * @return
     */
    private SearchResult buildSearchResult(SearchRequest searchRequest, SearchParam param) {
        SearchResult result = new SearchResult();
        try {
            SearchResponse<JsonData> search = elasticsearchClient.search(searchRequest, JsonData.class);
            log.info("es检索结果：{}", search);
            // 1.封装检索到的商品信息
            List<Hit<JsonData>> hits = search.hits().hits();
            List<SkuEsModel> models = hits.stream().map(h -> {
                JsonData source = h.source();
                SkuEsModel esModel = null;
                if (source != null) {
                    esModel = source.to(SkuEsModel.class);
                    if (h.highlight().get("skuTitle") != null) {
                        esModel.setSkuTitle(h.highlight().get("skuTitle").get(0));
                    }
                }

                return esModel;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            result.setProduct(models);
            // 2.封装聚合信息
            Map<String, Aggregate> aggregations = search.aggregations();
            // 2.1封装品牌聚合信息
            Aggregate catalogIdAgg = aggregations.get("catalogId_agg");
            if (catalogIdAgg != null) {
                LongTermsAggregate longTermsCatalogIdAgg = catalogIdAgg.lterms();
                List<LongTermsBucket> catalogIdAggList = longTermsCatalogIdAgg.buckets().array();
                List<SearchResult.CatalogVo> catalogs = catalogIdAggList.stream().map(ca -> {
                    Map<String, Aggregate> subAgg = ca.aggregations();
                    Aggregate catalogNameAgg = subAgg.get("catalog_name_agg");

                    List<StringTermsBucket> catalogNameAggList = catalogNameAgg.sterms().buckets().array();
                    SearchResult.CatalogVo c = new SearchResult.CatalogVo();
                    // TODO：获取第一个即可
                    //List<String> catalogNameCollect = catalogNameAggList.stream().map(catalogName -> {
                    //    String keyAsString = catalogName.keyAsString();
                    //    return keyAsString;
                    //}).collect(Collectors.toList());

                    //分类id
                    Long catalogId = ca.key();
                    c.setCatalogId(catalogId);
                    StringTermsBucket bucket = catalogNameAggList.get(0);
                    String catalogName = bucket.key().stringValue();
                    c.setCatalogName(catalogName);
                    return c;
                }).collect(Collectors.toList());
                result.setCatalogs(catalogs);
            }


            // 品牌聚合
            Aggregate brandAgg = aggregations.get("brand_agg");
            if (brandAgg != null) {
                LongTermsAggregate LongTermsBrandAgg = brandAgg.lterms();
                List<LongTermsBucket> brandAggList = LongTermsBrandAgg.buckets().array();
                List<SearchResult.BrandVo> brandVoList = brandAggList.stream().map(b -> {
                    SearchResult.BrandVo brands = new SearchResult.BrandVo();
                    brands.setBrandId(b.key());
                    List<StringTermsBucket> brandImgAgg = b.aggregations().get("brand_img_agg").sterms().buckets().array();
                    List<StringTermsBucket> brandNameAgg = b.aggregations().get("brand_name_agg").sterms().buckets().array();
                    brands.setBrandImg(brandImgAgg.get(0).key().stringValue());
                    brands.setBrandName(brandNameAgg.get(0).key().stringValue());
                    return brands;
                }).collect(Collectors.toList());
                result.setBrands(brandVoList);
            }
            // 封装聚合
            Aggregate attrAgg = aggregations.get("attr_agg");
            if (attrAgg != null) {
                NestedAggregate nestedAttrAgg = aggregations.get("attr_agg").nested();
                List<LongTermsBucket> attrIdAgg = nestedAttrAgg.aggregations().get("attr_id_agg").lterms().buckets().array();
                List<SearchResult.AttrVo> attrVoList = attrIdAgg.stream().map(a -> {
                    SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                    List<StringTermsBucket> attrNameAgg = a.aggregations().get("attr_name_agg").sterms().buckets().array();
                    List<StringTermsBucket> attrValueAgg = a.aggregations().get("attr_value_agg").sterms().buckets().array();
                    attrVo.setAttrId(a.key());
                    attrVo.setAttrName(attrNameAgg.get(0).key().stringValue());
                    List<String> attrValues = new ArrayList<>();
                    for (StringTermsBucket bucket : attrValueAgg) {
                        attrValues.add(bucket.key().stringValue());
                    }
                    attrVo.setAttrValue(attrValues);
                    return attrVo;
                }).collect(Collectors.toList());
                result.setAttrs(attrVoList);
            }
            // 3.封装分页信息
            long total = search.hits().total().value();
            // 总页码 = 总记录数 / 每页显示的记录数  (total - 1) / size + 1
            int totalPages = (int) (total - 1) / EsConstant.PRODUCT_PAGE_SIZE + 1;
            result.setTotalPages(totalPages);
            result.setTotal(total);
            result.setPageNum(param.getPageNum());
            List<Integer> pageNavs = new ArrayList<>();
            for(int i = 1; i <= totalPages; i++) {
                pageNavs.add(i);
            }
            result.setPageNavs(pageNavs);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("es检索异常：{}", e.getMessage());
        }
        return result;
    }

    /**
     * 构建检索请求
     * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
     *
     * @return 检索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(EsConstant.PRODUCT_INDEX);
        // 1.过滤
        // 1.1 build -- must 模糊匹配 skuTitle
        BoolQuery.Builder bool = QueryBuilders.bool();
        if (param.getKeyword() != null && StringUtils.isNotBlank(param.getKeyword())) {
            //bool.must(q2 ->q2.match(q3 ->q3.field("skuTitle").query(param.getKeyword()))));
            bool.must(QueryBuilders.match(q -> q.field("skuTitle").query(param.getKeyword())));
        }
        // 1.2 bool -- filter 品牌
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            List<Long> brandId = param.getBrandId();
            //转为 List<FieldValue> v
            List<FieldValue> v = brandId.stream().map(
                    FieldValue::of
            ).collect(Collectors.toList());
            bool.filter(q2 ->
                    q2.terms(t -> t.field("brandId")
                            .terms(ts -> ts.value(v))));
        }
        //分类
        if (param.getCatalog3Id() != null) {
            bool.filter(q2 ->
                    q2.term(t ->
                            t.field("catalogId").value(param.getCatalog3Id())));
        }
        // 1.3 bool -- filter -- hasStock 库存
        if (param.getHasStock() != null) {
            bool.filter(q2 ->
                    q2.term(t ->
                            t.field("hasStock").value(param.getHasStock() == 1)));
        }
        // 1.4 bool -- filter -- skuPrice 价格区间
        if (param.getSkuPrice() != null) {
            String[] split = param.getSkuPrice().split("_");
            if ("".equalsIgnoreCase(split[0])) {
                //说明没有最低价格
                bool.filter(q2 ->
                        q2.range(r ->
                                r.field("skuPrice")
                                        .lte(JsonData.of(split[1]))));
                // 6000_ 分割之后长度为1 ['6000']

            } else if (split.length == 1) {
                //说明没有最高价格
                bool.filter(q2 ->
                        q2.range(r ->
                                r.field("skuPrice")
                                        .gte(JsonData.of(split[0]))));
            } else {
                //说明有最低价格和最高价格
                bool.filter(q2 ->
                        q2.range(r ->
                                r.field("skuPrice")
                                        .gte(JsonData.of(split[0]))
                                        .lte(JsonData.of(split[1]))));
            }
        }
        // 1.5 bool -- filter -- attrs 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<String> attrs = param.getAttrs();
            for (String attr : attrs) {
                String[] split = attr.split("_");
                String attrId = split[0];
                String[] attrValues = split[1].split(":");
                //attrs=1_5寸:8寸&attrs=2_16G:8G
                //attrs=1_5寸:8寸&attrs=2_16G:8G&attrs=3 _ 白色:黑色
                BoolQuery.Builder bool1 = QueryBuilders.bool();
                bool1.must(QueryBuilders.term(t ->
                                t.field("attrs.attrId").value(attrId)))
                        .must(QueryBuilders.terms(t ->
                                t.field("attrs.attrValue")
                                        .terms(ts -> ts.value(Arrays.stream(attrValues).map(FieldValue::of).collect(Collectors.toList())))));
                bool.filter(QueryBuilders.nested(n ->
                        n.path("attrs")
                                .query(b ->
                                        b.bool(bool1.build()))));
            }
        }
        // 2.排序，分页，高亮，聚合分析
        // 2.1排序
        if (StringUtils.isNotBlank(param.getSort())) {
            builder.sort(s -> {
                String[] split = param.getSort().split("_");
                s.field(f ->
                        f.field(split[0])
                                .order(split[1].equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc));
                return s;
            });
        }

        // 2.2 高亮
        builder.highlight(h ->
                h.fields("skuTitle",
                        h1 -> h1.preTags("<b style='color:red'>").postTags("</b>")));
        // 2.3 分页
        builder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        builder.size(EsConstant.PRODUCT_PAGE_SIZE);

        // 3.聚合分析

        //if (param.getAttrs() != null && param.getAttrs().size() > 0) {
        //品牌的聚合
        builder.aggregations("brand_agg", a ->
                        a.terms(t ->
                                        t.field("brandId").size(50))
                                // 品牌的子聚合
                                .aggregations("brand_name_agg", a1 ->
                                        a1.terms(t1 ->
                                                t1.field("brandName").size(1)))
                                .aggregations("brand_img_agg", a2 ->
                                        a2.terms(t2 ->
                                                t2.field("brandImg").size(1)))
                ).aggregations("catalogId_agg", a ->
                        a.terms(t ->
                                        t.field("catalogId").size(50))
                                .aggregations("catalog_name_agg", a1 ->
                                        a1.terms(t1 ->
                                                t1.field("catalogName").size(1))))
                .aggregations("attr_agg", a ->
                        a.nested(n ->
                                        n.path("attrs"))
                                .aggregations("attr_id_agg", a1 ->
                                        a1.terms(t1 ->
                                                        t1.field("attrs.attrId").size(50))
                                                .aggregations("attr_name_agg", a2 ->
                                                        a2.terms(t2 ->
                                                                t2.field("attrs.attrName").size(1)))
                                                .aggregations("attr_value_agg", a3 ->
                                                        a3.terms(t3 ->
                                                                t3.field("attrs.attrValue").size(50)))));
        //}
        builder.query(q -> q.bool(bool.build()));
        return builder.build();
    }


}