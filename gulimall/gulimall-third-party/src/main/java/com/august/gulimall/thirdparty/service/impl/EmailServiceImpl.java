package com.august.gulimall.thirdparty.service.impl;

import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.thirdparty.config.MyEmailConfig;
import com.august.gulimall.thirdparty.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-05-11   22:27
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    MyEmailConfig myEmailConfig;

    @Override
    public void sendEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new RenException(400, "邮箱不能为空");
        }
        // 校验邮箱格式
        if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new RenException(400, "邮箱格式不正确");
        }
        // 发送邮件
        // 随机生成验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 发送邮件
        // 引入Template的Context
        Context context = new Context();
        // 设置变量
        context.setVariable("code", Arrays.asList(code.split("")));
        // 模板引擎处理模板
        String emailContent = templateEngine.process("emailTemplate.html", context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 邮件的标题
            helper.setSubject("【谷粒商城】验证码");
            // 发送者
            helper.setFrom(myEmailConfig.getFrom());
            // 接收者
            helper.setTo(email);
            // 时间
            helper.setSentDate(new Date());
            // 第二个参数true表示这是一个html文本
            helper.setText(emailContent, true);
        } catch (Exception e) {
            throw new RenException(500, "邮件发送异常");
        }
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RenException(500, "邮件发送异常");
        }

    }

    @Override
    public void sendEmailWithText(String email, String code) {
        if (StringUtils.isBlank(email)) {
            throw new RenException(400, "邮箱不能为空");
        }
        // 校验邮箱格式
        if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new RenException(400, "邮箱格式不正确");
        }
        // 发送邮件
        if (StringUtils.isBlank(code)) {
            throw new RenException(400, "验证码不能为空");
        }
        // 引入Template的Context
        Context context = new Context();
        // 设置变量
        context.setVariable("code", Arrays.asList(code.split("")));
        // 模板引擎处理模板
        String emailContent = templateEngine.process("emailTemplate.html", context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 邮件的标题
            helper.setSubject("【谷粒商城】验证码");
            // 发送者
            helper.setFrom(myEmailConfig.getFrom());
            // 接收者
            helper.setTo(email);
            // 时间
            helper.setSentDate(new Date());
            // 第二个参数true表示这是一个html文本
            helper.setText(emailContent, true);
        } catch (Exception e) {
            throw new RenException(500, "邮件发送异常");
        }
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RenException(500, "邮件发送异常");
        }
    }
}
