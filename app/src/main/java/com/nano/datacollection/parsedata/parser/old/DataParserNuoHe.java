package com.nano.datacollection.parsedata.parser.old;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataNuoHe;
import com.nano.datacollection.parsedata.UpdateDataEntity;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.common.logger.Logger;

/**
 * Description: 诺和数据解析器(版本过期,暂时不用了)
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 23:53
 */
public class DataParserNuoHe implements DeviceDataParser {

    private Logger logger = new Logger("[ParserNuoHe]");

    /**
     * 解析数据
     *
     * @param deviceCode 仪器号
     * @param serialNumber 序列号
     * @param deviceOriginData 原始数据
     * @return 数据
     */
    @Override
    public DeviceData parseData(int deviceCode, String serialNumber, String deviceOriginData) {
        // 初始化数据结构
        DataNuoHe dataNuoHe = new DataNuoHe();
        // 设置序列号
        dataNuoHe.setSerialNumber(serialNumber);
        if (verifyData(deviceOriginData)){
            // 注意:用程序接收到的数据是连续的,而使用串口助手接收到的数据是按空格分开的
            // 这句话如果是连续的也没啥用
            String[] values = deviceOriginData.split(" ");
            int csi = 0;
            int bs = 0;
            int sqi = 0;
            int emg = 0;
            // 说明有空格(用串口助手接收的数据)
            if (values.length > 20) {
                // CSI:偏移量15字节 BS:偏移量16字节 SQI:偏移量17字节 EMG:偏移量20字节
                csi = Integer.parseInt(values[14], 16);
                bs = Integer.parseInt(values[15], 16);
                sqi = Integer.parseInt(values[16], 16);
                emg = Integer.parseInt(values[19], 16);

                // 说明收到的数据没有空格(用程序接收的数据)
            } else {
                deviceOriginData = deviceOriginData.trim().replace(" ", "");
                csi = Integer.parseInt(deviceOriginData.substring(28, 30), 16);
                bs = Integer.parseInt(deviceOriginData.substring(30, 32), 16);
                sqi = Integer.parseInt(deviceOriginData.substring(32, 34), 16);
                emg = Integer.parseInt(deviceOriginData.substring(38, 40), 16);
            }
            // 设置值
            if (csi == 255){
                dataNuoHe.setCsi(DataCons.INVALID_DATA_INTEGER);
            }else{
                dataNuoHe.setCsi(csi);
            }
            if (bs == 255){
                dataNuoHe.setBs(DataCons.INVALID_DATA_INTEGER);
            }else {
                dataNuoHe.setBs(bs);
            }
            if (sqi == 255){
                dataNuoHe.setSqi(DataCons.INVALID_DATA_INTEGER);
            }else {
                dataNuoHe.setSqi(sqi);
            }
            if (emg == 255){
                dataNuoHe.setEmg(DataCons.INVALID_DATA_INTEGER);
            }else {
                dataNuoHe.setEmg(emg);
            }
        }
        String dataString = JSON.toJSONString(dataNuoHe);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataNuoHe, JSON.toJSONString(new UpdateDataEntity(deviceCode, dataString)));
    }

    /**
     * 验证数据格式
     *
     * @param data 原始数据
     * @return 是否合格
     */
    @Override
    public boolean verifyData(String data) {
        // 长度低于262直接不对
        if (data.length() < 262) {
            return false;
        }
        return data.trim().startsWith("FF") && data.endsWith("FE");
    }
}
