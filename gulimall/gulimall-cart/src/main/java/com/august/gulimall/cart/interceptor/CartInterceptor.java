package com.august.gulimall.cart.interceptor;

import com.august.gulimall.cart.vo.UserInfoTO;
import com.august.gulimall.common.constant.AuthServerConstant;
import com.august.gulimall.common.constant.CartConstant;
import com.august.gulimall.common.to.MemberTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author : Crazy_August
 * @description : 购物车拦截器
 * @Time: 2023-05-13   13:09
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTO UserInfoTO = new UserInfoTO();

        HttpSession session = request.getSession();
        MemberTO member = (MemberTO) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (member != null) {
            // 如果用户登录了设置用户id
            UserInfoTO.setUserId(member.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    UserInfoTO.setUserKey(cookie.getValue());
                    UserInfoTO.setTempUser(true);
                }
            }
        }

        // 如果没临时用户一定要分配一个userKey
        if (StringUtils.isBlank(UserInfoTO.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            UserInfoTO.setUserKey(uuid);
        }
        threadLocal.set(UserInfoTO);

        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTO UserInfoTO = threadLocal.get();

        // 如果tempUser为false说明没有cookie，那就创建
        if (!UserInfoTO.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, UserInfoTO.getUserKey());
            cookie.setDomain("xx.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
