package com.nano.datacollection;

import lombok.Getter;

/**
 * 采集器状态
 * @author cz
 */
@Getter
public enum CollectionStatusEnum {

    /**
     * 等待开始
     */
    WAITING_START(0, "等待开始采集"),


    CONNECTING(1, "仪器连接中"),


    /**
     * 按照正常的频率接收数据的状态
     */
    COLLECTING(2, "正在采集中"),


    /**
     * 采集完成
     */
    FINISHED(3, "采集完成"),

    /**
     * 已丢弃
     */
    ABANDON(-1, "已丢弃")

    ;

    Integer code;

    String message;

    CollectionStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
