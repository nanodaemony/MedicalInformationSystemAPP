package com.nano.datacollection.old.udp;


import com.nano.device.MedicalDevice;
import com.nano.datacollection.Collector;

/**
 * 宝莱特A8 监护仪
 * @author cz
 */
public class CollectorBaoLaiTeA8 implements Collector {


    /**
     * 使用的仪器
     */
    private MedicalDevice medicalDevice;


    /**
     * 构造器
     */
    public CollectorBaoLaiTeA8(MedicalDevice medicalDevice) {
        this.medicalDevice = medicalDevice;
    }


    @Override
    public void startCollection() {

    }

    @Override
    public void stopCollection() {

    }

    @Override
    public void resetCollector() {

    }
}
