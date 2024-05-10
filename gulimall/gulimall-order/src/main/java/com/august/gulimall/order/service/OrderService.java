package com.august.gulimall.order.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.order.dto.OrderDTO;
import com.august.gulimall.order.entity.OrderEntity;
import com.august.gulimall.order.vo.OrderConfirmVO;
import com.august.gulimall.order.vo.OrderSubmitVO;
import com.august.gulimall.order.vo.SubmitOrderResponseVO;

/**
 * 订单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-15
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

    /**
     * 确认订单
     * @return
     */
    OrderConfirmVO confirmOrder();

    /**
     * 提交订单
     * @param vo
     * @return
     */
    SubmitOrderResponseVO submitOrder(OrderSubmitVO vo);

    /**
     * 关闭订单
     * @param order
     */
    void closeOrder(OrderEntity order);

    /**
     * 获取订单
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);
}