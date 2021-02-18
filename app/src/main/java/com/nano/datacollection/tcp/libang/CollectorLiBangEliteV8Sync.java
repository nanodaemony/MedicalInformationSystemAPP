package com.nano.datacollection.tcp.libang;

import com.nano.common.logger.Logger;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description: 理邦EliteV8采集器 同步
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 14:36
 */
public class CollectorLiBangEliteV8Sync implements Collector {
    /**
     * Logger
     */
    private Logger logger = new Logger("LiBangEliteV8CollectorSync");

    /**
     * 发送线程
     */
    private Thread sendThread;


    /**
     * 客户端端口
     */
    private Socket connectSocket;

    /**
     * 输入输出流
     */
    private OutputStream sendStream;
    private InputStream receiveStream;

    /**
     * 使用的仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 构造器
     */
    public CollectorLiBangEliteV8Sync(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    @Override
    public void startCollection() {
        sendThread = new Thread(listenWork);
        sendThread.setName(medicalDevice.getDeviceName());
        sendThread.start();
    }


    /**
     * 监听任务
     */
    private Runnable listenWork = new Runnable() {
        @Override
        public void run() {
            logger.info("理邦Elite V8接收线程等待中:....");
            while (true) {
                try {
                    // 获取一次连接(理邦有点特殊,每次都需要再次连接.)
                    getOnceConnection();
                    // 接收Buffer长度在此设置
                    final byte[] buf = new byte[medicalDevice.getReceiveBufferLength()];
                    final int len = receiveStream.read(buf);
                    if (len != -1) {
                        // 获取16进制数据
                        String hexData = CollectionUtils.getBufHexStr(buf);
                        String trimData = CollectionUtils.trimZero(hexData);
                        String realHexData = getRealData(trimData);
                        // 将16进制转换为HL7的XML格式数据
                        String realData = CollectionUtils.hexStringToString(realHexData);
                        medicalDevice.parseAndUploadDeviceData(realData);
                        // 一次请求之后释放连接
                        releaseConnection();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    /**
     * 去掉HL7协议的头部和尾部
     *
     * @param trimData 数据
     * @return 真实的数据
     */
    public static String getRealData(String trimData) {

        if (trimData.startsWith("0B") && trimData.endsWith("1C0D")) {
            return trimData.substring(2, trimData.length() - 4);
        } else {
            return trimData;
        }
    }

    /**
     * 获取连接
     */
    private void getOnceConnection() {
        if (connectSocket == null) {
            try {
                // 根据IP与端口号连接仪器服务器
                connectSocket = new Socket(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (receiveStream == null) {
            try {
                receiveStream = connectSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sendStream == null) {
            try {
                sendStream = connectSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 释放一次连接
     */
    private void releaseConnection() {

        try {
            // 1秒查询一次
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sendStream != null) {
            try {
                sendStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendStream = null;
        }

        if (receiveStream != null) {
            try {
                receiveStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            receiveStream = null;
        }

        if (connectSocket != null) {
            try {
                connectSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            connectSocket = null;
        }
    }


    @Override
    public void stopCollection() {
        try {
            sendStream.close();
            receiveStream.close();
            connectSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sendThread.interrupt();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetCollector() {
        stopCollection();
        startCollection();
    }


}
