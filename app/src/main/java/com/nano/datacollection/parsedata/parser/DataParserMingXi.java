package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.ParamDeviceDataPad;
import com.nano.datacollection.parsedata.entity.DataMingXi;

/**
 * Description: 名希的数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/13 10:59
 */
public class DataParserMingXi implements DeviceDataParser {

    /**
     * 解析数据
     *
     * @param deviceCode 仪器号
     * @param serialNumber 序列号
     * @param deviceOriginData 原始数据
     * @return 数据
     */
    @Override
    public DeviceData parseData(int deviceCode, Integer collectionNumber, String serialNumber, String deviceOriginData) {

        // MNIR-P100:L=0.001;R=0.743;LHB=0.000;LHBO=0.000;RHB=585.000;RHBO=1639.000;

        DataMingXi dataMingXi = new DataMingXi();
        dataMingXi.setSerialNumber(serialNumber);
        dataMingXi.setCollectionNumber(collectionNumber);
        deviceOriginData = deviceOriginData.trim();




        String dataString = JSON.toJSONString(dataMingXi);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataMingXi, JSON.toJSONString(new ParamDeviceDataPad(deviceCode, collectionNumber, dataString)));
    }



    /**
     * 验证数据
     *
     * @param data 原始数据
     * @return 是否合格
     */
    @Override
    public boolean verifyData(String data) {
        return data.startsWith("MNIR") && data.endsWith(";");
    }

}
