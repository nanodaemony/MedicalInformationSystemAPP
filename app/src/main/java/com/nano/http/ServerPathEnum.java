package com.nano.http;

import lombok.Getter;

/**
 * Description: 服务器路径枚举
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/7 16:29
 */
public enum ServerPathEnum {

    /**
     * 本地环境使用校园网
     */
    LOCAL("172.20.29.106",
            10086,
            "/eval/data-collection/device-info",
            "/eval/data-collection/device-data"
            ),


    /**
     * 本地环境使用4G路由器
     */
    LOCAL_WIFI("192.168.8.100",
            10086,
            "/eval/data-collection/device-info",
            "/eval/data-collection/device-data"
    ),


    /**
     * 云服务器生产环境
     */
    CLOUD_SERVER_PROD("39.98.122.209",
            10086,
            "/eval/data-collection/device-info",
            "/eval/data-collection/device-data"
    ),


    /**
     * 云服务器预生产环境
     */
    CLOUD_SERVER_PRE_PROD("39.98.122.209",
            10085,
            "/eval/data-collection/device-info",
            "/eval/data-collection/device-data"
    ),


    ;

    String ipAddress;

    Integer port;

    @Getter
    String infoPath;

    @Getter
    String dataPath;

    ServerPathEnum(String ipAddress, Integer port, String infoPath, String dataPath) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.infoPath = "http://" + ipAddress + ":" + port + infoPath;
        this.dataPath = "http://" + ipAddress + ":" + port + dataPath;
    }
}
