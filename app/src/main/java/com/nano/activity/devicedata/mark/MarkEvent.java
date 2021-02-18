package com.nano.activity.devicedata.mark;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import androidx.annotation.RequiresApi;
import lombok.Data;

/**
 * Description: 标记事件类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/2 0:12
 */
@Data
public class MarkEvent {

    //////////////////////////////////////////////////////////////////////////////////////////
    // 下面是传输给服务器的数据字段
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 标记时间
     */
    private long markTime;

    /**
     * 该标记的唯一号码(可以辅助判断是否上传成功)
     */
    private String uniqueNumber;

    /**
     * 标记事件在数据库的ID号(下面ID与markEventId是一致de,其中id是由数据库直接查询
     * 出来的,而markEventId是本次标记选择的,因此两种为同一个)
     */
    private Integer markEventId;
    private Integer id;

    /**
     * 标记事件的大类
     */
    private String markMainType;

    /**
     * 标记事件小类
     */
    private String markSubType;

    /**
     * 标记事件
     */
    private String markEvent;

    /**
     * 给药途径
     */
    private String giveMedicineMethod = "--";

    /**
     * 给药剂量
     */
    private String giveMedicineVolume = "--";

    /**
     * 不良反应/特殊情况
     */
    private String sideEffect;


    //////////////////////////////////////////////////////////////////////////////////////////
    // 下面是相关辅助数据字段
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 是否已经上传
     */
    private boolean isUpdated = false;

    /**
     * 是否需要补充信息
     */
    private boolean isNeedAdditionalInfo;

    /**
     * 手术场次号
     */
    private Integer operationNumber;


    /**
     * 生成用于ListView展示的String
     */
    public String generateListViewString() {
        markEventId = id;
        return markMainType + "  " + markSubType + "  " + markEvent;
    }


    /**
     * 生成用于ListView展示的结果String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateResultListViewString() {
        // 获取时间
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(markTime / 1000, 0, ZoneOffset.ofHours(8));
        String date = dateTime.toLocalDate().toString();
        LocalTime localTime = dateTime.toLocalTime();
        String time = localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond();

        if (isUpdated) {
            return date + " " + time + "  " + markMainType + "  " + markSubType + "  " + markEvent + "  " +
                    giveMedicineMethod + "  " + giveMedicineVolume + "  " + sideEffect + "  " + "已上传";
        } else {
            return date + " " + time + "  " + markMainType + "  " + markSubType + "  " + markEvent + "  " +
                    giveMedicineMethod + "  " + giveMedicineVolume + "  " + sideEffect + "  " + "未上传";
        }

    }


}
