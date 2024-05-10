package com.august.gulimall.cart.vo;

import lombok.Data;

@Data
public class UserInfoTO {
    /**
     * 登录用户的id
     */
    private Long userId;
    /**
     * 临时用户的值，就是登录了也会设置
     */
    private String userKey;

    private boolean tempUser = false;
}
