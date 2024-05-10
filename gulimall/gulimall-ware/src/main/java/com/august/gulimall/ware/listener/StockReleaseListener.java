package com.august.gulimall.ware.listener;

import com.august.gulimall.common.to.mq.OrderTO;
import com.august.gulimall.common.to.mq.StockLockedTO;
import com.august.gulimall.ware.constant.WareConstant;
import com.august.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author xzy
 */
@Component
@RabbitListener(queues = WareConstant.MQ_STOCK_RELEASE_QUEUE)
public class StockReleaseListener {
    @Resource
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTO to, Message message, Channel channel) throws IOException {
        System.out.println("手动解锁库存的消息");
        try {
            wareSkuService.unlockStock(to);
            // 接收成功，手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 如果接受消息失败，重新归队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTO to, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁库存");
        try {
            wareSkuService.unlockStock(to);
            // 接收成功，手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 如果接受消息失败，重新归队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }


}