package com.august.gulimall.order.constant;

public class OrderConstant {
    public static final String USER_ORDER_TOKEN_PREFIX = "order:token:";

    public static final String MQ_ORDER_EXCHANGE = "order-event-exchange";

    public static final String MQ_ORDER_RELEASE_ROUTING_KEY = "order.release.order";

    public static final String MQ_ORDER_RELEASE_QUEUE = "order.release.order.queue";

    public static final String MQ_ORDER_CREATE_DELAY_ROUTING_KEY = "order.create.order";

    public static final String MQ_ORDER_DELAY_QUEUE = "order.delay.queue";

    public static final String MQ_ORDER_RELEASE_COUPON_QUEUE = "order.release.coupon.queue";

    public static final String MQ_OTHER_RELEASE_ROUTING_KEY_WITH = "order.release.other.#";

    public static final String MQ_OTHER_RELEASE_ROUTING_KEY = "order.release.other";

    public static final Long MQ_ORDER_MESSAGE_TTL = 60000L;
}
