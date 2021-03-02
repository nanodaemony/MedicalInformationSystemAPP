package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DataParseUtils;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.ParamDeviceDataPad;
import com.nano.datacollection.parsedata.entity.DataAiQin600C;

/**
 * Description: 爱琴EGOS600C数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/11 13:41
 */
public class DataParserAiQin600C implements DeviceDataParser {

    /**
     * 解析数据
     *
     * @param deviceCode 仪器号
     * @param serialNumber 序列号
     * @param deviceOriginData 原始数据
     * @return 数据
     */
    @Override
    public DeviceData parseData(int deviceCode, Integer collectionNumber, String serialNumber, String deviceOriginData) {

        DataAiQin600C dataAiQin600C = new DataAiQin600C();
        dataAiQin600C.setSerialNumber(serialNumber);
        dataAiQin600C.setCollectionNumber(collectionNumber);

        // 正常仪器数据
        // EFEFEFXX23180000000B3135303238370001200030427B9F683DDC2A307FC000007FC000007FC0000032FEFEFE
        // 帧头: EFEFEF
        // 长度: 65
        // 测量状态(数据类型): 23
        // 帧号: 18000000
        // 仪器类型: 0B
        // 设备SN码: 31353032383700012000
        // 探头质量(依次表示2个通道的质量): 30(比四通道少两个字节)
        // 通道1数据: 427B9F683DDC2A307FC000007FC000007FC00000
        // 校验和: 32
        // 帧尾: FEFEFE
        deviceOriginData = deviceOriginData.replace(" ", "").trim();
        if (verifyData(deviceOriginData)) {
            // 得到两个通道的数据
            // 427B9F683DDC2A307FC000007FC000007FC00000
            String channelData1 = deviceOriginData.substring(42, 82);
            // 分别解析四个通道的数据
            parseOneChannelData(channelData1, dataAiQin600C, 1);
            // 解析探头质量(4个通道共8字节)
            String probeStatus = deviceOriginData.substring(40, 42);
            dataAiQin600C.setProbeStatus1(probeStatus);
        }

        String dataString = JSON.toJSONString(dataAiQin600C);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataAiQin600C, JSON.toJSONString(new ParamDeviceDataPad(deviceCode, collectionNumber, dataString)));
    }


    /**
     * 解析一个通道的数据
     *
     * @param channelData 通道数据
     * @param dataAiQin600C 数据
     * @param channelNumber 通道索引数
     * 解析后的数据
     */
    private static void parseOneChannelData(String channelData, DataAiQin600C dataAiQin600C, int channelNumber) {
        // 说明这个通道没有数据直接返回
        if ("0000000000000000000000000000000000000000".equals(channelData)) {
            return;
        }
        // System.out.println(channelData + channelNumber);
        switch (channelNumber) {
            case 1:
                double toi1 = DataParseUtils.getDoubleValueByHexString(channelData.substring(0, 8));
                double thi1 = DataParseUtils.getDoubleValueByHexString(channelData.substring(8, 16));
                double chb1 = DataParseUtils.getDoubleValueByHexString(channelData.substring(16, 24));
                double chbo21 = DataParseUtils.getDoubleValueByHexString(channelData.substring(24, 32));
                double cthb1 = DataParseUtils.getDoubleValueByHexString(channelData.substring(32));
                dataAiQin600C.setToi1(toi1);
                dataAiQin600C.setThi1(thi1);
                dataAiQin600C.setChb1(chb1);
                dataAiQin600C.setChbo21(chbo21);
                dataAiQin600C.setCthb1(cthb1);
                break;
            default:
        }
    }



    /**
     * 验证数据格式
     *
     * @param data 原始数据
     * @return 是否合格
     */
    @Override
    public boolean verifyData(String data) {
        return data.startsWith("EFEFEF") && data.endsWith("FEFEFE") && "23".equals(data.substring(8, 10)) ;
    }


}
