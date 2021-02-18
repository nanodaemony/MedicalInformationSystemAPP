package com.nano.common.logger;

import android.util.Log;

import com.nano.AppStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录类
 * @author nano
 */
public class Logger {

    /**
     * 日志TAG
     */
    private static final String LOG_TAG = "admin:";

    /**
     * 日志来源
     */
    private String source;

    /**
     * 构造器
     *
     * @param source 日志来源
     */
    public Logger(String source) {
        this.source = source;
    }

    /**
     * Debug 级别的日志
     * 调试级别的日志仅仅打印不保存
     *
     * @param content 信息
     */
    public void debug(String content) {
        int currentLevel = AppStatic.getLogLevelEnum().getLevel();
        // 如果当前等级不等于DEBUG直接不管了
        if (currentLevel == LogLevelEnum.DEBUG.getLevel()) {
            Log.d(LOG_TAG + source, content);
        }
    }


    /**
     * Debug 级别的日志
     * 调试级别的日志仅仅打印不保存
     *
     * @param content1 信息
     */
    public void debug(String content1, String content2) {
        int currentLevel = AppStatic.getLogLevelEnum().getLevel();
        // 如果当前等级等于DEBUG则进行记录
        if (currentLevel == LogLevelEnum.DEBUG.getLevel()) {
            Log.d(LOG_TAG + source, content1 + content2);
        }
    }


    /**
     * INFO 级别的日志
     *
     * @param content 日志
     */
    public void info(String content) {
        Log.i(LOG_TAG + source, content);
        // Save to list
        LogInfo logInfo = new LogInfo(LogLevelEnum.INFO.getName(), source, content);
        LogStatic.getInfoLogList().add(logInfo);
    }

    /**
     * INFO 级别的日志
     *
     * @param content1 日志
     */
    public void info(String content1, String content2) {
        Log.i(LOG_TAG + source, content1 + content2);

        // Save to list
        LogInfo logInfo = new LogInfo(LogLevelEnum.INFO.getName(), source, content1 + content2);
        LogStatic.getInfoLogList().add(logInfo);
    }

    /**
     * 错误级别的日志
     *
     * @param content 日志
     */
    public void error(String content) {
        Log.e(LOG_TAG + source, content);

        // Save to list
        LogInfo logInfo = new LogInfo(LogLevelEnum.ERROR.getName(), source, content);
        LogStatic.getErrorLogList().add(logInfo);
        // 如果遇到错误就把错误信息上传到服务器中
        //HttpManager.postCollectorErrorInfo(content);
    }


    /**
     * 错误级别的日志
     *
     * @param content1 日志
     */
    public void error(String content1, String content2) {
        Log.e(LOG_TAG + source, content1 + content2);

        // Save to list
        LogInfo logInfo = new LogInfo(LogLevelEnum.ERROR.getName(), source, content1 + content2);
        LogStatic.getErrorLogList().add(logInfo);
    }

    /**
     * 获取调试信息的个数
     *
     * @return 调试日志格式
     */
    public static int getDebugLogNumber() {
        return LogStatic.getDebugLogList().size();
    }

    /**
     * 获取调试列表
     *
     * @return 调试列表
     */
    public static List<String> getDebugList() {
        List<LogInfo> debugLogInfoList = LogStatic.getDebugLogList();
        List<String> debugList = new ArrayList<>(debugLogInfoList.size());
        for (LogInfo logInfo : debugLogInfoList) {
            debugList.add(logInfo.toString());
        }
        return debugList;
    }


    /**
     * 获取INFO基本日志列表
     *
     * @return INFO列表
     */
    public static List<String> getInfoList() {
        List<LogInfo> infoLogInfoList = LogStatic.getInfoLogList();
        List<String> infoList = new ArrayList<>(infoLogInfoList.size());
        for (LogInfo logInfo : infoLogInfoList) {
            infoList.add(logInfo.toString());
        }
        return infoList;
    }

    /**
     * 获取ERROR基本日志列表
     *
     * @return ERROR日志列表
     */
    public static List<String> getErrorList() {
        List<LogInfo> errorLogInfoList = LogStatic.getErrorLogList();
        List<String> infoList = new ArrayList<>(errorLogInfoList.size());
        for (LogInfo logInfo : errorLogInfoList) {
            infoList.add(logInfo.toString());
        }
        return infoList;
    }
}
