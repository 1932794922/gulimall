package com.august.gulimall.ware.feign;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.ware.vo.MemberAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @RequestMapping("/member/memberreceiveaddress/{id}")
    Result<MemberAddressVO> addrInfo(@PathVariable("id") Long id);
}
