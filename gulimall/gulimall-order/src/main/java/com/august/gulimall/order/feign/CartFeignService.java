package com.august.gulimall.order.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.order.vo.OrderItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    Result<List<OrderItemVO>> getCurrentUserCartItems();
}
