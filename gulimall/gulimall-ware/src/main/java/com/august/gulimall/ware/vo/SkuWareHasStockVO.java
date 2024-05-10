package com.august.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuWareHasStockVO {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}
