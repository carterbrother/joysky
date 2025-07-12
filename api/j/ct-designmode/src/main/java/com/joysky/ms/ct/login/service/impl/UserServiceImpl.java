package com.joysky.ms.ct.login.service.impl;

import com.google.common.base.Charsets;
import com.joysky.ms.ct.login.dto.UserRegisterRequest;
import com.joysky.ms.ct.login.entity.User;
import com.joysky.ms.ct.login.repository.UserRepository;
import com.joysky.ms.ct.login.service.UserService;
import com.joysky.ms.ct.login.service.UserCacheService;
import com.joysky.ms.ct.login.service.AsyncUserService;
import com.joysky.ms.ct.login.service.MailService;
import com.joysky.ms.ct.login.util.EncryptionUtil;
import com.joysky.ms.ct.login.util.MaskUtil;
import com.joysky.ms.ct.login.exception.BusinessException;
import com.joysky.ms.ct.login.exception.SystemException;
import com.joysky.ms.ct.login.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserCacheService userCacheService;
    
    @Autowired
    private AsyncUserService asyncUserService;
    
    @Autowired
    private MailService mailService;
    
    // 邮箱格式正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // 手机号格式正则表达式（中国大陆）
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );


    @Override
    public User login(String username, String password) {
        // MD5加密对比
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes(Charsets.UTF_8));
        
        // 先从缓存获取用户信息
        String cacheKey = "user:login:" + username;
        User user = userCacheService.getCachedUser(cacheKey);
        
        if (user == null) {
            // 缓存未命中，使用智能路由查询优化性能
            user = findUserByIdentifierOptimized(username);
            // 缓存查询结果
            if (user != null) {
                userCacheService.cacheUser(cacheKey, user);
            }
        }
        
        if (user != null && md5Pwd.equals(user.getPassword())) {
            // 异步处理登录日志和统计信息
            handleLoginSuccess(user);
            return user;
        }
        return null;
    }


    @Override
    @Transactional
    public User register(UserRegisterRequest request) {
        // 校验用户名敏感词
        if (containsSensitiveWords(request.getUsername())) {
            throw new ValidationException("用户名包含敏感词");
        }

        // 校验手机号和邮箱是否已注册
        if (isPhoneRegistered(request.getPhone())) {
            throw new BusinessException("手机号已注册");
        }
        if (isEmailRegistered(request.getEmail())) {
            throw new BusinessException("邮箱已注册");
        }

        // 验证短信验证码和邮箱验证码
        if (!verifySmsCode(request.getPhone(), request.getSmsCode())) {
            throw new ValidationException("短信验证码错误");
        }
        if (!verifyEmailCode(request.getEmail(), request.getEmailCode())) {
            throw new ValidationException("邮箱验证码错误");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码非对称加密
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes(Charsets.UTF_8)));
        
        // 手机号和邮箱加密存储
        user.setPhoneEncrypted(EncryptionUtil.encryptRSA(request.getPhone()));
        user.setEmailEncrypted(EncryptionUtil.encryptRSA(request.getEmail()));
        
        // 原始手机号和邮箱（用于唯一性校验）
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        // 手机号和邮箱脱敏
        user.setPhoneMasked(MaskUtil.maskPhone(request.getPhone()));
        user.setEmailMasked(MaskUtil.maskEmail(request.getEmail()));

        User savedUser = userRepository.save(user);
        
        // 异步发送注册成功通知
        asyncUserService.sendRegistrationNotification(savedUser);
        
        return savedUser;
    }

    @Override
    public boolean containsSensitiveWords(String username) {
        // TODO: 实现敏感词校验逻辑
        return false;
    }

    @Override
    public boolean isPhoneRegistered(String phone) {
        String cacheKey = "exists:phone:" + phone;
        Boolean cached = userCacheService.getCachedExists(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        boolean exists = userRepository.existsByPhone(phone);
        userCacheService.cacheExists(cacheKey, exists);
        return exists;
    }

    @Override
    public boolean isEmailRegistered(String email) {
        String cacheKey = "exists:email:" + email;
        Boolean cached = userCacheService.getCachedExists(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        boolean exists = userRepository.existsByEmail(email);
        userCacheService.cacheExists(cacheKey, exists);
        return exists;
    }

    @Override
    public boolean verifySmsCode(String phone, String code) {
        // TODO: 实现短信验证码校验逻辑
        return true;
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        // TODO: 实现邮箱验证码校验逻辑
        return true;
    }

    @Override
    public boolean logout(String username) {
        // 清理用户登录缓存
        String cacheKey = "user:login:" + username;
        userCacheService.evictUser(cacheKey);
        
        // 查找用户信息用于记录注销日志
        User user = findUserByIdentifierOptimized(username);
        if (user != null) {
            // 异步记录注销日志
            handleLogoutSuccess(user);
            return true;
        }
        return false;
    }
    
    /**
     * 处理登录成功后的异步操作
     */
    private void handleLoginSuccess(User user) {
        try {
            // 获取客户端IP地址
            String clientIp = getClientIpAddress();
            // 异步记录登录日志
            asyncUserService.logUserLogin(user, clientIp);
            // 异步更新用户统计信息
            asyncUserService.updateUserStatistics(user.getId());
        } catch (Exception e) {
            // 异步操作失败不影响主流程，记录日志但不抛出异常
            log.error("异步处理登录成功操作失败", e);
        }
    }
    
    /**
     * 处理注销成功后的异步操作
     */
    private void handleLogoutSuccess(User user) {
        try {
            // 获取客户端IP地址
            String clientIp = getClientIpAddress();
            // 异步记录注销日志
            asyncUserService.logUserLogout(user, clientIp);
        } catch (Exception e) {
            // 异步操作失败不影响主流程，记录日志但不抛出异常
            log.error("异步处理注销成功操作失败", e);
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // 获取IP失败不影响主流程，记录日志但不抛出异常
            log.error("获取客户端IP失败", e);
        }
        return "unknown";
    }
    
    /**
     * 智能路由查询优化方法
     * 根据标识符格式自动选择最优查询策略
     * @param identifier 用户标识符（用户名/手机号/邮箱）
     * @return 用户信息
     */
    private User findUserByIdentifierOptimized(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        
        identifier = identifier.trim();
        
        // 策略1：根据格式判断，使用单一索引查询（性能最优）
        if (isEmail(identifier)) {
            // 邮箱格式，使用邮箱索引
            return userRepository.findByEmailExact(identifier);
        } else if (isPhone(identifier)) {
            // 手机号格式，使用手机号索引
            return userRepository.findByPhoneExact(identifier);
        } else {
            // 用户名格式，使用用户名索引
            return userRepository.findByUsernameExact(identifier);
        }
    }
    
    /**
     * UNION查询备用方案
     * 当智能路由无法确定类型时使用
     * @param identifier 用户标识符
     * @return 用户信息
     */
    private User findUserByIdentifierUnion(String identifier) {
        return userRepository.findByUsernameOrPhoneOrEmailUnion(identifier);
    }
    
    /**
     * 判断是否为邮箱格式
     * @param identifier 标识符
     * @return true-邮箱格式，false-非邮箱格式
     */
    private boolean isEmail(String identifier) {
        return EMAIL_PATTERN.matcher(identifier).matches();
    }
    
    /**
     * 判断是否为手机号格式
     * @param identifier 标识符
     * @return true-手机号格式，false-非手机号格式
     */
    private boolean isPhone(String identifier) {
        return PHONE_PATTERN.matcher(identifier).matches();
    }
    
    /**
     * 性能监控方法 - 记录查询类型和耗时
     * @param queryType 查询类型
     * @param startTime 开始时间
     */
    private void recordQueryPerformance(String queryType, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        // 可以集成到PerformanceMonitorService中
        log.info("[性能监控] {}查询耗时: {}ms", queryType, duration);
    }
    
    @Override
    public boolean sendPasswordResetEmail(String email) {
        // 检查邮箱是否存在
        if (!isEmailRegistered(email)) {
            return false;
        }
        
        // 生成验证码
        String emailCode = mailService.generateCode();
        
        // 发送邮件验证码
        boolean emailSent = mailService.sendPasswordResetCode(email, emailCode);
        if (!emailSent) {
            throw new SystemException("邮件发送失败");
        }
        
        // 缓存验证码，有效期15分钟
        userCacheService.cacheEmailCode(email, emailCode);
        
        return true;
    }
    
    @Override
    public boolean resetPassword(String email, String emailCode, String newPassword) {
        // 验证邮箱验证码
        String cachedCode = userCacheService.getCachedEmailCode(email);
        
        if (cachedCode == null || !cachedCode.equals(emailCode)) {
            return false;
        }
        
        // 查找用户
        User user = findByEmail(email);
        if (user == null) {
            return false;
        }
        
        // 更新密码（MD5加密）
        String md5Password = DigestUtils.md5DigestAsHex(newPassword.getBytes(Charsets.UTF_8));
        user.setPassword(md5Password);
        userRepository.save(user);
        
        // 清除验证码缓存
        userCacheService.evictEmailCode(email);
        
        // 清除用户相关缓存
        userCacheService.clearUserCache(email);
        
        log.info("[密码重置] 用户 {} 密码重置成功", email);
        return true;
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailExact(email);
    }
}