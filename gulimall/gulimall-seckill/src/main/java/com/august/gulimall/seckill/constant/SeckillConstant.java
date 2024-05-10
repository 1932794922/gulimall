package com.august.gulimall.seckill.constant;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-28   17:02
 */
public class SeckillConstant {
    public static final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    public static final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    public static final String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    /**
     * 分布式锁的key
     */
    public static final String SECKILL_UPLOAD_LOCK = "seckill:upload:lock";
}
