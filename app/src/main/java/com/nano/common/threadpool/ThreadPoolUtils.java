package com.nano.common.threadpool;


import com.nano.common.threadpool.core.TaskExecutor;

/**
 * 线程池工具类
 *
 * @author cz
 */
public class ThreadPoolUtils {
    /**
     * 执行线程池任务
     *
     * @param runnable 任务
     */
    public static void executeNormalTask(Runnable runnable) {
        TaskExecutor.executeNormalTask(runnable);
    }
}
