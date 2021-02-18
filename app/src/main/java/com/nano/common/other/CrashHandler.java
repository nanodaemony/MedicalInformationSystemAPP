package com.nano.common.other;

import android.annotation.SuppressLint;
import android.content.Context;

import org.apache.log4j.Logger;

/**
 * 当发生未知错误时的重启逻辑
 * @author cz
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    @SuppressLint("StaticFieldLeak")
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 传入Context
     * @param context context
     */
    public void init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        // 将未捕获的异常记录进入日志
        Logger logger = Logger.getLogger(CrashHandler.class);
        logger.error(thread, ex);

        // 延时500ms
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
