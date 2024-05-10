package com.august.gulimall.order.feign;

import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.order.vo.FareVO;
import com.august.gulimall.order.vo.SkuStockVO;
import com.august.gulimall.order.vo.WareSkuLockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WmsFeignService {
    @PostMapping("/ware/waresku/hasStock")
    Result<List<SkuStockVO>> getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    Result<FareVO> getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    Result orderLockStock(@RequestBody WareSkuLockVO vo);
}
