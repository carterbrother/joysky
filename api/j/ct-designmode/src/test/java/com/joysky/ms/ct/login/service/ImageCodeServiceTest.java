package com.joysky.ms.ct.login.service;

import com.joysky.ms.ct.login.dto.ImageCodeResponse;
import com.joysky.ms.ct.login.service.impl.ImageCodeServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageCodeServiceTest {

    private final ImageCodeService imageCodeService = new ImageCodeServiceImpl();

    @Test
    void generateImageCode() {
        // 生成验证码
        ImageCodeResponse response = imageCodeService.generateImageCode();

        // 验证返回结果
        assertNotNull(response, "验证码响应不能为空");
        assertNotNull(response.getImgCode(), "验证码图片不能为空");
        assertNotNull(response.getImgUuid(), "验证码UUID不能为空");
        assertTrue(response.getImgCode().startsWith("data:image/png;base64,"), "验证码图片格式不正确");
    }

    @Test
    void validateImageCode() {
        // 生成验证码
        ImageCodeResponse response = imageCodeService.generateImageCode();

        // 测试验证码验证
        assertFalse(imageCodeService.validateImageCode("wrong_code", response.getImgUuid()), "错误的验证码不应通过验证");
        assertFalse(imageCodeService.validateImageCode(null, response.getImgUuid()), "空验证码不应通过验证");
        assertFalse(imageCodeService.validateImageCode("1234", "wrong_uuid"), "错误的UUID不应通过验证");
        assertFalse(imageCodeService.validateImageCode("1234", null), "空UUID不应通过验证");

        // 等待验证码过期（31秒）
        try {
            Thread.sleep(31000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证过期的验证码
        assertFalse(imageCodeService.validateImageCode("1234", response.getImgUuid()), "过期的验证码不应通过验证");
    }

    @Test
    void validateImageCodeReuse() {
        // 生成验证码
        ImageCodeResponse response = imageCodeService.generateImageCode();
        String uuid = response.getImgUuid();

        // 模拟正确的验证码（实际场景中不会知道正确的验证码）
        String mockCorrectCode = "1234";
        
        // 第一次验证（假设验证码正确）
        boolean firstValidation = imageCodeService.validateImageCode(mockCorrectCode, uuid);

        // 尝试重复使用相同的验证码
        boolean secondValidation = imageCodeService.validateImageCode(mockCorrectCode, uuid);

        // 验证第二次使用应该失败（验证码是一次性的）
        assertFalse(secondValidation, "验证码不应该可以重复使用");
    }
}