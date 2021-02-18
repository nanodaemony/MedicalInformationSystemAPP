package com.nano.datacollection.old.tcp;


import com.nano.datacollection.tcp.mairuiwatoex65.ClientHandler;
import com.nano.device.MedicalDevice;
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.common.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * 迈瑞T8监护仪采集器
 * @author cz
 */
public class CollectorMaiRuiBeneViewT8 implements Collector {


    /**
     * Logger
     */
    private Logger logger = new Logger("MaiRuiT8Collector");


    /**
     * 监听线程
     */
    private Thread listenThread;


    /**
     * 发送线程
     */
    private Thread sendThread;


    /**
     * 客户端端口
     */
    private Socket connectSocket;


    /**
     * 输入输出流
     */
    private OutputStream sendStream;
    private InputStream receiveStream;


    /**
     * 用于寄存接收数据的寄存器
     */
    private StringBuilder dataBuilder = new StringBuilder();

    /**
     * Netty 时间循环组
     */
    private EventLoopGroup group;

    /**
     * 使用的仪器
     */
    private MedicalDevice medicalDevice;


    /**
     * 构造器
     */
    public CollectorMaiRuiBeneViewT8(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    @Override
    public void startCollection() {

        group = new NioEventLoopGroup();

        listenThread = new Thread(listenWork);
        listenThread.setName(medicalDevice.getDeviceName() + "接收");
        listenThread.start();

        sendThread = new Thread(sendWork);
        sendThread.setName(medicalDevice.getDeviceName() + "发送");
        sendThread.start();
    }



    /**
     * 监听任务
     */
    private Runnable listenWork = new Runnable() {
        @Override
        public void run() {

            logger.info("迈瑞接收线程等待中:....");

            while (true) {

                getConnection();
                try {

                    // 接收Buffer长度在此设置
                    final byte[] buf = new byte[medicalDevice.getReceiveBufferLength()];
                    final int len = receiveStream.read(buf);

                    if (len != -1) {
                        // 原始数据字符串
//                        final String text = new String(buf, 0, len);
                        // 获取16进制数据
                        String hexData = CollectionUtils.getBufHexStr(buf);

                        logger.debug(hexData);

                        String trimData = CollectionUtils.trimZero(hexData);

                        // 说明是一条完整的数据
                        if (trimData.startsWith("0B") && trimData.endsWith("1C0D")) {
                            // 直接传出去
//                            medicalDevice.parseAndBroadcastDeviceData(trimData);
                        } else {
                            if (trimData.startsWith("0B") && dataBuilder.length() == 0) {
                                dataBuilder.append(trimData);
                            } else if (!trimData.startsWith("0B") && !trimData.endsWith("1C0D")) {
                                dataBuilder.append(trimData);
                            } else if (!trimData.startsWith("0B") && trimData.endsWith("1C0D") && dataBuilder.length() != 0) {
                                dataBuilder.append(trimData);
                                logger.debug("数据长度:" + dataBuilder.length());
//                                medicalDevice.parseAndBroadcastDeviceData(dataBuilder.toString());
                                dataBuilder.setLength(0);
                            }
                        }

                        sendStream.write(DeviceAccessCommands.MAI_RUI_HEART_MESSAGE.getBytes());
                        logger.debug("发送心跳包");

                        if (medicalDevice.getReceiveCounter() % 20 == 0) {
                            sendStream.write(DeviceAccessCommands.MAI_RUI_QUERY_MONITOR_DATA.getBytes());
                            logger.info("发送迈瑞请求参数指令.........");
                        }
                        logger.debug("缓存长度:" + dataBuilder.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    };


    /**
     * 发送任务
     */
    private Runnable sendWork = new Runnable() {
        @Override
        public void run() {

            logger.info("迈瑞发送线程工作中:....");
            try {
                Bootstrap clientStrap = new Bootstrap();
                clientStrap.group(group);
                clientStrap.channel(NioSocketChannel.class);
                clientStrap.remoteAddress(new InetSocketAddress(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort()));
                clientStrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });

                // 在此处阻塞 不会执行之后的逻辑 Test 6 不会输出
                ChannelFuture channelFuture = clientStrap.connect().sync();

                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    };


    /**
     * 获取连接
     */
    private void getConnection() {

        if (connectSocket == null) {
            try {
                // 根据IP与端口号连接仪器服务器
                connectSocket = new Socket(medicalDevice.getDeviceIpAddress(), medicalDevice.getReceivePort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (receiveStream == null) {
            try {
                receiveStream = connectSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sendStream == null) {
            try {
                sendStream = connectSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void stopCollection() {
        try {
            sendStream.close();
            receiveStream.close();
            connectSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            listenThread.interrupt();
            sendThread.interrupt();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetCollector() {

    }

}
