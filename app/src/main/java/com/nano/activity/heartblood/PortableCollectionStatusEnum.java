package com.nano.activity.heartblood;

import lombok.Getter;

/**
 * Description: 便携式仪器采集状态枚举
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/22 22:07
 */
@Getter
public enum PortableCollectionStatusEnum {


    WAITING("等待采集"),


    COLLECTING("采集中"),


    FINISH("采集完成")

    ;

    String status;

    PortableCollectionStatusEnum(String status) {
        this.status = status;
    }
}
