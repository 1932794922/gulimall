package com.august.gulimall.seckill.service;

import com.august.gulimall.seckill.dto.SeckillSessionRedisDTO;

import java.util.List;

public interface SeckillService {
    void uploadSeckillSkuLatest3Days();

    /**
     * 获取缓存活动信息
     * @return
     */
    List<SeckillSessionRedisDTO> getCurrentSeckillSkus();

    /**
     * 获取缓存中的商品详情
     * @param skuId
     * @return
     */
    SeckillSessionRedisDTO getSeckillSkuInfo(Long skuId);
}
