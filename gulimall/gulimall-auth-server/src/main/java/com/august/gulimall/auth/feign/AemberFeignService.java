package com.august.gulimall.auth.feign;

import com.august.gulimall.auth.vo.UserLoginVO;
import com.august.gulimall.auth.vo.UserRegistVO;
import com.august.gulimall.common.to.MemberTO;
import com.august.gulimall.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xzy
 */
@FeignClient("gulimall-member")
public interface AemberFeignService {

    @PostMapping("member/member/regist")
    Result regist(@RequestBody UserRegistVO vo);

    @PostMapping("member/member/login")
    Result<MemberTO> login(@RequestBody UserLoginVO vo);
}
