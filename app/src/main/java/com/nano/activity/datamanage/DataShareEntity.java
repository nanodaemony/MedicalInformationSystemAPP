package com.nano.activity.datamanage;


import com.nano.share.MessageEntity;

import java.math.BigInteger;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 数据分享的参数
 * Usage:
 * 1.
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/7 16:26
 */
@Data
@NoArgsConstructor
public class DataShareEntity {

    /**
     * 数据分享ID
     */
    private String dataShareId = "S" + System.currentTimeMillis();

    /**
     * 是否已经处理
     */
    private boolean isHandled;

    /**
     * 发送方是否同意分享
     */
    private boolean isAgree;

    /**
     * 数据的TID
     */
    private String dataTid;

    /**
     * 数据接收方PID
     */
    private String receiverPid;

    /**
     * 数据发送方PID
     */
    private String senderPid;

    /**
     * 数据分享的实体
     */
    private String shareData;

}
