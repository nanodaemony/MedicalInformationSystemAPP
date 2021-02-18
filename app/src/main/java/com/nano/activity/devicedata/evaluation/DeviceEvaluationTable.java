package com.nano.activity.devicedata.evaluation;


import com.alibaba.fastjson.annotation.JSONField;
import com.nano.AppStatic;

import lombok.Data;

/**
 * 仪器评价表单
 * @author cz
 */
@Data
public class DeviceEvaluationTable {

    /**
     * 仪器代号
     */
    private int deviceCode = 0;

    /**
     * 手术场次号
     */
    private int operationNumber = AppStatic.operationNumber;

    /**
     * 评价的唯一序号
     */
    private String uniqueNumber = "" + AppStatic.operationNumber + System.currentTimeMillis();

    /**
     * 仪器序列号
     */
    private String serialNumber;

    /**
     * 使用科室
     */
    private String deviceDepartment;

    /**
     * 使用评价等级
     */
    private Integer experienceLevel;

    /**
     * 可靠性等级
     */
    private Integer reliabilityLevel;

    /**
     * 是否有错误信息
     */
    private boolean hasError;

    /**
     * 错误原因
     */
    private String knownError;

    /**
     * 其他错误
     */
    private String otherError;

    /**
     * 备注
     */
    private String remark;

    /**
     * 记录人签名
     */
    private String recordName;

    /**
     * 是否上传
     */
    @JSONField(serialize = false)
    private boolean isUpdated = false;

    /**
     * 是否被评价
     */
    @JSONField(serialize = false)
    private boolean isEvaluated = false;


    @Override
    public String toString() {
        return "EvaluationTable{" +
                "deviceCode='" + deviceCode + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", deviceDepartment='" + deviceDepartment + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", reliabilityLevel='" + reliabilityLevel + '\'' +
                ", hasError='" + hasError + '\'' +
                ", knownError='" + knownError + '\'' +
                ", otherError='" + otherError + '\'' +
                ", remark='" + remark + '\'' +
                ", recordName='" + recordName + '\'' +
                '}';
    }
}
