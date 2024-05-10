package com.august.gulimall.thirdparty.controller;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.thirdparty.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author : Crazy_August
 * @description : 邮件发送
 * @Time: 2023-05-11   22:21
 */
@Controller
public class EmailController {


    @Resource
    private EmailService emailService;

    @PostMapping("/sendEmail")
    @ResponseBody
    public Result sendEmail(@RequestParam String email, @RequestParam String code) {
        emailService.sendEmailWithText(email, code);
        return new Result().ok();
    }


}
