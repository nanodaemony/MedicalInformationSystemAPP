package com.nano.activity.devicedata.collection;

import lombok.Data;

/**
 * Description: Activity与Fragment之间传输数据的消息体
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 21:44
 */
@Data
public class MessageEntity {

    /**
     * 仪器号
     */
    private Integer deviceCode;

    /**
     * 需要传递的数据
     */
    private Object data;

    public MessageEntity(Integer deviceCode) {
        this.deviceCode = deviceCode;
    }

    public MessageEntity(Integer deviceCode, Object data) {
        this.deviceCode = deviceCode;
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "deviceCode=" + deviceCode +
                ", data=" + data +
                '}';
    }
}
