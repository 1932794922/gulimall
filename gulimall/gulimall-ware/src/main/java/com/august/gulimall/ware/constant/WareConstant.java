package com.august.gulimall.ware.constant;

/**
 * @author : Crazy_August
 * @description : 仓储常量
 * @Time: 2023-05-26   21:03
 */
public class WareConstant {

    public static final String MQ_STOCK_EXCHANGE = "stock-event-exchange";

    public static final String MQ_STOCK_RELEASE_ROUTING_KEY = "stock.release.#";

    public static final String MQ_STOCK_RELEASE_QUEUE = "stock.release.stock.queue";

    public static final String MQ_STOCK_LOCKED_DELAY_ROUTING_KEY = "stock.locked";

    public static final String MQ_STOCK_DELAY_QUEUE = "stock.delay.queue";
    public static final Long MQ_STOCK_MESSAGE_TTL = 60000L;
}
