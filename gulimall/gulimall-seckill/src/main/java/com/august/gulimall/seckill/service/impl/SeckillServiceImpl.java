package com.august.gulimall.seckill.service.impl;

import cn.hutool.json.JSON;
import com.august.gulimall.common.to.SkuInfoTO;
import com.august.gulimall.common.utils.Result;
import com.august.gulimall.seckill.constant.SeckillConstant;
import com.august.gulimall.seckill.dto.SeckillSessionDTO;
import com.august.gulimall.seckill.dto.SeckillSessionRedisDTO;
import com.august.gulimall.seckill.dto.SeckillSkuRelationDTO;
import com.august.gulimall.seckill.feign.CouponFeignService;
import com.august.gulimall.seckill.feign.ProductFeignService;
import com.august.gulimall.seckill.service.SeckillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-28   15:13
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Resource
    CouponFeignService couponFeignService;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    RedissonClient redissonClient;
    @Resource
    RedisTemplate redisTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1.查询最近三天需要上架的秒杀商品
        Result<List<SeckillSessionDTO>> result = couponFeignService.getSeckillSkuLatest3Days();
        if (result.getCode() == 200) {
            List<SeckillSessionDTO> data = result.getData();
            if (data == null || data.size() == 0) {
                return;
            }
            //TODO 上架
            //2.缓存到redis
            //2.1 缓存活动信息
            saveSessionInfos(data);
            //2.2 缓存活动关联的商品信息
            saveSessionSkuInfos(data);
        }
    }

    @Override
    public List<SeckillSessionRedisDTO> getCurrentSeckillSkus() {
        List<SeckillSessionRedisDTO> collect = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(SeckillConstant.SESSIONS_CACHE_PREFIX + "*").build();
        Cursor<String> scan = redisTemplate.scan(options);
        List<String> keys = scan.stream().collect(Collectors.toList());
        long currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        for (String key : keys) {
            String replace = key.replace(SeckillConstant.SESSIONS_CACHE_PREFIX, "");
            String[] split = replace.split("_");
            long startTime = Long.parseLong(split[1]);
            long endTime = Long.parseLong(split[2]);
            //当前秒杀活动处于有效期内
            if (currentTime > startTime && currentTime < endTime) {
                List<String> range = redisTemplate.opsForList().range(key, 0, -1);
                BoundHashOperations<String, String, SeckillSessionRedisDTO> ops = redisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
                //获取所有的商品信息
                if (range != null && range.size() > 0) {
                    List<SeckillSessionRedisDTO> skuInfos = ops.multiGet(range);
                    if (skuInfos != null && skuInfos.size() > 0) {
                        List<SeckillSessionRedisDTO> collect1 = skuInfos.stream().map(item -> {
                            SeckillSessionRedisDTO redisDTO = new SeckillSessionRedisDTO();
                            BeanUtils.copyProperties(item, redisDTO);
                            return redisDTO;
                        }).collect(Collectors.toList());
                        collect.addAll(collect1);
                    }
                }
            }
        }
        return collect;
    }


    @Override
    public SeckillSessionRedisDTO getSeckillSkuInfo(Long skuId) {
        BoundHashOperations<String, String, SeckillSessionRedisDTO> ops = redisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
        Set<String> keys = ops.keys();
        if (keys == null || keys.size() == 0) {
            return null;
        }
        for (String key : keys) {
            if (Pattern.matches("\\d+_" + skuId, key)) {
                SeckillSessionRedisDTO sessionRedisDTO = ops.get(key);
                //当前商品参与秒杀活动
                if (sessionRedisDTO != null) {
                    //转为东八区时间
                    LocalDateTime now = LocalDateTime.now();
                    //转为long
                    long currentTime = now.toInstant(ZoneOffset.UTC).toEpochMilli();
                    long startTime = sessionRedisDTO.getStartTime();
                    long endTime = sessionRedisDTO.getEndTime();
                    if (currentTime > startTime && currentTime < endTime) {
                        //当前活动在有效期，暴露商品随机码返回
                    }else {
                        sessionRedisDTO.setRandomCode(null);
                    }
                    return sessionRedisDTO;
                }
            }
        }
        return null;
    }

    /**
     * 缓存活动信息
     *
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionDTO> sessions) {
        sessions.forEach(session -> {
            List<SeckillSkuRelationDTO> dtos = session.getRelationDTOS();
            if (dtos == null || dtos.size() == 0) {
                return;
            }
            //TODO 缓存到redis
            //获取当前商品的秒杀时间段
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            Long promotionSessionId = dtos.get(0).getPromotionSessionId();
            String redisKey = SeckillConstant.SESSIONS_CACHE_PREFIX +
                    promotionSessionId + "_" + startTime + "_" + endTime;
            Boolean aBoolean = redisTemplate.hasKey(redisKey);
            //获取所有的skuId
            if (!aBoolean) {
                List<String> collect = dtos.stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(redisKey, collect);
            }
        });
    }

    /**
     * 缓存活动关联的商品信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionDTO> sessions) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
        sessions.forEach(session -> {
            List<SeckillSkuRelationDTO> dtos = session.getRelationDTOS();
            if (dtos == null || dtos.size() == 0) {
                return;
            }
            //TODO 缓存到redis
            dtos.forEach(dto -> {
                if (Boolean.TRUE.equals(ops.hasKey(dto.getPromotionSessionId() + "_" + dto.getSkuId()))) {
                    //如果缓存中已经有了,就不再缓存,幂等性
                    return;
                }
                //缓存活动关联的商品信息
                SeckillSessionRedisDTO seckillSessionRedisDTO = new SeckillSessionRedisDTO();
                BeanUtils.copyProperties(dto, seckillSessionRedisDTO);
                //查询sku的详细信息
                Result<SkuInfoTO> skuInfoTO = productFeignService.getSkuInfoTO(dto.getSkuId());
                if (skuInfoTO.getCode() == 200) {
                    SkuInfoTO data = skuInfoTO.getData();
                    //1.设置sku的详细信息
                    seckillSessionRedisDTO.setSkuInfoTO(data);
                }
                //2.设置sku的秒杀信息
                seckillSessionRedisDTO.setStartTime(session.getStartTime().getTime());
                seckillSessionRedisDTO.setEndTime(session.getEndTime().getTime());
                //3.设置随机码,防止商品id泄露,脚本抢票
                String randomCode = UUID.randomUUID().toString().replace("-", "");
                seckillSessionRedisDTO.setRandomCode(randomCode);
                //4.设置库存作为分布式信号量
                String SemaphoreKey = SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode;
                redissonClient.getSemaphore(SemaphoreKey).trySetPermits(dto.getSeckillCount());
                //5.缓存到redis
                String key = dto.getPromotionSessionId() + "_" + dto.getSkuId();
                ops.put(key, seckillSessionRedisDTO);
            });
        });
    }
}
