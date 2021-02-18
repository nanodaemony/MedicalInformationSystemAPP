package com.nano.common.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBus工具类
 * @author cz
 */
public class EventBusUtils {


    /**
     * 广播消息
     *
     * @param busMessage 消息实体
     */
    public static void sendMessage(BusMessage busMessage) {
        EventBus.getDefault().post(busMessage);
    }


}
