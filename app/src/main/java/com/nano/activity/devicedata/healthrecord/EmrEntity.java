package com.nano.activity.devicedata.healthrecord;

import com.nano.AppStatic;

import lombok.Data;

/**
 * Description: 存储EMR数据的实体类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/6 19:13
 */
@Data
public class EmrEntity {


    /**
     * 新桥医院默认CODE
     */
    private String hospitalName = "新桥医院";

    /**
     * 没有输入数据
     */
    private static final String NO_INPUT = "";

    /**
     * 医院地区
     */
    private String hospitalArea = "重庆市";

    /**
     * 医院等级
     */
    private String hospitalLevel = "三级甲等";

    /**
     * 病人身份证号
     */
    private String patientId = NO_INPUT;

    /**
     * 病人住院号
     */
    private String admissionId = NO_INPUT;

    /**
     * 病人年龄
     */
    public String patientAge = NO_INPUT;

    /**
     * 病人身高
     */
    private String patientHeight = NO_INPUT;

    /**
     * 病人体重
     */
    private String patientWeight = NO_INPUT;

    /**
     * 病人性别
     */
    private String patientSex = NO_INPUT;

    /**
     * 手术名称
     */
    private String operationName;

    /**
     * 经过选择的手术名称
     */
    private String choosedOperationName = NO_INPUT;

    /**
     * 医院实际的手术序列号
     */
    private String hospitalOperationNumber = NO_INPUT;

    /**
     * 术前诊断
     */
    private String beforeOperationDiagnosis = NO_INPUT;

    /**
     * 手术是否急诊
     */
    private String operationIsUrgent = NO_INPUT;

    /**
     * 手术ASA等级
     */
    private String operationASALevel = NO_INPUT;

    /**
     * 心功能分级(0表示没有选择)
     */
    private String operationHeartFunctionLevel = NO_INPUT;

    /**
     * 肺功能分级(0表示没有选择)
     */
    private String operationLungFunctionLevel = NO_INPUT;

    /**
     * 肝功能分级(0表示没有选择)
     */
    private String operationLiverFunctionLevel = NO_INPUT;

    /**
     * 肾功能分级(0表示没有选择)
     */
    private String operationKidneyFunctionLevel = NO_INPUT;


    /**
     * 病人既往病史
     */
    private String pastMedicalHistory = "无";

    /**
     * 病人特殊疾病情况
     */
    private String specialDiseaseCase = "无";

    /**
     * 采集器MAC地址
     */
    private String collectorMacAddress = AppStatic.macAddress;


}
