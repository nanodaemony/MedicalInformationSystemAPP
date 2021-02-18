package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.common.logger.Logger;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.UpdateDataEntity;
import com.nano.datacollection.parsedata.entity.DataNuoHe;

/**
 * Description: 诺和数据解析器V405
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 23:53
 */
public class DataParserNuoHeV405 implements DeviceDataParser {

    private Logger logger = new Logger("[ParserNuoHeV405]");

    /**
     * 解析数据
     *
     * @param deviceCode       仪器号
     * @param serialNumber     序列号
     * @param deviceOriginData 原始数据
     * @return 数据
     */
    @Override
    public DeviceData parseData(int deviceCode, String serialNumber, String deviceOriginData) {
        // 初始化数据结构
        DataNuoHe dataNuoHe = new DataNuoHe();
        // 设置序列号
        dataNuoHe.setSerialNumber(serialNumber);
        // 新版本数据:
        // 66666630313538313035310432000000E1FFFF000F0F01513D007979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797979797900000000000000005F5F0000080D040000000014EE53C5C4AF01F2FF00000000000000000000000000000000000000000000000430E40000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        if (verifyData(deviceOriginData)) {
            // 按协议顺序进行解析
            int csi = Integer.parseInt(deviceOriginData.substring(32, 34), 16);
            int emg = Integer.parseInt(deviceOriginData.substring(34, 36), 16);
            int bs = Integer.parseInt(deviceOriginData.substring(36, 38), 16);
            int sqi = Integer.parseInt(deviceOriginData.substring(38, 40), 16);
            // 设置值
            if (csi == 255) {
                dataNuoHe.setCsi(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataNuoHe.setCsi(csi);
            }
            if (bs == 255) {
                dataNuoHe.setBs(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataNuoHe.setBs(bs);
            }
            if (sqi == 255) {
                dataNuoHe.setSqi(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataNuoHe.setSqi(sqi);
            }
            if (emg == 255) {
                dataNuoHe.setEmg(DataCons.INVALID_DATA_INTEGER);
            } else {
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
        if (data.length() < 360) {
            return false;
        }
        return data.trim().startsWith("666666");
    }
}
