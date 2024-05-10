package com.august.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author xzy
 */
@Data
public class WareSkuLockVO {
    // 订单号
    private String orderSn;
    // 需要锁住的所有库存信息
    private List<OrderItemVO> locks;
}
