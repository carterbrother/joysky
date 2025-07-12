package com.joysky.ms.ct.login.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求体缓存配置
 * 用于缓存请求体和响应体，以便在异常处理时记录
 */
@Component
public class RequestBodyCacheConfig implements Filter {
    
    private static final ThreadLocal<String> REQUEST_BODY_CACHE = new ThreadLocal<>();
    private static final ThreadLocal<String> RESPONSE_BODY_CACHE = new ThreadLocal<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 包装请求和响应以支持多次读取
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);
        
        try {
            // 继续执行过滤器链
            chain.doFilter(requestWrapper, responseWrapper);
            
            // 缓存请求体
            cacheRequestBody(requestWrapper);
            
            // 缓存响应体
            cacheResponseBody(responseWrapper);
            
        } finally {
            // 将响应内容写回客户端
            responseWrapper.copyBodyToResponse();
        }
    }
    
    /**
     * 缓存请求体
     */
    private void cacheRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String requestBody = new String(content, StandardCharsets.UTF_8);
                REQUEST_BODY_CACHE.set(requestBody);
            }
        } catch (Exception e) {
            // 忽略缓存失败
        }
    }
    
    /**
     * 缓存响应体
     */
    private void cacheResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String responseBody = new String(content, StandardCharsets.UTF_8);
                RESPONSE_BODY_CACHE.set(responseBody);
            }
        } catch (Exception e) {
            // 忽略缓存失败
        }
    }
    
    /**
     * 获取缓存的请求体
     */
    public static String getCachedRequestBody() {
        return REQUEST_BODY_CACHE.get();
    }
    
    /**
     * 获取缓存的响应体
     */
    public static String getCachedResponseBody() {
        return RESPONSE_BODY_CACHE.get();
    }
    
    /**
     * 清理缓存
     */
    public static void clearCache() {
        REQUEST_BODY_CACHE.remove();
        RESPONSE_BODY_CACHE.remove();
    }
}