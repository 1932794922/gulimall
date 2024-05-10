package com.august.gulimall.cart.feign;

import com.august.gulimall.common.to.SkuInfoTO;
import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xzy
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 获取sku信息
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/{skuId}")
    Result<SkuInfoTO> getSkuInfo(@PathVariable("skuId") Long skuId);


    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    Result<List<String>> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/price")
    Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId);
}
