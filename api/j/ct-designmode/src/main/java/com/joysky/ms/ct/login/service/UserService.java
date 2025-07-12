package com.joysky.ms.ct.login.service;

import com.joysky.ms.ct.login.dto.UserRegisterRequest;
import com.joysky.ms.ct.login.entity.User;

public interface UserService {
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册成功的用户信息
     */
    User register(UserRegisterRequest request);

    /**
     * 校验用户名是否包含敏感词
     * @param username 用户名
     * @return true-包含敏感词，false-不包含敏感词
     */
    boolean containsSensitiveWords(String username);

    /**
     * 校验手机号是否已注册
     * @param phone 手机号
     * @return true-已注册，false-未注册
     */
    boolean isPhoneRegistered(String phone);

    /**
     * 校验邮箱是否已注册
     * @param email 邮箱
     * @return true-已注册，false-未注册
     */
    boolean isEmailRegistered(String email);

    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return true-验证通过，false-验证失败
     */
    boolean verifySmsCode(String phone, String code);

    /**
     * 验证邮箱验证码
     * @param email 邮箱
     * @param code 验证码
     * @return true-验证通过，false-验证失败
     */
    boolean verifyEmailCode(String email, String code);
/**
     * 登录操作，支持用户名/手机号/邮箱+密码
     * @param username 用户名/手机号/邮箱
     * @param password 密码（明文）
     * @return 匹配到的用户实体，失败返回null
     */
    User login(String username, String password);

    /**
     * 用户注销
     * @param username 用户名/手机号/邮箱
     * @return true-注销成功，false-注销失败
     */
    boolean logout(String username);
    
    /**
     * 发送密码重置邮件
     * @param email 邮箱地址
     * @return true-发送成功，false-发送失败
     */
    boolean sendPasswordResetEmail(String email);
    
    /**
     * 重置密码
     * @param email 邮箱地址
     * @param emailCode 邮箱验证码
     * @param newPassword 新密码
     * @return true-重置成功，false-重置失败
     */
    boolean resetPassword(String email, String emailCode, String newPassword);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱地址
     * @return 用户信息，未找到返回null
     */
    User findByEmail(String email);
}