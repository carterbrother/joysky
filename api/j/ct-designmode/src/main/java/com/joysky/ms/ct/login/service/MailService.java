package com.joysky.ms.ct.login.service;

/**
 * 邮件服务接口
 */
public interface MailService {
    
    /**
     * 发送密码重置验证码邮件
     * 
     * @param toEmail 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendPasswordResetCode(String toEmail, String code);
    
    /**
     * 生成验证码
     * 
     * @return 验证码
     */
    String generateCode();
}