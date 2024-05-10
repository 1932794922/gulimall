package com.august.gulimall.order.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.order.vo.MemberAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    Result<List<MemberAddressVO>> getAddressById(@PathVariable("memberId") Long memberId);


}