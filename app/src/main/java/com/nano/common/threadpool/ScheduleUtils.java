package com.nano.common.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 调度工具类
 *
 * @author cz
 */
public class ScheduleUtils {


    /**
     * 任务调度相关 固定时间间隔执行
     */
    @SuppressWarnings("AlibabaThreadPoolCreation")
    private static ScheduledExecutorService scheduledExecutorService;


    // 静态初始化块
    static {
        scheduledExecutorService =  Executors.newScheduledThreadPool(6);
    }




    /**
     * 执行定时任务
     *
     * @param taskRunnable 任务
     * @param period 间隔时间
     * @param timeUnit 时间单位
     */
    public static void executeTask(Runnable taskRunnable, long period, TimeUnit timeUnit) {
        scheduledExecutorService.scheduleAtFixedRate(taskRunnable, 0, period, timeUnit);
    }


    public static void executeTask(Runnable taskRunnable, long delay, long period, TimeUnit timeUnit) {
        scheduledExecutorService.scheduleAtFixedRate(taskRunnable, delay, period, timeUnit);
    }

}
