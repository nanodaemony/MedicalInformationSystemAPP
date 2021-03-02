package com.nano.datacollection.parsedata;

import lombok.Data;

/**
 * 上传到服务器的数据实体
 * @author cz
 */
@Data
public class ParamDeviceDataPad {

    /**
     * 仪器号
     */
    private Integer deviceCode;

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 数据
     */
    private String deviceData;


    public ParamDeviceDataPad() {}

    public ParamDeviceDataPad(Integer deviceCode, Integer collectionNumber, String deviceData) {
        this.deviceCode = deviceCode;
        this.collectionNumber = collectionNumber;
        this.deviceData = deviceData;
    }
}
