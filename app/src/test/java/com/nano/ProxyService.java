package com.nano;



import com.alibaba.fastjson.JSON;
import com.nano.share.Encryption;
import com.nano.share.MessageEntity;
import com.nano.share.ProxyReEncryptionServer;

import org.junit.Test;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Description:
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/6 17:13
 */
public class ProxyService {


    public static void main(String[] args) {
        new ProxyService().testShareMedicalData();
    }

    /**
     * 被分享数据明文
     */
    private static final String SHARED_DATA_PLAIN_TEXT = "{\"admissionId\":\"577311724\",\"beforeOperationDiagnosis\":\"普通感冒\",\"choosedOperationName\":\"\",\"hospitalArea\":\"重庆\",\"hospitalCode\":\"50\",\"hospitalLevel\":\"三甲\",\"hospitalOperationNumber\":\"1607254982954\",\"operationASALevel\":3,\"operationAnesthesiaMode\":\"局麻\",\"operationHeartFunctionLevel\":\"II级\",\"operationIsUrgent\":false,\"operationKidneyFunctionLevel\":\"4\",\"operationLiverFunctionLevel\":\"B级\",\"operationLungFunctionLevel\":\"3级\",\"operationName\":\"XXXXXX术\",\"pastMedicalHistory\":\"无\",\"patientAge\":\"90\",\"patientHeight\":\"190\",\"patientId\":\"160725498295428008251\",\"patientSex\":\"1\",\"patientWeight\":\"70\",\"specialDiseaseCase\":\"高血压\"}\n";

    /**
     * Do test.
     */
    @Test
    public void testShareMedicalData() {
        // 发送方
        Encryption sender = new Encryption();
        // 服务器
        ProxyReEncryptionServer server = new ProxyReEncryptionServer();
        // 接收方
        Encryption receiver = new Encryption();
        try {
            // 发送者用AES加密消息明文
            MessageEntity senderMessage = sender.encryptMessageByAes(SHARED_DATA_PLAIN_TEXT);
            // 加密消息内的AES秘钥(此时消息体内是A生成的代理秘钥)
            // 发送者对AES秘钥加密(现在消息体内存储的是加密后的AES秘钥)
            senderMessage = sender.encryptKeyByRsa(senderMessage);
            System.out.println(senderMessage.toString());

            System.out.println("\n\n\n\n");

            JSON.toJSONString(senderMessage);
            // 用接收方的公钥与发送方的私钥生成转换秘钥
            BigInteger conversionKey = receiver.rsa.e.multiply(sender.rsa.d);
            senderMessage.setConversionKey(conversionKey);

            // Re-Encryption代理重加密
            MessageEntity middleMessage = server.reEncrypt(senderMessage);
            // 构造数据使用实体(并上传到区块链中)
//            }
            // 接受者解密
            MessageEntity receiverEntity = receiver.decryptKey(middleMessage);
            // 获取明文
            String decryptedPlainText = receiver.decryptMessageByAes(receiverEntity);
            // Print Decrypted Message
            // System.out.println("Get shared message: \n" + decryptedPlainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
