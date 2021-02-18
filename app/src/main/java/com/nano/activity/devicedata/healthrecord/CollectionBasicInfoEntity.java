package com.nano.activity.devicedata.healthrecord;


import com.nano.AppStatic;

import lombok.Data;

/**
 * 采集基本信息实体
 * @author cz
 *
 * 这个实体包含了病人信息和仪器基本信息,用于上传到服务器
 */
@Data
public class CollectionBasicInfoEntity {


    /**
     * 新桥医院默认CODE
     */
    private static final String HOSPITAL_CODE_XIN_QIAO = "50";

    /**
     * 没有输入数据
     */
    private static final String NO_INPUT = "";

    /**
     * 医院地区
     */
    private String hospitalArea = NO_INPUT;

    /**
     * 医院等级
     */
    private String hospitalLevel = NO_INPUT;

    /**
     * 医院代号
     */
    private String hospitalCode = HOSPITAL_CODE_XIN_QIAO;

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
    private Boolean operationIsUrgent = false;

    /**
     * 手术ASA等级
     */
    private Integer operationASALevel = 0;

    /**
     * 心功能分级(0表示没有选择)
     */
    private Integer operationHeartFunctionLevel = 0;

    /**
     * 肺功能分级(0表示没有选择)
     */
    private Integer operationLungFunctionLevel = 0;

    /**
     * 肝功能分级(0表示没有选择)
     */
    private Integer operationLiverFunctionLevel = 0;

    /**
     * 肾功能分级(0表示没有选择)
     */
    private Integer operationKidneyFunctionLevel = 0;

    /**
     * 手术麻醉方式
     */
    private String operationAnesthesiaMode = NO_INPUT;

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

    /**
     * 使用的仪器信息
     */
    private String usedDeviceInfo;

}
