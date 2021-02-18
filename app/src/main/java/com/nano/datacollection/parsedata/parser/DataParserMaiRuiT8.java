package com.nano.datacollection.parsedata.parser;

import com.alibaba.fastjson.JSON;
import com.nano.common.logger.Logger;
import com.nano.datacollection.DeviceData;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.datacollection.parsedata.DataParseUtils;
import com.nano.datacollection.parsedata.UpdateDataEntity;
import com.nano.datacollection.parsedata.entity.DataMaiRuiT8;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 迈瑞T8的数据采集器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/1 20:11
 */
public class DataParserMaiRuiT8 implements DeviceDataParser {

    private Logger logger = new Logger("[DataParserMaiRuiT8]");

    /**
     * HL7协议数据开始头部与结尾标志
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

        DataMaiRuiT8 dataMaiRuiT8 = new DataMaiRuiT8();
        dataMaiRuiT8.setSerialNumber(serialNumber);

        // 存放单个HL7数据块
        List<String> dataBlocks = new ArrayList<>(16);

        // Data: 0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3130367C507C322E332E317C0D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3130315E48527C323130317C36307C7C7C7C7C7C460D4F42587C7C4E4D7C3130325E505643737C323130317C307C7C7C7C7C7C460D4F42587C7C4E4D7C3130355E53542D497C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3130365E53542D49497C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3130375E53542D4949497C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3130385E53542D6156527C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3130395E53542D61564C7C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131305E53542D6156467C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131315E53542D56317C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131325E53542D56327C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131335E53542D56337C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131345E53542D56347C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131355E53542D56357C323130317C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3131365E53542D56367C323130317C2D3130302E30307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3130317C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130327C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130357C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130367C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130377C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130387C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3130397C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131307C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131317C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131327C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131337C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131347C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131357C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3131367C325E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3135315E52527C323130327C32307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3135317C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3230305E54317C323130347C33372E307C7C7C7C7C7C460D4F42587C7C4E4D7C3230315E54327C323130347C33372E327C7C7C7C7C7C460D4F42587C7C4E4D7C3230325E54447C323130347C302E327C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3230307C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3230317C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3230327C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3136305E53704F327C323130337C39387C7C7C7C7C7C460D4F42587C7C4E4D7C3136315E50527C323130337C36307C7C7C7C7C7C460D4F42587C7C4E4D7C3136325E50497C323130337C31322E3030307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3136307C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3136317C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3136327C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3630305E50527C323131357C36307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3630307C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3530305E5379737C323131367C3132307C7C7C7C7C7C460D4F42587C7C4E4D7C3530315E4D65616E7C323131367C39307C7C7C7C7C7C460D4F42587C7C4E4D7C3530325E4469617C323131367C37357C7C7C7C7C7C460D4F42587C7C4E4D7C3538375E5050567C323131367C377C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3530307C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3530317C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3530327C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3538377C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3530335E5379737C323131377C32357C7C7C7C7C7C460D4F42587C7C4E4D7C3530345E4D65616E7C323131377C31347C7C7C7C7C7C460D4F42587C7C4E4D7C3530355E4469617C323131377C397C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3530337C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3530347C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3530357C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3231325E432E492E7C323130387C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3231305E432E4F2E7C323130387C2D3130302E30307C7C7C7C7C7C460D4F42587C7C4E4D7C3231335E54427C323130387C33372E32307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3231327C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3231307C325E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3231337C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C3235305E4574434F327C323130367C33387C7C7C7C7C7C460D4F42587C7C4E4D7C3235315E4669434F327C323130367C327C7C7C7C7C7C460D4F42587C7C4E4D7C3235335E45744F327C323130367C3134347C7C7C7C7C7C460D4F42587C7C4E4D7C3235345E46694F327C323130367C3136307C7C7C7C7C7C460D4F42587C7C4E4D7C3235365E45744E324F7C323130367C34357C7C7C7C7C7C460D4F42587C7C4E4D7C3235375E46694E324F7C323130367C35307C7C7C7C7C7C460D4F42587C7C4E4D7C3236385E457449736F7C323130367C312E33397C7C7C7C7C7C460D4F42587C7C4E4D7C3236395E466949736F7C323130367C312E36397C7C7C7C7C7C460D4F42587C7C4E4D7C3238305E61775252284147297C323130367C32307C7C7C7C7C7C460D4F42587C7C4E4D7C3238315E4D41437C323130367C312E38317C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C3235307C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3235317C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3235337C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3235347C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3235367C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3235377C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3236387C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3236397C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3238307C335E7C7C7C7C7C7C460D4F42587C7C43457C323033375E7C3238317C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230347C507C322E332E317C0D4F42587C7C4E4D7C305E52527C307C32307C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C3230357C507C322E332E317C0D4F42587C7C43457C323033375E7C7C335E7C7C7C7C7C7C460D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C35347C507C322E332E317C0D1C0D0B4D53487C5E7E5C267C7C7C7C7C7C7C4F52555E5230317C35367C507C322E332E317C0D1C0D
        // 以0B为接线切分字符串
        String[] values = deviceOriginData.split(HL7_START_FLAG);

        // 此时数据块都没有了前缀0B，但依然有后缀1C0D
        for (String data : values) {
            if (data.endsWith(HL7_END_FLAG)) {
                // 去掉每一个数据串的最后4个字符
                dataBlocks.add(data.substring(0, data.length() - 4));
            }
        }

        for (String dataBlock : dataBlocks) {
            // 不能直接转成字符串，因为遇到不可解析的字符时后面的字符串会被省略掉
            parseOneDataBlock(dataMaiRuiT8, dataBlock);
        }

        String dataString = JSON.toJSONString(dataMaiRuiT8);
        // 返回解析好的数据
        return new DeviceData(deviceCode, dataMaiRuiT8, JSON.toJSONString(new UpdateDataEntity(deviceCode, dataString)));
    }


    /**
     * 解析一条数据块
     * @param dataMaiRuiT8 迈瑞数据
     * @param dataBlock 一个数据块
     */
    private static void parseOneDataBlock(DataMaiRuiT8 dataMaiRuiT8, String dataBlock) {

        // 需要去掉每个串中的0D即换行符不然解析不成功
        String dataBlockRemoveCR = dataBlock.replace("0D", "#");
        String[] smallDataBlock = dataBlockRemoveCR.split("#");
        for (String s : smallDataBlock) {
            // 获取真实的数据值
            String realData = DataParseUtils.hexStringToString(s);


            // 将真实的数据按照竖线划分
            String[] realDataBlock = realData.split("\\|");

            // 解析后的真实数据如下:
            //MSH|^~\&|||||||ORU^R01|106|P|2.3.1|
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|101^HR|2101|60||||||F
            //OBX||NM|102^PVCs|2101|0||||||F
            //OBX||NM|105^ST-I|2101|-100.00||||||F
            //OBX||NM|106^ST-II|2101|-100.00||||||F
            //OBX||NM|107^ST-III|2101|-100.00||||||F
            //OBX||NM|108^ST-aVR|2101|-100.00||||||F
            //OBX||NM|109^ST-aVL|2101|-100.00||||||F
            //OBX||NM|110^ST-aVF|2101|-100.00||||||F
            //OBX||NM|111^ST-V1|2101|-100.00||||||F
            //OBX||NM|112^ST-V2|2101|-100.00||||||F
            //OBX||NM|113^ST-V3|2101|-100.00||||||F
            //OBX||NM|114^ST-V4|2101|-100.00||||||F
            //OBX||NM|115^ST-V5|2101|-100.00||||||F
            //OBX||NM|116^ST-V6|2101|-100.00||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|101|3^||||||F
            //OBX||CE|2037^|102|3^||||||F
            //OBX||CE|2037^|105|2^||||||F
            //OBX||CE|2037^|106|2^||||||F
            //OBX||CE|2037^|107|2^||||||F
            //OBX||CE|2037^|108|2^||||||F
            //OBX||CE|2037^|109|2^||||||F
            //OBX||CE|2037^|110|2^||||||F
            //OBX||CE|2037^|111|2^||||||F
            //OBX||CE|2037^|112|2^||||||F
            //OBX||CE|2037^|113|2^||||||F
            //OBX||CE|2037^|114|2^||||||F
            //OBX||CE|2037^|115|2^||||||F
            //OBX||CE|2037^|116|2^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|151^RR|2102|20||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|151|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|200^T1|2104|37.0||||||F
            //OBX||NM|201^T2|2104|37.2||||||F
            //OBX||NM|202^TD|2104|0.2||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|200|3^||||||F
            //OBX||CE|2037^|201|3^||||||F
            //OBX||CE|2037^|202|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|160^SpO2|2103|98||||||F
            //OBX||NM|161^PR|2103|60||||||F
            //OBX||NM|162^PI|2103|12.000||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|160|3^||||||F
            //OBX||CE|2037^|161|3^||||||F
            //OBX||CE|2037^|162|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|600^PR|2115|60||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|600|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|500^Sys|2116|120||||||F
            //OBX||NM|501^Mean|2116|90||||||F
            //OBX||NM|502^Dia|2116|75||||||F
            //OBX||NM|587^PPV|2116|7||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|500|3^||||||F
            //OBX||CE|2037^|501|3^||||||F
            //OBX||CE|2037^|502|3^||||||F
            //OBX||CE|2037^|587|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|503^Sys|2117|25||||||F
            //OBX||NM|504^Mean|2117|14||||||F
            //OBX||NM|505^Dia|2117|9||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|503|3^||||||F
            //OBX||CE|2037^|504|3^||||||F
            //OBX||CE|2037^|505|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|212^C.I.|2108|-100.00||||||F
            //OBX||NM|210^C.O.|2108|-100.00||||||F
            //OBX||NM|213^TB|2108|37.20||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|212|2^||||||F
            //OBX||CE|2037^|210|2^||||||F
            //OBX||CE|2037^|213|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|250^EtCO2|2106|38||||||F
            //OBX||NM|251^FiCO2|2106|2||||||F
            //OBX||NM|253^EtO2|2106|144||||||F
            //OBX||NM|254^FiO2|2106|160||||||F
            //OBX||NM|256^EtN2O|2106|45||||||F
            //OBX||NM|257^FiN2O|2106|50||||||F
            //OBX||NM|268^EtIso|2106|1.39||||||F
            //OBX||NM|269^FiIso|2106|1.69||||||F
            //OBX||NM|280^awRR(AG)|2106|20||||||F
            //OBX||NM|281^MAC|2106|1.81||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^|250|3^||||||F
            //OBX||CE|2037^|251|3^||||||F
            //OBX||CE|2037^|253|3^||||||F
            //OBX||CE|2037^|254|3^||||||F
            //OBX||CE|2037^|256|3^||||||F
            //OBX||CE|2037^|257|3^||||||F
            //OBX||CE|2037^|268|3^||||||F
            //OBX||CE|2037^|269|3^||||||F
            //OBX||CE|2037^|280|3^||||||F
            //OBX||CE|2037^|281|3^||||||F
            //MSH|^~\&|||||||ORU^R01|204|P|2.3.1|
            //OBX||NM|0^RR|0|20||||||F
            //MSH|^~\&|||||||ORU^R01|205|P|2.3.1|
            //OBX||CE|2037^||3^||||||F
            //MSH|^~\&|||||||ORU^R01|54|P|2.3.1|
            //MSH|^~\&|||||||ORU^R01|56|P|2.3.1|
            if (realDataBlock.length > 5) {
                switch (realDataBlock[3]) {
                    // 对应屏幕最顶上的ECG值为: 60
                    case  "101^HR":
                        int ecgHeartRate = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setEcgHeartRate(ecgHeartRate);
                        break;
                        // 能解析但屏幕没有展示
                    case "102^PVCs":
                        int ecgPvcSum = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setEcgPvcSum(ecgPvcSum);
                        break;

                    case "105^ST-I":
                        double ecgStParameterValueST_I = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamI(ecgStParameterValueST_I);
                        break;

                    case "106^ST-II":
                        double ecgStParameterValueST_II = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamII(ecgStParameterValueST_II);
                        break;

                    case "107^ST-III":
                        double ecgStParameterValueST_III = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamIII(ecgStParameterValueST_III);
                        break;

                    case "108^ST-aVR":
                        double ecgStParameterValueST_avr = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamAvr(ecgStParameterValueST_avr);
                        break;

                    case "109^ST-aVL":
                        double ecgStParameterValueST_aVL = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamAvl(ecgStParameterValueST_aVL);
                        break;

                    case "110^ST-aVF":
                        double ecgStParameterValueST_aVF = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamAvf(ecgStParameterValueST_aVF);
                        break;

                    case "111^ST-V1":
                        double ecgStParameterValueST_V1 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV1(ecgStParameterValueST_V1);
                        break;

                    case "112^ST-V2":
                        double ecgStParameterValueST_V2 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV2(ecgStParameterValueST_V2);
                        break;

                    case "113^ST-V3":
                        double ecgStParameterValueST_V3 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV3(ecgStParameterValueST_V3);
                        break;

                    case "114^ST-V4":
                        double ecgStParameterValueST_V4 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV4(ecgStParameterValueST_V4);
                        break;

                    case "115^ST-V5":
                        double ecgStParameterValueST_V5 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV5(ecgStParameterValueST_V5);
                        break;

                    case "116^ST-V6":
                        double ecgStParameterValueST_V6 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setEcgStParamV6(ecgStParameterValueST_V6);
                        break;

                        // RESP 对应屏幕下方的黄色参数
                    case "151^RR":
                        int respRespirationRate = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setRespRespirationRate(respRespirationRate);
                        break;

                        // SpO2 对应屏幕上的蓝色参数(对应屏幕的SPO2参数)
                    case "160^SpO2":
                        int spo2PercentOxygenSaturation = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setSpo2PercentOxygenSaturation(spo2PercentOxygenSaturation);
                        break;

                        // PR 对应屏幕上的蓝色参数(对应屏幕蓝色的PR参数)
                    case "161^PR":
                        int spo2PulseRate = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setSpo2PulseRate(spo2PulseRate);
                        break;

                        // PI 屏幕无显示
                    case "162^PI":
                        double spo2PI = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setSpo2Pi(spo2PI);
                        break;

                    case "170^NIBP S":
                        double nibpSystolic = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setNibpSystolic(nibpSystolic);
                        break;

                    case "171^NIBP D":
                        double nibpDiastolic = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setNibpDiastolic(nibpDiastolic);
                        break;

                    case "172^NIBP M":
                        double nibpMean = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setNibpMean(nibpMean);
                        break;

                        // 温度Temperature1
                    case "200^T1":
                        double temp1 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setTempTemperature1(temp1);
                        break;

                        // 温度Temperature2
                    case "201^T2":
                        double temp2 = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setTempTemperature2(temp2);
                        break;

                        // 温度Temperature差值
                    case "202^TD":
                        double tempDifference = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setTempTemperatureDifference(tempDifference);
                        break;

                        // ART模块产生的有创血压参数 对应屏幕红色的参数 120.
                    case "500^Sys":
                        int artInvasiveBloodPressureSystolic = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setArtIbpSystolic(artInvasiveBloodPressureSystolic);
                        break;

                        // ART模块产生的有创血压参数 对应屏幕红色的参数 93.
                    case "501^Mean":
                        int artInvasiveBloodPressureMean = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setArtIbpMean(artInvasiveBloodPressureMean);
                        break;

                        // ART模块产生的有创血压参数 对应屏幕红色的参数 80.
                    case "502^Dia":
                        int artInvasiveBloodPressureDiastolic = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setArtIbpDiastolic(artInvasiveBloodPressureDiastolic);
                        break;

                        // PA模块产生的有创血压参数 屏幕不显示
                    case "503^Sys":
                        int paInvasiveBloodPressureSystolic = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setPaIbpSystolic(paInvasiveBloodPressureSystolic);
                        break;

                    // PA模块产生的有创血压参数 屏幕不显示
                    case "504^Mean":
                        int paInvasiveBloodPressureMean = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setPaIbpMean(paInvasiveBloodPressureMean);
                        break;

                    // PA模块产生的有创血压参数 屏幕不显示
                    case "505^Dia":
                        int paInvasiveBloodPressureDiastolic = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setPaIbpDiastolic(paInvasiveBloodPressureDiastolic);
                        break;

                        // 貌似是屏幕上的CVP参数
                    case "587^PPV":
                        double artPpv = Double.parseDouble(realDataBlock[5]);
                        dataMaiRuiT8.setArtPpv(artPpv);
                        break;

                        // 屏幕无展示 (来自PR模块的PR参数)
                    case "600^PR":
                        int prPr = Integer.parseInt(realDataBlock[5]);
                        dataMaiRuiT8.setPrPr(prPr);
                        break;

                    // 下面的参数是存疑的,感觉有点像麻醉机的参数
//                    case "212^C.I.":
//                        double coCardiacOutput = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setCoCardiacOutput(coCardiacOutput);
//                        break;
//
//                    case "210^C.O.":
//                        double coCardiacIndex = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setCoCardiacIndex(coCardiacIndex);
//                        break;
//
//                    case "213^TB":
//                        double coBloodPressure = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setCoBloodPressure(coBloodPressure);
//                        break;
//
//                    case "250^EtCO2":
//                        int etCo2 = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgCo2Et(etCo2);
//                        break;
//
//                    case "251^FiCO2":
//                        int fiCo2 = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgCo2Fi(fiCo2);
//                        break;
//
//                    case "253^EtO2":
//                        int o2Et = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgO2Et(o2Et);
//                        break;
//
//                    case "254^FiO2":
//                        int o2Fi = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgO2Fi(o2Fi);
//                        break;
//
//                    case "256^EtN2O":
//                        int n2oEt = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgN2oEt(n2oEt);
//                        break;
//
//                    case "257^FiN2O":
//                        int n2oFi = (int) Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgN2oFi(n2oFi);
//                        break;
//
//                    case "268^EtIso":
//                        double isoEt = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgIsoEt(isoEt);
//                        break;
//
//                    case "269^FiIso":
//                        double isoFi = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgIsoFi(isoFi);
//                        break;
//
//                    case "280^awRR(AG)":
//                        double agAwRr = Double.parseDouble(realDataBlock[5]);
//                        dataMaiRuiT8.setAgAwRr(agAwRr);
//                        break;
                    default:
                }
            }
        }

    }

    /**
     * 检验数据合法性
     * @param data 仪器数据
     * @return 是否合法
     */
    @Override
    public boolean verifyData(String data) {
        return data.startsWith(HL7_START_FLAG) && data.endsWith(HL7_END_FLAG);
    }
}
