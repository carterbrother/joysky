package com.joysky.ms.ct.login.exception;

/**
 * 系统异常类
 * 用于处理系统级别的异常
 */
public class SystemException extends BaseException {
    
    public SystemException() {
        super();
    }
    
    public SystemException(String message) {
        super(500, message);
    }
    
    public SystemException(Integer code, String message) {
        super(code, message);
    }
    
    public SystemException(String message, Throwable cause) {
        super(500, message, cause);
    }
    
    public SystemException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}