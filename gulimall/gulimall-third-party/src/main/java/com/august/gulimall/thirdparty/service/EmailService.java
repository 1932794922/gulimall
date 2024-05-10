package com.august.gulimall.thirdparty.service;


/**
 * @author xzy
 */
public interface EmailService {
    /**
     * 发送邮件
     * @param email
     */
    void sendEmail(String email);

    /**
     * 发送带有文本的邮件
     * @param email
     * @param text
     */
    void sendEmailWithText(String email,String code);
}
