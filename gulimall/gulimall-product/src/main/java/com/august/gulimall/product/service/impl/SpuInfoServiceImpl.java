package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.constant.ProductConstant;
import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.to.SkuReductionTO;
import com.august.gulimall.common.to.SpuBoundTO;
import com.august.gulimall.common.to.esmodel.SkuEsModel;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.product.dao.SkuInfoDao;
import com.august.gulimall.product.dao.SpuInfoDescDao;
import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.dto.BrandDTO;
import com.august.gulimall.product.dto.CategoryDTO;
import com.august.gulimall.product.entity.*;
import com.august.gulimall.product.feign.CouponFeignService;
import com.august.gulimall.product.feign.ElasticSearchFeignService;
import com.august.gulimall.product.feign.WareFeignService;
import com.august.gulimall.product.service.*;
import com.august.gulimall.product.vo.SpuInfoSaveVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.SpuInfoDao;
import com.august.gulimall.product.dto.SpuInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * spu信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
public class SpuInfoServiceImpl extends CrudServiceImpl<SpuInfoDao, SpuInfoEntity, SpuInfoDTO> implements SpuInfoService {


    @Resource
    private SpuInfoDescDao spuInfoDescDao;


    @Resource
    SpuInfoDao spuInfoDao;

    @Resource
    SpuImagesService spuImagesService;


    @Resource
    AttrService attrService;

    @Resource
    ProductAttrValueService productAttrValueService;

    @Resource
    SkuInfoService skuInfoService;

    @Resource
    SkuImagesService skuImagesService;

    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    CouponFeignService couponFeignService;

    @Resource
    BrandService brandService;

    @Resource
    CategoryService categoryService;

    @Resource
    WareFeignService wareFeignService;


    @Resource
    ElasticSearchFeignService elasticSearchFeignService;

    @Override
    public QueryWrapper<SpuInfoEntity> getWrapper(Map<String, Object> params) {
        //status: 0
        //key: 33
        //brandId: 1
        //catelogId: 225
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        wrapper.eq(StringUtils.isNotBlank(status), "publish_status", status);
        wrapper.eq(StringUtils.isNotBlank(brandId), "brand_id", brandId);
        wrapper.eq(StringUtils.isNotBlank(catelogId), "catalog_id", catelogId);
        wrapper.and(StringUtils.isNotBlank(key), w -> {
            w.eq("id", key).or().like("spu_name", key);
        });
        //wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void spuInfoSave(SpuInfoSaveVO spuinfoSaveVO) {
        //1、保存spu基本信息、 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuinfoSaveVO, spuInfoEntity);
        //转为Data类型
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoDao.insert(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //2、保存spu的描述图片pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        List<String> decript = spuinfoSaveVO.getDecript();
        spuInfoDescEntity.setDecript(StringUtils.join(decript, ","));
        spuInfoDescDao.insert(spuInfoDescEntity);
        //3、保存spu的图片集 pms_spu_images
        List<String> images = spuinfoSaveVO.getImages();
        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
        spuImagesEntity.setSpuId(spuId);
        spuImagesEntity.setImgUrl(StringUtils.join(images, ","));
        spuImagesService.saveImages(spuId, images);
        //4、保存spu的规格参数; pms_product_attr_value
        List<SpuInfoSaveVO.BaseAttrs> baseAttrs = spuinfoSaveVO.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(attr, productAttrValueEntity);
            productAttrValueEntity.setSpuId(spuId);
            AttrDTO attrDTO = attrService.get(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrDTO.getAttrName());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        //批量保存
        productAttrValueService.saveBatch(collect);
        //5、保存spu的积分信息; gulimall_smslms_spu_bounds
        SpuInfoSaveVO.Bounds bounds = spuinfoSaveVO.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        spuBoundTO.setBuyBounds(bounds.getBuyBounds());
        spuBoundTO.setGrowBounds(bounds.getGrowBounds());
        spuBoundTO.setSpuId(spuId);
        Result result = couponFeignService.saveSpuBounds(spuBoundTO);
        if (result.getCode() != 200) {
            log.error("远程保存spu积分信息失败");
        }
        List<SpuInfoSaveVO.Skus> skus = spuinfoSaveVO.getSkus();
        //5、保存当前spu对应的所有sku信息.;
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (SpuInfoSaveVO.Skus.Images img : item.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                        break;
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuinfoSaveVO.getBrandId());
                skuInfoEntity.setCatalogId(spuinfoSaveVO.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1)、sku的基本信息;pms_sku_info
                Long skuId = skuInfoService.saveSkuInfo(skuInfoEntity);
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream()
                        .filter(e -> {
                            //过滤掉没有图片的
                            return StringUtils.isNotBlank(e.getImgUrl());
                        })
                        .map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuId);
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());
                            return skuImagesEntity;
                        }).collect(Collectors.toList());

                //5.2)、sku的图片信息; pms_sku_images
                skuImagesService.saveBatch(skuImagesEntities);
                //5.3)、sku的销售属性信息: pms_sku_sale_attr_vaLue
                List<SpuInfoSaveVO.Skus.Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(attr1 -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr1, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                //5.3)、sku的销售属性信息: pms_sku_sale_attr_vaLue
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //5.4)、sku的优惠、满减等信息; gulimall_sms->sms_sku_ladder \sms_sku_full_reduction \sms
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item, skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    //skuReductionTO.setAddOther(1);
                    Result result1 = couponFeignService.saveSkuReduction(skuReductionTO);
                    if (result1.getCode() != 200) {
                        log.error("远程保存sku优惠信息失败");
                    }
                } else {
                    //skuReductionTO.setAddOther(0);
                }

            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void spuInfoUp(Long spuId) {
        //1.根据spuId查询所有skuId，要查询当前sku的所有可以被检索的规格属性
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.selectSkuIdListBySpuId(spuId);
        if (skuInfoEntities == null || skuInfoEntities.size() == 0) {
            throw new RenException(500, "当前商品没有sku");
        }
        //2.根据skuId查询所有可以被检索的规格属性
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        //2.1、过滤出可以被检索的规格属性
        List<Long> attrIds = attrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        if (attrIds.size() == 0) {
            attrIds.add(0L);
        }
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);

        //2.2、去重,只保留可以被检索的规格属性,使用set比list效率高
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        //2.3、过滤出可以被检索的规格属性
        List<SkuEsModel.Attrs> attrsList = attrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        //TODO: 查询品牌信息和分类信息,同一个spu下的sku的品牌和分类信息都是一样的
        Long brandId = skuInfoEntities.get(0).getBrandId();
        BrandDTO brandDTO = brandService.get(brandId);
        Long catalogId = skuInfoEntities.get(0).getCatalogId();
        CategoryDTO categoryDTO = categoryService.get(catalogId);

        //TODO: 发送远程调用,查询库存信息
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Result<List<SkuHasStore>> result = wareFeignService.hasStock(skuIdList);
        Map<Long, Boolean> stockMap = null;
        if (result.getCode() != HttpStatus.SC_OK) {
            log.error("远程查询库存服务失败");
        } else {
            stockMap = result.getData().stream().peek(item -> {
                item.setHasStock(item.getStore() > 0);
            }).collect(Collectors.toMap(SkuHasStore::getSkuId, SkuHasStore::getHasStock));
        }
        //3.封装每一个sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esModels = skuInfoEntities.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

            //设置品牌和分类信息
            skuEsModel.setBrandImg(brandDTO.getLogo());
            skuEsModel.setBrandName(brandDTO.getName());
            skuEsModel.setBrandId(brandDTO.getBrandId());
            skuEsModel.setCatalogId(categoryDTO.getCatId());
            skuEsModel.setCatalogName(categoryDTO.getName());
            //hasStock sku库存信息
            if (finalStockMap != null) {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            } else {
                skuEsModel.setHasStock(true);
            }
            //hotScore 热度评分
            skuEsModel.setHotScore(0L);

            //设置检索属性
            skuEsModel.setAttrs(attrsList);
            return skuEsModel;
        }).collect(Collectors.toList());
        //TODO: es保存数据
        Result r = elasticSearchFeignService.productStatusUp(esModels);
        if (r.getCode() != HttpStatus.SC_OK) {
            log.error("远程es保存数据失败");
            //TODO: 重复调用,接口幂等性
        }else {
            //TODO: 修改spu的状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            baseDao.updateById(spuInfoEntity);
        }
    }

}