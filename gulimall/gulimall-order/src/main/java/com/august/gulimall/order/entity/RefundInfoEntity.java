package com.august.gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
@Data
@TableName("oms_refund_info")
public class RefundInfoEntity {

    /**
     * id
     */
    @TableId
	private Long id;
    /**
     * 退款的订单
     */
	private Long orderReturnId;
    /**
     * 退款金额
     */
	private BigDecimal refund;
    /**
     * 退款交易流水号
     */
	private String refundSn;
    /**
     * 退款状态
     */
	private Integer refundStatus;
    /**
     * 退款渠道[1-支付宝，2-微信，3-银联，4-汇款]
     */
	private Integer refundChannel;
    /**
     * 
     */
	private String refundContent;
}