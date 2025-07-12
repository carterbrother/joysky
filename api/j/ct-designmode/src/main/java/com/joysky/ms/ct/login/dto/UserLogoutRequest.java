package com.joysky.ms.ct.login.dto;

import lombok.Data;

@Data
public class UserLogoutRequest {
    private String username; // 可为用户名、手机号或邮箱
}