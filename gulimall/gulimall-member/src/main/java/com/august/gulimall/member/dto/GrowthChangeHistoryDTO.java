package com.august.gulimall.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 成长值变化历史记录
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@ApiModel(value = "成长值变化历史记录")
public class GrowthChangeHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "member_id")
	private Long memberId;

	@ApiModelProperty(value = "create_time")
	private Date createTime;

	@ApiModelProperty(value = "改变的值（正负计数）")
	private Integer changeCount;

	@ApiModelProperty(value = "备注")
	private String note;

	@ApiModelProperty(value = "积分来源[0-购物，1-管理员修改]")
	private Integer sourceType;


}