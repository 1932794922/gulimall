package com.august.gulimall.seckill.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.seckill.dto.SeckillSessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author xzy
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 上架最近三天的秒杀商品
     */
    @GetMapping("coupon/seckillsession/last3daysSession")
    Result<List<SeckillSessionDTO>> getSeckillSkuLatest3Days();
}
