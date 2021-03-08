/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nano.share;
import android.util.Base64;

import com.nano.share.rsa.Base64Utils;

import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.Data;

/**
 * @author singh
 */
@Data
public class MessageEntity {


    /**
     * 消息
     */
    private String message;

    /**
     * 发送PID
     */
    private String senderPid;

    /**
     * 接收方PID
     */
    private String receiverPid;

    /**
     * 治疗ID
     */
    private String treatmentId;

    /**
     * 秘钥(大数)
     */
    private BigInteger key;

    /**
     * 由发送方生成的中间转换秘钥
     */
    private BigInteger conversionKey;


    public MessageEntity() {
    }

    public MessageEntity(String message, SecretKey key) {
        this.message = message;
        // 秘钥转换
        String key1 =  Base64.encodeToString(key.getEncoded(), Base64.URL_SAFE);
        System.out.println("Key1:" + key1);
        this.key = new BigInteger(key1.getBytes());
    }

    public MessageEntity(String message, BigInteger key) {
        this.message = message;
        this.key = key;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public BigInteger getConversionKey() {
        return conversionKey;
    }

    public void setConversionKey(BigInteger conversionKey) {
        this.conversionKey = conversionKey;
    }

    /**
     * 将秘钥转换为大数
     */
    public void setKey(SecretKey key) {
        this.key = new BigInteger(Base64.encode(key.getEncoded(), Base64.URL_SAFE));
    }

    public void setKey(BigInteger key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public SecretKey getKey() {
        String keyString = new String(key.toByteArray());
        byte[] decodedKey = Base64.decode(keyString, Base64.URL_SAFE);
        // Constructs a secret key from the given byte array 
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

    public BigInteger getBigIntKey() {
        return key;
    }
}
