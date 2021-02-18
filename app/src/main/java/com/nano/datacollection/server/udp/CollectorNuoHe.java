package com.nano.datacollection.server.udp;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.nano.AppStatic;
import com.nano.device.MedicalDevice;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;

import java.net.InetSocketAddress;

/**
 * 诺和采集器
 * @author cz
 */
public class CollectorNuoHe implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("CollectorNuoHe");

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
    public CollectorNuoHe(MedicalDevice medicalDevice) {
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
            logger.info("诺和接收端口监听打开:" + medicalDevice.getDeviceName());
        } catch (Exception e) {
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_OPEN_SOCKET, medicalDevice.getDeviceCode(), "无法打开端口"));
        }
        // 处理接收到的数据
        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bufferList) {
                // 获取数据字节缓存
                byte[] dataBuf = bufferList.getAllByteArray();
                // 转换为16进制字符串
                String hexData = CollectionUtils.getBufHexStr(dataBuf);
                logger.debug("诺和NW9002S收到数据:" + hexData);
                // 解析仪器数据
//                medicalDevice.parseAndBroadcastDeviceData(hexData);
            }
        });

        // 关闭连接
        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[诺和NW9002S] Successfully closed connection");
            }
        });

        // 完成连接
        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[诺和NW9002S] Successfully end connection");
            }
        });
    }

    /**
     * 停止采集
     */
    @Override
    public void stopCollection() {
        // 关闭端口
        asyncDatagramSocket.close();
    }

    /**
     * 重启采集器
     */
    @Override
    public void resetCollector() {

    }
}
