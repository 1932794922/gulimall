package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.SkuImagesDTO;
import com.august.gulimall.product.entity.SkuImagesEntity;

import java.util.List;

/**
 * sku图片
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface SkuImagesService extends CrudService<SkuImagesEntity, SkuImagesDTO> {

    void saveBatch(List<SkuImagesEntity> collect1);

    /**
     * 通过spuId获取图片列表
     * @param spuId
     * @return
     */
    List<SkuImagesEntity> getImagesBySkuId(Long spuId);
}