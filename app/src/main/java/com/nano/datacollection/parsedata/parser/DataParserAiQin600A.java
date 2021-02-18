package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.DataParseUtils;
import com.nano.datacollection.parsedata.UpdateDataEntity;
import com.nano.datacollection.parsedata.entity.DataAiQin600A;

/**
 * Description: 爱琴EGOS600A数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/11 13:41
 */
public class DataParserAiQin600A implements DeviceDataParser {

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

        DataAiQin600A dataAiQin600A = new DataAiQin600A();
        dataAiQin600A.setSerialNumber(serialNumber);

        // 正常仪器数据
        // EF EF EF 65 23 18 00 00 00 0A 31 35 30 32 38 37 00 01 20 00 30 30 11 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 42 7B 9F 68 3D DC 2A 30 7F C0 00 00 7F C0 00 00 7F C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 32 FE FE FE
        // EF EF EF
        // 65：长度
        // 23：数据类型消息
        // 18 00 00 00：帧号
        // 0A：设备型号：4个通道
        // 31 35 30 32 38 37 00 01 20 00：10字节，设备SN码
        // 30 30 11 30：探头质量 依次表示4个通道的质量
        // 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00：通道1数据
        // 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00：通道2数据
        // 42 7B 9F 68 3D DC 2A 30 7F C0 00 00 7F C0 00 00 7F C0 00 00：通道3数据
        // 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00：通道4数据
        // 2E：校验和
        // FE FE FE

        // 将空格去掉(经过代码后可能受到的是没有空格的数据)
        // EFEFEF
        // 65
        // 23
        // 18000000
        // 0A
        // 31353032383700012000
        // 30301130 (探头质量:依次表示4个通道的质量)
        // 0000000000000000000000000000000000000000
        // 0000000000000000000000000000000000000000
        // 427B9F683DDC2A307FC000007FC000007FC00000
        // 0000000000000000000000000000000000000000
        // 32
        // FEFEFE
        deviceOriginData = deviceOriginData.replace(" ", "").trim();
        if (verifyData(deviceOriginData)) {
            // 得到四个通道的数据
            // 0000000000000000000000000000000000000000
            // 0000000000000000000000000000000000000000
            // 427B9F683DDC2A307FC000007FC000007FC00000
            // 0000000000000000000000000000000000000000
            String channelData1 = deviceOriginData.substring(48, 88);
            String channelData2 = deviceOriginData.substring(88, 128);
            String channelData3 = deviceOriginData.substring(128, 168);
            String channelData4 = deviceOriginData.substring(168, 208);
            // 分别解析四个通道的数据
            parseOneChannelData(channelData1, dataAiQin600A, 1);
            parseOneChannelData(channelData2, dataAiQin600A, 2);
            parseOneChannelData(channelData3, dataAiQin600A, 3);
            parseOneChannelData(channelData4, dataAiQin600A, 4);
            // 解析探头质量(4个通道共8字节)
            String probeStatus = deviceOriginData.substring(40, 48);
            dataAiQin600A.setProbeStatus1(probeStatus.substring(0, 2));
            dataAiQin600A.setProbeStatus2(probeStatus.substring(2, 4));
            dataAiQin600A.setProbeStatus3(probeStatus.substring(4, 6));
            dataAiQin600A.setProbeStatus4(probeStatus.substring(6));
        }

        String dataString = JSON.toJSONString(dataAiQin600A);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataAiQin600A, JSON.toJSONString(new UpdateDataEntity(deviceCode, dataString)));
    }


    /**
     * 解析一个通道的数据
     *
     * @param channelData 通道数据
     * @param dataAiQin600A 数据
     * @param channelNumber 通道索引数
     * 解析后的数据
     */
    private static void parseOneChannelData(String channelData, DataAiQin600A dataAiQin600A, int channelNumber) {
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
                dataAiQin600A.setToi1(toi1);
                dataAiQin600A.setThi1(thi1);
                dataAiQin600A.setChb1(chb1);
                dataAiQin600A.setChbo21(chbo21);
                dataAiQin600A.setCthb1(cthb1);
                break;
            case 2:
                double toi2 = DataParseUtils.getDoubleValueByHexString(channelData.substring(0, 8));
                double thi2 = DataParseUtils.getDoubleValueByHexString(channelData.substring(8, 16));
                double chb2 = DataParseUtils.getDoubleValueByHexString(channelData.substring(16, 24));
                double chbo22 = DataParseUtils.getDoubleValueByHexString(channelData.substring(24, 32));
                double cthb2 = DataParseUtils.getDoubleValueByHexString(channelData.substring(32));
                dataAiQin600A.setToi2(toi2);
                dataAiQin600A.setThi2(thi2);
                dataAiQin600A.setChb2(chb2);
                dataAiQin600A.setChbo22(chbo22);
                dataAiQin600A.setCthb2(cthb2);
                break;
            case 3:
                double toi3 = DataParseUtils.getDoubleValueByHexString(channelData.substring(0, 8));
                double thi3 = DataParseUtils.getDoubleValueByHexString(channelData.substring(8, 16));
                double chb3 = DataParseUtils.getDoubleValueByHexString(channelData.substring(16, 24));
                double chbo23 = DataParseUtils.getDoubleValueByHexString(channelData.substring(24, 32));
                double cthb3 = DataParseUtils.getDoubleValueByHexString(channelData.substring(32));
                dataAiQin600A.setToi3(toi3);
                dataAiQin600A.setThi3(thi3);
                dataAiQin600A.setChb3(chb3);
                dataAiQin600A.setChbo23(chbo23);
                dataAiQin600A.setCthb3(cthb3);
                break;

            case 4:
                double toi4 = DataParseUtils.getDoubleValueByHexString(channelData.substring(0, 8));
                double thi4 = DataParseUtils.getDoubleValueByHexString(channelData.substring(8, 16));
                double chb4 = DataParseUtils.getDoubleValueByHexString(channelData.substring(16, 24));
                double chbo24 = DataParseUtils.getDoubleValueByHexString(channelData.substring(24, 32));
                double cthb4 = DataParseUtils.getDoubleValueByHexString(channelData.substring(32));
                dataAiQin600A.setToi4(toi4);
                dataAiQin600A.setThi4(thi4);
                dataAiQin600A.setChb4(chb4);
                dataAiQin600A.setChbo24(chbo24);
                dataAiQin600A.setCthb4(cthb4);
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
