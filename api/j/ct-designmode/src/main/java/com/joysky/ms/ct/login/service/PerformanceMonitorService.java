package com.joysky.ms.ct.login.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控服务
 */
@Service
public class PerformanceMonitorService {
    
    // 登录请求计数器
    private final AtomicLong loginRequestCount = new AtomicLong(0);
    // 注册请求计数器
    private final AtomicLong registerRequestCount = new AtomicLong(0);
    // 注销请求计数器
    private final AtomicLong logoutRequestCount = new AtomicLong(0);
    // 缓存命中计数器
    private final AtomicLong cacheHitCount = new AtomicLong(0);
    // 缓存未命中计数器
    private final AtomicLong cacheMissCount = new AtomicLong(0);
    
    // 响应时间统计
    private final ConcurrentHashMap<String, AtomicLong> responseTimeStats = new ConcurrentHashMap<>();
    
    /**
     * 记录登录请求
     */
    public void recordLoginRequest() {
        loginRequestCount.incrementAndGet();
    }
    
    /**
     * 记录注册请求
     */
    public void recordRegisterRequest() {
        registerRequestCount.incrementAndGet();
    }
    
    /**
     * 记录注销请求
     */
    public void recordLogoutRequest() {
        logoutRequestCount.incrementAndGet();
    }
    
    /**
     * 记录缓存命中
     */
    public void recordCacheHit() {
        cacheHitCount.incrementAndGet();
    }
    
    /**
     * 记录缓存未命中
     */
    public void recordCacheMiss() {
        cacheMissCount.incrementAndGet();
    }
    
    /**
     * 记录响应时间
     */
    public void recordResponseTime(String operation, long timeMs) {
        responseTimeStats.computeIfAbsent(operation, k -> new AtomicLong(0))
                        .addAndGet(timeMs);
    }
    
    /**
     * 获取登录请求总数
     */
    public long getLoginRequestCount() {
        return loginRequestCount.get();
    }
    
    /**
     * 获取注册请求总数
     */
    public long getRegisterRequestCount() {
        return registerRequestCount.get();
    }
    
    /**
     * 获取注销请求总数
     */
    public long getLogoutRequestCount() {
        return logoutRequestCount.get();
    }
    
    /**
     * 获取缓存命中率
     */
    public double getCacheHitRate() {
        long hits = cacheHitCount.get();
        long misses = cacheMissCount.get();
        long total = hits + misses;
        return total > 0 ? (double) hits / total : 0.0;
    }
    
    /**
     * 获取平均响应时间
     */
    public long getAverageResponseTime(String operation) {
        AtomicLong totalTime = responseTimeStats.get(operation);
        if (totalTime == null) {
            return 0;
        }
        
        long requestCount;
        switch (operation) {
            case "login":
                requestCount = loginRequestCount.get();
                break;
            case "register":
                requestCount = registerRequestCount.get();
                break;
            case "logout":
                requestCount = logoutRequestCount.get();
                break;
            default:
                requestCount = 1;
        }
        return requestCount > 0 ? totalTime.get() / requestCount : 0;
    }
    
    /**
     * 重置统计数据
     */
    public void resetStats() {
        loginRequestCount.set(0);
        registerRequestCount.set(0);
        logoutRequestCount.set(0);
        cacheHitCount.set(0);
        cacheMissCount.set(0);
        responseTimeStats.clear();
    }
    
    /**
     * 获取性能统计报告
     */
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 性能统计报告 ===\n");
        report.append("登录请求总数: ").append(getLoginRequestCount()).append("\n");
        report.append("注册请求总数: ").append(getRegisterRequestCount()).append("\n");
        report.append("注销请求总数: ").append(getLogoutRequestCount()).append("\n");
        report.append("缓存命中率: ").append(String.format("%.2f%%", getCacheHitRate() * 100)).append("\n");
        report.append("登录平均响应时间: ").append(getAverageResponseTime("login")).append("ms\n");
        report.append("注册平均响应时间: ").append(getAverageResponseTime("register")).append("ms\n");
        report.append("注销平均响应时间: ").append(getAverageResponseTime("logout")).append("ms\n");
        return report.toString();
    }
}