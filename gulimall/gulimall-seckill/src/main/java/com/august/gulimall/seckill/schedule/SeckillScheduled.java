package com.august.gulimall.seckill.schedule;

import com.august.gulimall.seckill.constant.SeckillConstant;
import com.august.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-28   15:18
 */
@Component
@Slf4j
public class SeckillScheduled {

    @Resource
    SeckillService seckillService;

    @Resource
    RedissonClient redissonClient;

    /**
     * 上架最近三天的秒杀商品
     */
    @Scheduled(cron = "*/60 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        //1.重复上架无需处理
        //加上分布式锁，防止多个服务同时上架
        RLock lock = redissonClient.getLock(SeckillConstant.SECKILL_UPLOAD_LOCK);
        log.info("上架最近三天的秒杀商品");
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        } finally {
            lock.unlock();
        }
    }

}
