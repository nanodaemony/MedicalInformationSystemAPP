package com.nano.datacollection.tcp.mairuit8;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.Util;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.net.InetSocketAddress;

/**
 * Description: 迈瑞BeneViewT8采集器 异步
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 12:38
 */
public class CollectorMaiRuiBeneViewT8 implements Collector {



    private Logger logger = new Logger("CollectorMaiRuiT8");

    /**
     * Used device
     */
    private MedicalDevice medicalDevice;

    /**
     * AsyncSocket
     */
    private AsyncSocket connectionSocket;


    private Thread collectionThread;

    /**
     * 用于寄存接收数据的寄存器
     */
    private StringBuilder dataBuilder = new StringBuilder();

    /**
     * 构造器
     *
     * @param medicalDevice 传入使用的仪器
     */
    public CollectorMaiRuiBeneViewT8(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }

    /**
     * 任务
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
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort()), (ex, socket) -> {
                // 处理连接完成
                handleConnectCompleted(ex, socket);
            });
        }
    };


    @Override
    public void startCollection() {
        collectionThread = new Thread(runnable);
        collectionThread.start();
        // 开启开关
        medicalDevice.setCollectorSwitchOn(true);
    }

    /**
     * 处理完成连接
     *
     * @param ex 异常
     * @param socket 端口
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) {
        if(ex != null) {
            // 注意:这里如果没有连网线就开始采集的话,会出现异常
            logger.error("连接错误:" + ex.toString());
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "仪器连接失败"));
            if (socket != null) {
                socket.close();
            }
            return;
        }

        this.connectionSocket = socket;

        // 接收到消息的情况
        socket.setDataCallback((emitter, bb) -> {
            // 获取数据字节缓存
            byte[] dataBuf = bb.getAllByteArray();
            // 转换为16进制字符串
            String hexData = CollectionUtils.getBufHexStr(dataBuf);
            logger.debug("迈瑞T8收到:" + hexData);
            // 发送心跳信息
            sendMessageToDevice(socket, DeviceAccessCommands.MAI_RUI_HEART_MESSAGE);

            if (hexData != null) {
                // 说明是一条完整的数据
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
                        // 解析数据
                        medicalDevice.parseAndUploadDeviceData(dataBuilder.toString());
                        // 重置数据缓存
                        dataBuilder.setLength(0);
                    }
                }
            }
            // 每隔一段时间发送一次心跳信息
            if (medicalDevice.getReceiveCounter() % 30 == 0) {
                sendMessageToDevice(socket, DeviceAccessCommands.MAI_RUI_QUERY_MONITOR_DATA);
            }
//
//            if (!medicalDevice.isCollectorSwitchOn()) {
//                emitter.close();
//            }
        });

        // Socket关闭的情况
        socket.setClosedCallback(ex1 -> {
            if(ex1 != null) throw new RuntimeException(ex1);
            logger.debug("[CollectorMaiRuiT8] Successfully closed connection");
        });

        socket.setEndCallback(ex12 -> {
            if(ex12 != null) throw new RuntimeException(ex12);
            logger.debug("[CollectorMaiRuiT8] Successfully end connection");
        });
    }


    /**
     * 发送消息
     *
     * @param socket socket
     * @param message 消息字符串
     */
    private void sendMessageToDevice(AsyncSocket socket, final String message) {
        // 通过socket发送一个消息
        // 发送消息之后的回调
        Util.writeAll(socket, message.getBytes(), ex -> {
            if (ex != null) {
                EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "发送消息失败"));
            }
        });
    }

    @Override
    public void stopCollection() {
        try {
//            medicalDevice.setCollectorSwitchOn(false);
            // 关闭端口
            connectionSocket.getServer().stop();
            connectionSocket.close();
            connectionSocket.end();
            connectionSocket = null;
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
