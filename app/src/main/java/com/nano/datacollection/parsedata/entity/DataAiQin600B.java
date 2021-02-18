package com.nano.datacollection.parsedata.entity;

import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import java.io.Serializable;

import lombok.Data;

/**
 * 苏州爱琴数据实体 (4个通道)
 * @author cz
 */
@Data
public class DataAiQin600B implements Serializable {

    /**
     * 手术场次号
     */
    private Integer operationNumber = AppStatic.operationNumber;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * TOI值
     */
    private Double toi1 = DataCons.INVALID_DATA_DOUBLE;
    private Double toi2 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * THI
     */
    private Double thi1 = DataCons.INVALID_DATA_DOUBLE;
    private Double thi2 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * △CHB值
     */
    private Double chb1 = DataCons.INVALID_DATA_DOUBLE;
    private Double chb2 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * △CHBO2值
     */
    private Double chbo21 = DataCons.INVALID_DATA_DOUBLE;
    private Double chbo22 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * △CTHB值
     */
    private Double cthb1  = DataCons.INVALID_DATA_DOUBLE;
    private Double cthb2  = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 探头状态字符串(包含探头状态和通信质量) 这里存的是未解析的字符串 如果需要就解析
     */
    private String probeStatus1 = DataCons.NO_DATA_STRING;
    private String probeStatus2 = DataCons.NO_DATA_STRING;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
