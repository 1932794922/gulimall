package com.august.gulimall.product.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 属性&属性分组关联
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
public class AttrAttrgroupRelationExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "属性id")
    private Long attrId;
    @Excel(name = "属性分组id")
    private Long attrGroupId;
    @Excel(name = "属性组内排序")
    private Integer attrSort;

}