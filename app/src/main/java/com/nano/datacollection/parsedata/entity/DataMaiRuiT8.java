package com.nano.datacollection.parsedata.entity;


import com.nano.AppStatic;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.util.TimeStampUtils;

import lombok.Data;

/**
 * 迈瑞T8监护仪数据实体
 * @author cz
 */
@Data
public class DataMaiRuiT8 {

    /**
     * 采集场次号
     */
    private Integer collectionNumber;

    /**
     * 仪器序列号
     */
    private String serialNumber;


    /**
     * ECG Heart Rate (对应屏幕最顶上的ECG值为: 60)
     */
    private Integer ecgHeartRate = DataCons.INVALID_DATA_INTEGER;

    /**
     * ECG PVC Sum
     */
    private Integer ecgPvcSum = DataCons.INVALID_DATA_INTEGER;

    /**
     * ECG 的许多参数,屏幕都没展示
     */
    private Double ecgStParamI = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamII = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamIII = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamAvr = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamAvl = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamAvf = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV1 = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV2 = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV3 = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV4 = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV5 = DataCons.INVALID_DATA_DOUBLE;
    private Double ecgStParamV6 = DataCons.INVALID_DATA_DOUBLE;

    /**
     * RESP Respiration Rate (RESP 对应屏幕下方的黄色参数)
     */
    private Integer respRespirationRate = DataCons.INVALID_DATA_INTEGER;

    /**
     * SPO2 Percent Oxygen Saturation(对应屏幕的SPO2参数)
     */
    private Integer spo2PercentOxygenSaturation = DataCons.INVALID_DATA_INTEGER;

    /**
     * SPO2 pulse rate (对应屏幕蓝色的PR参数)
     */
    private Integer spo2PulseRate = DataCons.INVALID_DATA_INTEGER;

    /**
     * SPO2 pulse PI
     */
    private Double spo2Pi = DataCons.INVALID_DATA_DOUBLE;


    /**
     * NIBP 无创血压的几个参数值
     */
    private Double nibpSystolic = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpDiastolic = DataCons.INVALID_DATA_DOUBLE;
    private Double nibpMean = DataCons.INVALID_DATA_DOUBLE;

    /**
     * 两个温度值及其差值
     */
    private Double tempTemperature1 = DataCons.INVALID_DATA_DOUBLE;
    private Double tempTemperature2 = DataCons.INVALID_DATA_DOUBLE;
    private Double tempTemperatureDifference = DataCons.INVALID_DATA_DOUBLE;


    /**
     * 由ART模块产生的有创血压参数: 120 93 80 (仪器展示)
     */
    private Integer artIbpSystolic = DataCons.INVALID_DATA_INTEGER;
    private Integer artIbpMean = DataCons.INVALID_DATA_INTEGER;
    private Integer artIbpDiastolic = DataCons.INVALID_DATA_INTEGER;


    /**
     * 由PA模块产生的有创血压参数: 25 14 9(仪器没有展示)
     */
    private Integer paIbpSystolic = DataCons.INVALID_DATA_INTEGER;
    private Integer paIbpMean = DataCons.INVALID_DATA_INTEGER;
    private Integer paIbpDiastolic = DataCons.INVALID_DATA_INTEGER;

    /**
     * ART PPV
     */
    private Double artPpv = DataCons.INVALID_DATA_DOUBLE;

    /**
     * PR(来自PR模块的PR参数)
     */
    private Integer prPr = DataCons.INVALID_DATA_INTEGER;

//    /**
//     * CO Cardiac output
//     */
//    private  Double coCardiacOutput = (double)DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * CO Aperiodic parameter Cardiac index
//     */
//    private Double coCardiacIndex = (double)DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * CO Aperiodic parameter Blood Pressure
//     */
//    private Double coBloodPressure = (double)DataCons.INVALID_INTEGER_DATA;
//
//
//    /**
//     * AG CO2ET
//     */
//    private Integer agCo2Et = DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * AG CO2Fi
//     */
//    private Integer agCo2Fi = DataCons.INVALID_INTEGER_DATA;
//
//
//    /**
//     * AG O2Et
//     */
//    private Integer agO2Et = DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * AG O2Fi
//     */
//    private Integer agO2Fi = DataCons.INVALID_INTEGER_DATA;
//
//
//    /**
//     * AG N2OEt
//     */
//    private Integer agN2oEt = DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * AG O2Fi
//     */
//    private Integer agN2oFi = DataCons.INVALID_INTEGER_DATA;
//
//
//    /**
//     * AG ISOEt
//     */
//    private Double agIsoEt = (double)DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * AG ISOFi
//     */
//    private Double agIsoFi = (double)DataCons.INVALID_INTEGER_DATA;
//
//    /**
//     * AG AwRR
//     */
//    private Double agAwRr = (double)DataCons.INVALID_INTEGER_DATA;

    /**
     * 采集时间
     */
    private String gmtCreate = TimeStampUtils.getCurrentTimeAsString();


}
