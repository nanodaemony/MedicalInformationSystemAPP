package com.nano.datacollection.parsedata;

import lombok.Data;

/**
 * 上传到服务器的数据实体
 * @author cz
 */
@Data
public class UpdateDataEntity {

    /**
     * 仪器号
     */
    private int deviceCode;

    /**
     * 数据
     */
    private String deviceData;


    public UpdateDataEntity() {}

    public UpdateDataEntity(int deviceCode, String deviceData) {
        this.deviceCode = deviceCode;
        this.deviceData = deviceData;
    }
}
