package com.nano.common.eventbus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventBusMessage消息实体类
 * @author cz
 */
@Data
@NoArgsConstructor
public class BusMessage {

    /**
     * 消息码
     */
    private MessageCodeEnum code;

    /**
     * 仪器代号
     */
    private int deviceCode;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 消息数据
     */
    private Object data;


    public BusMessage(MessageCodeEnum code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BusMessage(MessageCodeEnum code, int deviceCode, String msg) {
        this.code = code;
        this.deviceCode = deviceCode;
        this.msg = msg;
    }
}
