package com.nano.activity.devicedata.mark;

/**
 * Description: 标记事件类型枚举
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/3 13:48
 */
public enum MarkEventTypeEnum {

    SHI_JIAN("事件"),


    CAO_ZUO("操作"),


    YONG_YAO("用药"),


    BU_YE_SHU_XUE("补液/输血"),

    ;

    String type;


    MarkEventTypeEnum(String type) {
        this.type = type;
    }

    /**
     * 获取事件的类型
     *
     * @param event 事件
     * @return 类型枚举
     */
    public static MarkEventTypeEnum getEventType(MarkEvent event) {
        for (MarkEventTypeEnum eventTypeEnum : MarkEventTypeEnum.values()) {
            if (eventTypeEnum.type.equals(event.getMarkMainType())) {
                return eventTypeEnum;
            }
        }
        return null;
    }
}
