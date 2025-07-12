package com.joysky.ms.ct.login.util;

import org.springframework.stereotype.Component;

@Component
public class MaskUtil {

    /**
     * 手机号脱敏
     * 规则：保留前三位和后四位，中间用4个星号代替
     * 示例：18812345678 -> 188****5678
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * 规则：用户名保留前两位，后面用3个星号代替，@及后面的域名保持不变
     * 示例：example@domain.com -> ex***@domain.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            throw new IllegalArgumentException("邮箱用户名长度不足");
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        return username.substring(0, 2) + "***" + domain;
    }
}