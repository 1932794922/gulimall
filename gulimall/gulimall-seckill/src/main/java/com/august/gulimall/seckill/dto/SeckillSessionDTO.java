package com.august.gulimall.seckill.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 秒杀活动场次
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@ApiModel(value = "秒杀活动场次")
public class SeckillSessionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "场次名称")
	private String name;

	@ApiModelProperty(value = "每日开始时间")
	private Date startTime;

	@ApiModelProperty(value = "每日结束时间")
	private Date endTime;

	@ApiModelProperty(value = "启用状态")
	private Integer status;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	private List<SeckillSkuRelationDTO> relationDTOS;


}