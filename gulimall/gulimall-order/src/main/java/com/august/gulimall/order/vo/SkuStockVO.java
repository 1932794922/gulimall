package com.august.gulimall.order.vo;

import lombok.Data;

/**
 * @author xzy
 */
@Data
public class SkuStockVO {
    private Long skuId;
    private Integer store;
    private Boolean hasStock;
}
