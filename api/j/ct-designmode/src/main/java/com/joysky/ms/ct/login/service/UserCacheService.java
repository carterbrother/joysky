package com.joysky.ms.ct.login.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.joysky.ms.ct.login.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户缓存服务
 */
@Service
public class UserCacheService {
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;
    
    // 用户信息缓存，5分钟过期
    private final Cache<String, User> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();
    
    // 用户存在性缓存，1分钟过期
    private final Cache<String, Boolean> existsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(50000)
            .build();
    
    // 邮箱验证码缓存，15分钟过期
    private final Cache<String, String> emailCodeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();
    
    /**
     * 获取缓存的用户信息
     */
    public User getCachedUser(String key) {
        User user = userCache.getIfPresent(key);
        if (user != null) {
            performanceMonitorService.recordCacheHit();
        } else {
            performanceMonitorService.recordCacheMiss();
        }
        return user;
    }
    
    /**
     * 缓存用户信息
     */
    public void cacheUser(String key, User user) {
        if (user != null) {
            userCache.put(key, user);
        }
    }
    
    /**
     * 获取缓存的存在性检查结果
     */
    public Boolean getCachedExists(String key) {
        Boolean exists = existsCache.getIfPresent(key);
        if (exists != null) {
            performanceMonitorService.recordCacheHit();
        } else {
            performanceMonitorService.recordCacheMiss();
        }
        return exists;
    }
    
    /**
     * 缓存存在性检查结果
     */
    public void cacheExists(String key, boolean exists) {
        existsCache.put(key, exists);
    }
    
    /**
     * 清除用户相关缓存
     */
    public void evictUser(String key) {
        userCache.invalidate(key);
    }
    
    /**
     * 清除存在性缓存
     */
    public void evictExists(String key) {
        existsCache.invalidate(key);
    }
    
    /**
     * 缓存邮箱验证码
     */
    public void cacheEmailCode(String email, String code) {
        String key = "email_code:" + email;
        emailCodeCache.put(key, code);
    }
    
    /**
     * 获取缓存的邮箱验证码
     */
    public String getCachedEmailCode(String email) {
        String key = "email_code:" + email;
        String code = emailCodeCache.getIfPresent(key);
        if (code != null) {
            performanceMonitorService.recordCacheHit();
        } else {
            performanceMonitorService.recordCacheMiss();
        }
        return code;
    }
    
    /**
     * 清除邮箱验证码缓存
     */
    public void evictEmailCode(String email) {
        String key = "email_code:" + email;
        emailCodeCache.invalidate(key);
    }
    
    /**
     * 清除用户相关的所有缓存
     */
    public void clearUserCache(String email) {
        // 清除用户登录缓存
        String userCacheKey = "user:login:" + email;
        evictUser(userCacheKey);
        
        // 清除邮箱存在性缓存
        String emailExistsKey = "exists:email:" + email;
        evictExists(emailExistsKey);
        
        // 清除邮箱验证码缓存
        evictEmailCode(email);
    }
}