package com.nano.datacollection.server.tcp;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.nano.device.MedicalDevice;
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.common.threadpool.ThreadPoolUtils;

import java.net.InetSocketAddress;

/**
 * 迈瑞WATOEX55Pro麻醉机
 * @author cz
 */
public class CollectorMaiRuiWatoex55Pro implements Collector {


    private Logger logger = new Logger("CollectorMaiRuiWatoex55Pro");

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

    /**
     * 构造器
     *
     * @param medicalDevice 传入使用的仪器
     */
    public CollectorMaiRuiWatoex55Pro(MedicalDevice medicalDevice) {
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

            logger.debug("开始连接:" + medicalDevice.getDeviceName());

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
        // 开始任务
        ThreadPoolUtils.executeNormalTask(runnable);
    }


    /**
     * 处理完成连接
     *
     * @param ex 异常
     * @param socket 端口
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {

        if(ex != null) {
            logger.error("连接错误:" + ex.toString());
            if (socket != null) {
                socket.close();
            }
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "仪器连接失败"));
            return;
        }

        this.connectionSocket = socket;

        // 连接成功就发送心跳信息
        sendMessage(connectionSocket, DeviceAccessCommands.MAI_RUI_HEART_MESSAGE);

        // 接收到消息的情况
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                // 获取数据字节缓存
                byte[] dataBuf = bb.getAllByteArray();

                // 转换为16进制字符串
                String hexData = CollectionUtils.getBufHexStr(dataBuf);

                logger.debug("收到:" + hexData);

                // 发送心跳信息
                sendMessage(socket, DeviceAccessCommands.MAI_RUI_HEART_MESSAGE);

                // 说明是一条完整的数据
                if (hexData != null && hexData.startsWith("0B") && hexData.endsWith("1C0D")) {
                    // 直接传出去
//                    medicalDevice.parseAndBroadcastDeviceData(hexData);
                } else {
                    if (hexData.startsWith("0B") && dataBuilder.length() == 0) {
                        dataBuilder.append(hexData);
                    } else if (!hexData.startsWith("0B") && !hexData.endsWith("1C0D")) {
                        dataBuilder.append(hexData);
                    } else if (!hexData.startsWith("0B") && hexData.endsWith("1C0D") && dataBuilder.length() != 0) {
                        dataBuilder.append(hexData);
                        logger.debug("数据长度:" + dataBuilder.length());
//                        medicalDevice.parseAndBroadcastDeviceData(dataBuilder.toString());
                        dataBuilder.setLength(0);
                    }
                }

                if (medicalDevice.getReceiveCounter() % 30 == 0) {
                    sendMessage(socket, DeviceAccessCommands.MAI_RUI_QUERY_MONITOR_DATA);
                }
            }
        });

        // Socket关闭的情况
        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                logger.debug("[Client] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                logger.debug("[Client] Successfully end connection");
            }
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
        Util.writeAll(socket, message.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "发送消息失败"));
                }
            }
        });
    }


    @Override
    public void stopCollection() {
        // 关闭端口
        connectionSocket.close();
    }


    @Override
    public void resetCollector() {

    }
}
