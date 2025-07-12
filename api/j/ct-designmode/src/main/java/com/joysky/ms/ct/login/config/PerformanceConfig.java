package com.joysky.ms.ct.login.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.concurrent.Executor;

/**
 * 性能优化配置
 */
@Configuration
@EnableAsync
public class PerformanceConfig {
    
    /**
     * 异步任务线程池配置
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(4);
        // 最大线程数
        executor.setMaxPoolSize(8);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("async-user-");
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
    
    /**
     * Tomcat容器优化配置
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            // 最大连接数
            factory.addConnectorCustomizers(connector -> {
                connector.setProperty("maxConnections", "2000");
                // 最大线程数
                connector.setProperty("maxThreads", "200");
                // 最小空闲线程数
                connector.setProperty("minSpareThreads", "20");
                // 连接超时时间
                connector.setProperty("connectionTimeout", "20000");
                // 启用压缩
                connector.setProperty("compression", "on");
                // 压缩的MIME类型
                connector.setProperty("compressableMimeType", 
                    "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml");
                // 压缩的最小大小
                connector.setProperty("compressionMinSize", "1024");
            });
        };
    }
}