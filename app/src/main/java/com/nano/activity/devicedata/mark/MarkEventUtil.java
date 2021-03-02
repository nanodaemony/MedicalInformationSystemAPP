package com.nano.activity.devicedata.mark;

import com.nano.AppStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 标记事件工具类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/2 0:22
 */
public class MarkEventUtil {

    /**
     * 给药操作时的给药途径
     */
    public static List<String> giveMedicineMethodList = new LinkedList<>(Arrays.asList( "给药途径", "静脉注射", "静脉滴注", "皮下注射", "肌肉注射", "气道吸入", "硬膜外", "蛛网膜下",  "关节腔内注射"));

    /**
     * 补液操作时的补液途径
     */
    public static List<String> giveFluidMethodList = new LinkedList<>(Arrays.asList( "补液途径", "静脉滴注", "静脉加压输注", "静脉快速推注"));

    /**
     * 用药与补液输血时的单位
     */
    public static List<String> giveVolumeUnitList = new LinkedList<>(Arrays.asList("单位(无)", "ml", "U", "mg", "g"));

    /**
     * 记录标记事件的列表
     */
    public static ArrayList<MarkEvent> markEventList = new ArrayList<>();


    /**
     * 对EventList进行排序
     */
    public static void getSortedEventListByUniqueNumber() {
        markEventList.sort((event1, event2) -> (int) (Long.parseLong(event1.getUniqueNumber()) - Long.parseLong(event2.getUniqueNumber())));
    }


    /**
     * 复制当前Event属性并返回一个新对象
     * @param oldEvent 旧对象
     * @return 新对象
     */
    public static MarkEvent copyMarkEvent(MarkEvent oldEvent) {
        MarkEvent newEvent = new MarkEvent();
        newEvent.setMarkEventId(oldEvent.getMarkEventId());
        newEvent.setId(newEvent.getMarkEventId());
        newEvent.setMarkMainType(oldEvent.getMarkMainType());
        newEvent.setMarkSubType(oldEvent.getMarkSubType());
        newEvent.setMarkEvent(oldEvent.getMarkEvent());
        newEvent.setCollectionNumberList(getCollectionNumberList());
        newEvent.setUniqueNumber("" + AppStatic.operationNumber + System.currentTimeMillis());
        newEvent.setUpdated(false);
        return newEvent;
    }


    /**
     * 返回本次采集场次号列表字符串
     */
    public static String getCollectionNumberList() {

        StringBuilder builder = new StringBuilder();
        for (Integer number : AppStatic.collectionNumberList) {
            builder.append(number).append("#");
        }
        String res = builder.toString();
        if (res.length() > 1) {
            return res.substring(0, res.length() - 1);
        }
        return res;
    }

    /**
     * 获取还没有上传的标记事件
     * @return 没上传的事件列表
     */
    public static List<MarkEvent> getNotUpdateMarkEvent() {

        List<MarkEvent> notUpdateEventList = new ArrayList<>();
        for (MarkEvent event : markEventList) {
            if (!event.isUpdated()) {
                notUpdateEventList.add(event);
            }
        }
        return notUpdateEventList;
    }


    /**
     * 是否全部事件都已经上传
     */
    public static boolean isAllMarkEventUpdated() {
        for (MarkEvent event : markEventList) {
            if (!event.isUpdated()) {
                return false;
            }
        }
        return true;
    }




}
