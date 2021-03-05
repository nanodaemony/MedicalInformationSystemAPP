/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nano.share;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * @author singh
 */
public class Encryption {

    /**
     * 使用RSA
     */
    public RSA rsa;

    public Encryption() {
        rsa = new RSA();
    }

    private static final String pwd = "password";

    /**
     * 利用AES加密消息
     */
    public MessageEntity encryptMessageByAes(String message) throws NoSuchAlgorithmException {
        // AES Symmetric Key Generation and Encryption of Plaintext Message
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(pwd.getBytes());
        keyGen.init(128, random);
        // 产生AES秘钥(随机生成???)
        SecretKey aesKey = keyGen.generateKey();
        //System.out.println("AES秘钥:" + aesKey.toString());
        // 使用AES加密明文
        message = AES.encrypt(message, aesKey);
        // 返回消息实体(记录这个AES的秘钥)
        return new MessageEntity(message, aesKey);
    }

    /**
     * 加密AES秘钥
     *
     * @param messageEntity 消息体
     */
    public MessageEntity encryptKeyByRsa(MessageEntity messageEntity) {
        // 对消息体内的AES对应的大数进行加密
        BigInteger key = rsa.encrypt(messageEntity.getBigIntKey());
        // 给原始消息体设置用RSA加密后的AES秘钥
        messageEntity.setKey(key);
        // 返回消息体
        return messageEntity;
    }

    /**
     * RSA解密AES秘钥
     */
    public MessageEntity decryptKey(MessageEntity messageEntity) {
        BigInteger key = rsa.decrypt(messageEntity.getBigIntKey());
        // 重新设置AES秘钥
        messageEntity.setKey(key);
        return messageEntity;
    }

    public String decryptMessageByAes(MessageEntity messageEntity) {
        SecretKey aesKey = messageEntity.getKey();
        //System.out.println("AES秘钥:" + aesKey.toString());
        // 使用AES解密
        return AES.decrypt(messageEntity.getMessage(), aesKey);
    }





}
