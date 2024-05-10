package com.august.gulimall.order.config;

import com.august.gulimall.order.constant.OrderConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : Crazy_August
 * @description : RabbitMQ 消息队列配置
 * @Time: 2023-05-26   20:28
 */
@Configuration
public class MyMQConfig {

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(OrderConstant.MQ_ORDER_EXCHANGE, true, false);
    }


    @Bean
    public Queue orderDelayQueue() {
        /**
         *设置延时队列的参数
         */
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", OrderConstant.MQ_ORDER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", OrderConstant.MQ_ORDER_RELEASE_ROUTING_KEY);
        arguments.put("x-message-ttl", TimeUnit.MILLISECONDS.toMillis(OrderConstant.MQ_ORDER_MESSAGE_TTL));
        return new Queue(OrderConstant.MQ_ORDER_DELAY_QUEUE, true, false, false,arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue(OrderConstant.MQ_ORDER_RELEASE_QUEUE, true, false, false);
    }

    @Bean
    public Queue orderReleaseCouponQueue() {
        return new Queue(OrderConstant.MQ_ORDER_RELEASE_COUPON_QUEUE, true, false, false);
    }

    @Bean
    public Binding orderCreateDelayBinding() {
        return new Binding(OrderConstant.MQ_ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.MQ_ORDER_EXCHANGE,
                OrderConstant.MQ_ORDER_CREATE_DELAY_ROUTING_KEY,
                null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(OrderConstant.MQ_ORDER_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.MQ_ORDER_EXCHANGE,
                OrderConstant.MQ_ORDER_RELEASE_ROUTING_KEY,
                null);
    }

    /**
     * 订单释放直接和库存释放绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                OrderConstant.MQ_ORDER_EXCHANGE,
                OrderConstant.MQ_OTHER_RELEASE_ROUTING_KEY_WITH,
                null);
    }

}
