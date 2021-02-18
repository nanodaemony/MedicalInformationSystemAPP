package com.nano.device;

import lombok.Getter;

/**
 * Description: 仪器信息的枚举类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/27 0:36
 */
@Getter
public enum DeviceEnum {

    //**************************************** UDP仪器 ***********************************************************//

    /**
     * 诺和 NW9002S
     */
    NUO_HE_NW9002S(30,
            "诺和 NW9002S 麻醉深度/血红蛋白",
            "合肥诺和电子科技有限公司",
            // 麻醉深度监测仪 + 无创血红蛋白监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_DEPTH_MONITOR, DeviceTypeEnum.HEMOGLOBIN_MONITOR),
            "NW9002S",
            "192.168.8.30",
            45677,
            0,
            400,
            "UDP"),

    /**
     * 普可 YY106
     */
    PU_KE_YY106(31,
            "普可 YY-106 麻醉深度",
            "浙江普可医疗科技有限公司",
            // 麻醉深度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_DEPTH_MONITOR),
            "YY106",
            "192.168.8.31",
            45678,
            0,
            20,
            "UDP"),

    /**
     * 宝莱特 A8
     */
    BAO_LAI_TE_A8(32,
            "宝莱特 A8 无创血压/血红蛋白",
            "广东宝莱特医用科技股份有限公司",
            // 无创血压监测仪+无创血红蛋白监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BLOOD_PRESSURE_MONITOR, DeviceTypeEnum.HEMOGLOBIN_MONITOR),
            "A8",
            "192.168.8.32",
            45679,
            8002,
            2500,
            "UDP"),

    /**
     * 宜安麻醉机 8700A
     */
    YI_AN_8700A(33,
            "宜安 8700A 麻醉机",
            "北京谊安医疗系统股份有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "8700A",
            "192.168.8.33",
            9101,
            0,
            500,
            "UDP"),


    //********************************** TCP仪器 ***********************************************************//

    /**
     * 迈瑞 BeneViewT8
     */
    MAI_RUI_T8(42,
            "迈瑞 T8 无创血压/监护仪",
            "深圳迈瑞生物医疗电子股份有限公司",
            // 无创血压监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BLOOD_PRESSURE_MONITOR),
            "BeneView T8",
            "192.168.8.42",
            4601,
            4601,
            8000,
            "TCP"),


    /**
     * 迈瑞 WATOEX65麻醉机
     */
    MAI_RUI_WATOEX65(43,
            "迈瑞 WATOEX65/55Pro 麻醉机",
            "深圳迈瑞生物医疗电子股份有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "WATOEX65",
            "192.168.8.43",
            4601,
            0,
            2000,
            "TCP"),


    /**
     * 理邦 EliteV8
     */
    LI_BANG_ELITEV8(45,
            "理邦 Elite-V8 无创血压/监护仪",
            "深圳市理邦精密仪器股份有限公司",
            // 无创血压监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BLOOD_PRESSURE_MONITOR),
            "Elite-V8",
            "192.168.8.45",
            9100,
            0,
            2000,
            "TCP"),


    //********************************** 串口仪器 ***********************************************************//

    /**
     * 爱琴 EGOS-600A
     */
    AI_QIN_EGOS600A(71,
            "爱琴 EGOS-600A 血红蛋白/脑氧",
            "苏州爱琴生物医疗电子有限公司",
            // 无创血红蛋白监测仪+无创脑氧饱和度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.HEMOGLOBIN_MONITOR, DeviceTypeEnum.BRAIN_OXYGEN_MONITOR),
            "EGOS-600A",
            "192.168.8.100",
            10000,
            0,
            500,
            "Serial"),


    /**
     * 爱琴 EGOS-600B
     */
    AI_QIN_EGOS600B(72,
            "爱琴 EGOS-600B 血红蛋白/脑氧",
            "苏州爱琴生物医疗电子有限公司",
            // 无创血红蛋白监测仪+无创脑氧饱和度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.HEMOGLOBIN_MONITOR, DeviceTypeEnum.BRAIN_OXYGEN_MONITOR),
            "EGOS-600B",
            "192.168.8.100",
            10000,
            0,
            400,
            "Serial"),

    /**
     * 爱琴 EGOS-600C
     */
    AI_QIN_EGOS600C(73,
            "爱琴 EGOS-600C 脑氧",
            "苏州爱琴生物医疗电子有限公司",
            // 无创脑氧饱和度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BRAIN_OXYGEN_MONITOR),
            "EGOS-600C",
            "192.168.8.100",
            10000,
            0,
            300,
            "Serial"),


    /**
     * 名希 MNIR-P100
     */
    MING_XI_MNIR_P100(74,
            "名希 MNIR-P100 脑氧",
            "重庆名希医疗器械有限公司",
            // 无创脑氧饱和度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BRAIN_OXYGEN_MONITOR),
            "MNIR-P100",
            "192.168.8.100",
            10000,
            0,
            200,
            "Serial"),


    /**
     * 美敦力 EEG_VISTA(186-1046)
     */
    MEI_DUN_LI_EEG_VISTA(75,
            "美敦力 186-1046 麻醉深度",
            "美国美敦力公司",
            // 麻醉深度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_DEPTH_MONITOR),
            "EEG-VISTA(186-1046)",
            "192.168.8.100",
            10000,
            0,
            500,
            "Serial"),


    /**
     * 美敦力 5100C
     */
    MEI_DUN_LI_5100C(76,
            "美敦力 5100C 血红蛋白",
            "美国美敦力公司",
            // 无创血红蛋白监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.HEMOGLOBIN_MONITOR),
            "5100C",
            "192.168.8.100",
            10000,
            0,
            500,
            "Serial"),


    //********************************** 未调试 ***********************************************************//

    /**
     * 科曼 AX700麻醉机
     */
    KE_MAN_AX700(1000000001,
            "科曼 AX700 麻醉机",
            "深圳市科曼医疗设备有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "AX700",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 航天长峰 ACM608B麻醉机
     */
    CHANG_FENG_ACM608B(1000000002,
            "航天长峰 ACM608B 麻醉机",
            "北京航天长峰股份有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "ACM608B",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 德尔格 Fabius Plus麻醉机
     */
    DRAGER_FABIUS_PLUS (1000000003,
            "德尔格 Fabius Plus 麻醉机",
            "德国德尔格公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "Fabius Plus",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 普博 Boaray700麻醉机
     */
    PU_BO_BOARAY700 (1000000004,
            "普博 Boaray700 麻醉机",
            "深圳市普博科技有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "Boaray700",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 晨伟 CWM-302麻醉机
     */
    CHEN_WEI_CWH302(1000000005,
            "晨伟 CWM-302 麻醉机",
            "南京晨伟医疗设备有限公司",
            // 麻醉机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_MACHINE),
            "CWM-302",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 迈瑞 SV300呼吸机
     */
    MAI_RUI_SV300 (1000000006,
            "迈瑞 SV300 呼吸机",
            "深圳迈瑞生物医疗电子股份有限公司",
            // 呼吸机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.RESPIRATOR_MACHINE),
            "SV300",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 宜安 VT5250呼吸机
     */
    YI_AN_VT5250 (1000000007,
            "谊安 VT5250 呼吸机",
            "北京谊安医疗系统股份有限公司",
            // 呼吸机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.RESPIRATOR_MACHINE),
            "VT5250",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 晨伟 CWH3020B呼吸机
     */
    CHEN_WEI_CWH3020B (1000000008,
            "晨伟 CWH3020B 呼吸机",
            "南京晨伟医疗设备有限公司",
            // 呼吸机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.RESPIRATOR_MACHINE),
            "CWH3020B",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 舒普思达 S1200呼吸机
     */
    SHU_PU_SI_DA_S1200 (1000000009,
            "舒普思达 S1200 呼吸机",
            "南京舒普思达医疗设备有限公司",
            // 呼吸机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.RESPIRATOR_MACHINE),
            "S1200",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 德尔格 V300呼吸机
     */
    DRAGER_V300 (1000000010,
            "德尔格 V300 呼吸机",
            "德国德尔格公司",
            // 呼吸机
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.RESPIRATOR_MACHINE),
            "V300",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 科曼 C90 无创血压监测仪
     */
    KE_MAN_C90 (1000000011,
            "科曼 C90 无创血压/监护仪",
            "深圳市科曼医疗设备有限公司",
            // 无创血压监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BLOOD_PRESSURE_MONITOR),
            "C90",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 飞利浦 MX系列 无创血压监测仪
     */
    PHILIPS_MX (1000000012,
            "飞利浦 MX系列 无创血压/监护仪",
            "荷兰皇家飞利浦公司",
            // 无创血压监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.BLOOD_PRESSURE_MONITOR),
            "MX",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    /**
     * 太极 TD3200A麻醉深度监测仪
     */
    TAI_JI_TD3200A (1000000013,
            "太极 TD3200A 麻醉深度",
            "深圳市太极医疗科技有限公司",
            // 麻醉深度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_DEPTH_MONITOR),
            "TD3200A",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * 威浩康 Angle600D麻醉深度监测仪
     */
    WEI_HAO_KANG_ANGEL6000D (1000000014,
            "威浩康 ANGEL6000D 麻醉深度",
            "深圳市威浩康医疗器械有限公司",
            // 麻醉深度监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.ANESTHESIA_DEPTH_MONITOR),
            "ANGEL6000D",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),


    /**
     * MASIMO Radical7无创血红蛋白监测仪
     */
    MASIMO_RADICAL7 (1000000015,
            "MASIMO Radical7 血红蛋白",
            "美国MASIMO公司",
            // 血红蛋白监测仪
            DeviceTypeEnum.getTypeString(DeviceTypeEnum.HEMOGLOBIN_MONITOR),
            "Radical7",
            "192.168.8.XXX",
            0,
            0,
            0,
            "XXXX"),

    ;

    /**
     * 仪器号
     */
    Integer deviceCode;

    /**
     * 仪器简要名称(用于仪器选择时的卡片展示)
     */
    String deviceShortName;

    /**
     * 公司名称(公司名称+仪器名称=最官方的名称)
     */
    String companyName;

    /**
     * 仪器名称
     */
    String deviceName;

    /**
     * 仪器类别
     */
    String deviceType;
    /**
     * IP地址
     */
    String ipAddress;

    /**
     * 接收端口
     */
    Integer receivePort;

    /**
     * 发送端口
     */
    Integer sendPort;

    /**
     * 数据接收缓冲区长度
     */
    Integer receiveBufferLength;

    /**
     * 仪器接口类型
     */
    String interfaceType;


    DeviceEnum(Integer deviceCode, String deviceShortName, String companyName, String deviceType,
               String deviceName, String ipAddress, Integer receivePort,
               Integer sendPort, Integer receiveBufferLength, String interfaceType) {
        this.deviceCode = deviceCode;
        this.deviceShortName = deviceShortName;
        this.companyName = companyName;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.receivePort = receivePort;
        this.sendPort = sendPort;
        this.receiveBufferLength = receiveBufferLength;
        this.interfaceType = interfaceType;
    }
}
