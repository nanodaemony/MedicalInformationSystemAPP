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
 * 宜安8700A采集器
 * @author Administrator
 */
public class CollectorYiAn8700A implements Collector {


    /**
     * Logger
     */
    private Logger logger = new Logger("YiAnCollector");

    /**
     * 监听线程
     */
    private Thread listenThread;

    /**
     * UDP数据报服务器 需关闭
     */
    private DatagramSocket collectorSocket;

    /**
     * 接收循环计数器
     */
    private int cycleCounter = 0;

    /**
     * 接收缓冲区
     */
    private StringBuilder receiveBuffer;


    /**
     * 使用的仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 构造器
     */
    public CollectorYiAn8700A(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }



    @Override
    public void startCollection() {

        listenThread = new Thread(listenWork);
        listenThread.setName(medicalDevice.getDeviceName());
        listenThread.start();
        receiveBuffer = new StringBuilder();
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

                    // 剪切掉0之后的真正的数据
                    String trimData = CollectionUtils.trimZero(hexData);

                    cycleCounter++;

                    receiveBuffer.append(trimData).append("%");

                    if (cycleCounter >= 4) {
                        // 广播数据
//                        medicalDevice.parseAndBroadcastDeviceData(receiveBuffer.toString());

                        // 重置计数器和缓冲区
                        receiveBuffer.setLength(0);
                        cycleCounter = 0;
                    }

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
