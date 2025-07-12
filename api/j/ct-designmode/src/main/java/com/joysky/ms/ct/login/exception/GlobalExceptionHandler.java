package com.joysky.ms.ct.login.exception;

import com.joysky.ms.ct.login.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有异常，记录日志，返回统一格式的响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Autowired
    private ExceptionLogger exceptionLogger;
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        R<Void> response = R.error(e.getCode(), e.getMessage());
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleSystemException(SystemException e, HttpServletRequest request) {
        R<Void> response = R.error(e.getCode(), e.getMessage());
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理验证异常
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleValidationException(ValidationException e, HttpServletRequest request) {
        R<Void> response = R.error(e.getCode(), e.getMessage());
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理参数校验异常（@Valid注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        R<Void> response = R.error(400, "参数校验失败: " + message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        R<Void> response = R.error(400, "参数绑定失败: " + message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        
        R<Void> response = R.error(400, "约束违反: " + message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = String.format("参数 '%s' 类型不匹配，期望类型: %s", 
                e.getName(), e.getRequiredType().getSimpleName());
        
        R<Void> response = R.error(400, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String message = String.format("请求路径 '%s' 不存在", e.getRequestURL());
        
        R<Void> response = R.error(404, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleSQLException(SQLException e, HttpServletRequest request) {
        String message = "数据库操作异常";
        
        R<Void> response = R.error(500, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        R<Void> response = R.error(400, e.getMessage());
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        String message = "系统内部错误，请联系管理员";
        
        R<Void> response = R.error(500, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String message = "系统运行时异常";
        
        R<Void> response = R.error(500, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String message = "系统异常，请稍后重试";
        
        R<Void> response = R.error(500, message);
        exceptionLogger.logException(e, getRequestBody(request), response);
        return response;
    }
    
    /**
     * 获取请求体参数
     * 优先从缓存中获取，如果没有则获取请求参数
     */
    private Object getRequestBody(HttpServletRequest request) {
        String cachedRequestBody = com.joysky.ms.ct.login.config.RequestBodyCacheConfig.getCachedRequestBody();
        if (cachedRequestBody != null && !cachedRequestBody.isEmpty()) {
            return cachedRequestBody;
        }
        return request.getParameterMap();
    }
}