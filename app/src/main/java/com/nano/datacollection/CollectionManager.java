package com.nano.datacollection;

import com.nano.device.DeviceEnum;

/**
 * Description: 数据采集管理器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/28 0:05
 */
public class CollectionManager {


    /**
     * 传入仪器枚举开启采集
     *
     * @param deviceEnum 仪器枚举
     */
    public void startCollection(DeviceEnum deviceEnum) {

        switch (deviceEnum) {
            case NUO_HE_NW9002S:

                break;

            case PU_KE_YY106:

                break;


            default:

        }
    }


    /**
     * 停止当前仪器采集
     *
     * @param deviceEnum 仪器枚举
     */
    public void stopCollection(DeviceEnum deviceEnum) {

        switch (deviceEnum) {
            case NUO_HE_NW9002S:

                break;

            case PU_KE_YY106:

                break;

            default:

        }
    }




}
