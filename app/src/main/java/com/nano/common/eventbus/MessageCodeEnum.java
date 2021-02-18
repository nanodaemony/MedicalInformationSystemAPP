package com.nano.common.eventbus;

import lombok.Getter;

/**
 * 消息类型的枚举
 * @author cz
 */
public enum MessageCodeEnum {

    /**
     * 建立与仪器的连接
     */
    BUILD_CONNECTION(100, "建立连接通道开启"),

    /**
     * 接收仪器数据
     */
    RECEIVE_DEVICE_DATA(200, "接收数据"),

    /**
     * 接收数据发生异常
     */
    RECEIVE_DATA_CATCH_EXCEPTION(300, "接收数据时发生异常"),

    /**
     * 无法连接到仪器
     */
    UNABLE_TO_CONNECT_DEVICE(400, "无法连接到仪器"),

    /**
     * 仪器连接异常
     */
    CONNECTION_EXCEPTION(500, "仪器连接异常"),

    /**
     * 无法打开端口
     */
    UNABLE_TO_OPEN_SOCKET(600, "无法打开端口");


    @Getter
    private int code;

    @Getter
    private String message;

    MessageCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
