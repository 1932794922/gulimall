package com.august.gulimall.cart.service;

import com.august.gulimall.cart.vo.Cart;
import com.august.gulimall.cart.vo.CartItem;

import java.util.List;

public interface CartService {
    /**
     * 将商品添加到购物车
     */
    CartItem addToCart(Long skuId, Integer num) ;

    /**
     * 获取购物车中某个购物项
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车里面的信息
     * @return
     */
    Cart getCart() ;

    /**
     * 清空购物车
     */
    void clearCart(String cartKey);


    /**
     * 勾选购物项
     * @param skuId
     * @param checked
     */
    void checkItem(Long skuId, Integer checked);

    /**
     * 改变商品数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     * @param skuId
     */
    void deleteIdCartInfo(Integer skuId);

    List<CartItem> getUserCartItems();
}