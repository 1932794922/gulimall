package com.august.gulimall.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单操作历史记录
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
@ApiModel(value = "订单操作历史记录")
public class OrderOperateHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "订单id")
	private Long orderId;

	@ApiModelProperty(value = "操作人[用户；系统；后台管理员]")
	private String operateMan;

	@ApiModelProperty(value = "操作时间")
	private Date createTime;

	@ApiModelProperty(value = "订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
	private Integer orderStatus;

	@ApiModelProperty(value = "备注")
	private String note;


}