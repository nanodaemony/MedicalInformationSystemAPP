package com.nano;

import com.alibaba.fastjson.JSON;
import com.nano.activity.devicedata.healthrecord.CollectionBasicInfoEntity;

import org.junit.Test;

import java.util.Random;


/**
 * 测试解析爱琴
 * @author cz
 */
public class TestParseAiQin {


    @Test
    public void parse() {

        String data = "EF EF EF 65 23 18 00 00 00 0A 31 35 30 32 38 37 00 01 20 00 30 30 11 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 42 7B 9F 68 3D DC 2A 30 7F C0 00 00 7F C0 00 00 7F C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 32 FE FE FE";
//        System.out.println(data.replace(" ", ""));
//        System.out.println(ParseAiQin.parseData(DeviceCode.AI_QIN_EGOS600A, data, "123"));;


    }


    @Test
    public void setDefaultPatientInfo() {
        CollectionBasicInfoEntity collectionBasicInfoEntity = new CollectionBasicInfoEntity();
        collectionBasicInfoEntity.setHospitalArea("重庆");
        collectionBasicInfoEntity.setHospitalLevel("三甲");
        collectionBasicInfoEntity.setHospitalCode("50");
        Random random = new Random();
        collectionBasicInfoEntity.setPatientId("" + System.currentTimeMillis() + random.nextInt(100000000));
        collectionBasicInfoEntity.setAdmissionId("" + random.nextInt(1000000000));
        collectionBasicInfoEntity.setPatientAge("90");
        collectionBasicInfoEntity.setPatientHeight("190");
        collectionBasicInfoEntity.setPatientWeight("70");
        collectionBasicInfoEntity.setPatientSex("1");
        collectionBasicInfoEntity.setOperationName("XXXXXX术");
        collectionBasicInfoEntity.setHospitalOperationNumber("" + System.currentTimeMillis());
        collectionBasicInfoEntity.setBeforeOperationDiagnosis("普通感冒");
        collectionBasicInfoEntity.setOperationIsUrgent(false);
        collectionBasicInfoEntity.setOperationASALevel(3);
        collectionBasicInfoEntity.setOperationHeartFunctionLevel(1);
        collectionBasicInfoEntity.setOperationLungFunctionLevel(2);
        collectionBasicInfoEntity.setOperationLiverFunctionLevel(3);
        collectionBasicInfoEntity.setOperationKidneyFunctionLevel(1);
        collectionBasicInfoEntity.setOperationAnesthesiaMode("局麻");
        collectionBasicInfoEntity.setPastMedicalHistory("无");
        collectionBasicInfoEntity.setSpecialDiseaseCase("高血压");
        //logger.debug(collectionBasicInfoEntity.toString());
        //PatientUtils.savePatientInfo(patientInfo);
        System.out.println(JSON.toJSONString(collectionBasicInfoEntity));



    }






}
