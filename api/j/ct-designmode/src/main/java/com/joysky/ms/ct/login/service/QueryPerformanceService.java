package com.joysky.ms.ct.login.service;

import com.joysky.ms.ct.login.entity.User;
import com.joysky.ms.ct.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * 查询性能对比测试服务
 * 用于验证UNION查询优化效果
 */
@Service
public class QueryPerformanceService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 邮箱格式正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // 手机号格式正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    /**
     * 性能对比测试
     * @param testIdentifiers 测试标识符列表
     * @return 性能对比结果
     */
    public PerformanceComparisonResult performanceComparison(List<String> testIdentifiers) {
        PerformanceComparisonResult result = new PerformanceComparisonResult();
        
        // 测试OR查询性能
        long orQueryTime = testOrQuery(testIdentifiers);
        result.setOrQueryTime(orQueryTime);
        
        // 测试UNION查询性能
        long unionQueryTime = testUnionQuery(testIdentifiers);
        result.setUnionQueryTime(unionQueryTime);
        
        // 测试智能路由查询性能
        long smartRouteTime = testSmartRouteQuery(testIdentifiers);
        result.setSmartRouteTime(smartRouteTime);
        
        // 计算性能提升比例
        result.calculateImprovements();
        
        return result;
    }
    
    /**
     * 测试OR查询性能
     */
    private long testOrQuery(List<String> identifiers) {
        long startTime = System.currentTimeMillis();
        
        for (String identifier : identifiers) {
            userRepository.findByUsernameOrPhoneOrEmail(identifier);
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 测试UNION查询性能
     */
    private long testUnionQuery(List<String> identifiers) {
        long startTime = System.currentTimeMillis();
        
        for (String identifier : identifiers) {
            userRepository.findByUsernameOrPhoneOrEmailUnion(identifier);
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 测试智能路由查询性能
     */
    private long testSmartRouteQuery(List<String> identifiers) {
        long startTime = System.currentTimeMillis();
        
        for (String identifier : identifiers) {
            findUserBySmartRoute(identifier);
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 智能路由查询实现
     */
    private User findUserBySmartRoute(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        
        identifier = identifier.trim();
        
        if (isEmail(identifier)) {
            return userRepository.findByEmailExact(identifier);
        } else if (isPhone(identifier)) {
            return userRepository.findByPhoneExact(identifier);
        } else {
            return userRepository.findByUsernameExact(identifier);
        }
    }
    
    /**
     * 并发性能测试
     * @param identifiers 测试标识符
     * @param concurrency 并发数
     * @return 并发测试结果
     */
    public ConcurrentPerformanceResult concurrentPerformanceTest(
            List<String> identifiers, int concurrency) {
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        ConcurrentPerformanceResult result = new ConcurrentPerformanceResult();
        
        // 测试OR查询并发性能
        long orConcurrentTime = testConcurrentQuery(executor, identifiers, "OR");
        result.setOrConcurrentTime(orConcurrentTime);
        
        // 测试智能路由并发性能
        long smartConcurrentTime = testConcurrentQuery(executor, identifiers, "SMART");
        result.setSmartConcurrentTime(smartConcurrentTime);
        
        executor.shutdown();
        result.calculateConcurrentImprovements();
        
        return result;
    }
    
    /**
     * 并发查询测试
     */
    private long testConcurrentQuery(ExecutorService executor, 
                                   List<String> identifiers, String queryType) {
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (String identifier : identifiers) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if ("OR".equals(queryType)) {
                    userRepository.findByUsernameOrPhoneOrEmail(identifier);
                } else {
                    findUserBySmartRoute(identifier);
                }
            }, executor);
            futures.add(future);
        }
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        return System.currentTimeMillis() - startTime;
    }
    
    private boolean isEmail(String identifier) {
        return EMAIL_PATTERN.matcher(identifier).matches();
    }
    
    private boolean isPhone(String identifier) {
        return PHONE_PATTERN.matcher(identifier).matches();
    }
    
    /**
     * 性能对比结果
     */
    public static class PerformanceComparisonResult {
        private long orQueryTime;
        private long unionQueryTime;
        private long smartRouteTime;
        private double unionImprovement;
        private double smartRouteImprovement;
        
        public void calculateImprovements() {
            if (orQueryTime > 0) {
                unionImprovement = ((double)(orQueryTime - unionQueryTime) / orQueryTime) * 100;
                smartRouteImprovement = ((double)(orQueryTime - smartRouteTime) / orQueryTime) * 100;
            }
        }
        
        // Getters and Setters
        public long getOrQueryTime() { return orQueryTime; }
        public void setOrQueryTime(long orQueryTime) { this.orQueryTime = orQueryTime; }
        
        public long getUnionQueryTime() { return unionQueryTime; }
        public void setUnionQueryTime(long unionQueryTime) { this.unionQueryTime = unionQueryTime; }
        
        public long getSmartRouteTime() { return smartRouteTime; }
        public void setSmartRouteTime(long smartRouteTime) { this.smartRouteTime = smartRouteTime; }
        
        public double getUnionImprovement() { return unionImprovement; }
        public double getSmartRouteImprovement() { return smartRouteImprovement; }
        
        @Override
        public String toString() {
            return String.format(
                "性能对比结果:\n" +
                "OR查询耗时: %dms\n" +
                "UNION查询耗时: %dms (提升%.1f%%)\n" +
                "智能路由耗时: %dms (提升%.1f%%)",
                orQueryTime, unionQueryTime, unionImprovement,
                smartRouteTime, smartRouteImprovement
            );
        }
    }
    
    /**
     * 并发性能测试结果
     */
    public static class ConcurrentPerformanceResult {
        private long orConcurrentTime;
        private long smartConcurrentTime;
        private double concurrentImprovement;
        
        public void calculateConcurrentImprovements() {
            if (orConcurrentTime > 0) {
                concurrentImprovement = ((double)(orConcurrentTime - smartConcurrentTime) / orConcurrentTime) * 100;
            }
        }
        
        // Getters and Setters
        public long getOrConcurrentTime() { return orConcurrentTime; }
        public void setOrConcurrentTime(long orConcurrentTime) { this.orConcurrentTime = orConcurrentTime; }
        
        public long getSmartConcurrentTime() { return smartConcurrentTime; }
        public void setSmartConcurrentTime(long smartConcurrentTime) { this.smartConcurrentTime = smartConcurrentTime; }
        
        public double getConcurrentImprovement() { return concurrentImprovement; }
        
        @Override
        public String toString() {
            return String.format(
                "并发性能对比结果:\n" +
                "OR查询并发耗时: %dms\n" +
                "智能路由并发耗时: %dms (提升%.1f%%)",
                orConcurrentTime, smartConcurrentTime, concurrentImprovement
            );
        }
    }
}