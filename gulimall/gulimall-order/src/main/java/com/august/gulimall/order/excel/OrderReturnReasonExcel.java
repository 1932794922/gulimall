package com.august.gulimall.order.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 退货原因
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
public class OrderReturnReasonExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "退货原因名")
    private String name;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "启用状态")
    private Integer status;
    @Excel(name = "create_time")
    private Date createTime;

}