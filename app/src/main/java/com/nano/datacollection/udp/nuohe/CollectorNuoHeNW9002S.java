package com.nano.datacollection.udp.nuohe;


import com.nano.common.logger.Logger;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Description: 诺和NW9002S采集器 同步采集
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 10:48
 */
public class CollectorNuoHeNW9002S implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("[NuoHeNW9002SCollector]");

    private MedicalDevice medicalDevice;

    /**
     * 采集线程
     */
    private Thread collectionThread;

    /**
     * UDP数据报服务器 需关闭
     */
    private DatagramSocket socket;

    /**
     * 是否正在采集
     */
    private boolean isCollecting = false;


    /**
     * 构造器 传入仪器信息
     */
    public CollectorNuoHeNW9002S(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    /**
     * 开启采集器
     */
    @Override
    public void startCollection() {
        collectionThread = new Thread(listenWork);
        collectionThread.setName(medicalDevice.getDeviceName());
        collectionThread.start();
    }

    /**
     * 数据接收线程
     */
    private Runnable listenWork = new Runnable() {
        @Override
        public void run() {
            logger.info("数据采集线程开启:" + medicalDevice.getDeviceName() + ", 监听端口:" + medicalDevice.getReceivePort());
            try {
                // 创建socket
                socket = new DatagramSocket(medicalDevice.getReceivePort());
            } catch (SocketException e) {
                e.printStackTrace();
                logger.error(medicalDevice.getDeviceName() + ":开启端口异常");
            }
            isCollecting = true;
            // 循环接收数据
            while (true){
                try {
                    if (!isCollecting) break;
                    // Buffer的最大长度值
                    byte[] data = new byte[medicalDevice.getReceiveBufferLength()];
                    // 构造packet
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                    // 此方法在接收到数据报之前会一直阻塞,接收到数据后继续执行
                    socket.receive(dataPacket);
                    // 以下是接收到数据之后执行的逻辑
                    // 从packet中获取值并存入字符数组中
                    byte[] bytes = dataPacket.getData();
                    // 诺和需要转换成十六进制(bytes转换为16进制的字符串 收到的是2倍长度的字符串！)
                    String hexData = CollectionUtils.getBufHexStr(bytes);
                    // 本地调试发送的数据会在后面加上0,所以此处需要截取子串
                    if (hexData != null && hexData.length() > 262) {
                        hexData = hexData.substring(0, 262);
                    }
                    logger.debug("接收NuoHe:" + hexData);
                    // 解析并上传数据
                    medicalDevice.parseAndUploadDeviceData(hexData);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(medicalDevice.getDeviceName() + "数据接收异常,已停止工作.");
                    socket.close();
                    break;
                }
            }
            if (socket != null) {
                try {
                    // 关闭资源
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 停止采集器
     */
    @Override
    public void stopCollection() {
        isCollecting = false;
        try {
            if (socket != null) {
                if (socket.isClosed()) {
                    socket.close();
                }
                socket = null;
            }

            // 关闭线程
            if (collectionThread != null) {
                collectionThread.interrupt();
                collectionThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置采集器
     */
    @Override
    public void resetCollector() {
        stopCollection();
        startCollection();
    }

}
