package com.nano.datacollection.udp.baolaite;

import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.util.CommonUtil;
import com.nano.datacollection.cons.DeviceAccessCommands;
import com.nano.datacollection.Collector;
import com.nano.device.MedicalDevice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 宝莱特A8采集器
 * @author cz
 */
public class CollectorBaoLaiTeA8 implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("[BaoLaiTeA8Collector]");

    /**
     * 使用的仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 接收命令计数
     */
    private int commandCounter = 0;

    // UDP数据报控制接入端口
    private DatagramSocket controlSocket = null;

    // 宝莱特数据接收端口
    private DatagramSocket receiveSocket = null;

    private Thread controlThread = null;

    private Thread receiveThread = null;

    /**
     * 构造器
     *
     * @param medicalDevice 使用的仪器
     */
    public CollectorBaoLaiTeA8(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }

    @Override
    public void startCollection() {

        // 打开采集接口
        medicalDevice.setCollectorSwitchOn(true);

        // 开启接收任务
        controlThread = new Thread(controlRunnable);
        receiveThread = new Thread(receiveRunnable);
        controlThread.start();
        receiveThread.start();
    }

    /**
     * UDP接收线程
     */
    private Runnable controlRunnable = new Runnable() {
        @Override
        public void run() {
            logger.info("开启宝莱特监听线程:" + medicalDevice.getSendPort());
            try {
                // 创建socket
                controlSocket = new DatagramSocket(medicalDevice.getSendPort());
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
            while (medicalDevice.isCollectorSwitchOn()){
                try {
                    // Buffer的最大长度值
                    byte[] data = new byte[medicalDevice.getReceiveBufferLength()];
                    // 构造packet
                    DatagramPacket udpPacket = new DatagramPacket(data, data.length);
                    // 此方法在接收到数据报之前会一直阻塞，接收到数据后继续执行
                    controlSocket.receive(udpPacket);

                    // 以下是接收到数据之后的逻辑
                    byte[] bytes = udpPacket.getData();

                    // bytes转换为16进制的字符串   收到的是2倍长度的字符串！
                    String hexData = CommonUtil.getBufHexStr(bytes);

                    // 代表是宝莱特的信息
                    if (hexData.startsWith("FFD0")) {
                        commandCounter++;
                        if (commandCounter == 5){
                            // 代表是广播数据
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_DATA);
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
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                            logger.debug("第一次响应接收病人--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                        }

                        // FFDA000F00F10003017405FFD7000F00F
                        if ("05".equals(hexData.substring(20, 22))){
                            // Here is "74"
                            String counter = hexData.substring(18, 20);
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_HEART_RATE_INFO + counter + "05");
                            logger.debug("第一次响应心律失常信息--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_HEART_RATE_INFO + counter + "05");
                            isResponseHeartRateInfo = true;
                        }

                        // FFDA000F00F10003016C06FFD4000F00F
                        if ("06".equals(hexData.substring(20, 22))){
                            String counter = hexData.substring(18, 20);
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_CONFIGURE_INFO + counter + "06");
                            logger.debug("第一次响应配置信息--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_CONFIGURE_INFO + counter + "06");
                            isResponseConfigureInfo = true;
                        }

                        // 只执行一次
                        if (isResponseHeartRateInfo && isResponseConfigureInfo){
                            // 指示第一阶段完成
                            connectStep = 2;
                            logger.debug("第一阶段完成 FFDA");
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_DATA);
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
                            sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                            logger.debug("第2次响应接收病人--FFDA", DeviceAccessCommands.BAO_LAI_TE_RESPONSE_GET_NEW_PATIENT + receivePatientNumber + "03");
                        }
                        // FFDA000F7FF10004010C0
                        if ("04".equals(hexData.substring(14, 16))){
                            logger.debug("采集器同意数据请求 FFDA");

                            if (isAgreeToSendData){
                                connectStep = 3;
                                sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_SYNIC_TIME);
                                logger.debug("请求同步时间 FFDA");

                                sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_REQUEST_CONFIGURE);
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
                        sendDataToBaoLaiTe(controlSocket, udpPacket, DeviceAccessCommands.BAO_LAI_TE_BRAODCAST_MESSAGE);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (controlSocket != null) {
                try {
                    // 关闭资源
                    controlSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };



    /**
     * 接收任务
     */
    private Runnable receiveRunnable = new Runnable() {
        @Override
        public void run() {
            // 用于接受宝莱特的监测数据端口号 45679
            logger.info("开启宝莱特接收线程:" + medicalDevice.getReceivePort());
            try {
                // 创建socket
                receiveSocket = new DatagramSocket(45679);
                receiveSocket.setReceiveBufferSize(2500);
                receiveSocket.setSendBufferSize(2500);

            } catch (SocketException e) {
                e.printStackTrace();
            }

            // 循环接收数据
            while (medicalDevice.isCollectorSwitchOn()){
                try {
                    // Buffer的最大长度值
                    byte[] data = new byte[medicalDevice.getReceiveBufferLength()];

                    // 构造packet
                    DatagramPacket udpPacket = new DatagramPacket(data, 2500);
                    // 此方法在接收到数据报之前会一直阻塞，接收到数据后继续执行
                    udpPacket.setLength(2500);
                    receiveSocket.receive(udpPacket);
                    // 以下是接收到数据之后的逻辑
                    byte[] bytes = udpPacket.getData();
                    // bytes转换为16进制的字符串   收到的是2倍长度的字符串！
                    String hexData = CommonUtil.getBufHexStr(bytes);
                    if (hexData != null && hexData.startsWith("FFD1")) {
                        medicalDevice.parseAndUploadDeviceData(hexData);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            if (receiveSocket != null) {
                try {
                    // 关闭资源
                    receiveSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void stopCollection() {
        // 关闭采集器开关
        medicalDevice.setCollectorSwitchOn(false);

        try {
            if (controlSocket != null) {
                if (controlSocket.isClosed()) {
                    controlSocket.close();
                }
                controlSocket = null;
            }

            // 关闭线程
            if (controlThread != null) {
                controlThread.interrupt();
                controlThread = null;
            }

            if (receiveSocket != null) {
                if (receiveSocket.isClosed()) {
                    receiveSocket.close();
                }
                receiveSocket = null;
            }

            // 关闭线程
            if (receiveThread != null) {
                receiveThread.interrupt();
                receiveThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void resetCollector() {

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

    /**
     * 获取接收到命令字符串的命令编号
     * @param data 接收命令
     * @return 命令编号
     */
    private static String getCommandType(String data){
        if (data.startsWith("FFDA")){
            return data.substring(18, 22);
        } else {
            return "0000";
        }
    }
}
