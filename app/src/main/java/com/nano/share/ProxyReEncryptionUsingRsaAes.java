/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nano.share;

import java.math.BigInteger;
import java.util.Scanner;

/**
 * Test
 * @author singh
 */
public class ProxyReEncryptionUsingRsaAes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Initialise Sender, Server and Receiver
            Encryption sender = new Encryption();
            ProxyReEncryptionServer server = new ProxyReEncryptionServer();
            Encryption receiver = new Encryption();

            // Input Message
            String plainText;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter message: ");
            plainText = scanner.nextLine();
            System.out.println();

            // 发送者对消息加密得到加密发送体
            System.out.println("Sender 公钥:" + sender.rsa.e);
            System.out.println("Sender 私钥:" + sender.rsa.d);
            // 发送者用AES加密消息明文
            MessageEntity senderMessage = sender.encryptMessageByAes(plainText);
            System.out.println("Key: " + senderMessage.getBigIntKey());

            // 加密消息内的AES秘钥(此时消息体内是A生成的代理秘钥)
            // 发送者对AES秘钥加密(现在消息体内存储的是加密后的AES秘钥)
            senderMessage = sender.encryptKeyByRsa(senderMessage);

            // Print Encrypted Key and Message
            System.out.println("Encrypted Key: " + senderMessage.getBigIntKey());
            System.out.println("Encrypted Message: " + senderMessage.getMessage());

            BigInteger midKey = receiver.rsa.e.multiply(sender.rsa.d);

            senderMessage.setMidKey(midKey);

            // Re-Encryption代理重加密
            MessageEntity middleMessage = server.reEncrypt(senderMessage);

            // Print Re-Encrypted Key
            // 代理重加密的秘钥
            System.out.println("Re-Encrypted Key: " + middleMessage.getBigIntKey());
            System.out.println("代理服务器生成的中间密文:" + middleMessage.getMessage());

            // Decryption
            // 接受者解密
            MessageEntity receiverEntity = receiver.decryptKey(middleMessage);
            // 获取明文
            String decryptedPlainText = receiver.decryptMessageByAes(receiverEntity);

            // Print Decrypted Key
            System.out.println("Decrypted Key: " + receiverEntity.getBigIntKey() + "\n");

            // Print Decrypted Message
            System.out.println("Decrypted Message: \n" + decryptedPlainText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
