package com.nano.http.entity;

import lombok.Data;

/**
 * 装在CommonResult中的数据对象
 * @author nano
 * 主要用于数据采集时与平板采集端交互使用
 */
@Data
public class ResultVo {

    /**
     * 请求代号 根据项目情况自己定义
     */
    private Integer code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private String data;

    public ResultVo(Integer code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVo() {
    }
}
