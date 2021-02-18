package com.nano.datacollection.cons;


/**
 * 用于存放仪器接入的控制指令的接口类
 * @author cz
 */
public interface DeviceAccessCommands {


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 宝莱特A8
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 宝莱特
     *
     * 请求数据的命令
     * First is 03146A, now is point to 45679 port
    */
    String BAO_LAI_TE_REQUEST_DATA = "FFDA7FF1000F000602080102646F";


    /**
     * 宝莱特
     *
     * 采集器响应B的0x203接收新病人命令
     * 格式为：FFDA7FF1000F00030AXX03，即 FF DA 7F F1 00 0F 00 03 0A XX 03
     * 这里XX根据仪器发出的接收病人命令序号获取。变化区间为04 0C 14 1C 24 2C 34 3C 44 4C 54 5C 64 6C 74 04 0C
     * XX处仪器发送什么就回复什么
     */
    String BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT = "FFDA7FF1000F00030A";

    /**
     * 宝莱特
     *
     * 采集器响应仪器的心率失常信息，格式为FFDA7FF1000F00030AXX05，即FF DA 7F F1 00 0F 00 03 0A XX 05
     * 这里XX根据仪器发出的接收病人命令序号获取。XX处仪器发送什么就回复什么
     * 中央站响应B的0x205的心律失常命令
     */
    String BAO_LAI_TE_RESPONSE_HEART_RATE_INFO = "FFDA7FF1000F00030A";

    /**
     * 宝莱特
     *
     * 中央站响应B的0x206的配置信息命令
     */
    String BAO_LAI_TE_RESPONSE_CONFIGURE_INFO = "FFDA7FF1000F00030A";


    /**
     * 宝莱特
     *
     * 请求同步时间,时间并不标准，这对数据用处不大
     */
    String BAO_LAI_TE_REQUEST_SYNIC_TIME = "FFDA7FF1000F000C02180D0F6106170E001015360000";

    /**
     * 宝莱特
     *
     * 采集器发送查询配置命令
     */
    String BAO_LAI_TE_REQUEST_CONFIGURE = "FFDA7FF1000F0003022004";

    /**
     * 宝莱特
     *
     * 采集器的广播消息,不需要设置模块信息，因此比仪器的广播消息更短
     */
    String BAO_LAI_TE_BRAODCAST_MESSAGE = "FFD07FF100FE000134";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 科曼C90
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 深圳科曼
     *
     * 上电注册应答包
     */
    String DEVICE_REGISTER_RESPONSE = "BBBB0A1E0900000031EEEE";

    /**
     * 深圳科曼
     *
     * 心跳包
     */
    String HERAT_BEAT_PACKAGE = "BBBB081E07002DEEEE";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 迈瑞T8+WATOEX65
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 迈瑞
     *
     * 心跳包信息
     */
    String MAI_RUI_HEART_MESSAGE = "MSH|^~\\&|||||||ORU^R01|106|P|2.3.1|";


    /**
     * 迈瑞
     *
     * 查询数据指令
     */
    String MAI_RUI_QUERY_MONITOR_DATA = "\u000BMSH|^~\\&|||||||QRY^R02|1203|P|2.3.1\r" +
            "QRD|20060731145557|R|I|Q895211|||||RES\r" +
            "QRF|MON||||0&0^1^1^1^\r\u001C\r";

}
