package com.joysky.ms.ct.login.exception;

/**
 * 验证异常类
 * 用于处理参数验证相关的异常
 */
public class ValidationException extends BaseException {
    
    public ValidationException() {
        super();
    }
    
    public ValidationException(String message) {
        super(400, message);
    }
    
    public ValidationException(Integer code, String message) {
        super(code, message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(400, message, cause);
    }
    
    public ValidationException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}