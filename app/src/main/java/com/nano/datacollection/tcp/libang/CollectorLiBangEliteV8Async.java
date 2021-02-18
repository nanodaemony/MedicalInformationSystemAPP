package com.nano.datacollection.tcp.libang;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.net.InetSocketAddress;

/**
 * Description: 理邦EliteV8采集器 异步
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 14:32
 */
public class CollectorLiBangEliteV8Async implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("LiBangEliteV8CollectorAsync");

    /**
     * Used device
     */
    private MedicalDevice medicalDevice;

    /**
     * 用于寄存接收数据的寄存器
     */
    private StringBuilder dataBuilder = new StringBuilder();

    private Thread collectionThread;

    /**
     * 构造器
     *
     * @param medicalDevice 传入使用的仪器
     */
    public CollectorLiBangEliteV8Async(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }

    /**
     * 任务
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 不断判断采集开关是否开着
            while (medicalDevice.isCollectorSwitchOn()) {
                try {
                    // 1秒查询一次
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("开始连接:" + medicalDevice.getDeviceName());
                AsyncServer.getDefault().connectSocket(new InetSocketAddress(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort()), new ConnectCallback() {
                    @Override
                    public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                        // 处理连接完成
                        handleConnectCompleted(ex, socket);
                    }
                });
            }
        }
    };


    @Override
    public void startCollection() {
        medicalDevice.setCollectorSwitchOn(true);
        collectionThread = new Thread(runnable);
        collectionThread.start();
    }


    /**
     * 处理完成连接
     *
     * @param ex     异常
     * @param socket 端口
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {

        if (ex != null) {
            if (socket != null) {
                socket.close();
            }
            logger.error("连接错误:" + ex.toString());
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "仪器连接失败"));
            return;
        }

        // 接收到消息的情况
        socket.setDataCallback((emitter, bb) -> {
            // 获取数据字节缓存
            byte[] dataBuf = bb.getAllByteArray();
            String hexData = CollectionUtils.getBufHexStr(dataBuf);
            String trimData = CollectionUtils.trimZero(hexData);
            String realHexData = getRealData(trimData);
            String realData = CollectionUtils.hexStringToString(realHexData);
            logger.debug("收到:" + hexData);
            medicalDevice.parseAndUploadDeviceData(realData);
            // 收到一次数据之后关闭连接
            socket.close();
        });

        // Socket关闭的情况
        socket.setClosedCallback(ex1 -> {
            if (ex1 != null) throw new RuntimeException(ex1);
            logger.debug("[Client] Successfully closed connection");
        });

        socket.setEndCallback(ex12 -> {
            if (ex12 != null) throw new RuntimeException(ex12);
            logger.debug("[Client] Successfully end connection");
        });
    }


    /**
     * 发送消息
     *
     * @param socket  socket
     * @param message 消息字符串
     */
    private void sendMessage(AsyncSocket socket, final String message) {
        // 通过socket发送一个消息
        Util.writeAll(socket, message.getBytes(), ex -> {
            if (ex != null) {
                EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "发送消息失败"));
            }
        });
    }


    @Override
    public void stopCollection() {
        try {
            // 关闭采集器开关
            medicalDevice.setCollectorSwitchOn(false);
            if (collectionThread != null) {
                collectionThread.isInterrupted();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void resetCollector() {

    }


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


}
