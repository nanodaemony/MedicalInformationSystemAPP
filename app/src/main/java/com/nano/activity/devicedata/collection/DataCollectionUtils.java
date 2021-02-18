package com.nano.activity.devicedata.collection;

import com.nano.activity.devicedata.collection.fragment.FragmentAiQin600A;
import com.nano.activity.devicedata.collection.fragment.FragmentAiQin600B;
import com.nano.activity.devicedata.collection.fragment.FragmentAiQin600C;
import com.nano.activity.devicedata.collection.fragment.FragmentBaoLaiTeA8;
import com.nano.activity.devicedata.collection.fragment.FragmentLiBangV8;
import com.nano.activity.devicedata.collection.fragment.FragmentMaiRuiT8;
import com.nano.activity.devicedata.collection.fragment.FragmentMaiRuiWatoex65;
import com.nano.activity.devicedata.collection.fragment.FragmentNuoHe;
import com.nano.activity.devicedata.collection.fragment.FragmentPuKe;
import com.nano.activity.devicedata.collection.fragment.FragmentYiAn8700A;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.datacollection.parsedata.parser.DataParserAiQin600A;
import com.nano.datacollection.parsedata.parser.DataParserAiQin600B;
import com.nano.datacollection.parsedata.parser.DataParserAiQin600C;
import com.nano.datacollection.parsedata.parser.DataParserBaoLaiTeA8;
import com.nano.datacollection.parsedata.parser.DataParserLiBangEliteV8;
import com.nano.datacollection.parsedata.parser.DataParserMaiRuiT8;
import com.nano.datacollection.parsedata.parser.DataParserMaiRuiWatoex65;
import com.nano.datacollection.parsedata.parser.DataParserNuoHeV405;
import com.nano.datacollection.parsedata.parser.DataParserPuKe;
import com.nano.datacollection.parsedata.parser.DataParserYiAn8700A;
import com.nano.datacollection.serial.CollectorAIQin600A;
import com.nano.datacollection.serial.CollectorAIQin600B;
import com.nano.datacollection.serial.CollectorAIQin600C;
import com.nano.datacollection.tcp.libang.CollectorLiBangEliteV8Sync;
import com.nano.datacollection.tcp.mairuit8.CollectorMaiRuiBeneViewT8;
import com.nano.datacollection.tcp.mairuiwatoex65.CollectorMaiRuiWatoex65Async;
import com.nano.datacollection.udp.baolaite.CollectorBaoLaiTeA8;
import com.nano.datacollection.udp.nuohe.CollectorNuoHeNW9002S;
import com.nano.datacollection.udp.puke.CollectorPuKeYY106;
import com.nano.datacollection.udp.yian8700a.CollectorYiAn8700ASync;
import com.nano.device.MedicalDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: Fragment的工具类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 15:12
 */
public class DataCollectionUtils {

    /**
     * 生成使用仪器的UnityMap
     *
     * @param usedDeviceList 使用仪器列表
     * @return UnityMap
     */
    public static Map<Integer, DeviceUnity> getUsedDeviceUnityMap(List<MedicalDevice> usedDeviceList) {

        Map<Integer, DeviceUnity> unityMap = new HashMap<>();
        // 遍历使用的仪器
        for (MedicalDevice device : usedDeviceList) {
            DeviceUnity unity;
            switch (device.getDeviceEnum()) {
                case NUO_HE_NW9002S:
                    FragmentNuoHe fragmentNuoHe = new FragmentNuoHe();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentNuoHe, fragmentNuoHe);
                    DataParserNuoHeV405 parserNuoHe = new DataParserNuoHeV405();
                    device.setDataParser(parserNuoHe);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentNuoHe);
                    // 关联采集器与Device
                    CollectorNuoHeNW9002S collectorNuoHeNW9002SSync = new CollectorNuoHeNW9002S(device);
                    unity.setCollector(collectorNuoHeNW9002SSync);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case PU_KE_YY106:
                    FragmentPuKe fragmentPuKe = new FragmentPuKe();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentPuKe, fragmentPuKe);
                    DataParserPuKe parserPuKe = new DataParserPuKe();
                    device.setDataParser(parserPuKe);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentPuKe);
                    // 关联采集器与Device
                    CollectorPuKeYY106 collectorPuKeYY106 = new CollectorPuKeYY106(device);
                    unity.setCollector(collectorPuKeYY106);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case BAO_LAI_TE_A8:
                    FragmentBaoLaiTeA8 fragmentBaoLaiTeA8 = new FragmentBaoLaiTeA8();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentBaoLaiTeA8, fragmentBaoLaiTeA8);
                    DataParserBaoLaiTeA8 parserBaoLaiTeA8 = new DataParserBaoLaiTeA8();
                    device.setDataParser(parserBaoLaiTeA8);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentBaoLaiTeA8);
                    // 关联采集器与Device
                    CollectorBaoLaiTeA8 collectorBaoLaiTeA8 = new CollectorBaoLaiTeA8(device);
                    unity.setCollector(collectorBaoLaiTeA8);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case YI_AN_8700A:
                    FragmentYiAn8700A fragmentYiAn8700A = new FragmentYiAn8700A();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentYiAn8700A, fragmentYiAn8700A);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentYiAn8700A);
                    DataParserYiAn8700A dataParserYiAn8700A = new DataParserYiAn8700A();
                    device.setDataParser(dataParserYiAn8700A);
                    CollectorYiAn8700ASync collectorYiAn8700ASync = new CollectorYiAn8700ASync(device);
                    unity.setCollector(collectorYiAn8700ASync);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case MAI_RUI_T8:
                    FragmentMaiRuiT8 fragmentMaiRuiT8 = new FragmentMaiRuiT8();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentMaiRuiT8, fragmentMaiRuiT8);
                    DataParserMaiRuiT8 parserMaiRuiT8 = new DataParserMaiRuiT8();
                    device.setDataParser(parserMaiRuiT8);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentMaiRuiT8);
                    // 关联采集器与Device
                    CollectorMaiRuiBeneViewT8 collectorMaiRuiT8Async = new CollectorMaiRuiBeneViewT8(device);
                    unity.setCollector(collectorMaiRuiT8Async);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case MAI_RUI_WATOEX65:
                    FragmentMaiRuiWatoex65 fragmentMaiRuiWatoex65 = new FragmentMaiRuiWatoex65();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentMaiRuiWatoex65, fragmentMaiRuiWatoex65);
                    DataParserMaiRuiWatoex65 dataParserMaiRuiWatoex65 = new DataParserMaiRuiWatoex65();
                    device.setDataParser(dataParserMaiRuiWatoex65);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentMaiRuiWatoex65);
                    // 关联采集器与Device
                    CollectorMaiRuiWatoex65Async collectorMaiRuiWatoex65Async = new CollectorMaiRuiWatoex65Async(device);
                    unity.setCollector(collectorMaiRuiWatoex65Async);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case LI_BANG_ELITEV8:
                    FragmentLiBangV8 fragmentLiBangV8 = new FragmentLiBangV8();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentLiBangV8, fragmentLiBangV8);
                    DataParserLiBangEliteV8 dataParserLiBangEliteV8 = new DataParserLiBangEliteV8();
                    device.setDataParser(dataParserLiBangEliteV8);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentLiBangV8);
                    // 关联采集器与Device
                    CollectorLiBangEliteV8Sync collectorLiBangEliteV8Sync = new CollectorLiBangEliteV8Sync(device);
                    unity.setCollector(collectorLiBangEliteV8Sync);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case AI_QIN_EGOS600A:
                    FragmentAiQin600A fragmentAiQin600A = new FragmentAiQin600A();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentAiQin600A, fragmentAiQin600A);
                    DataParserAiQin600A dataParserAiQin600A = new DataParserAiQin600A();
                    device.setDataParser(dataParserAiQin600A);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentAiQin600A);
                    // 关联采集器与Device
                    CollectorAIQin600A collectorAIQin600A = new CollectorAIQin600A(device);
                    unity.setCollector(collectorAIQin600A);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case AI_QIN_EGOS600B:
                    FragmentAiQin600B fragmentAiQin600B = new FragmentAiQin600B();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentAiQin600B, fragmentAiQin600B);
                    DataParserAiQin600B dataParserAiQin600B = new DataParserAiQin600B();
                    device.setDataParser(dataParserAiQin600B);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentAiQin600B);
                    // 关联采集器与Device
                    CollectorAIQin600B collectorAIQin600B = new CollectorAIQin600B(device);
                    unity.setCollector(collectorAIQin600B);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case AI_QIN_EGOS600C:
                    FragmentAiQin600C fragmentAiQin600C = new FragmentAiQin600C();
                    unity = new DeviceUnity(device.getDeviceCode(), device, device.getDeviceEnum(), fragmentAiQin600C, fragmentAiQin600C);
                    DataParserAiQin600C dataParserAiQin600C = new DataParserAiQin600C();
                    device.setDataParser(dataParserAiQin600C);
                    // 将Fragment与仪关联(用于数据上传与更新)
                    device.setFragmentDataExchanger(fragmentAiQin600C);
                    // 关联采集器与Device
                    CollectorAIQin600C collectorAIQin600C = new CollectorAIQin600C(device);
                    unity.setCollector(collectorAIQin600C);
                    unityMap.put(device.getDeviceCode(), unity);
                    break;
                case MING_XI_MNIR_P100:
                    break;
                case MEI_DUN_LI_EEG_VISTA:
                    break;
                case MEI_DUN_LI_5100C:
                    break;
                case KE_MAN_AX700:
                    break;
                case CHANG_FENG_ACM608B:
                    break;
                case DRAGER_FABIUS_PLUS:
                    break;
                case PU_BO_BOARAY700:
                    break;
                case CHEN_WEI_CWH302:
                    break;
                case MAI_RUI_SV300:
                    break;
                case YI_AN_VT5250:
                    break;
                case CHEN_WEI_CWH3020B:
                    break;
                case SHU_PU_SI_DA_S1200:
                    break;
                case DRAGER_V300:
                    break;
                case KE_MAN_C90:
                    break;
                case PHILIPS_MX:
                    break;
                case TAI_JI_TD3200A:
                    break;
                case WEI_HAO_KANG_ANGEL6000D:
                    break;
                case MASIMO_RADICAL7:
                    break;

                default:
            }

        }
        return unityMap;
    }


    /**
     * 判断当前是不是第一个开始采集的仪器
     *
     * @param deviceUnityMap 仪器合计Map
     */
    public static boolean isFirstDeviceToStartCollection(Map<Integer, DeviceUnity> deviceUnityMap) {
        int collectingNumber = 0;
        for (Map.Entry<Integer, DeviceUnity> entry : deviceUnityMap.entrySet()) {
            if (entry.getValue().getMedicalDevice().getStatusEnum() == CollectionStatusEnum.COLLECTING) {
                collectingNumber++;
            }
        }
        return collectingNumber == 1;
    }


    /**
     * 判断当前是不是最后一个结束采集的仪器
     *
     * @param deviceUnityMap 仪器合计Map
     */
    public static boolean isLastDeviceToStopCollection(Map<Integer, DeviceUnity> deviceUnityMap) {
        for (Map.Entry<Integer, DeviceUnity> entry : deviceUnityMap.entrySet()) {
            if (entry.getValue().getMedicalDevice().getStatusEnum() != CollectionStatusEnum.FINISHED) {
                return false;
            }
        }
        return true;
    }

}
