package com.august.gulimall.product.feign;

import com.august.gulimall.common.to.SkuReductionTO;
import com.august.gulimall.common.to.SpuBoundTO;
import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author : Crazy_August
 * @description : 优惠价openFeign
 * @Time: 2023-04-23   21:46
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 保存优惠信息
     * @param spuBoundTO
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    Result saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    /**
     * 保存sku优惠信息
     * @param skuReductionTO
     * @return
     */
    @PostMapping("/coupon/spubounds/saveureduction")
    Result saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
