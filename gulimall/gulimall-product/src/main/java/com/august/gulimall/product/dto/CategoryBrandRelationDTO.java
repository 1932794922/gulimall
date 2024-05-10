package com.august.gulimall.product.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2023-04-02
 */
@Data
@ApiModel(value = "分类关联的品牌")
public class CategoryBrandRelationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "分类id")
	private Long catelogId;

	@ApiModelProperty(value = "品牌名")
	private String brandName;

	@ApiModelProperty(value = "分类名")
	private String catelogName;


}