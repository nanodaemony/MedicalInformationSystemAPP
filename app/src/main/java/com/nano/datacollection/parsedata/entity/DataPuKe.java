package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 浙江普可的数据实体类
 * @author cz
 */
@Data
public class DataPuKe {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * AI
     */
    private Integer ai = DataCons.INVALID_DATA_INTEGER;

    /**
     * BSR
     */
    private Integer bsr = DataCons.INVALID_DATA_INTEGER;

    /**
     * EMG
     */
    private Integer emg = DataCons.INVALID_DATA_INTEGER;

    /**
     * SQI
     */
    private Integer sqi = DataCons.INVALID_DATA_INTEGER;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
