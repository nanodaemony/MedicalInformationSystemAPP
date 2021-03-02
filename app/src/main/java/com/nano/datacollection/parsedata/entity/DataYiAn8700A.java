package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 宜安8700A麻醉机的数据实体类
 * @author cz
 */
@Data
public class DataYiAn8700A {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 序列号
     */
    private String serialNumber;

    /////////////////////////////////////////////////////////////////////////////////////
    // 下面是仪器数据
    /////////////////////////////////////////////////////////////////////////////////////

    private Double peak =  DataCons.INVALID_DATA_DOUBLE;

    private Double plat = DataCons.INVALID_DATA_DOUBLE;

    private Double pmean = DataCons.INVALID_DATA_DOUBLE;

    private Double peep = DataCons.INVALID_DATA_DOUBLE;

    private Double mv = DataCons.INVALID_DATA_DOUBLE;

    private Double vte = DataCons.INVALID_DATA_DOUBLE;

    private Double freq = DataCons.INVALID_DATA_DOUBLE;

    private Double fio2 = DataCons.INVALID_DATA_DOUBLE;

    private Double etco2 = DataCons.INVALID_DATA_DOUBLE;

    private Double fico2 = DataCons.INVALID_DATA_DOUBLE;

    private Double n2oInsp = DataCons.INVALID_DATA_DOUBLE;

    private Double n2oExp = DataCons.INVALID_DATA_DOUBLE;

    private Double mac = DataCons.INVALID_DATA_DOUBLE;

    private Double n2o = DataCons.INVALID_DATA_DOUBLE;

    private Double air = DataCons.INVALID_DATA_DOUBLE;

    private Double o2 = DataCons.INVALID_DATA_DOUBLE;


    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();

}
