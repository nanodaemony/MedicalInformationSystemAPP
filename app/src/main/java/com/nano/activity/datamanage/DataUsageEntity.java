package com.nano.activity.datamanage;

import java.io.Serializable;

import lombok.Data;

/**
 * Description: 数据使用的实体
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/4 17:22
 */
@Data
public class DataUsageEntity implements Serializable {

    private String treatmentId;

    private String senderPseudonymId;

    private String receiverPseudonymId;

    private Long timestamp;

    @Override
    public String toString() {
        return "DataUsageEntity{" +
                "treatmentId='" + treatmentId + '\'' +
                ", senderPseudonymId='" + senderPseudonymId + '\'' +
                ", receiverPseudonymId='" + receiverPseudonymId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static DataUsageEntity getInstance() {
        DataUsageEntity entity = new DataUsageEntity();
        entity.setTreatmentId(System.currentTimeMillis() + "12380ABSAH");
        entity.setSenderPseudonymId(System.currentTimeMillis() + "12ANS");
        entity.setReceiverPseudonymId(System.currentTimeMillis() + "129BL");
        entity.setTimestamp(System.currentTimeMillis());
        return entity;
    }



}
