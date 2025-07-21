package com.joysky.ice.easyhttp.app.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 统一API响应结果封装类
 * @param <T> 数据类型
 * @author EasyHttp
 */
public class ApiResult<T> implements Serializable {
    
    private int code;
    private String message;
    private T data;
    
    public ApiResult() {
    }
    
    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 创建成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResult
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(0, "success", data);
    }
    
    /**
     * 创建成功响应（无数据）
     * @return ApiResult
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(0, "success", null);
    }
    
    /**
     * 创建失败响应
     * @param message 错误信息
     * @param <T> 数据类型
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(1, message, null);
    }
    
    /**
     * 创建失败响应
     * @param code 错误码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
    
    // Getter and Setter methods
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    /**
     * 判断是否成功
     * @return true if success
     */
    public boolean isSuccess() {
        return code == 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResult<?> apiResult = (ApiResult<?>) o;
        return code == apiResult.code &&
                Objects.equals(message, apiResult.message) &&
                Objects.equals(data, apiResult.data);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }
    
    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}