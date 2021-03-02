package com.nano.activity.heartblood;

import lombok.Data;

/**
 * Description: 上传到区块链的医疗数据实体类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/1 11:16
 */
@Data
public class MedicalDataEntity {

    /**
     * 数据类型(EMR/SHR/ PHR)
     */
    private String dataType;

    /**
     * 加密数据摘要
     */
    private String dataMessageDigest;

    /**
     * 数据存储地址
     */
    private String dataSaveUrl;

    /**
     * 医生签名
     */
    private String dataSignatureDoctor;

    /**
     * 病人签名
     */
    private String dataSignaturePatient;

    /**
     * 医生伪身份ID
     */
    private String doctorPseudonymId;

    /**
     * 病人伪身份ID
     */
    private String patientPseudonymId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 治疗ID
     */
    private String treatmentId;


}
