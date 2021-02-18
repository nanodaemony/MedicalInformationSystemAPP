package com.nano.datacollection.udp.puke;


import com.nano.common.logger.Logger;
import com.nano.datacollection.Collector;
import com.nano.device.MedicalDevice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Description: 普可YY106采集器 同步采集
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 10:49
 */
public class CollectorPuKeYY106 implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("PuKeYY106Collector");

    /**
     * 监听线程
     */
    private Thread listenThread;

    /**
     * UDP数据报服务器 需关闭
     */
    private DatagramSocket collectorSocket;

    /**
     * 使用的医疗仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 构造器
     */
    public CollectorPuKeYY106(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    @Override
    public void startCollection() {

        listenThread = new Thread(listenWork);
        listenThread.setName(medicalDevice.getDeviceName());
        listenThread.start();
    }


    /**
     * 数据接收线程
     */
    private Runnable listenWork = new Runnable() {
        @Override
        public void run() {
            logger.info("数据采集线程开启:" + medicalDevice.getDeviceName() + ": " + medicalDevice.getReceivePort());
            try {
                // 创建socket
                collectorSocket = new DatagramSocket(medicalDevice.getReceivePort());
            } catch (SocketException e) {
                e.printStackTrace();
                logger.error(medicalDevice.getDeviceName() + ":开启端口异常");
            }

            // 循环接收数据
            while (true){
                try {
                    // Buffer的最大长度值
                    byte[] data = new byte[medicalDevice.getReceiveBufferLength()];
                    // 构造packet
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                    // 此方法在接收到数据报之前会一直阻塞，接收到数据后继续执行
                    collectorSocket.receive(dataPacket);

                    // 以下是接收到数据之后的逻辑
                    // 从packet中获取值并存入字符数组中
                    byte[] bytes = dataPacket.getData();
                    // 获取仪器数据
                    String deviceData = new String(bytes).trim();
                    // 广播数据
                    medicalDevice.parseAndUploadDeviceData(deviceData);

                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(medicalDevice.getDeviceName() + "数据接收异常");
                    break;
                }
            }

            if (collectorSocket != null) {
                try {
                    // 关闭资源
                    collectorSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    @Override
    public void stopCollection() {

        if (collectorSocket != null) {
            if (collectorSocket.isClosed()) {
                collectorSocket.close();
            }
            collectorSocket = null;
        }

        if (listenThread != null) {
            listenThread.interrupt();
            listenThread = null;
        }

    }

    @Override
    public void resetCollector() {
        stopCollection();
        startCollection();
    }


}
