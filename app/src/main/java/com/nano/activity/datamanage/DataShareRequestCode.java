package com.nano.activity.datamanage;

import lombok.Getter;

/**
 * Description: 数据分享Code枚举类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/7 16:36
 */
@Getter
public class DataShareRequestCode {

    /**
     * 接收方请求数据
     */
    public static Integer RECEIVER_REQUEST_DATA = 1;

    /**
     * 发送方检查数据分享请求
     */
    public static Integer SENDER_CHECK_DATA_SHARE_REQUEST = 2;

    /**
     * 发送方处理数据分享请求
     */
    public static Integer SENDER_HANDLE_DATA_SHARE_REQUEST = 3;

    /**
     * 接收方检查处理结果
     */
    public static Integer RECEIVER_CHECK_HANDLE_RESULT = 4;

}
