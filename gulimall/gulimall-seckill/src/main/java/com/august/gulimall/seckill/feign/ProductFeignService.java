package com.august.gulimall.seckill.feign;

import com.august.gulimall.common.to.SkuInfoTO;
import com.august.gulimall.common.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("product/skuinfo/{id}")
    Result<SkuInfoTO> getSkuInfoTO(@PathVariable("id") Long id);
}
