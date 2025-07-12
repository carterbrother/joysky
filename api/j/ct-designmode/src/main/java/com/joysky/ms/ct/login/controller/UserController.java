package com.joysky.ms.ct.login.controller;

import com.joysky.ms.ct.login.common.R;
import com.joysky.ms.ct.login.dto.ImageCodeResponse;
import com.joysky.ms.ct.login.dto.UserRegisterRequest;
import com.joysky.ms.ct.login.dto.UserLoginRequest;
import com.joysky.ms.ct.login.dto.UserLogoutRequest;
import com.joysky.ms.ct.login.dto.ForgotPasswordRequest;
import com.joysky.ms.ct.login.dto.ResetPasswordRequest;
import com.joysky.ms.ct.login.entity.User;
import com.joysky.ms.ct.login.service.ImageCodeService;
import com.joysky.ms.ct.login.service.UserService;
import com.joysky.ms.ct.login.service.PerformanceMonitorService;
import com.joysky.ms.ct.login.exception.BusinessException;
import com.joysky.ms.ct.login.exception.SystemException;
import com.joysky.ms.ct.login.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageCodeService imageCodeService;
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;

    /**
     * 获取图形验证码
     * @return 图形验证码响应
     */
    @GetMapping("/api/users/img-code")
    public R<ImageCodeResponse> getImageCode() {
        ImageCodeResponse response = imageCodeService.generateImageCode();
        return R.success(response);
    }

    /**
     * 用户注册接口
     * @param request 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/api/users/register")
    public R<User> register(@RequestBody @Valid UserRegisterRequest request) {
        long startTime = System.currentTimeMillis();
        performanceMonitorService.recordRegisterRequest();
        
        try {
            // 验证图形验证码
            if (!imageCodeService.validateImageCode(request.getImgCode(), request.getImgUuid())) {
                throw new ValidationException("验证码错误或已过期");
            }

            User user = userService.register(request);
            return R.success(user);
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordResponseTime("register", responseTime);
        }
    }

    /**
     * 用户登录接口
     * @param request 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/api/users/login")
    public R<User> login(@RequestBody UserLoginRequest request) {
        long startTime = System.currentTimeMillis();
        performanceMonitorService.recordLoginRequest();
        
        try {
            // 验证图形验证码
            if (!imageCodeService.validateImageCode(request.getImgCode(), request.getImgUuid())) {
                throw new ValidationException("验证码错误或已过期");
            }
            // 账号密码校验
            User user = userService.login(request.getUsername(), request.getPassword());
            if (user == null) {
                throw new BusinessException("账号或密码错误");
            }
            return R.success(user);
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordResponseTime("login", responseTime);
        }
    }

    /**
     * 用户注销接口
     * @param request 注销请求参数
     * @return 注销结果
     */
    @PostMapping("/api/users/logout")
    public R<String> logout(@RequestBody UserLogoutRequest request) {
        long startTime = System.currentTimeMillis();
        performanceMonitorService.recordLogoutRequest();
        
        try {
            boolean success = userService.logout(request.getUsername());
            if (success) {
                return R.success("注销成功");
            } else {
                throw new BusinessException("注销失败，用户不存在");
            }
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordResponseTime("logout", responseTime);
        }
    }

    /**
     * 发送密码重置邮件
     * @param request 忘记密码请求参数
     * @return 发送结果
     */
    @PostMapping("/api/users/forgot-password")
    public R<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        // 验证图形验证码
        if (!imageCodeService.validateImageCode(request.getImgCode(), request.getImgUuid())) {
            throw new ValidationException("验证码错误或已过期");
        }
        
        boolean success = userService.sendPasswordResetEmail(request.getEmail());
        if (success) {
            return R.success("密码重置邮件已发送");
        } else {
            throw new BusinessException("邮箱未注册或发送失败");
        }
    }

    /**
     * 重置密码
     * @param request 重置密码请求参数
     * @return 重置结果
     */
    @PostMapping("/api/users/reset-password")
    public R<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        // 验证两次密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("两次输入的密码不一致");
        }
        
        boolean success = userService.resetPassword(request.getEmail(), request.getEmailCode(), request.getNewPassword());
        if (success) {
            return R.success("密码重置成功");
        } else {
            throw new ValidationException("验证码错误或已过期");
        }
    }
}