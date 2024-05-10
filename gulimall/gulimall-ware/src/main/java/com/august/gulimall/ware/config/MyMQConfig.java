package com.august.gulimall.ware.config;

import com.august.gulimall.ware.constant.WareConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
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
    public Exchange stockEventExchange() {
        return new TopicExchange(WareConstant.MQ_STOCK_EXCHANGE, true, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        /**
         *设置延时队列的参数
         */
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", WareConstant.MQ_STOCK_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", WareConstant.MQ_STOCK_RELEASE_ROUTING_KEY);
        arguments.put("x-message-ttl", TimeUnit.MILLISECONDS.toMillis(WareConstant.MQ_STOCK_MESSAGE_TTL));
        return new Queue(WareConstant.MQ_STOCK_DELAY_QUEUE, true, false, false,arguments);
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue(WareConstant.MQ_STOCK_RELEASE_QUEUE, true, false, false);
    }

    @Bean
    public Binding stockLockedDelayBinding() {
        return new Binding(WareConstant.MQ_STOCK_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                WareConstant.MQ_STOCK_EXCHANGE,
                WareConstant.MQ_STOCK_LOCKED_DELAY_ROUTING_KEY,
                null);
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(WareConstant.MQ_STOCK_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,
                WareConstant.MQ_STOCK_EXCHANGE,
                WareConstant.MQ_STOCK_RELEASE_ROUTING_KEY,
                null);
    }
}
