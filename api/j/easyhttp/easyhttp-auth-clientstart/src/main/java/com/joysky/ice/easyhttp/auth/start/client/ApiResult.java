package com.joysky.ice.easyhttp.auth.start.client;


import java.util.Objects;


public class ApiResult<T> {

    private int code;
    private String msg;
    private T data;


    public ApiResult() {
    }

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public  static ApiResult successful(Object data) {
        return  new ApiResult(0,"success",data);
    }

    public  static ApiResult failed(Object data) {
        return  new ApiResult(1,"fail",data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApiResult apiResult = (ApiResult) o;
        return getCode() == apiResult.getCode() && Objects.equals(getMsg(), apiResult.getMsg()) && Objects.equals(getData(), apiResult.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getMsg(), getData());
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
