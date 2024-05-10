package com.august.gulimall.product.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.SpuImagesDTO;
import com.august.gulimall.product.entity.SkuImagesEntity;
import com.august.gulimall.product.entity.SpuImagesEntity;

import java.util.List;

/**
 * spu图片
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface SpuImagesService extends CrudService<SpuImagesEntity, SpuImagesDTO> {

    void saveImages(Long spuId, List<String> images);

    /**
     * 通过spuId获取图片列表
     * @param spuId
     * @return
     */
    List<SpuImagesEntity> getImagesBySpuId(Long spuId);
}