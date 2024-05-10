package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.SkuInfoDTO;
import com.august.gulimall.product.entity.SkuInfoEntity;
import com.august.gulimall.product.vo.SkuItemVO;

import java.util.List;

/**
 * sku信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface SkuInfoService extends CrudService<SkuInfoEntity, SkuInfoDTO> {

    Long saveSkuInfo(SkuInfoEntity skuInfoEntity);

    List<SkuInfoEntity> selectSkuIdListBySpuId(Long spuId);

    /**
     * 商品详情
     * @param skuId
     * @return
     */
    SkuItemVO item(Long skuId);

    /**
     * 根据skuId查询sku信息
     * @param skuId
     * @return
     */
    SkuInfoEntity getById(Long skuId);
}