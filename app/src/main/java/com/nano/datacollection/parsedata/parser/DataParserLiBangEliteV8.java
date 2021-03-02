package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.ParamDeviceDataPad;
import com.nano.datacollection.parsedata.entity.DataLiBangEliteV8;

/**
 * Description: 理邦EliteV8数据解析器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/10 14:26
 */
public class DataParserLiBangEliteV8 implements DeviceDataParser {

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

        DataLiBangEliteV8 dataLiBangEliteV8 = new DataLiBangEliteV8();
        dataLiBangEliteV8.setSerialNumber(serialNumber);
        dataLiBangEliteV8.setCollectionNumber(collectionNumber);

        if (verifyData(deviceOriginData)) {
            // 获取真实数据
            String data = deviceOriginData.replace("\t", "");
            // 数据切分
            String[] dataBlocks = data.split("<OBX>");

            // 一个小数据块长这样
            // <OBX.2>NM</OBX.2>
            // <OBX.3>T1</OBX.3>
            // <OBX.5>36.00</OBX.5>
            // <OBX.6>C</OBX.6>
            // <OBX.7>36.00-39.00</OBX.7>
            // </OBX>
            for (String dataBlock : dataBlocks) {
                String removeEndDataBlock = dataBlock.replace("\n", "%");
                // System.out.println(removeEndDataBlock);
                // %<OBX.2>NM</OBX.2>%<OBX.3>HR</OBX.3>%<OBX.5>60</OBX.5>%<OBX.6>bpm</OBX.6>%<OBX.7>50-120</OBX.7>%</OBX>%
                // %<OBX.2>NM</OBX.2>%<OBX.3>PVCs</OBX.3>%<OBX.5>0</OBX.5>%<OBX.6>/min</OBX.6>%<OBX.7>&lt;10</OBX.7>%</OBX>%
                // %<OBX.2>NM</OBX.2>%<OBX.3>RR</OBX.3>%<OBX.5>14</OBX.5>%<OBX.6>rpm</OBX.6>%<OBX.7>8-30</OBX.7>%</OBX>%
                // %<OBX.2>NM</OBX.2>%<OBX.3>SpO2</OBX.3>%<OBX.5>99</OBX.5>%<OBX.6>%</OBX.6>%<OBX.7>90-100</OBX.7>%</OBX>%
                // %<OBX.2>NM</OBX.2>%<OBX.3>PR</OBX.3>%<OBX.5>60</OBX.5>%<OBX.6>bpm</OBX.6>%<OBX.7>50-120</OBX.7>%</OBX>%
                if (verifyDataBlock(removeEndDataBlock)) {
                    //System.out.println("解析一个小数据块:");
                    parseOneDataBlock(dataLiBangEliteV8, removeEndDataBlock);
                }
            }
        }

        String dataString = JSON.toJSONString(dataLiBangEliteV8);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataLiBangEliteV8, JSON.toJSONString(new ParamDeviceDataPad(deviceCode, collectionNumber, dataString)));
    }


    /**
     * 解析一条数据块
     *
     * @param dataLiBangEliteV8 实体
     * @param dataBlock         数据块
     */
    private static void parseOneDataBlock(DataLiBangEliteV8 dataLiBangEliteV8, String dataBlock) {
        // 一个小数据块长这样
        // <OBX.2>NM</OBX.2>
        // <OBX.3>T1</OBX.3>
        // <OBX.5>36.00</OBX.5>
        // <OBX.6>C</OBX.6>
        // <OBX.7>36.00-39.00</OBX.7>
        // </OBX>
        String data1 = dataBlock.replace("<", "#");
        String data2 = data1.replace(">", "#");
        // 获取每一行
        String[] smallDataBlocks = data2.split("%");

        // 替换#号之后小数据块长这样
        // #OBX.2#NM#/OBX.2#
        // #OBX.3#T1#/OBX.3#
        // #OBX.5#36.00#/OBX.5#
        // #OBX.6#C#/OBX.6#
        // #OBX.7#36.00-39.00#/OBX.7#
        // #/OBX#

        if (smallDataBlocks.length >= 6) {
            // 获取数据类型的字符串 --T1
            String valueKey = smallDataBlocks[2].split("#")[2];
            switch (valueKey) {
                // 体重 60
                case "WEIGHT":
                    double weight = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setWeight(weight);
                    break;
                    // 身高 175
                case "HEIGHT":
                    double height = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setHeight(height);
                    break;
                    // 顶部绿色参数
                case "HR":
                    double HR = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setHr(HR);
                    break;
                    // 顶部绿色参数(很小)
                case "PVCs":
                    double PVCs = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setPvcs(PVCs);
                    break;
                    // 呼吸率 黄色参数 14
                case "RR":
                    double RR = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setRr(RR);
                    break;
                    // 蓝色参数 99
                case "SpO2":
                    double SpO2 = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setSpo2(SpO2);
                    break;
                    // 蓝色参数 60
                case "PR":
                    double PR = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setPr(PR);
                    break;
                    // 温度1 37
                case "T1":
                    double temp1 = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setTemp1(temp1);
                    break;
                    // 温度2 37
                case "T2":
                    double temp2 = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setTemp1(temp2);
                    break;
                    // 中间的红色参数
                case "CVP_MAP":
                    double CVP_MAP = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setCvpMap(CVP_MAP);
                    break;
                    // 左下角红色参数
                case "LAP_MAP":
                    double LAP_MAP = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setLapMap(LAP_MAP);
                    break;
                    // ART相关参数
                case "Art_SYS":
                    double Art_SYS = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setArtSys(Art_SYS);
                    break;
                case "Art_DIA":
                    double Art_DIA = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setArtDia(Art_DIA);
                    break;
                case "Art_MAP":
                    double Art_MAP = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setArtMap(Art_MAP);
                    break;
                    // P2相关参数
                case "P2_SYS":
                    double P2_SYS = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setP2Sys(P2_SYS);
                    break;
                case "P2_DIA":
                    double P2_DIA = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setP2Dia(P2_DIA);
                    break;
                case "P2_MAP":
                    double P2_MAP = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setP2Map(P2_MAP);
                    break;
                    // 下面的参数是NIBP的参数,演示模式没有这个数据但是根据协议应该会有
                case "NIBP_SYS":
                    double nibpSys = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setNibpSys(nibpSys);
                    break;
                case "NIBP_DIA":
                    double nibpDia = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setNibpSys(nibpDia);
                    break;
                case "NIBP_MAP":
                    double nibpMap = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setNibpSys(nibpMap);
                    break;
                case "NIBP_PR":
                    double nibpPr = Double.parseDouble(smallDataBlocks[3].split("#")[2]);
                    dataLiBangEliteV8.setNibpSys(nibpPr);
                    break;

                default:
            }

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
        // 判断是不是有标准的开头与结尾
        return data.startsWith("<ORU_R01>") && data.endsWith("</ORU_R01>");
    }


    /**
     * 验证一个小的数据块是否正确
     *
     * @param dataBlock 数据块
     * @return 是否合理
     */
    private static boolean verifyDataBlock(String dataBlock) {
        return dataBlock.startsWith("%<OBX.2>") && dataBlock.endsWith("</OBX>%");
    }

}
