package com.august.gulimall.coupon.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品分类积分设置
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
public class CategoryBoundsExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "")
    private Long categoryId;
    @Excel(name = "成长积分")
    private BigDecimal growBounds;
    @Excel(name = "购物积分")
    private BigDecimal buyBounds;
    @Excel(name = "优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]")
    private Integer work;

}