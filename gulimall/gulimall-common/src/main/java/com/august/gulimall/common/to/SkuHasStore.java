package com.august.gulimall.common.to;

import lombok.Data;

/**
 * @author : Crazy_August
 * @description : 库存
 * @Time: 2023-04-30   22:58
 */
@Data
public class SkuHasStore {
    private Long skuId;
    private Integer store;
    private Boolean hasStock;

}
