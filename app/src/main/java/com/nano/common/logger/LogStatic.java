package com.nano.common.logger;

import com.nano.AppStatic;

import lombok.Getter;

/**
 * 日志静态类
 * @author nano
 *
 * 存放Log的信息关闭应用后消失
 */
public class LogStatic {

    /**
     * 调试信息的日志列表
     */
    @Getter
    private static FixedSizeList<LogInfo> debugLogList;

    /**
     * 有效信息的日志列表
     */
    @Getter
    private static FixedSizeList<LogInfo> infoLogList;

    /**
     * 错误日志列表
     */
    @Getter
    private static FixedSizeList<LogInfo> errorLogList;

    /**
     * 初始化
     */
    public static void init() {
        debugLogList = new FixedSizeList<>(AppStatic.DEBUG_LOG_SIZE);
        infoLogList = new FixedSizeList<>(AppStatic.INFO_LOG_SIZE);
        errorLogList = new FixedSizeList<>(AppStatic.ERROR_LOG_SIZE);
    }

}
