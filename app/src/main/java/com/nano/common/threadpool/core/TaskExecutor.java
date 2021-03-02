package com.nano.common.threadpool.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 线程池执行器
 *
 * @author cz
 */
public class TaskExecutor {


    /**
     * 线程池核心线程数量
     */
    private static final int CORE_POOL_SIZE = 20;

    /**
     * 最大线程数量
     */
    private static final int MAXIMUM_POOL_SIZE = 30;

    /**
     * 线程池保存存活时间
     */
    private static final int KEEP_ALIVE_TIME = 60;

    /**
     * 线程池执行器
     */
    public static ThreadPoolExecutor taskExecutor;

    /**
     * 系统初始化时进行调用，进而初始化Http功能
     */
    public static void init() {
        // 初始化线程池
        BlockingQueue httpTaskQueue = new LinkedBlockingDeque(16);
        taskExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, httpTaskQueue);
    }

    /**
     * 执行HTTP任务
     *
     * @param runnable 任务逻辑
     */
    public static void executeHttpTask(Runnable runnable) {
        taskExecutor.execute(runnable);
    }



    /**
     * 执行一般任务
     *
     * @param runnable 任务逻辑
     */
    public static void executeNormalTask(Runnable runnable) {
        taskExecutor.execute(runnable);
    }

}