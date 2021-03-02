package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.common.logger.Logger;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataPuKe;
import com.nano.datacollection.parsedata.ParamDeviceDataPad;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;

/**
 * Description: 普可YY106数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/1 20:02
 */
public class DataParserPuKe implements DeviceDataParser {

    private static Logger logger = new Logger("ParsePuKe");

    @Override
    public DeviceData parseData(int deviceCode, Integer collectionNumber, String serialNumber, String deviceOriginData) {
        DataPuKe dataPuKe = new DataPuKe();
        dataPuKe.setSerialNumber(serialNumber);
        dataPuKe.setCollectionNumber(collectionNumber);
        if (verifyData(deviceOriginData)) {
            String[] values = deviceOriginData.split(" ");
            int ai = Integer.parseInt(values[3], 16);
            int bsr = Integer.parseInt(values[4], 16);
            int emg = Integer.parseInt(values[5], 16);
            int sqi = Integer.parseInt(values[6], 16);
            if (ai > 100 || ai < 0) {
                dataPuKe.setAi(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataPuKe.setAi(ai);
            }

            if (sqi == 255) {
                dataPuKe.setSqi(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataPuKe.setSqi(sqi);
            }

            if (emg == 255) {
                dataPuKe.setEmg(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataPuKe.setEmg(emg);
            }

            if (bsr == 255) {
                dataPuKe.setBsr(DataCons.INVALID_DATA_INTEGER);
            } else {
                dataPuKe.setBsr(bsr);
            }
        }
        String dataString = JSON.toJSONString(dataPuKe);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataPuKe, JSON.toJSONString(new ParamDeviceDataPad(deviceCode, collectionNumber, dataString)));
    }

    @Override
    public boolean verifyData(String data) {
        return data.startsWith("FF") && data.split(" ").length >= 7;
    }
}
