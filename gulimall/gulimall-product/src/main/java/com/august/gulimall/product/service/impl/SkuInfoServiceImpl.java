package com.august.gulimall.product.service.impl;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.product.dao.SpuInfoDescDao;
import com.august.gulimall.product.entity.SkuImagesEntity;
import com.august.gulimall.product.entity.SpuInfoDescEntity;
import com.august.gulimall.product.feign.SeckillFeignService;
import com.august.gulimall.product.service.*;
import com.august.gulimall.product.vo.SeckillSkuVO;
import com.august.gulimall.product.vo.SkuItemVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.product.dao.SkuInfoDao;
import com.august.gulimall.product.dto.SkuInfoDTO;
import com.august.gulimall.product.entity.SkuInfoEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * sku信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Service
@Slf4j
public class SkuInfoServiceImpl extends CrudServiceImpl<SkuInfoDao, SkuInfoEntity, SkuInfoDTO> implements SkuInfoService {



    @Resource
    SkuImagesService skuImagesService;

    @Resource
    AttrGroupService attrGroupService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    ThreadPoolExecutor executor;

    @Resource
    SpuInfoDescDao spuInfoDescDao;


    @Resource
    SeckillFeignService seckillFeignService;
    @Override
    public QueryWrapper<SkuInfoEntity> getWrapper(Map<String, Object> params) {
        //skuId:
        //key: 2121
        //brandId: 1
        //catelogId: 225
        //price: {"min":0,"max":5000}
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String price = (String) params.get("price");
        //"{"min":0,"max":5000}" 转为json对象
        ObjectMapper objectMapper = new ObjectMapper();
        Map priceMap = null;
        try {
            priceMap = objectMapper.readValue(price, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String min = priceMap.get("min").toString();
        String max = priceMap.get("max").toString();
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        if (StringUtils.isNotBlank(min) && !"0".equalsIgnoreCase(min)) {
            wrapper.ge("price", min);
        }
        if (StringUtils.isNotBlank(max) && !"0".equalsIgnoreCase(max)) {
            wrapper.le("price", max);
        }

        return wrapper;
    }


    @Override
    public Long saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        int insert = baseDao.insert(skuInfoEntity);
        Long skuId = (long) insert;
        return skuId;
    }

    @Override
    public List<SkuInfoEntity> selectSkuIdListBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfoEntity::getSpuId, spuId);
        List<SkuInfoEntity> entityList = baseDao.selectList(wrapper);
        return entityList;
    }

    /**
     * 获取sku的详细信息(串行化)
     *
     * @param skuId
     * @return
     */
    public SkuItemVO itemSerialization(Long skuId) {
        long sysTime = System.currentTimeMillis();
        SkuItemVO skuItemVO = new SkuItemVO();
        // 1.sku基本信息获取 pms_sku_info
        SkuInfoEntity skuInfoEntity = baseDao.selectById(skuId);
        skuItemVO.setInfo(skuInfoEntity);
        Long catalogId = skuInfoEntity.getCatalogId();
        // 2.sku图片信息 pms_sku_images
        Long spuId = skuInfoEntity.getSpuId();
        List<SkuImagesEntity> skuImages = skuImagesService.getImagesBySkuId(skuId);
        skuItemVO.setImages(skuImages);
        // 3.获取spu的销售属性
        List<SkuItemVO.SkuItemSaleAttrVO> saleAttr = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVO.setSaleAttr(saleAttr);
        // 4.获取spu的介绍
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescDao.selectById(spuId);
        skuItemVO.setDesp(spuInfoDescEntity);
        // 5.获取spu的规格参数信息
        List<SkuItemVO.SpuItemAttrGroupVO> groupAttrs = attrGroupService.getAttrGroupWithAttrs(spuId, catalogId);
        skuItemVO.setGroupAttrs(groupAttrs);
        log.info("获取sku的详细信息(串行化)耗时：{}", System.currentTimeMillis() - sysTime);
        return skuItemVO;
    }

    /**
     * 获取sku的详细信息(并行化)
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO skuItemVO = new SkuItemVO();
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.sku基本信息获取 pms_sku_info
            SkuInfoEntity skuInfoEntity = baseDao.selectById(skuId);
            skuItemVO.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        CompletableFuture<Void> saleFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            // 3.获取spu的销售属性
            Long spuId = res.getSpuId();
            List<SkuItemVO.SkuItemSaleAttrVO> saleAttr = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
            skuItemVO.setSaleAttr(saleAttr);
        }, executor);
        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            // 4.获取spu的介绍
            Long spuId = res.getSpuId();
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescDao.selectById(spuId);
            skuItemVO.setDesp(spuInfoDescEntity);
        }, executor);

        CompletableFuture<Void> groupFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            // 5.获取spu的规格参数信息
            Long catalogId = res.getCatalogId();
            Long spuId = res.getSpuId();
            List<SkuItemVO.SpuItemAttrGroupVO> groupAttrs = attrGroupService.getAttrGroupWithAttrs(spuId, catalogId);
            skuItemVO.setGroupAttrs(groupAttrs);
        }, executor);

        // 2.sku图片信息 pms_sku_images
        List<SkuImagesEntity> skuImages = skuImagesService.getImagesBySkuId(skuId);
        skuItemVO.setImages(skuImages);

        //3.秒杀商品的优惠信息
        CompletableFuture<Void> seckFuture = CompletableFuture.runAsync(() -> {
            Result<SeckillSkuVO> r = seckillFeignService.getSeckillSkuInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuVO seckillSkuVo = r.getData();
                long current = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
                //如果返回结果不为空且活动未过期，设置秒杀信息
                if (seckillSkuVo != null&&current < seckillSkuVo.getEndTime()) {
                    skuItemVO.setSeckillSkuVO(seckillSkuVo);
                }
            }
        }, executor);
        //等待所有任务完成
        CompletableFuture.allOf(saleFuture, descFuture, groupFuture,seckFuture).join();
        return skuItemVO;
    }

    @Override
    public SkuInfoEntity getById(Long skuId) {
        SkuInfoEntity skuInfoEntity = baseDao.selectById(skuId);
        return skuInfoEntity;
    }
}