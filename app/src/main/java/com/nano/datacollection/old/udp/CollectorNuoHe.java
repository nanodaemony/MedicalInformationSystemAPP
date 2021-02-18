package com.nano.datacollection.old.udp;


import com.nano.device.MedicalDevice;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * 诺和数据采集器
 * @author cz
 */
public class CollectorNuoHe implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("NuoHeCollector");

    /**
     * 监听线程
     */
    private Thread listenThread;

    /**
     * UDP数据报服务器 需关闭
     */
    private DatagramSocket collectorSocket;


    private MedicalDevice medicalDevice;

    /**
     * 构造器 传入仪器信息
     */
    public CollectorNuoHe(MedicalDevice medicalDevice) {
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

                    // 诺和需要转换成十六进制
                    // bytes转换为16进制的字符串 收到的是2倍长度的字符串！
                    String hexData = CollectionUtils.getBufHexStr(bytes);

                    // 解析并广播数据
                    // medicalDevice.parseAndBroadcastDeviceData(hexData);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(medicalDevice.getDeviceName() + "数据接收异常");
                    break;
                }
            }
            // 关闭资源
            collectorSocket.close();
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
