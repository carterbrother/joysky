package com.joysky.ms.ct.login.service;

import com.joysky.ms.ct.login.dto.UserRegisterRequest;
import com.joysky.ms.ct.login.entity.User;
import com.joysky.ms.ct.login.repository.UserRepository;
import com.joysky.ms.ct.login.service.impl.UserServiceImpl;
import com.joysky.ms.ct.login.exception.ValidationException;
import com.joysky.ms.ct.login.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCacheService userCacheService;

    @Mock
    private MailService mailService;

    @Mock
    private AsyncUserService asyncUserService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSuccess() {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setPhone("13800138000");
        request.setEmail("test@example.com");
        request.setSmsCode("123456");
        request.setEmailCode("654321");

        // Mock 依赖方法
        when(userCacheService.getCachedExists(any())).thenReturn(false);
        when(userRepository.existsByPhone(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(new User());

        // 执行测试
        User result = userService.register(request);

        // 验证结果
        assertNotNull(result);
    }

    @Test
    void registerWithExistingPhone() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setPhone("13800138000");
        request.setEmail("test@example.com");
        request.setSmsCode("123456");
        request.setEmailCode("654321");

        when(userCacheService.getCachedExists("exists:phone:13800138000")).thenReturn(true);

        // 验证异常
        assertThrows(BusinessException.class, () -> userService.register(request));
    }

    @Test
    void registerWithExistingEmail() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setPhone("13800138000");
        request.setEmail("test@example.com");
        request.setSmsCode("123456");
        request.setEmailCode("654321");

        when(userCacheService.getCachedExists("exists:phone:13800138000")).thenReturn(false);
        when(userCacheService.getCachedExists("exists:email:test@example.com")).thenReturn(true);
        when(userRepository.existsByPhone(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // 验证异常
        assertThrows(BusinessException.class, () -> userService.register(request));
    }
}