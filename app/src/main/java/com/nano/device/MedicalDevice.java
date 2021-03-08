package com.nano.device;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.nano.AppStatic;
import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationTable;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.common.logger.Logger;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.common.util.PersistUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医疗仪器实体类
 *
 * @author cz
 */
@Data
@NoArgsConstructor
public class MedicalDevice implements HttpHandler {

    private Logger logger = new Logger("[MedicalDevice]");

    private static final long serialVersionUID = 233410313766289238L;

    /**
     * 判定仪器断线的时间长度
     */
    private static final long DEVICE_OFF_LINE_TIME = 30000;

    /**
     * 仪器信息枚举
     */
    private DeviceEnum deviceEnum;

    /**
     * 仪器代号
     */
    private Integer deviceCode;

    /**
     * 仪器类型
     */
    private String deviceType;

    /**
     * 仪器名称
     */
    private String deviceName;

    /**
     * 仪器序列号
     */
    private String serialNumber;

    /**
     * 仪器使用年限
     */
    private Double serviceLife;

    /**
     * 接收端口
     */
    private int receivePort;

    /**
     * 发送信息端口
     */
    private int sendPort;

    /**
     * 接收缓冲区长度
     */
    private int receiveBufferLength;

    /**
     * IP 地址
     */
    private String deviceIpAddress;


    /**
     * 本次采集是否使用
     */
    private boolean deviceUsed;


    /**
     * 上一次接收到数据的时间
     */
    private long lastReceiveDataTime;

    /**
     * 仪器接收计数
     */
    private int receiveCounter = 0;

    /**
     * 成功上传的计数器
     */
    private int successfulUploadCounter = 0;

    /**
     * 采集开关
     */
    private boolean collectorSwitchOn;

    /**
     * 服务器分配的采集序列号
     */
    private Integer collectionNumber = 0;


    /**
     * 采集的状态
     */
    private CollectionStatusEnum statusEnum;

    /**
     * 仪器数据解析器
     */
    private DeviceDataParser dataParser;


    /**
     * 与Fragment进行数据交互
     */
    private FragmentDataExchanger fragmentDataExchanger;

    /**
     * 仪器评价表
     */
    private DeviceEvaluationTable deviceEvaluationTable;

    /**
     * 评价信息是否上传
     */
    private boolean evaluationTableUpdated;


    /**
     * 仪器的图片资源ID(用于获取该仪器的图片信息)
     */
    private int deviceImageSource;

    /**
     * 仪器上次使用的时间(用于展示排序)
     */
    private long deviceLastUseTime;

    /**
     * 网络上传器
     */
    private HttpManager httpManager;


    // 初始化块进行初始化操作
    {
        lastReceiveDataTime = System.currentTimeMillis();
        // 初始化为等待开始状态
        statusEnum = CollectionStatusEnum.WAITING_START;

        httpManager = new HttpManager(this);
    }

    /**
     * 传入枚举进行定义
     *
     * @param infoEnum 仪器信息
     */
    public MedicalDevice(DeviceEnum infoEnum) {
        this.deviceEnum = infoEnum;
        this.deviceCode = infoEnum.getDeviceCode();
        this.deviceType = infoEnum.getInterfaceType();
        this.deviceName = infoEnum.getDeviceName();
        this.receivePort = infoEnum.getReceivePort();
        this.sendPort = infoEnum.getSendPort();
        this.receiveBufferLength = infoEnum.getReceiveBufferLength();
        this.deviceIpAddress = infoEnum.getIpAddress();
        // 从缓存获取仪器上次使用的时间(用于排序)
        deviceLastUseTime = PersistUtil.getLongValue("DeviceLastUseTime" + deviceCode);
    }


    /**
     * 解析并上传原始仪器数据
     *
     * @param deviceRawData 仪器原始数据
     */
    public void parseAndUploadDeviceData(String deviceRawData) {
        // 只将正在采集状态的数据上传
        if (statusEnum == CollectionStatusEnum.COLLECTING) {
            // 使用解析器进行数据解析
            DeviceData deviceData = dataParser.parseData(this.deviceCode, this.collectionNumber, this.serialNumber, deviceRawData);
            // 打印解析后的数据实体
            receiveCounter++;
            // 进行仪器数据上传
            httpManager.postDeviceData(deviceData.getDeviceData());
            logger.debug("接收数据:" + deviceData.toString());

            // 通过消息巴士发送到采集界面进行存储到本地
            AppStatic.deviceDataBuilder.append(JSON.toJSONString(deviceData)).append("\n");
            logger.info("仪器数据：" + deviceData.toString());
            // fragmentDataExchanger.updateReceiveCounterAndDeviceData(receiveCounter, deviceData.getDataObject());
        }
    }


    /**
     * 检查是否采集暂停 暂停即已经有数据了但是中途停止了
     *
     * @return 是否暂停
     */
    public boolean checkPaused() {
        // 处于采集的状态才判断是否暂停
        // 接收计数器为0 说明还没有接收到数据
        if (receiveCounter == 0) {
            return false;
        } else {
            // 当前时间
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastReceiveDataTime > DEVICE_OFF_LINE_TIME) {
                logger.debug("仪器暂停接收数据:" + this.deviceName);
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * HTTP成功回调
     *
     * @param message 数据
     */
    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        logger.debug(deviceName + "服务器响应:" + message);
        switch (message.getPathEnum()) {
            // 收到仪器数据
            case POST_DEVICE_DATA:
                successfulUploadCounter++;
                // 通知Fragment进行数据UI更新上传成功计数器
                fragmentDataExchanger.updateSuccessfulUploadCounter(successfulUploadCounter);
                break;
        }
    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {
    }

    @Override
    public void handleNetworkFailedMessage() {
    }


    /**
     * 构造用于传输给服务器的医疗仪器信息
     */
    public String getMedicalDeviceInfoForServer() {
        return JSON.toJSONString(new InfoMedicalDevice(deviceCode, serialNumber, serviceLife));
    }


    /**
     * 用于上传到服务器的仪器信息类
     * @author cz
     */
    @Data
    private static class InfoMedicalDevice implements Serializable {

        private static final long serialVersionUID = 233410313766289238L;
        // 仪器号
        private Integer deviceCode;
        // 序列号
        private String serialNumber;
        // 生产日期
        private LocalDate produceDate;
        // 服务年限
        private Double serviceLife;

        public InfoMedicalDevice(Integer deviceCode, String serialNumber, Double serviceLife) {
            this.deviceCode = deviceCode;
            this.serialNumber = serialNumber;
            this.serviceLife = serviceLife;
        }
    }
}
