package com.august.gulimall.auth.service.impl;

import com.august.gulimall.auth.feign.AuthServerFeignService;
import com.august.gulimall.auth.feign.AemberFeignService;
import com.august.gulimall.auth.service.LoginService;
import com.august.gulimall.auth.vo.UserLoginVO;
import com.august.gulimall.auth.vo.UserRegistVO;
import com.august.gulimall.common.constant.AuthServerConstant;
import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.common.to.MemberTO;
import com.august.gulimall.common.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-12   14:16
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    AuthServerFeignService authServerFeignService;

    @Resource
    AemberFeignService memberFeignService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendEmail(String email) {
        // 1.TODO：接口防刷
        // 2.刷新页码也不能60秒内重复发送
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.EMAIL_CODE_CACHE_PREFIX + email);
        if (StringUtils.isNotBlank(redisCode)) {
            // 如果redis存在当前手机号的验证码
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                // 小于60秒不能发送
                throw new RenException(400, "验证码发送频率太高，请稍后再试");
            }
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        Result result = authServerFeignService.sendEmail(email, code);
        if (result.getCode() != HttpStatus.SC_OK) {
            throw new RenException(400, result.getMsg());
        }
        // 存入redis并设置过期时间 email:code:13326166085 -> 1234_系统时间
        code = code + "_" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(AuthServerConstant.EMAIL_CODE_CACHE_PREFIX + email, code,
                5, TimeUnit.MINUTES);
    }

    @Override
    public void registr(UserRegistVO vo) {
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.EMAIL_CODE_CACHE_PREFIX + vo.getEmail());
        // 验证码通过
        if (StringUtils.isNotBlank(s) && code.equals(s.split("_")[0])) {
            // 删除验证码，防止用户多次使用使用过的验证码
            // TODO:存在问题又可以会有表单重复提交问题，应该用lua脚本
            stringRedisTemplate.delete(AuthServerConstant.EMAIL_CODE_CACHE_PREFIX + vo.getEmail());
            // 调用远程注册服务
            Result r = memberFeignService.regist(vo);
            if (r.getCode() != 200) {
                // 注册失败，转发到注册页
                throw new RenException(r.getCode(), r.getMsg());
            }
        } else {
            throw new RenException(4000, "验证码错误");
        }
    }

    @Override
    public Result login(UserLoginVO vo) {
        Result<MemberTO> member = memberFeignService.login(vo);
        return member;
    }
}
