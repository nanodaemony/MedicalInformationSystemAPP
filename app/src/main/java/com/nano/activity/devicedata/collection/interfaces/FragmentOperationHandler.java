package com.nano.activity.devicedata.collection.interfaces;

import com.nano.activity.devicedata.collection.MessageEntity;

/**
 * Description: Fragment向Activity传递数据的接口
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 22:38
 */
public interface FragmentOperationHandler {

    /**
     * 仪器开始采集
     *
     * @param messageEntity 仪器实体
     */
    void handleDeviceStartCollection(MessageEntity messageEntity);

    /**
     * 仪器结束采集
     *
     * @param messageEntity 仪器实体
     */
    void handleDeviceStopCollection(MessageEntity messageEntity);

}
