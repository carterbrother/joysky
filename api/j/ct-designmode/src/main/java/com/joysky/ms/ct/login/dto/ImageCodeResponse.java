package com.joysky.ms.ct.login.dto;

import lombok.Data;

@Data
public class ImageCodeResponse {
    private String imgCode;  // 图形验证码的base64编码
    private String imgUuid;  // 图形验证码的UUID
}