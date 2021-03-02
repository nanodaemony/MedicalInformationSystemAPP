package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.DataParseUtils;
import com.nano.datacollection.parsedata.entity.DataYiAn8700A;
import com.nano.datacollection.parsedata.ParamDeviceDataPad;

/**
 * Description: 宜安8700A的数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/9 19:53
 */
public class DataParserYiAn8700A implements DeviceDataParser {

    /**
     * HL7协议数据开始头部与结尾标志
     */
    private static final String HL7_START_FLAG = "0B";
    private static final String HL7_END_FLAG = "1C0D";

    @Override
    public DeviceData parseData(int deviceCode, Integer collectionNumber, String serialNumber, String deviceOriginData) {

        DataYiAn8700A dataYiAn8700A = new DataYiAn8700A();
        dataYiAn8700A.setSerialNumber(serialNumber);
        dataYiAn8700A.setCollectionNumber(collectionNumber);

        // 将数据切分为4条进行分别解析
        String[] dataBlocks = deviceOriginData.split("%");
        for (String dataString : dataBlocks) {
            parseDataBlock(dataYiAn8700A, dataString);
        }
        String dataString = JSON.toJSONString(dataYiAn8700A);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataYiAn8700A, JSON.toJSONString(new ParamDeviceDataPad(deviceCode, collectionNumber, dataString)));
    }


    /**
     * 解析一个数据块
     *
     * @param dataYiAn8700A 数据实体
     * @param dataBlock 数据块
     */
    private void parseDataBlock(DataYiAn8700A dataYiAn8700A, String dataBlock) {

        if (verifyData(dataBlock)) {
            // 去掉HL7协议数据的头和尾 不然解析不成功
            String trimHeadAndTailOfHL7 = dataBlock.substring(2, dataBlock.length() - 4);

            // 需要去掉每个串中的0D即换行符不然解析不成功
            String dataBlockRemoveCR = trimHeadAndTailOfHL7.replace("0D", "#");

            // 转换为小的数据块
            String[] smallDataBlock = dataBlockRemoveCR.split("#");
            for (String s : smallDataBlock) {
                // 获取真实的数据值
                String realData = DataParseUtils.hexStringToString(s);
                // 将真实的数据按照竖线划分
                String[] realDataBlock = realData.split("\\|");

                if (realDataBlock.length > 5) {

                    switch (realDataBlock[3]) {
                        case  "101^PEAK":
                            double peak = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setPeak(peak);
                            break;

                        case  "102^PLAT":
                            double plat = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setPlat(plat);
                            break;

                        case  "103^Pmean":
                            double pMean = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setPmean(pMean);
                            break;

                        case  "104^PEEP":
                            double peep = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setPeep(peep);
                            break;

                        case  "105^MV":
                            double mv = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setMv(mv);
                            break;

                        case  "106^Vte":
                            double vte = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setVte(vte);
                            break;

                        case  "107^Freq":
                            double freq = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setFreq(freq);
                            break;

                        case  "108^Fio2":
                            double fio2 = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setFio2(fio2);
                            break;

                        case  "120^Etco2":
                            double etco2 = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setEtco2(etco2);
                            break;

                        case  "121^fico2":
                            double fico2 = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setFico2(fico2);
                            break;

                        case  "123^N2OExp":
                            double n2OExp = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setN2oExp(n2OExp);
                            break;

                        case  "122^N2OInsp":
                            double N2OInsp = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setN2oInsp(N2OInsp);
                            break;

                        case  "134^MAC":
                            double mac = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setMac(mac);
                            break;

                        case  "151^AIR":
                            double air = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setAir(air);
                            break;

                        case  "150^N2O":
                            double n2O = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setN2o(n2O);
                            break;

                        case  "152^O2":
                            double o2 = Double.parseDouble(realDataBlock[5]);
                            dataYiAn8700A.setO2(o2);
                            break;

                        default:
                    }
                }
            }

        }


    }




    @Override
    public boolean verifyData(String data) {
        return data.startsWith(HL7_START_FLAG) && data.endsWith(HL7_END_FLAG);
    }

}
