package com.august.gulimall.coupon.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 秒杀活动商品关联
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@ApiModel(value = "秒杀活动商品关联")
public class SeckillSkuRelationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "活动id")
	private Long promotionId;

	@ApiModelProperty(value = "活动场次id")
	private Long promotionSessionId;

	@ApiModelProperty(value = "商品id")
	private Long skuId;

	@ApiModelProperty(value = "秒杀价格")
	private BigDecimal seckillPrice;

	@ApiModelProperty(value = "秒杀总量")
	private Integer seckillCount;

	@ApiModelProperty(value = "每人限购数量")
	private BigDecimal seckillLimit;

	@ApiModelProperty(value = "排序")
	private Integer seckillSort;


}