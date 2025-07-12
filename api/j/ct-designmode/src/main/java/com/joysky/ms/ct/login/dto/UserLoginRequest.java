package com.joysky.ms.ct.login.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username; // 可为用户名、手机号或邮箱
    private String password;
    private String imgCode;
    private String imgUuid;
}