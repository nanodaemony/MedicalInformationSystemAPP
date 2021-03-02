package com.nano.activity.heartblood;

/**
 * Description: 心电血氧采集工具类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/27 23:25
 */
public class HeartBloodUtil {


    /**
     * 获取新采集数据的文件名称
     *
     * @param collectionNumber 采集号
     * @return 文件名称
     */
    public static String getNewFileName(long collectionNumber) {
        return "HeartBloodOriginNew" + collectionNumber + ".txt";
    }


    /**
     * 翻转数据值
     * @param data 数据
     */
    public static String reverseHeartValue(String data) {
        if (data == null) {
            return null;
        }
        char[] res = new char[data.length()];
        int i = data.length() - 1;
        for (char c : data.toCharArray()) {
            res[i] = c;
            i--;
        }
        return new String(res);
    }
}
