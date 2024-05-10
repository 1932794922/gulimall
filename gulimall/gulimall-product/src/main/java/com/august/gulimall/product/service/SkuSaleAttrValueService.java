package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.SkuSaleAttrValueDTO;
import com.august.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.august.gulimall.product.vo.SkuItemVO;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface SkuSaleAttrValueService extends CrudService<SkuSaleAttrValueEntity, SkuSaleAttrValueDTO> {

    void saveBatch(List<SkuSaleAttrValueEntity> collect2);

    /**
     * 通过spuId获取销售属性列表
     * @param spuId
     * @return
     */
    List<SkuItemVO.SkuItemSaleAttrVO> getSaleAttrsBySpuId(Long spuId);

    /**
     * 获取sku销售属性值的id集合
     * @param skuId
     * @return
     */
    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}