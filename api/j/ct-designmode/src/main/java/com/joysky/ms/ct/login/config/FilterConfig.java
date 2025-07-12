package com.joysky.ms.ct.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置类
 * 注册自定义过滤器
 */
@Configuration
public class FilterConfig {
    
    @Autowired
    private RequestBodyCacheConfig requestBodyCacheConfig;
    
    /**
     * 注册请求体缓存过滤器
     */
    @Bean
    public FilterRegistrationBean<RequestBodyCacheConfig> requestBodyCacheFilter() {
        FilterRegistrationBean<RequestBodyCacheConfig> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestBodyCacheConfig);
        registration.addUrlPatterns("/api/*"); // 只对API接口生效
        registration.setOrder(1); // 设置过滤器优先级
        registration.setName("requestBodyCacheFilter");
        return registration;
    }
}