package com.august.gulimall.coupon.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.common.to.SkuReductionTO;
import com.august.gulimall.coupon.dto.SpuBoundsDTO;
import com.august.gulimall.coupon.entity.SpuBoundsEntity;

/**
 * 商品spu积分设置
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface SpuBoundsService extends CrudService<SpuBoundsEntity, SpuBoundsDTO> {

    void saveSkuReduction(SkuReductionTO skuReductionTO);
}