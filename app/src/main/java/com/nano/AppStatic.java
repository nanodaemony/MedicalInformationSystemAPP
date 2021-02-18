package com.nano;

import com.nano.device.MedicalDevice;
import com.nano.activity.devicedata.healthrecord.CollectionBasicInfoEntity;
import com.nano.common.logger.LogLevelEnum;
import com.nano.common.util.CommonUtil;
import com.nano.http.ServerPathEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;

/**
 * 关于监测一些全局静态变量
 * @author cz
 */
public class AppStatic {

    /**
     * 静态变量初始化
     */
    public static void init(){
        macAddress = CommonUtil.getDeviceMacAddress();
        collectionBasicInfoEntity = new CollectionBasicInfoEntity();
        medicalDeviceMap = new HashMap<>();
    }


    // *************************** 需要进行配置的 *******************************************//

    /**
     * 调试
     */
    public static boolean debug = true;

    /**
     * 默认的等级为普通信息
     */
    @Getter
    public static LogLevelEnum logLevelEnum = LogLevelEnum.DEBUG;

    /**
     * 服务器默认路径(修改这个即可)
     */
    public static ServerPathEnum serverPathEnum = ServerPathEnum.CLOUD_SERVER_PROD;

    /**
     *  采集器本地IP地址
     */
    public static String COLLECTOR_LOCAL_IP = "192.168.8.75";


    // *************************** 不需要进行配置的 *******************************************//

    /**
     * 配置文件
     */
    public static Properties properties;

    /**
     * 当前的网络状态
     */
    public static boolean isNetworkConnected = false;

    /**
     * 设备MAC地址
     */
    public static String macAddress;

    /**
     * 手术场次号
     */
    public static int operationNumber = 0;

    /**
     * 采集基本信息：病人信息、手术信息、仪器信息
     */
    public static CollectionBasicInfoEntity collectionBasicInfoEntity;

    /**
     * 存放全部仪器信息的MAP
     */
    public static Map<Integer, MedicalDevice> medicalDeviceMap;

    /**
     * 调试信息日志数量
     */
    public static final int DEBUG_LOG_SIZE = 100;

    /**
     * 有效信息日志数量
     */
    public static final int INFO_LOG_SIZE = 200;

    /**
     * 错误日志数量
     */
    public static final int ERROR_LOG_SIZE = 100;

}
