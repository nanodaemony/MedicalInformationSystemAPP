package com.nano.datacollection;
import lombok.Data;

/**
 * 上传到服务器的数据实体
 * @author cz
 */
@Data
public class DeviceData {

    /**
     * 仪器号
     */
    private int deviceCode;

    /**
     * 数据实体
     */
    private Object dataObject;

    /**
     * 仪器数据
     */
    private String deviceData;


    public DeviceData() {
    }

    public DeviceData(int deviceCode, String deviceData) {
        this.deviceCode = deviceCode;
        this.deviceData = deviceData;
    }

    public DeviceData(int deviceCode, Object dataObject, String deviceData) {
        this.deviceCode = deviceCode;
        this.dataObject = dataObject;
        this.deviceData = deviceData;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "deviceCode=" + deviceCode +
                ", deviceData='" + deviceData + '\'' +
                '}';
    }
}
