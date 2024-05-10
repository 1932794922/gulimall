package com.august.gulimall.order.vo;

import com.august.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVO {
    private OrderEntity order;

    /** 错误状态码  200代表成功 1代表失败 2价格校验错误 3库存锁定失败**/
    private Integer code;

}
