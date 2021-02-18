package com.nano;

import android.app.Application;
import android.content.SharedPreferences;

import com.nano.device.DeviceUtil;
import com.nano.common.logger.LogStatic;
import com.nano.common.logger.Logger;
import com.nano.common.util.PersistUtil;
import com.nano.common.threadpool.ScheduleUtils;
import com.nano.common.util.TimeStampUtils;
import com.nano.common.threadpool.core.TaskExecutor;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import lombok.Getter;


/**
 * 应用Application类
 *
 * @author nano
 */
public class GlobalApplication extends Application {

    /**
     * 日志
     */
    @Getter
    private static Logger logger = new Logger("main");


    /**
     * 实现SharedPreference,用于实现关键信息的本地存储
     */
    private static SharedPreferences shareMap;
    private static SharedPreferences.Editor shareEditor;


    @Override
    public void onCreate() {
        super.onCreate();
        basicInit();
        // 静态类初始化 初始化顺序有要求!
        LogStatic.init();
        TaskExecutor.init();
        PersistUtil.init();
        TimeStampUtils.init();
        AppStatic.init();
        DeviceUtil.init();
    }

    /**
     * 初始化工作
     */
    private void basicInit() {
        System.out.println("Happy");
        shareMap = getSharedPreferences("CommonData", MODE_PRIVATE);
        shareEditor = shareMap.edit();
        // 读取配置文件
        AppStatic.properties = new Properties();
        try {
            AppStatic.properties.load(getApplicationContext().getAssets().open("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean debug = Boolean.parseBoolean(AppStatic.properties.getProperty("Debug"));
        // logger.info(debug + "");
    }

    /**
     * Getter and setters
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getShareMap() {
        return shareMap;
    }

    public static SharedPreferences.Editor getShareEditor() {
        return shareEditor;
    }

}
