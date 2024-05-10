package com.august.gulimall.ware.vo;

import lombok.Data;

/**
 * @author : Crazy_August
 * @description : 合并采购单
 * @Time: 2023-04-27   21:28
 */
@Data
public class MergeVO {
    /**
     * 合并后的采购单id
     */
    private Long purchaseId;
    /**
     * 合并的采购项
     */
    private Long[] items;
}
