package com.joysky.ms.ct.login.exception;

/**
 * 基础异常类
 * 所有自定义异常的父类
 */
public class BaseException extends RuntimeException {
    
    private Integer code;
    private String message;
    
    public BaseException() {
        super();
    }
    
    public BaseException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
    
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }
    
    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}