package com.august.gulimall.coupon.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 专题商品
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
public class HomeSubjectSpuExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "专题名字")
    private String name;
    @Excel(name = "专题id")
    private Long subjectId;
    @Excel(name = "spu_id")
    private Long spuId;
    @Excel(name = "排序")
    private Integer sort;

}