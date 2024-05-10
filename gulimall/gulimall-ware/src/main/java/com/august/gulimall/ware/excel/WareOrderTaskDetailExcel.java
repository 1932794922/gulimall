package com.august.gulimall.ware.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 库存工作单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
public class WareOrderTaskDetailExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "sku_id")
    private Long skuId;
    @Excel(name = "sku_name")
    private String skuName;
    @Excel(name = "购买个数")
    private Integer skuNum;
    @Excel(name = "工作单id")
    private Long taskId;

}