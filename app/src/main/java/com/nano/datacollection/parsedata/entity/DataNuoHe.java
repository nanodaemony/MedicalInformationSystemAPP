package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 诺和的数据实体类
 * @author cz
 */
@Data
public class DataNuoHe {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * BS
     */
    private Integer bs = DataCons.INVALID_DATA_INTEGER;

    /**
     * EMG
     */
    private Integer emg = DataCons.INVALID_DATA_INTEGER;

    /**
     * SQI
     */
    private Integer sqi = DataCons.INVALID_DATA_INTEGER;

    /**
     * CSI
     */
    private Integer csi = DataCons.INVALID_DATA_INTEGER;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();


}
