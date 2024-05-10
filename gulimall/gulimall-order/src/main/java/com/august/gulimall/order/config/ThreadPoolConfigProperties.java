package com.august.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-11   18:26
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    public Integer coreSize;
    public Integer maxSize;
    public Integer keepAliveTime;
    public Integer queueCapacity;
}
