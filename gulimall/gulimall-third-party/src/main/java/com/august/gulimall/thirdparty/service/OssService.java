package com.august.gulimall.thirdparty.service;

import java.util.Map;

/**
 * @author xzy
 */
public interface OssService {
    /**
     * 生成签名
     * @return
     */
    Map<String,String> generatePolicy();
}
