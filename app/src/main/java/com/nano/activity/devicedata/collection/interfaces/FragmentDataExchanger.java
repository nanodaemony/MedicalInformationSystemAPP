package com.nano.activity.devicedata.collection.interfaces;

import com.nano.datacollection.CollectionStatusEnum;

/**
 * Description: 采集时数据更新的监听器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 14:00
 */
public interface FragmentDataExchanger {

    /**
     * 更新采集状态
     *
     * @param status 状态
     */
    void updateCollectionStatus(CollectionStatusEnum status);

    /**
     * 更新仪器数据
     *
     * @param successfulUploadCounter 已经成功上传的计数器
     */
    void updateSuccessfulUploadCounter(Integer successfulUploadCounter);

    /**
     * 更新仪器展示的数据
     * @param dataObject 数据实体
     */
    void updateReceiveCounterAndDeviceData(Integer receiveCounter, Object dataObject);

}
