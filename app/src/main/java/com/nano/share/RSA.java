package com.nano.share;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

/**
 * @author Divyansh
 */
public class RSA {

    private static final Integer SIZE = 512;
    public BigInteger p, q;
    public BigInteger n;
    public BigInteger euler;
    public BigInteger e, d;

    public RSA() {
        initialize();
    }

    /**
     * 将公钥与私钥转换为大数
     */
    public RSA(PrivateKey privateKey, PublicKey publicKey) {
        initialize();
    }

    /**
     * 初始化生成e与d
     * e: 公钥Public Key
     * d: 私钥Private Key
     */
    public void initialize() {
        // p and q are 2154 digit Prime Numbers which are used in the generation of RSA Keys
        // p与q是用来产生RSA秘钥的大质数
        p = new BigInteger("8513222065247162701695105220665738877312063308356937563625345485856710133446374665834898192825484459951443770023314504441479244278247980992441766519074969");
        q = new BigInteger("8364581280641288933593527550533091363060086128207408134848028170130641974184553465641962883238792572920670310338579332490687347012348067644317739328586993");
        // n = p * q
        n = p.multiply(q);
        // 计算欧拉函数
        euler = p.subtract(BigInteger.valueOf(1));
        euler = euler.multiply(q.subtract(BigInteger.valueOf(1)));
        do {
            e = new BigInteger(2 * SIZE, new Random());
        } while ((e.compareTo(euler) != 1) || (e.gcd(euler).compareTo(BigInteger.valueOf(1)) != 0));
        d = e.modInverse(euler);
        // 生成的都是随机的e和d
        // System.out.println("公钥e:" + e);
        // System.out.println("私钥d:" + d);
    }

    /**
     * 加密
     * @param plaintext 明文
     */
    public BigInteger encrypt(BigInteger plaintext) {
        // plainText^d mod n
        return plaintext.modPow(e, n);
    }

    /**
     * 解密
     * @param ciphertext 密文
     */
    public BigInteger decrypt(BigInteger ciphertext) {
        return ciphertext.modPow(d, n);
    }
}