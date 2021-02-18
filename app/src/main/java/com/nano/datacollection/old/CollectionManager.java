package com.nano.datacollection.old;

import com.nano.device.MedicalDevice;
import com.nano.datacollection.Collector;
import com.nano.datacollection.old.tcp.CollectorLiBangEliteV8;
import com.nano.datacollection.old.tcp.CollectorMaiRuiBeneViewT8;
import com.nano.datacollection.old.tcp.CollectorMaiRuiWatoex55Pro;
import com.nano.datacollection.old.tcp.CollectorMaiRuiWatoex65;
import com.nano.datacollection.old.udp.CollectorBaoLaiTeA8;
import com.nano.datacollection.old.udp.CollectorNuoHe;
import com.nano.datacollection.old.udp.CollectorPuKe;
import com.nano.datacollection.old.udp.CollectorYiAn8700A;
import com.nano.AppStatic;
import com.nano.common.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 采集管理器
 * @author cz
 */
public class CollectionManager {

    private Logger logger = new Logger("NettyManager");

    /**
     * 采集器列表
     */
    @Getter
    private List<Collector> collectorList = new ArrayList<>(10);


    /**
     * 仪器接收数据的线程类
     */
    private CollectorNuoHe collectorNuoHe;
    private CollectorPuKe collectorPuKe;
    private CollectorBaoLaiTeA8 collectorBaoLaiTeA8;
    private CollectorYiAn8700A collectorYiAn8700A;

    private CollectorMaiRuiBeneViewT8 collectorMaiRuiBeneViewT8;
    private CollectorMaiRuiWatoex65 collectorMaiRuiWatoex65;
    private CollectorMaiRuiWatoex55Pro collectorMaiRuiWatoex55Pro;
    private CollectorLiBangEliteV8 collectorLiBangEliteV8;


    {

    }

    /**
     * 开始采集
     */
    public void startCollection() {
        for (MedicalDevice medicalDevice : AppStatic.medicalDeviceMap.values()) {
            // 如果仪器使用
            if (medicalDevice.isDeviceUsed()) {
//                switch (medicalDevice.getDeviceCode()) {
//
//                    case DeviceCode.NUO_HE:
//                        collectorNuoHe = new CollectorNuoHe(medicalDevice);
//                        collectorNuoHe.startCollection();
//                        collectorList.add(collectorNuoHe);
//                        break;
//
//                    case DeviceCode.PU_KE:
//                        collectorPuKe = new CollectorPuKe(medicalDevice);
//                        collectorPuKe.startCollection();
//                        collectorList.add(collectorPuKe);
//                        break;
//
//                    case DeviceCode.BAO_LAI_TE:
//
//                        break;
//
//                    case DeviceCode.YI_AN_8700A:
//                        collectorYiAn8700A = new CollectorYiAn8700A(medicalDevice);
//                        collectorYiAn8700A.startCollection();
//                        collectorList.add(collectorYiAn8700A);
//                        break;
//
//                    case DeviceCode.MAI_RUI_T8:
//                        collectorMaiRuiBeneViewT8 = new CollectorMaiRuiBeneViewT8(medicalDevice);
//                        collectorMaiRuiBeneViewT8.startCollection();
//                        collectorList.add(collectorMaiRuiBeneViewT8);
//                        break;
//
//                    case DeviceCode.MAI_RUI_WATOEX_65:
//                        collectorMaiRuiWatoex65 = new CollectorMaiRuiWatoex65(medicalDevice);
//                        collectorMaiRuiWatoex65.startCollection();
//                        collectorList.add(collectorMaiRuiWatoex65);
//                        break;
//
//                    case DeviceCode.MAI_RUI_WATOEX_55_PRO:
//                        collectorMaiRuiWatoex55Pro = new CollectorMaiRuiWatoex55Pro(medicalDevice);
//                        collectorMaiRuiWatoex55Pro.startCollection();
//                        collectorList.add(collectorMaiRuiWatoex55Pro);
//                        break;
//
//                    case DeviceCode.LI_BANG_ELITE_V8:
//                        collectorLiBangEliteV8  = new CollectorLiBangEliteV8(medicalDevice);
//                        collectorLiBangEliteV8.startCollection();
//                        collectorList.add(collectorLiBangEliteV8);
//                        break;
//
//                    default:
//                }
            }
        }
    }



    /**
     * 关闭采集
     */
    public void stopCollection() {
        // 依次关闭采集器
        for (Collector collector : collectorList) {
            collector.stopCollection();
        }
    }

}
