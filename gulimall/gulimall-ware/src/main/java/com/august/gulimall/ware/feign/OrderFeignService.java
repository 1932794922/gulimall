package com.august.gulimall.ware.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.ware.vo.OrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{orderSn}")
    Result<OrderVO> getOrderStatus(@PathVariable("orderSn") String orderSn);
}
