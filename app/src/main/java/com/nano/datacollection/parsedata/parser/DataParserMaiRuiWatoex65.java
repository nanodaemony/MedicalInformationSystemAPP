package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.DataParseUtils;
import com.nano.datacollection.parsedata.UpdateDataEntity;
import com.nano.datacollection.parsedata.entity.DataMaiRuiWatoex65;

/**
 * Description: 迈瑞WATOEX65数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/10 0:16
 */
public class DataParserMaiRuiWatoex65 implements DeviceDataParser {


    /**
     * HL7协议数据开始头部
     */
    private static final String HL7_START_FLAG = "0B";
    private static final String HL7_END_FLAG = "1C0D";

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

        DataMaiRuiWatoex65 dataMaiRuiWatoex65 = new DataMaiRuiWatoex65();
        dataMaiRuiWatoex65.setSerialNumber(serialNumber);

        if (verifyData(deviceOriginData)) {
            // 去掉HL7的头和尾字段用于解析
            String realData = deviceOriginData.substring(2, deviceOriginData.length() - 4);
            parseOneDataBlock(dataMaiRuiWatoex65, realData);
        }

        String dataString = JSON.toJSONString(dataMaiRuiWatoex65);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataMaiRuiWatoex65, JSON.toJSONString(new UpdateDataEntity(deviceCode, dataString)));
    }


    /**
     * 验证数据格式
     *
     * @param data 原始数据
     * @return 是否合格
     */
    @Override
    public boolean verifyData(String data) {
        return data.startsWith(HL7_START_FLAG) && data.endsWith(HL7_END_FLAG);
    }


    /**
     * 解析一条数据块
     * @param dataMaiRuiWatoex65 迈瑞数据
     * @param dataBlock 一个数据块
     */
    private static void parseOneDataBlock(DataMaiRuiWatoex65 dataMaiRuiWatoex65, String dataBlock) {

        // 需要去掉每个串中的0D即换行符不然解析不成功
        String dataBlockRemoveCR = dataBlock.replace("0D", "#");
        String[] smallDataBlock = dataBlockRemoveCR.split("#");
        for (String s : smallDataBlock) {
            // 获取真实的数据值
            String realData = DataParseUtils.hexStringToString(s);
            // 将真实的数据按照竖线划分
            String[] realDataBlock = realData.split("\\|");
            if (realDataBlock.length > 5) {

                switch (realDataBlock[3]) {
                    case "301^Pmean":
                        double pMean = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setPMean(pMean);
                        break;

                    case "302^PEEP":
                        double peep = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setPeep(peep);
                        break;

                    case "303^Pplat":
                        double pPlat = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setPPlat(pPlat);
                        break;

                    case "304^Ppeak":
                        double pPeak = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setPPeak(pPeak);
                        break;

                    case "305^MV":
                        double mv = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setMv(mv);
                        break;

                    case "306^TVe":
                        double tve = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setTve(tve);
                        break;

                    case "308^I:E":
                        double ie = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setIe(ie);
                        break;

                    case "309^Rate":
                        double rate = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setRate(rate);
                        break;

                    case "311^C":
                        double c = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setC(c);
                        break;

                    case "312^R":
                        double r = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiWatoex65.setR(r);
                        break;

                    default:
                }

            }

        }

    }

}
