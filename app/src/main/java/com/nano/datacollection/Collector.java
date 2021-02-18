package com.nano.datacollection;

/**
 * 采集器监视器接口
 * @author Administrator
 */
public interface Collector {

    /**
     * 开启采集器
     */
    void startCollection();

    /**
     * 关闭采集器
     */
    void stopCollection();


    /**
     * 重置采集器
     */
    void resetCollector();

}

