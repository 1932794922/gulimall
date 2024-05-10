package com.august.gulimall.seckill.dto;

import com.august.gulimall.common.to.SkuInfoTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-28   17:33
 */
@Data
public class SeckillSessionRedisDTO {

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

    /**
     * 商品的详细信息
     */
    private SkuInfoTO  skuInfoTO;

    /**
     * 秒杀开始时间
     */
    private Long startTime;

    /**
     * 秒杀结束时间
     */
    private Long endTime;

    /**
     * 当前商品秒杀的随机码
     */
    private String randomCode;
}
