package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 理邦 ELite V8监护仪数据实体
 * @author cz
 */
@Data
public class DataLiBangEliteV8 {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 序列号
     */
    private String serialNumber;


    private Double weight = DataCons.INVALID_DATA_DOUBLE;

    private Double height = DataCons.INVALID_DATA_DOUBLE;

    private Double hr = DataCons.INVALID_DATA_DOUBLE;
    private Double pvcs = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 呼吸率
     */
    private Double rr = DataCons.INVALID_DATA_DOUBLE;

    private Double spo2 = DataCons.INVALID_DATA_DOUBLE;

    private Double pr = DataCons.INVALID_DATA_DOUBLE;

    private Double temp1 = DataCons.INVALID_DATA_DOUBLE;
    private Double temp2 = DataCons.INVALID_DATA_DOUBLE;

    private Double cvpMap = DataCons.INVALID_DATA_DOUBLE;

    private Double lapMap = DataCons.INVALID_DATA_DOUBLE;

    /**
     * ART参数
     */
    private Double artSys = DataCons.INVALID_DATA_DOUBLE;
    private Double artDia = DataCons.INVALID_DATA_DOUBLE;
    private Double artMap = DataCons.INVALID_DATA_DOUBLE;

    /**
     * P2参数
     */
    private Double p2Sys = DataCons.INVALID_DATA_DOUBLE;
    private Double p2Dia = DataCons.INVALID_DATA_DOUBLE;
    private Double p2Map = DataCons.INVALID_DATA_DOUBLE;

    /**
     * NIBP相关参数(演示模式没有这个参数,但是根据协议应该是有的.)
     */
    private Double nibpSys = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpDia = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpMap = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpPr = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
