package com.august.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : Crazy_August
 * @description : 优惠价传输对象
 * @Time: 2023-04-23   22:08
 */
@Data
public class SpuBoundTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
