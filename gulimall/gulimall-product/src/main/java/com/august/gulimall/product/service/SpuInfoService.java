package com.august.gulimall.product.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.product.dto.SpuInfoDTO;
import com.august.gulimall.product.entity.SpuInfoEntity;
import com.august.gulimall.product.vo.SpuInfoSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface SpuInfoService extends CrudService<SpuInfoEntity, SpuInfoDTO> {

    void spuInfoSave(SpuInfoSaveVO spuinfoSaveVO);


    /**
     * 商品上架
     * @param spuId
     */
    void spuInfoUp(Long spuId);
}