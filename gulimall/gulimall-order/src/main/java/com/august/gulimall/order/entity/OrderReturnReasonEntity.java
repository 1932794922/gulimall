package com.august.gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 退货原因
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
@TableName("oms_order_return_reason")
public class OrderReturnReasonEntity {

    /**
     * id
     */
    @TableId
	private Long id;
    /**
     * 退货原因名
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 启用状态
     */
	private Integer status;
    /**
     * create_time
     */
	private Date createTime;
}