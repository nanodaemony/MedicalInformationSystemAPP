package com.nano.share;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES算法
 * @author Divyansh
 */
public class AES {


    private static SecretKeySpec secretKeySpec;

    /**
     * 秘钥
     */
    private static byte[] key;

    /**
     * 设置AES对称加密的秘钥
     */
    public static void setAesKey(SecretKey myKey) {
        MessageDigest sha = null;
        try {
            String str = new String(Base64.getEncoder().encodeToString(myKey.getEncoded()));
            key = str.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进行AES加密
     *
     * @param plainText  明文
     * @param secret 秘钥
     */
    public static String encrypt(String plainText, SecretKey secret) {
        try {
            // 设置秘钥
            setAesKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES解密
     *
     * @param egyptText 密文
     * @param secret 秘钥
     */
    public static String decrypt(String egyptText, SecretKey secret) {
        try {
            setAesKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(egyptText)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}