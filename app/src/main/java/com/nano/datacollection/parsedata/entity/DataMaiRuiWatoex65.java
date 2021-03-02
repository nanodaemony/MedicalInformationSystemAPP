package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 迈瑞WATOEX麻醉机数据实体类
 * @author msc206
 */
@Data
public class DataMaiRuiWatoex65 {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    private String serialNumber;

    private Double pMean = DataCons.INVALID_DATA_DOUBLE;

    private Double peep = DataCons.INVALID_DATA_DOUBLE;

    private Double pPlat = DataCons.INVALID_DATA_DOUBLE;

    private Double pPeak = DataCons.INVALID_DATA_DOUBLE;

    private Double mv = DataCons.INVALID_DATA_DOUBLE;

    private Double tve = DataCons.INVALID_DATA_DOUBLE;

    private Double ie = DataCons.INVALID_DATA_DOUBLE;

    private Double rate = DataCons.INVALID_DATA_DOUBLE;

    private Double c = DataCons.INVALID_DATA_DOUBLE;

    private Double r = DataCons.INVALID_DATA_DOUBLE;

    private Double fiO2 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
