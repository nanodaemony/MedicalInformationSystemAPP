package com.nano.datacollection.parsedata.entity;

import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * Description: 名希数据实体
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/13 10:54
 */
@Data
public class DataMingXi {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * 左通道数据
     */
    private Double l = DataCons.INVALID_DATA_DOUBLE;
    private Double lhb = DataCons.INVALID_DATA_DOUBLE;
    private Double lhbo = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 右通道数据
     */
    private Double r = DataCons.INVALID_DATA_DOUBLE;
    private Double rhb = DataCons.INVALID_DATA_DOUBLE;
    private Double rhbo = DataCons.INVALID_DATA_DOUBLE;


    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
