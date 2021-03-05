/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nano.share;

import java.math.BigInteger;

/**
 * 代理重加密
 *
 * @author singh
 */
public class ProxyReEncryptionServer extends Encryption {

    public ProxyReEncryptionServer() {
        super();
    }

    /**
     * 代理服务器进行重加密
     *
     * @param messageEntity 消息实体
     * @param sender 发送方
     * @param receiver 接收方
     * @return 消息体
     */
    public MessageEntity reEncrypt(MessageEntity messageEntity, Encryption sender, Encryption receiver) {

        // 利用接受者的公钥 + 发送者的私钥生成代理重加密秘钥
        this.rsa.e = receiver.rsa.e.multiply(sender.rsa.d);
        // 获取Sender提供的会话秘钥
        BigInteger key = messageEntity.getBigIntKey();
        // 重新生成秘钥
        key = rsa.encrypt(key);
        // 设置代理重加密转换后的秘钥
        messageEntity.setKey(key);
        return messageEntity;
    }

    /**
     * 代理服务器进行重加密
     *
     * @param messageEntity 消息实体
     * @return 消息体
     */
    public MessageEntity reEncrypt(MessageEntity messageEntity) {

        // 利用接受者的公钥 + 发送者的私钥生成代理重加密秘钥
        this.rsa.e = messageEntity.getMidKey();
        // 获取Sender提供的会话秘钥
        BigInteger key = messageEntity.getBigIntKey();
        // 重新生成秘钥
        key = rsa.encrypt(key);
        // 设置代理重加密转换后的秘钥
        messageEntity.setKey(key);
        return messageEntity;
    }
}
