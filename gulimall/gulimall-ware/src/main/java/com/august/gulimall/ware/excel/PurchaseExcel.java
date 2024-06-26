package com.august.gulimall.ware.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
public class PurchaseExcel {
    @Excel(name = "")
    private Long id;
    @Excel(name = "")
    private Long assigneeId;
    @Excel(name = "")
    private String assigneeName;
    @Excel(name = "")
    private String phone;
    @Excel(name = "")
    private Integer priority;
    @Excel(name = "")
    private Integer status;
    @Excel(name = "")
    private Long wareId;
    @Excel(name = "")
    private BigDecimal amount;
    @Excel(name = "")
    private Date createTime;
    @Excel(name = "")
    private Date updateTime;

}