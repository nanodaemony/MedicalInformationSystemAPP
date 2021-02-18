package com.nano.http.entity;


import java.io.Serializable;

/**
 * 通用返回对象
 * @author nano
 */
public class CommonResult implements Serializable {

    private static final long serialVersionUID = -2927490351829187712L;

    /**
     * 状态码 用于返回各种状态信息 如200 401 403 500
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private String data;

    protected CommonResult() {
    }

    private CommonResult(Integer code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
