package com.nano.datacollection.parsedata;

import com.nano.datacollection.DeviceData;

/**
 * Description: 仪器数据解析接口
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 23:50
 */
public interface DeviceDataParser {

    /**
     * 解析数据
     *
     * @param deviceCode 仪器号
     * @param serialNumber 序列号
     * @param deviceOriginData 原始数据
     * @return 构造的数据实体
     */
    DeviceData parseData(int deviceCode, Integer collectionNumber, String serialNumber, String deviceOriginData);

    /**
     * 验证数据是否合格
     *
     * @param data 原始数据
     * @return 是否满足格式
     */
    boolean verifyData(String data);
}
