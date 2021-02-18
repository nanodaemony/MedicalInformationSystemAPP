package com.nano.datacollection.udp.nuohe;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.nano.common.logger.Logger;
import com.nano.common.eventbus.BusMessage;
import com.nano.common.eventbus.EventBusUtils;
import com.nano.common.eventbus.MessageCodeEnum;
import com.nano.datacollection.Collector;
import com.nano.datacollection.util.CollectionUtils;
import com.nano.device.MedicalDevice;

import java.net.InetSocketAddress;

/**
 * Description: 诺和NW9002S采集器 异步采集(暂时不行)
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 10:48
 */
@Deprecated
public class CollectorNuoHeNW9002SAsync implements Collector {

    /**
     * Logger
     */
    private Logger logger = new Logger("NuoHeNW9002SCollectorByAsync");

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
    public CollectorNuoHeNW9002SAsync(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }

    /**
     * 开始采集
     */
    @Override
    public void startCollection() {
        // 初始化
        this.host = new InetSocketAddress("localhost", medicalDevice.getReceivePort());
        setup();
    }

    /**
     * 初始化
     */
    private void setup() {
        try {
            // 开启端口监听
            asyncDatagramSocket = AsyncServer.getDefault().openDatagram(host, true);
            logger.info("是否开启:" + asyncDatagramSocket.isOpen());
            logger.info("是否开启:" + asyncDatagramSocket.isChunked());

            logger.info("诺和接收端口监听打开:" + medicalDevice.getReceivePort());

            

        } catch (Exception e) {
            EventBusUtils.sendMessage(new BusMessage(MessageCodeEnum.UNABLE_TO_OPEN_SOCKET, medicalDevice.getDeviceCode(), "无法打开端口"));
        }
        // 处理接收到的数据
        asyncDatagramSocket.setDataCallback((emitter, bufferList) -> {
            // 获取数据字节缓存
            byte[] dataBuf = bufferList.getAllByteArray();
            // 转换为16进制字符串
            String hexData = CollectionUtils.getBufHexStr(dataBuf);
            logger.debug("诺和NW9002S收到数据:" + hexData);
            // 解析并上传仪器数据
            medicalDevice.parseAndUploadDeviceData(hexData);
        });

        // 关闭连接
        asyncDatagramSocket.setClosedCallback(ex -> {
            if (ex != null) throw new RuntimeException(ex);
            logger.info("[诺和NW9002S] Successfully closed connection");
        });

        // 完成连接
        asyncDatagramSocket.setEndCallback(ex -> {
            logger.info("[诺和NW9002S] Successfully end connection");
            if (ex != null) {
                ex.printStackTrace();
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
        asyncDatagramSocket.end();
    }

    /**
     * 重启采集器
     */
    @Override
    public void resetCollector() {

    }


}
