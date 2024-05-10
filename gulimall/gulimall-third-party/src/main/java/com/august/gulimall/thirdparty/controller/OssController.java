package com.august.gulimall.thirdparty.controller;


import com.august.gulimall.common.utils.Result;
import com.august.gulimall.thirdparty.service.OssService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author : Crazy_August
 * @description : OSS Controller
 * @Time: 2022-11-01   21:28
 */
@RestController
public class OssController {

    @Resource
    OssService ossService;

    @RequestMapping("oss/policy")
    public Result<Map<String, String>> policy() {
        Map<String, String> stringStringMap = ossService.generatePolicy();
        return new Result<Map<String, String>>().ok(stringStringMap);
    }

    @RequestMapping("oss/callback")
    public Result<Map<String, String>> callback() {
        return new Result<Map<String, String>>().ok(null);
    }

}
