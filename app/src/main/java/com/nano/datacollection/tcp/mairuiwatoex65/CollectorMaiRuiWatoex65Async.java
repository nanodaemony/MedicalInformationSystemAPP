package com.nano.datacollection.tcp.mairuiwatoex65;

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
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.net.InetSocketAddress;

/**
 * Description: 迈瑞WATOEX65采集器 异步
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 12:41
 */
public class CollectorMaiRuiWatoex65Async implements Collector {


    private Logger logger = new Logger("[MaiRuiWatoex65CollectorAsync]");

    /**
     * Used device
     */
    private MedicalDevice medicalDevice;

    /**
     * AsyncSocket
     */
    private AsyncSocket connectionSocket;


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
    public CollectorMaiRuiWatoex65Async(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    /**
     * 连接任务
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug("采集器开始连接:" + medicalDevice.getDeviceName());
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort()), new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                    // 处理连接完成
                    handleConnectCompleted(ex, socket);
                }
            });
        }
    };


    @Override
    public void startCollection() {
        collectionThread = new Thread(runnable);
        collectionThread.start();
    }


    /**
     * 处理完成连接
     *
     * @param ex 异常
     * @param socket 端口
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {

        if(ex != null) {
            if (socket != null) {
                socket.close();
            }
            logger.error("连接错误:" + ex.toString());
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "仪器连接失败"));
            return;
        }

        this.connectionSocket = socket;

        // 连接成功就发送心跳信息
        sendMessage(connectionSocket, DeviceAccessCommands.MAI_RUI_HEART_MESSAGE);

        // 接收到消息的情况
        socket.setDataCallback((emitter, bb) -> {
            // 获取数据字节缓存
            byte[] dataBuf = bb.getAllByteArray();
            // 转换为16进制字符串
            String hexData = CollectionUtils.getBufHexStr(dataBuf);

            // 发送心跳信息
            sendMessage(socket, DeviceAccessCommands.MAI_RUI_HEART_MESSAGE);

            // 说明是一条完整的数据
            if (hexData != null) {
                if (hexData.startsWith("0B") && hexData.endsWith("1C0D")) {
                    // 直接传出去
                    medicalDevice.parseAndUploadDeviceData(hexData);
                } else {
                    if (hexData.startsWith("0B") && dataBuilder.length() == 0) {
                        dataBuilder.append(hexData);
                    } else if (!hexData.startsWith("0B") && !hexData.endsWith("1C0D")) {
                        dataBuilder.append(hexData);
                    } else if (!hexData.startsWith("0B") && hexData.endsWith("1C0D") && dataBuilder.length() != 0) {
                        dataBuilder.append(hexData);
                        medicalDevice.parseAndUploadDeviceData(hexData);
                        dataBuilder.setLength(0);
                    }
                }
            }
            // 发送心跳信息
            if (medicalDevice.getReceiveCounter() % 30 == 0) {
                sendMessage(socket, DeviceAccessCommands.MAI_RUI_QUERY_MONITOR_DATA);
            }
        });

        // Socket关闭的情况
        socket.setClosedCallback(ex1 -> {
            if(ex1 != null) throw new RuntimeException(ex1);
            logger.debug("[Client] Successfully closed connection");
        });

        socket.setEndCallback(ex12 -> {
            if(ex12 != null) throw new RuntimeException(ex12);
            logger.debug("[Client] Successfully end connection");
        });
    }


    /**
     * 发送消息
     *
     * @param socket socket
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
            // 关闭端口
            connectionSocket.close();
            connectionSocket.end();
            if (collectionThread != null) {
                collectionThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void resetCollector() {

    }

}
