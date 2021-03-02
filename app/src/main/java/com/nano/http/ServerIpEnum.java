package com.nano.http;

import lombok.Getter;

/**
 * Description: 服务器IP枚举
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/23 17:04
 */
public enum ServerIpEnum {

    /**
     * 本地环境使用4G路由器
     */
    LOCAL_WIFI("192.168.8.120", 10086),


    /**
     * 云服务器生产环境
     */
    CLOUD_SERVER_PROD("39.98.122.209", 10086),


    /**
     * 云服务器预生产环境
     */
    CLOUD_SERVER_PRE_PROD("39.98.122.209", 10085),

    ;

    String ipAddress;

    Integer port;

    @Getter
    String path;

    ServerIpEnum(String ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.path = "http://" + ipAddress + ":" + port;
    }
}
