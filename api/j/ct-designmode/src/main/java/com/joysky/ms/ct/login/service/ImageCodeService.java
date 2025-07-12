package com.joysky.ms.ct.login.service;

import com.joysky.ms.ct.login.dto.ImageCodeResponse;

public interface ImageCodeService {
    /**
     * 生成图形验证码
     * @return 图形验证码响应对象
     */
    ImageCodeResponse generateImageCode();

    /**
     * 验证图形验证码
     * @param code 验证码
     * @param uuid 验证码UUID
     * @return 是否验证通过
     */
    boolean validateImageCode(String code, String uuid);
}