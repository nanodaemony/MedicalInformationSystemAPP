package com.nano;

import com.nano.activity.User;
import com.nano.common.util.PersistUtil;
import com.nano.device.MedicalDevice;
import com.nano.activity.devicedata.healthrecord.CollectionBasicInfoEntity;
import com.nano.common.logger.LogLevelEnum;
import com.nano.common.util.CommonUtil;
import com.nano.http.ServerIpEnum;
import com.nano.share.Encryption;
import com.nano.share.rsa.RsaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // 生成用户
        user = new User();
        // 为用户生成数据分享密钥对
        Encryption encryption = new Encryption();
        user.setSharePublicKey(encryption.rsa.e);
        user.setSharePrivateKey(encryption.rsa.d);
        user.setPid(PersistUtil.getStringValue("PID"));
        user.setEncryption(encryption);

        // 产生数据加密RSA密钥对
        user.setRsaKeyPair(RsaUtils.generateRSAKeyPair());
        // 一来就生成TID给用户
        user.setTidEmr("AS" + System.currentTimeMillis() + RsaUtils.encryptDataLong(System.currentTimeMillis() + "", user.getRsaKeyPair().getPublic()).substring(0, 10));
        user.setTidPhr("AS" + System.currentTimeMillis() + RsaUtils.encryptDataLong(System.currentTimeMillis() + "", user.getRsaKeyPair().getPublic()).substring(0, 10));
        user.setTidShr("AS" + System.currentTimeMillis() + RsaUtils.encryptDataLong(System.currentTimeMillis() + "", user.getRsaKeyPair().getPublic()).substring(0, 10));
    }


    public static User user;

    // *************************** 需要进行配置的 *******************************************//

    /**
     * 调试
     */
    public static boolean debug = true;

    /**
     * 默认的等级为普通信息
     */
    @Getter
    public static LogLevelEnum logLevelEnum = LogLevelEnum.INFO;

    /**
     * 服务器默认路径(修改这个即可)
     */
    public static ServerIpEnum serverIpEnum = ServerIpEnum.LOCAL_WIFI;


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
     * 存放采集场次号的列表
     */
    public static List<Integer> collectionNumberList = new ArrayList<>();

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



    // 关于数据管理的全局变量
    public static WorkingModeEnum workingMode = WorkingModeEnum.DATA_MANAGEMENT;

    /**
     * 伪身份ID
     */
    public static String pid = "8912098GHSAIOJ87";
    public static String receiverPseudoId = "HDJK1780ASBC8912";
    public static String doctorPseudoId = "21435AJS328H23F5";


    /**
     * 治疗ID号(打开APP就自动生成)
     */
    public static String treatmentId = "";


    /**
     * EMR数据
     */
    public static String emrData = "";

    /**
     * 缓存采集的仪器数据SHR
     */
    public static StringBuilder deviceDataBuilder = new StringBuilder();



}
