package com.august.gulimall.ware.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 仓库信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@ApiModel(value = "仓库信息")
public class WareInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "仓库名")
	private String name;

	@ApiModelProperty(value = "仓库地址")
	private String address;

	@ApiModelProperty(value = "区域编码")
	private String areacode;


}