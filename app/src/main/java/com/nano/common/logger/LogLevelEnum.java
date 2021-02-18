package com.nano.common.logger;

import lombok.Getter;

/**
 * 日志等级的枚举
 *
 * @author nano
 */
public enum LogLevelEnum {

    /**
     * 调试
     */
    DEBUG("DEBUG", 1),

    /**
     * 信息
     */
    INFO("INFO", 2),

    /**
     * 错误
     */
    ERROR("ERROR", 3);


    @Getter
    private Integer level;

    /**
     * 名称
     */
    @Getter
    private String name;

    LogLevelEnum(String name, int level) {
        this.name = name;
        this.level = level;
    }


}
