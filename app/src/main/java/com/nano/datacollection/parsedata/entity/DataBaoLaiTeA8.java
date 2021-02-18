package com.nano.datacollection.parsedata.entity;

import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;
import lombok.ToString;

/**
 * 宝莱特的数据实体类
 * @author cz
 *
 * 存储不同的参数数值以及对应的波形字符串以备后续使用
 */
@Data
@ToString
public class DataBaoLaiTeA8 {

    /**
     * 手术场次号
     */
    private Integer operationNumber = AppStatic.operationNumber;

    /**
     * 序列号
     */
    private String serialNumber;


    /**
     * 心率数据值 E0 (对应顶部显示的HR值)
     */
    private Integer hr = DataCons.INVALID_DATA_INTEGER;

    /**
     * 血氧值模块数据 E1 血氧饱和度对应显示的SPO2 其左侧的曲线Pleth好像也是这个血氧值
     */
    private Integer spo2 = DataCons.INVALID_DATA_INTEGER;

    /**
     * 脉搏数据值 E2 PulseRate (对应屏幕显示的PR值)
     */
    private Integer pr = DataCons.INVALID_DATA_INTEGER;

    /**
     * 体温值 E4 (演示模式会传两个温度出来)
     */
    private Double temperature1 = DataCons.INVALID_DATA_DOUBLE;
    private Double temperature2 = DataCons.INVALID_DATA_DOUBLE;
    private Double temperatureDifference = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 无创血压全部数值(3个) E5 NIBP
     */
    private Double nibpSys =  DataCons.INVALID_DATA_DOUBLE;
    private Double nibpMap = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpDia = DataCons.INVALID_DATA_DOUBLE;


    /**
     * 有创血压全部数值(6个) 分别对应屏幕上的 P1与P2的三个值
     */
    private Double ibpSys1 = DataCons.INVALID_DATA_DOUBLE;
    private Double ibpMap1 = DataCons.INVALID_DATA_DOUBLE;
    private Double ibpDia1 = DataCons.INVALID_DATA_DOUBLE;

    private Double ibpSys2 = DataCons.INVALID_DATA_DOUBLE;
    private Double ibpMap2 = DataCons.INVALID_DATA_DOUBLE;
    private Double ibpDia2 = DataCons.INVALID_DATA_DOUBLE;


    /**
     * 采集时间
     */
    private String gmtCreate;

    public DataBaoLaiTeA8() {
        gmtCreate = TimeStampUtils.getCurrentTimeAsString();
    }

}
