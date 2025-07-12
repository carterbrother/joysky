package com.joysky.ms.ct.login.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 只放行.html、静态资源目录，所有.html由static下查找，其余全部进后端API
        registry
            .addResourceHandler("/*.html")
            .addResourceLocations("classpath:/static/");
        registry
            .addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
    }
}