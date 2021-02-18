package com.nano.datacollection.old.udp;



import com.nano.device.DeviceEnum;
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.common.logger.Logger;
import com.nano.common.util.CommonUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * 宝莱特Q6的数据接入线程 实现接入心跳包维持等功能
 * @author cz
 */
public class UdpServerBaoLaiTeAccess {

    private Logger logger = new Logger("UdpServerBaoLaiTeAccess");

    /**
     * UDP数据报服务器
     */
    private DatagramSocket udpSocket;
    /**
     * UDP接收端口
     */
    private Thread baoLaiTeAccessThread;
    private long receiveTime;

    /**
     * 接收命令计数
     */
    private int commandCounter = 0;

    /**
     * Start the service
     */
    public void startServer() {
        baoLaiTeAccessThread = new Thread(receiveWork);
        baoLaiTeAccessThread.setName("宝莱特接入");
        baoLaiTeAccessThread.start();
    }

    /**
     * UDP接收线程
     */
    private Runnable receiveWork = new Runnable() {
        @Override
        public void run() {
            logger.debug("开启宝莱特监听线程:" + 8002);
            try {
                // 创建socket
                udpSocket = new DatagramSocket(8002);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            // 判断是否第一次收到了心律不齐的信息
            boolean isResponseHeartRateInfo = false;
            // 判断是否第一次收到配置信息
            boolean isResponseConfigureInfo = false;
            // 接收阶段标志
            int connectStep = 1;
            // 是否同意数据发送请求
            boolean isAgreeToSendData = false;

            // 循环接收数据
            while (true){
                try {
                    // Buffer的最大长度值
                    byte[] data = new byte[2500];
                    // 构造packet
                    DatagramPacket udpPacket = new DatagramPacket(data, data.length);
                    // 此方法在接收到数据报之前会一直阻塞，接收到数据后继续执行
                    udpSocket.receive(udpPacket);

                    // 以下是接收到数据之后的逻辑
                    // 从packet中获取值并存入字符数组中
                    byte[] bytes = udpPacket.getData();

                    // bytes转换为16进制的字符串   收到的是2倍长度的字符串！
                    String hexData = CommonUtil.getBufHexStr(bytes);

                    logger.debug(hexData);

                    // 代表是宝莱特的信息
                    if (hexData.startsWith("FFD0")) {
                        commandCounter++;
                        if (commandCounter == 5){
                            // 代表是广播数据
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_DATA);
                            logger.debug("第1次请求数据--FFDA");
                            // 请求获取数据之后仪器返回：FFDA000F7FF10085014C0300FD00691E208AAA  即为病人信息数据
                        }
                    }

                    if (connectStep == 1){
                        // 表示此条数据是接收病人的数据

                        if ("03".equals(getCommandType(hexData).substring(2))){

                            logger.debug("接收到病人信息");
                            String receivePatientNumber = hexData.substring(18, 20);
                            // 响应接收病人的命令
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                            logger.debug("第一次响应接收病人--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                        }

                        // FFDA000F00F10003017405FFD7000F00F
                        if ("05".equals(hexData.substring(20, 22))){
                            // Here is "74"
                            String counter = hexData.substring(18, 20);
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_HEART_RATE_INFO + counter + "05");
                            logger.debug("第一次响应心律失常信息--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_HEART_RATE_INFO + counter + "05");
                            isResponseHeartRateInfo = true;
                        }

                        // FFDA000F00F10003016C06FFD4000F00F
                        if ("06".equals(hexData.substring(20, 22))){
                            String counter = hexData.substring(18, 20);
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_CONFIGURE_INFO + counter + "06");
                            logger.debug("第一次响应配置信息--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_CONFIGURE_INFO + counter + "06");
                            isResponseConfigureInfo = true;
                        }

                        // 只执行一次
                        if (isResponseHeartRateInfo && isResponseConfigureInfo){
                            // 指示第一阶段完成
                            connectStep = 2;
                            logger.debug("第一阶段完成 FFDA");
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_DATA);
                            // 仪器马上回复病人信息的数据
                            logger.debug("第2次请求数据 FFDA");
                            isResponseHeartRateInfo = false;
                            isResponseConfigureInfo = false;
                        }
                    }

                    if (connectStep == 2){
                        logger.debug("进入第二阶段 FFDA");

                        // 现在的情况是后面step = 3不用发送也能收到数据，所以此处加上了这个避免一直进入第二阶段  ！！！ 应该可以调整！！！
                        if (commandCounter > 20){
                            connectStep = 4;
                        }

                        if (hexData.startsWith("FFDA000F7FF1008501")){
                            String receivePatientNumber = hexData.substring(18, 20);
                            // 响应接收病人的命令
                            sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                            logger.debug("第2次响应接收病人--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                        }

                        // FFDA000F7FF10004010C0
                        if ("04".equals(hexData.substring(14, 16))){
                            logger.debug("采集器同意数据请求 FFDA");

                            if (isAgreeToSendData){
                                connectStep = 3;
                                sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_SYNIC_TIME);
                                logger.debug("请求同步时间 FFDA");

                                sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_CONFIGURE);
                                logger.debug("发送查询配置命令 FFDA");
                            }

                            isAgreeToSendData = true;
                        }
                    }

                    if (connectStep == 3){
                        if (hexData.startsWith("FFDA000F7FF100030A180D")){
                            logger.debug("时间已同步 FFDA");
                        }
                        connectStep = 4;
                        logger.debug("接入完成 FFDA");
                    }

                    if (hexData.startsWith("FFD0")) {
                        // 回复广播数据
                        sendDataToBaoLaiTe(udpSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_BRAODCAST_MESSAGE);
                        logger.debug("仪器发送广播数据 FFDA");
                    }
                    // 更改时间
                    receiveTime = System.currentTimeMillis();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    /**
     * 关闭线程
     */
    public void restartServer() {
        try {
            if (baoLaiTeAccessThread.getState() == Thread.State.RUNNABLE){
                baoLaiTeAccessThread.interrupt();
                baoLaiTeAccessThread = null;
            }
            // 关闭资源
            if (!udpSocket.isClosed()){
                udpSocket.close();
            }

//            deviceServerList.remove(baoLaiTeAccessThread);
            startServer();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void stopServer() {
        try {
            if (baoLaiTeAccessThread.getState() == Thread.State.RUNNABLE){
                baoLaiTeAccessThread.interrupt();
                baoLaiTeAccessThread = null;
            }

            // 关闭资源
            if (!udpSocket.isClosed()){
                udpSocket.close();
            }

            // 宝莱特 即使没有也不会报错
            //deviceServerList.remove(nuoHeThread);
            logger.debug("关闭诺和线程");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 判断是否在线
     * @return 是否在线
     */
    public boolean isServerOn() {
        return System.currentTimeMillis() - receiveTime > 30000;
    }

    public String getServerName() {
        if (baoLaiTeAccessThread != null){
            return baoLaiTeAccessThread.getName();
        }else {
            return "宝莱特接入NULL";
        }
    }

    public int getDeviceCode() {
        return DeviceEnum.BAO_LAI_TE_A8.getDeviceCode();
    }


    /**
     * 获取命令字符串的长度
     * @param data 接收数据
     * @return 字符串长度的字符串表示 需要转换
     */
    public static String getCommandLength(String data){
        if (data.startsWith("FFDA")){
            return data.substring(14, 16);
        } else {
            return "00";
        }
    }


    /**
     * 获取接收到命令字符串的命令编号
     * @param data 接收命令
     * @return 命令编号
     */
    public static String getCommandType(String data){
        if (data.startsWith("FFDA")){
            return data.substring(18, 22);
        } else {
            return "0000";
        }
    }


    /**
     * 发送数据给宝莱特
     */
    private void sendDataToBaoLaiTe(DatagramSocket getSocket, DatagramPacket getPacket, String sendData) throws UnknownHostException {
        // 通过数据包得到发送方的套接字ip
        SocketAddress sendAddress = getPacket.getSocketAddress();
        logger.debug("Address", sendAddress.toString());

        // 由于16进制字符发送时只能发送字节，这里讲字符串转换成字节
        byte[] bytes = CommonUtil.getHexBytes(sendData);
        // 此处仅适合诺和的数据回发
        InetAddress address = InetAddress.getByName("192.168.8.32");
        // 创建发送类型的数据包
        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address, 8002);
        // 通过套接字发送数据
        try {
            getSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
