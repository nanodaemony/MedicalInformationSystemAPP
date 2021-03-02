package com.nano.activity.heartblood;

import lombok.Data;

/**
 * Description: 存储心电血氧数据的实体类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/27 16:54
 */
@Data
public class DataEntity {

    private Integer type;

    private String data;

    public DataEntity(int type, String data) {
        this.type = type;
        this.data = data;
    }
}
