package com.joysky.ms.ct.login.controller;

import com.joysky.ms.ct.login.service.QueryPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询优化测试控制器
 * 提供UNION查询优化效果的测试接口
 */
@RestController
@RequestMapping("/api/query-optimization")
public class QueryOptimizationController {
    
    @Autowired
    private QueryPerformanceService queryPerformanceService;
    
    /**
     * 基础性能对比测试
     * @return 性能对比结果
     */
    @GetMapping("/performance-test")
    public ResponseEntity<Map<String, Object>> performanceTest() {
        // 模拟测试数据
        List<String> testIdentifiers = Arrays.asList(
            "user001",           // 用户名
            "13812345678",       // 手机号
            "test@example.com",  // 邮箱
            "admin",             // 用户名
            "13987654321",       // 手机号
            "admin@test.com"     // 邮箱
        );
        
        QueryPerformanceService.PerformanceComparisonResult result = 
            queryPerformanceService.performanceComparison(testIdentifiers);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "性能测试完成");
        response.put("data", result);
        response.put("analysis", generateAnalysis(result));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 并发性能测试
     * @param concurrency 并发数，默认10
     * @return 并发测试结果
     */
    @GetMapping("/concurrent-test")
    public ResponseEntity<Map<String, Object>> concurrentTest(
            @RequestParam(defaultValue = "10") int concurrency) {
        
        // 生成更多测试数据用于并发测试
        List<String> testIdentifiers = Arrays.asList(
            "user001", "user002", "user003", "user004", "user005",
            "13812345678", "13812345679", "13812345680", "13812345681",
            "test1@example.com", "test2@example.com", "test3@example.com"
        );
        
        QueryPerformanceService.ConcurrentPerformanceResult result = 
            queryPerformanceService.concurrentPerformanceTest(testIdentifiers, concurrency);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "并发性能测试完成");
        response.put("concurrency", concurrency);
        response.put("data", result);
        response.put("analysis", generateConcurrentAnalysis(result, concurrency));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询策略说明
     * @return 优化策略详细说明
     */
    @GetMapping("/optimization-info")
    public ResponseEntity<Map<String, Object>> optimizationInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // 优化策略说明
        Map<String, String> strategies = new HashMap<>();
        strategies.put("OR查询问题", "使用OR条件会导致MySQL无法有效使用索引，需要进行全表扫描");
        strategies.put("UNION查询优化", "将OR查询拆分为多个UNION查询，每个查询都能使用对应的索引");
        strategies.put("智能路由策略", "根据输入格式自动判断类型，直接使用最优的单一索引查询");
        
        // 性能提升预期
        Map<String, String> improvements = new HashMap<>();
        improvements.put("响应时间", "预期减少80-95%");
        improvements.put("QPS吞吐量", "预期提升300-600%");
        improvements.put("CPU使用率", "预期降低50-70%");
        improvements.put("数据库负载", "预期降低60-80%");
        
        // 实施建议
        Map<String, String> recommendations = new HashMap<>();
        recommendations.put("生产环境部署", "建议先在测试环境验证，然后灰度发布");
        recommendations.put("监控指标", "重点关注查询响应时间、QPS、错误率等指标");
        recommendations.put("回滚策略", "保留原有OR查询方法作为备用方案");
        
        info.put("strategies", strategies);
        info.put("improvements", improvements);
        info.put("recommendations", recommendations);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查询优化策略说明");
        response.put("data", info);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 自定义性能测试
     * @param testData 自定义测试数据
     * @return 测试结果
     */
    @PostMapping("/custom-test")
    public ResponseEntity<Map<String, Object>> customTest(
            @RequestBody CustomTestRequest testData) {
        
        QueryPerformanceService.PerformanceComparisonResult result = 
            queryPerformanceService.performanceComparison(testData.getIdentifiers());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "自定义性能测试完成");
        response.put("testCount", testData.getIdentifiers().size());
        response.put("data", result);
        response.put("analysis", generateAnalysis(result));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 生成性能分析报告
     */
    private Map<String, Object> generateAnalysis(QueryPerformanceService.PerformanceComparisonResult result) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 性能等级评估
        String performanceGrade;
        if (result.getSmartRouteImprovement() > 80) {
            performanceGrade = "优秀 (A+)";
        } else if (result.getSmartRouteImprovement() > 60) {
            performanceGrade = "良好 (A)";
        } else if (result.getSmartRouteImprovement() > 40) {
            performanceGrade = "一般 (B)";
        } else {
            performanceGrade = "需要优化 (C)";
        }
        
        analysis.put("performanceGrade", performanceGrade);
        analysis.put("bestStrategy", result.getSmartRouteTime() < result.getUnionQueryTime() ? "智能路由" : "UNION查询");
        analysis.put("maxImprovement", Math.max(result.getUnionImprovement(), result.getSmartRouteImprovement()));
        
        // 建议
        String recommendation;
        if (result.getSmartRouteImprovement() > 70) {
            recommendation = "优化效果显著，建议立即部署到生产环境";
        } else if (result.getSmartRouteImprovement() > 30) {
            recommendation = "优化效果良好，建议进一步测试后部署";
        } else {
            recommendation = "优化效果有限，建议检查数据库索引配置";
        }
        
        analysis.put("recommendation", recommendation);
        
        return analysis;
    }
    
    /**
     * 生成并发性能分析报告
     */
    private Map<String, Object> generateConcurrentAnalysis(
            QueryPerformanceService.ConcurrentPerformanceResult result, int concurrency) {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("concurrencyLevel", concurrency);
        analysis.put("improvementPercentage", result.getConcurrentImprovement());
        
        String concurrentGrade;
        if (result.getConcurrentImprovement() > 70) {
            concurrentGrade = "优秀 - 高并发性能显著提升";
        } else if (result.getConcurrentImprovement() > 50) {
            concurrentGrade = "良好 - 并发性能有明显改善";
        } else {
            concurrentGrade = "一般 - 并发优化效果有限";
        }
        
        analysis.put("concurrentGrade", concurrentGrade);
        
        // 计算理论QPS提升
        double qpsImprovement = (result.getConcurrentImprovement() / 100.0) * concurrency;
        analysis.put("estimatedQpsImprovement", String.format("预计QPS提升%.1f倍", qpsImprovement));
        
        return analysis;
    }
    
    /**
     * 自定义测试请求
     */
    public static class CustomTestRequest {
        private List<String> identifiers;
        
        public List<String> getIdentifiers() {
            return identifiers;
        }
        
        public void setIdentifiers(List<String> identifiers) {
            this.identifiers = identifiers;
        }
    }
}