package com.august.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {
    private List<CartItem> items;
    // 商品数量
    private Integer countNum;
    // 商品类型
    private Integer countType;
    // 商品总价格
    private BigDecimal totalAmount;
    // 减免价格
    private BigDecimal reduce = new BigDecimal("0.00");



    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int countType = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                countType += 1;

            }
        }
        return countType;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amout = new BigDecimal("0");
        // 1.计算购物项总价格
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                   BigDecimal totalPrice = item.getTotalPrice();
                   amout = amout.add(totalPrice);
                }
            }
        }

        // 2.减去优惠价格
        BigDecimal subtract = amout.subtract(getReduce());

        return subtract;
    }


}
