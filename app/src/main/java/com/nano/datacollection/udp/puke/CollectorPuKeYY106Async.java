package com.nano.datacollection.udp.puke;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.nano.AppStatic;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.datacollection.Collector;
import com.nano.device.MedicalDevice;

import java.net.InetSocketAddress;

/**
 * Description: 普可YY106采集器 异步采集(暂时不行)
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 10:49
 */
@Deprecated
public class CollectorPuKeYY106Async implements Collector {
    /**
     * Logger
     */
    private Logger logger = new Logger("PuKeYY106CollectorAsync");

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
     * 构造器
     *
     * @param medicalDevice 使用的仪器
     */
    public CollectorPuKeYY106Async(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }

    /**
     * 开始采集
     */
    @Override
    public void startCollection() {
        // 初始化
        this.host = new InetSocketAddress(AppStatic.COLLECTOR_LOCAL_IP, medicalDevice.getReceivePort());
        setup();
    }


    /**
     * 初始化
     */
    private void setup() {
        try {
            // 开启端口监听
            asyncDatagramSocket = AsyncServer.getDefault().openDatagram(host, true);
            logger.info("普可YY106接收端口打开:" + medicalDevice.getDeviceName());
        } catch (Exception e) {
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_OPEN_SOCKET, medicalDevice.getDeviceCode(), "无法打开端口"));
        }

        // 处理接收到的数据
        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                // 获取数据字节缓存
                byte[] dataBuf = bb.getAllByteArray();
                // 获取仪器数据 普可的数据不需要转换为16进制
                String deviceData = new String(dataBuf).trim();
                logger.debug("普可YY106收到数据:" + deviceData);
                // 解析仪器数据
//                medicalDevice.parseAndBroadcastDeviceData(deviceData);
            }
        });

        // 关闭连接
        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[普可YY106] Successfully closed connection");
            }
        });

        // 完成连接
        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[普可YY106] Successfully end connection");
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
