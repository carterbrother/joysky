package com.joysky.ms.ct.login.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑相关的异常
 */
public class BusinessException extends BaseException {
    
    public BusinessException() {
        super();
    }
    
    public BusinessException(String message) {
        super(400, message);
    }
    
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(400, message, cause);
    }
    
    public BusinessException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}