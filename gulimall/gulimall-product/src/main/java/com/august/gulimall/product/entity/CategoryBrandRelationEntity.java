package com.august.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity {


    @TableId(type = IdType.AUTO)
	private Long id;
    /**
     * 品牌id
     */
	private Long brandId;
    /**
     * 分类id
     */
	private Long catelogId;
    /**
     * 
     */
	private String brandName;
    /**
     * 
     */
	private String catelogName;
}