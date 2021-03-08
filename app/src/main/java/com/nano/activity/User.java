package com.nano.activity;

import com.nano.share.Encryption;

import java.math.BigInteger;
import java.security.KeyPair;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 用户类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/5 23:00
 */
@Data
@NoArgsConstructor
public class User {

    private String userName;

    private String password;

    private String pid;

    private BigInteger sharePublicKey;

    private BigInteger sharePrivateKey;


    private KeyPair rsaKeyPair;

    /**
     * 本次TID
     */
    private String tidEmr;
    private String tidShr;
    private String tidPhr;

    private Encryption encryption;

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", pid='" + pid + '\'' +
                ", sharePublicKey=" + sharePublicKey +
                ", sharePrivateKey=" + sharePrivateKey +
                ", rsaKeyPair=" + rsaKeyPair +
                ", tid='" + tidEmr + '\'' +
                '}';
    }
}
