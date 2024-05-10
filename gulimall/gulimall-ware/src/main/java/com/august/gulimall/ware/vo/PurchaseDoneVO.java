package com.august.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author xzy
 */
@Data
public class PurchaseDoneVO {
    /**
     * 采购单id
     */
    @NotNull
    private Long id;
    private List<PurchaseItemDoneVO> items;
    @Data
    public static class PurchaseItemDoneVO {
        //{itemId:1,status:4,reason:""}
        private Long itemId;
        private Integer status;
        private String reason;
    }
}



