package com.nano.activity.devicedata.evaluation;

import com.nano.activity.devicedata.collection.MessageEntity;

/**
 * Description: 仪器评价时的处理器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/12 15:22
 */
public interface FragmentEvaluationHandler {

    /**
     * 处理仪器放弃采集的信息
     */
    void handleAbandonDeviceData(MessageEntity messageEntity);

    /**
     * 处理上传仪器评价信息
     */
    void handleUpdateEvaluationInfo(MessageEntity messageEntity);


}
