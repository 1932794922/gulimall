package com.august.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-28   15:19
 */
//支持异步任务
@EnableAsync
//支持定时任务
@EnableScheduling
@Configuration
public class ScheduleConfig {
}
