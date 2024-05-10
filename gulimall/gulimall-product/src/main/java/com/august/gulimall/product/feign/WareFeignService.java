package com.august.gulimall.product.feign;

import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 仓库openFeign
 *
 * @author xzy
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/ware/waresku/hasStock")
    Result<List<SkuHasStore>> hasStock(@RequestBody List<Long> skuIds);
}
