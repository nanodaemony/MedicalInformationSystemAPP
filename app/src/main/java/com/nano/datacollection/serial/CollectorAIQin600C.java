package com.nano.datacollection.serial;

import com.nano.datacollection.Collector;
import com.nano.device.MedicalDevice;

/**
 * Description: 爱琴EGOS600C数据采集器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/11 14:20
 */
public class CollectorAIQin600C implements Collector {


    private MedicalDevice medicalDevice;


    public CollectorAIQin600C(MedicalDevice medicalDevice) {
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
