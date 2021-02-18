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
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.common.threadpool.ThreadPoolUtils;

import java.net.InetSocketAddress;

/**
 * 理邦EliteV8监护仪采集器
 * @author cz
 */
public class CollectorLiBangEliteV8 implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("CollectorMaiRuiT8");

    /**
     * Used device
     */
    private MedicalDevice medicalDevice;

    /**
     * 用于寄存接收数据的寄存器
     */
    private StringBuilder dataBuilder = new StringBuilder();

    /**
     * 构造器
     *
     * @param medicalDevice 传入使用的仪器
     */
    public CollectorLiBangEliteV8(MedicalDevice medicalDevice) {
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
        // 执行任务
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
            if (socket != null) {
                socket.close();
            }
            logger.error("连接错误:" + ex.toString());
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_CONNECT_DEVICE, medicalDevice.getDeviceCode(), "仪器连接失败"));
            return;
        }

        // 接收到消息的情况
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                // 获取数据字节缓存
                byte[] dataBuf = bb.getAllByteArray();

                String hexData = CollectionUtils.getBufHexStr(dataBuf);

                String trimData = CollectionUtils.trimZero(hexData);
                String realHexData = getRealData(trimData);
                String realData = CollectionUtils.hexStringToString(realHexData);
                logger.debug("收到:" + hexData);
//                medicalDevice.parseAndBroadcastDeviceData(realData);

                // 收到一次数据之后关闭连接
                socket.close();
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

        // 关闭采集器开关
        medicalDevice.setCollectorSwitchOn(false);

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
