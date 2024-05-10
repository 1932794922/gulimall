package com.august.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.august.gulimall.common.to.esmodel.SkuEsModel;
import com.august.gulimall.search.constant.EsConstant;
import com.august.gulimall.search.service.ElasticSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-02   16:09
 */
@Service
@Slf4j
public class ElasticSaveServiceImpl implements ElasticSaveService {


    @Resource
    ElasticsearchClient elasticsearchClient;

    @Override
    public void productStatusUp(List<SkuEsModel> skuEsModels) {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SkuEsModel skuEsModel : skuEsModels) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(EsConstant.PRODUCT_INDEX)
                            .id(skuEsModel.getSkuId().toString())
                            .document(skuEsModel)
                    )
            );
        }
        BulkResponse result = null;
        try {
            result = elasticsearchClient.bulk(br.build());
            // Log errors, if any
            if (result.errors()) {
                log.error("Bulk had errors");
                for (BulkResponseItem item : result.items()) {
                    if (item.error() != null) {
                        //TODO：商品上架失败，重试机制
                        log.error("商品上架错误：{}", item.error());
                        log.error(item.error().reason());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
