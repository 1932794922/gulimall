package com.august.gulimall.product.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 品牌
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
public class BrandExcel {
    @Excel(name = "品牌id")
    private Long brandId;
    @Excel(name = "品牌名")
    private String name;
    @Excel(name = "品牌logo地址")
    private String logo;
    @Excel(name = "介绍")
    private String descript;
    @Excel(name = "显示状态[0-不显示；1-显示]")
    private Integer showStatus;
    @Excel(name = "检索首字母")
    private String firstLetter;
    @Excel(name = "排序")
    private Integer sort;

}