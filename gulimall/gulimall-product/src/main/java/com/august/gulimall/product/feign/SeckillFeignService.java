package com.august.gulimall.product.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.product.vo.SeckillSkuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("gulimall-seckill")
public interface SeckillFeignService {

    @GetMapping(value = "/seckill/getSeckillSkuInfo/{skuId}")
    Result<SeckillSkuVO> getSeckillSkuInfo(@PathVariable("skuId") Long skuId);
}
