package com.joysky.ms.ct.login.controller;

import com.joysky.ms.ct.login.common.R;
import com.joysky.ms.ct.login.service.MailService;
import com.joysky.ms.ct.login.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件测试控制器
 * 用于测试邮件发送功能
 */
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailTestController {
    
    private final MailService mailService;
    
    /**
     * 测试发送密码重置邮件
     * 
     * @param email 收件人邮箱
     * @return 发送结果
     */
    @PostMapping("/test-send")
    public R<String> testSendEmail(@RequestParam String email) {
        // 生成测试验证码
        String code = mailService.generateCode();
        
        // 发送邮件
        boolean success = mailService.sendPasswordResetCode(email, code);
        
        if (success) {
            return R.success("邮件发送成功，验证码: " + code);
        } else {
            throw new BusinessException("邮件发送失败，可能原因：1. 发送频率受限(5秒内只能发送一次) 2. 邮件服务器配置错误 3. 网络异常");
        }
    }
    
    /**
     * 测试邮件发送限速功能
     * 连续发送多封邮件测试限速
     * 
     * @param email 收件人邮箱
     * @param count 发送次数
     * @return 测试结果
     */
    @PostMapping("/test-rate-limit")
    public R<String> testRateLimit(@RequestParam String email, @RequestParam(defaultValue = "3") int count) {
        StringBuilder result = new StringBuilder();
        int successCount = 0;
        int failedCount = 0;
        
        for (int i = 1; i <= count; i++) {
            String code = mailService.generateCode();
            boolean success = mailService.sendPasswordResetCode(email, code);
            
            if (success) {
                successCount++;
                result.append(String.format("第%d次发送: 成功 (验证码: %s)\n", i, code));
            } else {
                failedCount++;
                result.append(String.format("第%d次发送: 失败 (限速保护)\n", i));
            }
            
            // 如果不是最后一次，等待1秒
            if (i < count) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        result.append(String.format("\n测试完成: 成功 %d 次, 失败 %d 次", successCount, failedCount));
        return R.success(result.toString());
    }
    
    /**
     * 生成验证码
     * 
     * @return 验证码
     */
    @GetMapping("/generate-code")
    public R<String> generateCode() {
        String code = mailService.generateCode();
        return R.success(code);
    }
}