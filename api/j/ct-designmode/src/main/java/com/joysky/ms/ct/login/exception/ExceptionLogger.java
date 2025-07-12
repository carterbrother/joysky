package com.joysky.ms.ct.login.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常日志记录组件
 * 负责记录异常信息、请求参数、响应参数等
 */
@Component
public class ExceptionLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 记录异常信息
     * @param exception 异常对象
     * @param requestBody 请求体参数
     * @param responseBody 响应体参数
     */
    public void logException(Throwable exception, Object requestBody, Object responseBody) {
        try {
            Map<String, Object> logInfo = new HashMap<>();
            
            // 基本信息
            logInfo.put("timestamp", LocalDateTime.now().format(formatter));
            logInfo.put("exceptionType", exception.getClass().getSimpleName());
            logInfo.put("exceptionMessage", exception.getMessage());
            
            // 获取根本原因
            Throwable rootCause = getRootCause(exception);
            logInfo.put("rootCause", rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage());
            
            // 堆栈信息
            logInfo.put("stackTrace", getStackTrace(exception));
            
            // 请求信息
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                logInfo.put("requestInfo", getRequestInfo(request));
            }
            
            // 请求参数
            String cachedRequestBody = com.joysky.ms.ct.login.config.RequestBodyCacheConfig.getCachedRequestBody();
            if (cachedRequestBody != null && !cachedRequestBody.isEmpty()) {
                logInfo.put("requestBody", cachedRequestBody);
            } else if (requestBody != null) {
                logInfo.put("requestBody", objectMapper.writeValueAsString(requestBody));
            }
            
            // 响应参数
            String cachedResponseBody = com.joysky.ms.ct.login.config.RequestBodyCacheConfig.getCachedResponseBody();
            if (cachedResponseBody != null && !cachedResponseBody.isEmpty()) {
                logInfo.put("responseBody", cachedResponseBody);
            } else if (responseBody != null) {
                logInfo.put("responseBody", objectMapper.writeValueAsString(responseBody));
            }
            
            // 输出日志
            String logMessage = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo);
            logger.error("[异常记录] \n{}", logMessage);
            
        } catch (Exception e) {
            logger.error("记录异常日志时发生错误", e);
        }
    }
    
    /**
     * 获取异常的根本原因
     * @param throwable 异常对象
     * @return 根本原因异常
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    /**
     * 获取异常堆栈信息
     * @param throwable 异常对象
     * @return 堆栈信息字符串
     */
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * 获取当前请求对象
     * @return HttpServletRequest对象
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取请求信息
     * @param request 请求对象
     * @return 请求信息Map
     */
    private Map<String, Object> getRequestInfo(HttpServletRequest request) {
        Map<String, Object> requestInfo = new HashMap<>();
        
        // 基本请求信息
        requestInfo.put("method", request.getMethod());
        requestInfo.put("url", request.getRequestURL().toString());
        requestInfo.put("uri", request.getRequestURI());
        requestInfo.put("queryString", request.getQueryString());
        requestInfo.put("remoteAddr", getClientIpAddress(request));
        requestInfo.put("userAgent", request.getHeader("User-Agent"));
        
        // 请求头信息
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 过滤敏感头信息
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        requestInfo.put("headers", headers);
        
        // 请求参数
        Map<String, String[]> parameters = request.getParameterMap();
        if (!parameters.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                // 过滤敏感参数
                if (!isSensitiveParameter(key)) {
                    params.put(key, values.length == 1 ? values[0] : values);
                }
            }
            requestInfo.put("parameters", params);
        }
        
        return requestInfo;
    }
    
    /**
     * 获取客户端IP地址
     * @param request 请求对象
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
    
    /**
     * 判断是否为敏感请求头
     * @param headerName 请求头名称
     * @return 是否敏感
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") || 
               lowerName.contains("token") || 
               lowerName.contains("cookie") ||
               lowerName.contains("session");
    }
    
    /**
     * 判断是否为敏感参数
     * @param paramName 参数名称
     * @return 是否敏感
     */
    private boolean isSensitiveParameter(String paramName) {
        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") || 
               lowerName.contains("pwd") || 
               lowerName.contains("token") ||
               lowerName.contains("secret") ||
               lowerName.contains("key");
    }
}