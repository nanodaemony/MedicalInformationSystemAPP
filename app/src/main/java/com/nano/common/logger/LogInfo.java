package com.nano.common.logger;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Log的实体类
 * @author nano
 */
@Data
@NoArgsConstructor
@ToString
public class LogInfo {

    /**
     * 等级
     */
    private String level;

    /**
     * 产生的来源
     */
    private String source;

    /**
     * 日志具体信息
     */
    private String content;

    public LogInfo(String level, String source, String content) {
        this.level = level;
        this.source = source;
        this.content = content;
    }
}
