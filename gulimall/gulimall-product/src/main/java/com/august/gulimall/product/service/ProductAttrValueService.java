package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.ProductAttrValueDTO;
import com.august.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;

/**
 * spu属性值
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface ProductAttrValueService extends CrudService<ProductAttrValueEntity, ProductAttrValueDTO> {

    void saveBatch(List<ProductAttrValueEntity> collect);

    /**
     * 根据spuId查询属性列表
     * @param spuId
     * @return
     */
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 修改spu属性
     * @param spuId
     * @param entities
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}