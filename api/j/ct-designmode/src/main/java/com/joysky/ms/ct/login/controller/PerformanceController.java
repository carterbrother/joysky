package com.joysky.ms.ct.login.controller;

import com.joysky.ms.ct.login.service.PerformanceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 性能监控控制器
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;
    
    /**
     * 获取性能统计报告
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getPerformanceStats() {
        String report = performanceMonitorService.getPerformanceReport();
        return ResponseEntity.ok(report);
    }
    
    /**
     * 获取登录请求总数
     */
    @GetMapping("/login-count")
    public ResponseEntity<Long> getLoginCount() {
        long count = performanceMonitorService.getLoginRequestCount();
        return ResponseEntity.ok(count);
    }
    
    /**
     * 获取注册请求总数
     */
    @GetMapping("/register-count")
    public ResponseEntity<Long> getRegisterCount() {
        long count = performanceMonitorService.getRegisterRequestCount();
        return ResponseEntity.ok(count);
    }
    
    /**
     * 获取缓存命中率
     */
    @GetMapping("/cache-hit-rate")
    public ResponseEntity<Double> getCacheHitRate() {
        double rate = performanceMonitorService.getCacheHitRate();
        return ResponseEntity.ok(rate);
    }
    
    /**
     * 获取登录平均响应时间
     */
    @GetMapping("/login-response-time")
    public ResponseEntity<Long> getLoginResponseTime() {
        long time = performanceMonitorService.getAverageResponseTime("login");
        return ResponseEntity.ok(time);
    }
    
    /**
     * 获取注册平均响应时间
     */
    @GetMapping("/register-response-time")
    public ResponseEntity<Long> getRegisterResponseTime() {
        long time = performanceMonitorService.getAverageResponseTime("register");
        return ResponseEntity.ok(time);
    }
    
    /**
     * 重置性能统计数据
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetStats() {
        performanceMonitorService.resetStats();
        return ResponseEntity.ok("性能统计数据已重置");
    }
}