package com.august.gulimall.order.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.order.to.SpuInfoTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据skuId查询spu的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/product/spuinfo/{skuId}")
    Result<SpuInfoTO> getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
