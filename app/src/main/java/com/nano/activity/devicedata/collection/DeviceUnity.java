package com.nano.activity.devicedata.collection;

import android.support.v4.app.Fragment;

import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationFragment;
import com.nano.datacollection.Collector;
import com.nano.datacollection.parsedata.DeviceDataParser;
import com.nano.device.DeviceEnum;
import com.nano.device.MedicalDevice;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 仪器信息合集
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 20:32
 */
@Data
@NoArgsConstructor
public class DeviceUnity {

    /**
     * 仪器号
     */
    private Integer deviceCode;

    /**
     * 仪器
     */
    private MedicalDevice medicalDevice;

    /**
     * 仪器枚举
     */
    private DeviceEnum deviceEnum;

    /**
     * 对应的Fragment
     */
    private Fragment fragment;

    /**
     * 对应的仪器评价Fragment
     */
    private DeviceEvaluationFragment evaluationFragment;


    /**
     * Fragment数据交互器
     */
    private FragmentDataExchanger fragmentDataExchanger;

    /**
     * 仪器数据解析器
     */
    private DeviceDataParser deviceDataParser;

    /**
     * 采集器实现类
     */
    private Collector[] collector;

    /**
     * 本次使用的采集器
     */
    private Collector usedCollector;

    /**
     * 对应这个仪器采集时的布局
     */
    private int deviceLayoutIds;


    public void setCollector(Collector... collector) {
        this.collector = collector;
        // 本次使用的默认是第0个
        usedCollector = collector[0];
    }


    public DeviceUnity(Integer deviceCode, MedicalDevice medicalDevice, DeviceEnum deviceEnum, FragmentDataExchanger fragmentDataExchanger, Fragment fragment, Collector... collector) {
        this.deviceCode = deviceCode;
        this.medicalDevice = medicalDevice;
        this.deviceEnum = deviceEnum;
        this.fragmentDataExchanger = fragmentDataExchanger;
        this.fragment = fragment;
        this.collector = collector;
    }


    public DeviceUnity(Integer deviceCode, MedicalDevice medicalDevice, DeviceEnum deviceEnum, FragmentDataExchanger fragmentDataExchanger, Fragment fragment) {
        this.deviceCode = deviceCode;
        this.medicalDevice = medicalDevice;
        this.deviceEnum = deviceEnum;
        this.fragmentDataExchanger = fragmentDataExchanger;
        this.fragment = fragment;
    }

}
