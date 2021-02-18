package com.nano.datacollection.old.udp;



import com.nano.device.DeviceEnum;
import com.nano.common.logger.Logger;
import com.nano.common.util.CommonUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * 宝莱特Q6的数据接收线程
 * @author cz
 */
public class UdpServerBaoLaiTeReceive  {

    private Logger logger = new Logger("UdpServerBaoLaiTeReceive");

    /**
     * UDP数据报服务器
     */
    private DatagramSocket udpSocket;


    private Thread baoLaiTeReceiveThread;

    /**
     * 接收到数据的时间
     */
    private long receiveTime;

    /**
     * 开启服务器
     */
    public void startServer() {

        baoLaiTeReceiveThread = new Thread(receiveWork);
        baoLaiTeReceiveThread.setName("宝莱特接收");
        baoLaiTeReceiveThread.start();
    }


    /**
     * UDP接收线程
     */
    private Runnable receiveWork = new Runnable() {
        @Override
        public void run() {
            // 用于接受宝莱特的监测数据端口号 45679
            logger.debug("开启宝莱特接收线程:" + 45679);
            try {
                // 创建socket
                udpSocket = new DatagramSocket(45679);
                udpSocket.setReceiveBufferSize(2500);
                udpSocket.setSendBufferSize(2500);

            } catch (SocketException e) {
                e.printStackTrace();
            }

            // 循环接收数据
            while (true){
                try {
                    // Buffer的最大长度值
                    byte[] data = new byte[2500];

                    // 构造packet
                    DatagramPacket udpPacket = new DatagramPacket(data, 2500);
                    // 此方法在接收到数据报之前会一直阻塞，接收到数据后继续执行
                    udpPacket.setLength(2500);
                    udpSocket.receive(udpPacket);

                    // 以下是接收到数据之后的逻辑
                    // 从packet中获取值并存入字符数组中
                    byte[] bytes = udpPacket.getData();
                    // bytes转换为16进制的字符串   收到的是2倍长度的字符串！

                    String hexData = CommonUtil.getBufHexStr(bytes);

                    if (hexData.startsWith("FFD")) {
                        logger.debug(hexData);
                        //EventBusUtil.sendDeviceData(new DeviceRawData(DeviceCode.BAO_LAI_TE_A8, hexData));
                    }

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
            if (baoLaiTeReceiveThread.getState() == Thread.State.RUNNABLE){
                baoLaiTeReceiveThread.interrupt();
                baoLaiTeReceiveThread = null;
            }

            // 关闭资源
            if (!udpSocket.isClosed()){
                udpSocket.close();
            }

            startServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (baoLaiTeReceiveThread.getState() == Thread.State.RUNNABLE){
                baoLaiTeReceiveThread.interrupt();
                baoLaiTeReceiveThread = null;
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


    public boolean isServerOn() {
        return System.currentTimeMillis() - receiveTime > 10000;
    }


    public String getServerName() {
        if (baoLaiTeReceiveThread != null){
            return baoLaiTeReceiveThread.getName();
        }else {
            return "宝莱特接收NULL";
        }

    }

    public int getDeviceCode() {
        return DeviceEnum.BAO_LAI_TE_A8.getDeviceCode();
    }

    /**
     * 根据数据的长度字符串获取参数的长度
     * @param lengthData 数据
     * @return 数据长度
     */
    public int getLength(String lengthData){
        StringBuilder builder = new StringBuilder();
        char[] array = lengthData.toCharArray();
        String[] resultBinary = new String[4];
        for (int i = 0; i < array.length; i++){
            resultBinary[i] = getCharBinaryString(array[i]);
        }
        builder.append("00").append(resultBinary[0].substring(1)).append(resultBinary[1]).append(resultBinary[2].substring(1)).append(resultBinary[3]);
        return Integer.parseInt(builder.toString(), 2);
    }

    /**
     * 根据字符获取其对应的二进制字符串
     * @param a 字符
     * @return 对应的二进制字符串
     */
    private String getCharBinaryString(char a){
        switch (a){
            case '0': return "0000";
            case '1': return "0001";
            case '2': return "0010";
            case '3': return "0011";
            case '4': return "0100";
            case '5': return "0101";
            case '6': return "0110";
            case '7': return "0111";
            case '8': return "1000";
            case '9': return "1001";
            case 'A': return "1010";
            case 'B': return "1011";
            case 'C': return "1100";
            case 'D': return "1101";
            case 'E': return "1110";
            case 'F': return "1111";
            default: return "0000";
        }
    }


}
