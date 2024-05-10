package com.august.gulimall.auth.service;

import com.august.gulimall.auth.vo.UserLoginVO;
import com.august.gulimall.auth.vo.UserRegistVO;
import com.august.gulimall.common.to.MemberTO;
import com.august.gulimall.common.utils.Result;

/**
 * @author xzy
 */
public interface LoginService {
    /**
     * 发送验证码
     * @param phone
     */
    void sendEmail(String email);

    /**
     * 用户注册
     * @param vo
     */
    void registr(UserRegistVO vo);

    Result<MemberTO> login(UserLoginVO vo);
}
