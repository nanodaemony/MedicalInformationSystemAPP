package com.nano.device;

import com.nano.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.nano.AppStatic.medicalDeviceMap;

/**
 * 仪器工具类
 * @author cz
 */
public class DeviceUtil {

    /**
     * 医疗仪器类实体
     */
    public static MedicalDevice medicalDeviceNuoHeNW900S;
    public static MedicalDevice medicalDevicePuKeYY106;
    public static MedicalDevice medicalDeviceBaoLaiTeA8;
    public static MedicalDevice medicalDeviceYiAn8700A;

    public static MedicalDevice medicalDeviceMaiRuiT8;
    public static MedicalDevice medicalDeviceMaiRuiWatoex65;
    public static MedicalDevice medicalDeviceLiBangEliteV8;

    public static MedicalDevice medicalDeviceAiQinRGOS600A;
    public static MedicalDevice medicalDeviceAiQinRGOS600B;
    public static MedicalDevice medicalDeviceAiQinRGOS600C;
    public static MedicalDevice medicalDeviceMingXiMNIRP100;
    public static MedicalDevice medicalDeviceMeiDunLiEEGVista;
    public static MedicalDevice medicalDeviceMeiDunLi5100C;

    public static MedicalDevice medicalDeviceKeManAX700;
    public static MedicalDevice medicalDeviceChangFengACM608B;
    public static MedicalDevice medicalDeviceDragerFabiusPlus;
    public static MedicalDevice medicalDevicePuBoBoaray700;
    public static MedicalDevice medicalDeviceChenWeiCWH302;
    public static MedicalDevice medicalDeviceMaiRuiSV300;
    public static MedicalDevice medicalDeviceYiAnVT5250;
    public static MedicalDevice medicalDeviceChenWeiCWH3020B;
    public static MedicalDevice medicalDeviceShuPuSiDaS1200;
    public static MedicalDevice medicalDeviceDragerV300;
    public static MedicalDevice medicalDeviceKeManC90;
    public static MedicalDevice medicalDevicePhilipsMX;
    public static MedicalDevice medicalDeviceTaiJiTD3200A;
    public static MedicalDevice medicalDeviceWeiHaoKangAngle6000D;
    public static MedicalDevice medicalDeviceMasimoRadical7;


    /**
     * 初始化
     *
     * 存入仪器信息
     */
    public static void init() {

        // UDP
        medicalDeviceNuoHeNW900S = new MedicalDevice(DeviceEnum.NUO_HE_NW9002S);
        medicalDevicePuKeYY106 = new MedicalDevice(DeviceEnum.PU_KE_YY106);
        medicalDeviceBaoLaiTeA8 = new MedicalDevice(DeviceEnum.BAO_LAI_TE_A8);
        medicalDeviceYiAn8700A = new MedicalDevice(DeviceEnum.YI_AN_8700A);

        // TCP
        medicalDeviceMaiRuiT8 =  new MedicalDevice(DeviceEnum.MAI_RUI_T8);
        medicalDeviceMaiRuiWatoex65 = new MedicalDevice(DeviceEnum.MAI_RUI_WATOEX65);
        medicalDeviceLiBangEliteV8 = new MedicalDevice(DeviceEnum.LI_BANG_ELITEV8);

        // Serial
        medicalDeviceAiQinRGOS600A = new MedicalDevice(DeviceEnum.AI_QIN_EGOS600A);
        medicalDeviceAiQinRGOS600B = new MedicalDevice(DeviceEnum.AI_QIN_EGOS600B);
        medicalDeviceAiQinRGOS600C = new MedicalDevice(DeviceEnum.AI_QIN_EGOS600C);
        medicalDeviceMingXiMNIRP100 = new MedicalDevice(DeviceEnum.MING_XI_MNIR_P100);
        medicalDeviceMeiDunLiEEGVista = new MedicalDevice(DeviceEnum.MEI_DUN_LI_EEG_VISTA);
        medicalDeviceMeiDunLi5100C = new MedicalDevice(DeviceEnum.MEI_DUN_LI_5100C);

        // Unknown
        medicalDeviceKeManAX700 = new MedicalDevice(DeviceEnum.KE_MAN_AX700);
        medicalDeviceChangFengACM608B = new MedicalDevice(DeviceEnum.CHANG_FENG_ACM608B);
        medicalDeviceDragerFabiusPlus = new MedicalDevice(DeviceEnum.DRAGER_FABIUS_PLUS);
        medicalDevicePuBoBoaray700 = new MedicalDevice(DeviceEnum.PU_BO_BOARAY700);
        medicalDeviceChenWeiCWH302 = new MedicalDevice(DeviceEnum.CHEN_WEI_CWH302);
        medicalDeviceMaiRuiSV300 = new MedicalDevice(DeviceEnum.MAI_RUI_SV300);
        medicalDeviceYiAnVT5250 = new MedicalDevice(DeviceEnum.YI_AN_VT5250);
        medicalDeviceChenWeiCWH3020B = new MedicalDevice(DeviceEnum.CHEN_WEI_CWH3020B);
        medicalDeviceShuPuSiDaS1200 = new MedicalDevice(DeviceEnum.SHU_PU_SI_DA_S1200);
        medicalDeviceDragerV300 = new MedicalDevice(DeviceEnum.DRAGER_V300);
        medicalDeviceKeManC90 = new MedicalDevice(DeviceEnum.KE_MAN_C90);
        medicalDevicePhilipsMX = new MedicalDevice(DeviceEnum.PHILIPS_MX);
        medicalDeviceTaiJiTD3200A = new MedicalDevice(DeviceEnum.TAI_JI_TD3200A);
        medicalDeviceWeiHaoKangAngle6000D = new MedicalDevice(DeviceEnum.WEI_HAO_KANG_ANGEL6000D);
        medicalDeviceMasimoRadical7 = new MedicalDevice(DeviceEnum.MASIMO_RADICAL7);

        // 设置这个仪器的图片资源(OK)
        medicalDeviceNuoHeNW900S.setDeviceImageSource(R.mipmap.device_nuohe_nw9002s);
        medicalDevicePuKeYY106.setDeviceImageSource(R.mipmap.device_puke_yy106);
        medicalDeviceBaoLaiTeA8.setDeviceImageSource(R.mipmap.device_baolaite_a8);
        medicalDeviceYiAn8700A.setDeviceImageSource(R.mipmap.device_yian_8700a);
        medicalDeviceMaiRuiT8.setDeviceImageSource(R.mipmap.device_mai_rui_t8);
        medicalDeviceMaiRuiWatoex65.setDeviceImageSource(R.mipmap.device_mai_rui_watoex_65);
        medicalDeviceLiBangEliteV8.setDeviceImageSource(R.mipmap.device_libang_elite8);
        medicalDeviceAiQinRGOS600A.setDeviceImageSource(R.mipmap.device_aiqin_egos_600a);
        medicalDeviceAiQinRGOS600B.setDeviceImageSource(R.mipmap.device_aiqin_egos_600b);
        medicalDeviceAiQinRGOS600C.setDeviceImageSource(R.mipmap.device_aiqin_egos_600c);
        medicalDeviceMingXiMNIRP100.setDeviceImageSource(R.mipmap.mingxi_mnir_p100);
        medicalDeviceMeiDunLiEEGVista.setDeviceImageSource(R.mipmap.device_meidunli_vista);
        medicalDeviceMeiDunLi5100C.setDeviceImageSource(R.mipmap.device_meidunli_5100c);
        medicalDeviceKeManAX700.setDeviceImageSource(R.mipmap.device_keman_ax700);
        medicalDeviceChangFengACM608B.setDeviceImageSource(R.mipmap.device_changfeng_acm608b);
        medicalDeviceDragerFabiusPlus.setDeviceImageSource(R.mipmap.device_drager_fabius_plus);
        medicalDevicePuBoBoaray700.setDeviceImageSource(R.mipmap.device_pubo_boaray700);
        medicalDeviceChenWeiCWH302.setDeviceImageSource(R.mipmap.device_chenwei_cwh302);
        medicalDeviceMaiRuiSV300.setDeviceImageSource(R.mipmap.device_mairui_sv300);
        medicalDeviceYiAnVT5250.setDeviceImageSource(R.mipmap.device_yian_vt5250);
        medicalDeviceChenWeiCWH3020B.setDeviceImageSource(R.mipmap.device_chenwei_cwh3020b);
        medicalDeviceShuPuSiDaS1200.setDeviceImageSource(R.mipmap.device_shupusida_s1200);
        medicalDeviceDragerV300.setDeviceImageSource(R.mipmap.device_drager_v300);
        medicalDeviceKeManC90.setDeviceImageSource(R.mipmap.device_keman_c90);
        medicalDevicePhilipsMX.setDeviceImageSource(R.mipmap.device_philips_mx);
        medicalDeviceTaiJiTD3200A.setDeviceImageSource(R.mipmap.device_taiji_td3200a);
        medicalDeviceWeiHaoKangAngle6000D.setDeviceImageSource(R.mipmap.device_weihaokang_angel6000a);
        medicalDeviceMasimoRadical7.setDeviceImageSource(R.mipmap.device_masimo_radical7);

        // 将仪器信息存入Map中(OK)
        medicalDeviceMap.put(DeviceEnum.NUO_HE_NW9002S.getDeviceCode(), medicalDeviceNuoHeNW900S);
        medicalDeviceMap.put(DeviceEnum.PU_KE_YY106.getDeviceCode(), medicalDevicePuKeYY106);
        medicalDeviceMap.put(DeviceEnum.BAO_LAI_TE_A8.getDeviceCode(), medicalDeviceBaoLaiTeA8);
        medicalDeviceMap.put(DeviceEnum.YI_AN_8700A.getDeviceCode(), medicalDeviceYiAn8700A);

        medicalDeviceMap.put(DeviceEnum.MAI_RUI_T8.getDeviceCode(), medicalDeviceMaiRuiT8);
        medicalDeviceMap.put(DeviceEnum.MAI_RUI_WATOEX65.getDeviceCode(), medicalDeviceMaiRuiWatoex65);
        medicalDeviceMap.put(DeviceEnum.LI_BANG_ELITEV8.getDeviceCode(), medicalDeviceLiBangEliteV8);

        medicalDeviceMap.put(DeviceEnum.AI_QIN_EGOS600A.getDeviceCode(), medicalDeviceAiQinRGOS600A);
        medicalDeviceMap.put(DeviceEnum.AI_QIN_EGOS600B.getDeviceCode(), medicalDeviceAiQinRGOS600B);
        medicalDeviceMap.put(DeviceEnum.AI_QIN_EGOS600C.getDeviceCode(), medicalDeviceAiQinRGOS600C);
        medicalDeviceMap.put(DeviceEnum.MING_XI_MNIR_P100.getDeviceCode(), medicalDeviceMingXiMNIRP100);
        medicalDeviceMap.put(DeviceEnum.MEI_DUN_LI_EEG_VISTA.getDeviceCode(), medicalDeviceMeiDunLiEEGVista);
        medicalDeviceMap.put(DeviceEnum.MEI_DUN_LI_5100C.getDeviceCode(), medicalDeviceMeiDunLi5100C);

        medicalDeviceMap.put(DeviceEnum.KE_MAN_AX700.getDeviceCode(), medicalDeviceKeManAX700);
        medicalDeviceMap.put(DeviceEnum.CHANG_FENG_ACM608B.getDeviceCode(), medicalDeviceChangFengACM608B);
        medicalDeviceMap.put(DeviceEnum.DRAGER_FABIUS_PLUS.getDeviceCode(), medicalDeviceDragerFabiusPlus);
        medicalDeviceMap.put(DeviceEnum.PU_BO_BOARAY700.getDeviceCode(), medicalDevicePuBoBoaray700);
        medicalDeviceMap.put(DeviceEnum.CHEN_WEI_CWH302.getDeviceCode(), medicalDeviceChenWeiCWH302);
        medicalDeviceMap.put(DeviceEnum.MAI_RUI_SV300.getDeviceCode(), medicalDeviceMaiRuiSV300);
        medicalDeviceMap.put(DeviceEnum.YI_AN_VT5250.getDeviceCode(), medicalDeviceYiAnVT5250);
        medicalDeviceMap.put(DeviceEnum.CHEN_WEI_CWH3020B.getDeviceCode(), medicalDeviceChenWeiCWH3020B);
        medicalDeviceMap.put(DeviceEnum.SHU_PU_SI_DA_S1200.getDeviceCode(), medicalDeviceShuPuSiDaS1200);
        medicalDeviceMap.put(DeviceEnum.DRAGER_V300.getDeviceCode(), medicalDeviceDragerV300);
        medicalDeviceMap.put(DeviceEnum.KE_MAN_C90.getDeviceCode(), medicalDeviceKeManC90);
        medicalDeviceMap.put(DeviceEnum.PHILIPS_MX.getDeviceCode(), medicalDevicePhilipsMX);
        medicalDeviceMap.put(DeviceEnum.TAI_JI_TD3200A.getDeviceCode(), medicalDeviceTaiJiTD3200A);
        medicalDeviceMap.put(DeviceEnum.WEI_HAO_KANG_ANGEL6000D.getDeviceCode(), medicalDeviceWeiHaoKangAngle6000D);
        medicalDeviceMap.put(DeviceEnum.MASIMO_RADICAL7.getDeviceCode(), medicalDeviceMasimoRadical7);
    }


    /**
     * 获取全部仪器信息
     */
    public static List<MedicalDevice> getMedicalDeviceList() {
        List<MedicalDevice> medicalDeviceList = new ArrayList<>();
        for (Map.Entry<Integer, MedicalDevice> entry : medicalDeviceMap.entrySet()) {
            medicalDeviceList.add(entry.getValue());
        }
        return medicalDeviceList;
    }



    /**
     * 获取指定仪器信息实体
     *
     * @param deviceCode 仪器代号
     * @return 仪器信息
     */
    public static MedicalDevice getMedicalDevice(int deviceCode) {
        return medicalDeviceMap.get(deviceCode);
    }

    /**
     * 获取指定仪器信息实体
     *
     * @param deviceEnum 仪器代号
     * @return 仪器信息
     */
    public static MedicalDevice getMedicalDevice(DeviceEnum deviceEnum) {
        return medicalDeviceMap.get(deviceEnum.getDeviceCode());
    }


    /**
     * 获取全部使用的仪器
     *
     * @return 全部仪器
     */
    public static List<MedicalDevice> getUsedDeviceList() {
        List<MedicalDevice> medicalDeviceList = new ArrayList<>(6);
        for (MedicalDevice medicalDevice : medicalDeviceMap.values()) {
            if (medicalDevice.isDeviceUsed()) {
                medicalDeviceList.add(medicalDevice);
            }
        }
        // 通过仪器号大小进行排序
        medicalDeviceList.sort((device1, device2) -> device1.getDeviceCode() - device2.getDeviceCode());
        return medicalDeviceList;
    }

    /**
     * 获取确认仪器选择的弹窗信息
     *
     * @return 仪器信息
     */
    public static String getConfirmDeviceChooseDialogInfo() {
        StringBuilder deviceInfo = new StringBuilder();
        for (MedicalDevice medicalDevice : medicalDeviceMap.values()) {
            if (medicalDevice.isDeviceUsed()) {
                deviceInfo.append(medicalDevice.getDeviceName()).append(":").append("序列号:").append(medicalDevice.getSerialNumber())
                        .append("    ").append("使用年限:").append(medicalDevice.getServiceLife()).append("\n").append("\n");
            }
        }
        return deviceInfo.toString();
    }



}
