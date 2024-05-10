package com.august.gulimall.product.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * 品牌
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "品牌")
public class BrandDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "品牌名")
	private String name;

	@ApiModelProperty(value = "品牌logo地址")
	private String logo;

	@ApiModelProperty(value = "介绍")
	private String descript;

	@ApiModelProperty(value = "显示状态[0-不显示；1-显示]")
	private Integer showStatus;

	@ApiModelProperty(value = "检索首字母")
	private String firstLetter;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "是否显示")
	private Integer switchStatus;


}