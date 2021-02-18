package com.nano.common.logger;

/**
 * Description: 日志工厂
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/22 22:24
 */
public class LoggerFactory {

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

}
