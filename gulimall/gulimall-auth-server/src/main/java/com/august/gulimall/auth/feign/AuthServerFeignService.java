package com.august.gulimall.auth.feign;

import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xzy
 */
@FeignClient("gulimall-third-party")
public interface AuthServerFeignService {

    @PostMapping("/sendEmail")
    Result sendEmail(@RequestParam String email, @RequestParam String code);
}
