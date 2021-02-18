package com.nano.datacollection.server.udp;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.nano.device.MedicalDevice;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;

import java.net.InetSocketAddress;

/**
 * 宜安8700A麻醉机采集器
 * @author cz
 */
public class CollectorYiAn implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("CollectorYiAn");

    /**
     * 使用的医疗仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 本地主机
     */
    private InetSocketAddress host;

    /**
     * 异步端口
     */
    private AsyncDatagramSocket asyncDatagramSocket;


    /**
     * 接收循环计数器
     */
    private int cycleCounter = 0;

    /**
     * 接收缓冲区
     */
    private StringBuilder receiveBuffer = new StringBuilder();



    /**
     * 构造器
     *
     * @param medicalDevice 使用的仪器
     */
    public CollectorYiAn(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    /**
     * 开始采集
     */
    @Override
    public void startCollection() {
        // 初始化
        this.host = new InetSocketAddress("192.168.8.75", medicalDevice.getReceivePort());
        setup();
    }


    /**
     * 初始化
     */
    private void setup() {

        try {
            // 开启端口监听
            asyncDatagramSocket = AsyncServer.getDefault().openDatagram(host, true);

            logger.info("接收端口打开:" + medicalDevice.getDeviceName());
        } catch (Exception e) {
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_OPEN_SOCKET, medicalDevice.getDeviceCode(), "无法打开端口"));
        }

        // 处理接收到的数据
        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                // 获取数据字节缓存
                byte[] dataBuf = bb.getAllByteArray();

                // 转换为16进制字符串
                String hexData = CollectionUtils.getBufHexStr(dataBuf);

                // 剪切掉0之后的真正的数据
                String trimData = CollectionUtils.trimZero(hexData);

                // 循环接收计数器增加
                cycleCounter++;

                receiveBuffer.append(trimData).append("%");

                if (cycleCounter >= 4) {
                    // 广播数据
//                    medicalDevice.parseAndBroadcastDeviceData(receiveBuffer.toString());

                    // 重置计数器和缓冲区
                    receiveBuffer.setLength(0);
                    cycleCounter = 0;
                }
            }
        });

        // 关闭连接
        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        // 完成连接
        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully end connection");
            }
        });
    }


    @Override
    public void stopCollection() {
        // 关闭端口
        asyncDatagramSocket.close();
    }

    @Override
    public void resetCollector() {

    }
}
