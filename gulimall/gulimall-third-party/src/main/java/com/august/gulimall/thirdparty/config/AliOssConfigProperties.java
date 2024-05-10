package com.august.gulimall.thirdparty.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author : Crazy_August
 * @description : 阿里云配置类
 * @Time: 2022-11-01   21:42
 */
@Configuration
@ConfigurationProperties(prefix = "alibaba.cloud")
@Data
public class AliOssConfigProperties {

    private String accessKey;

    private String secretKey;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.callback-url}")
    private String callbackUrl;

}
