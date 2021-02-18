package com.nano.common.util;

import android.content.SharedPreferences;
import android.util.ArraySet;


import com.nano.GlobalApplication;

import java.util.Set;

/**
 * 持久化管理器
 *
 * @author nano
 */
public class PersistUtil {

    /**
     * 默认的字符串值
     */
    private static final String DEFAULT_STRING_VALUE = "";

    /**
     * 默认的整型值
     */
    public static final int DEFAULT_INTEGER_VALUE = -1;

    /**
     * 默认long型值
     */
    private static final long DEFAULT_LONG_VALUE = 0L;

    /**
     * 默认的布尔值
     */
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;


    /**
     * 实现SharedPreference,用于实现关键信息的本地存储
     */
    private static SharedPreferences shareGetter;
    private static SharedPreferences.Editor sharePutter;


    /**
     * 初始化
     */
    public static void init() {
        shareGetter = GlobalApplication.getShareMap();
        sharePutter = GlobalApplication.getShareEditor();
    }


    /**
     * 持久化存放字符串类型数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putStringValue(String key, String value) {
        sharePutter.putString(key, value);
        sharePutter.commit();
    }

    /**
     * 持久化存放整数类型数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putIntegerValue(String key, int value) {
        sharePutter.putInt(key, value);
        sharePutter.commit();
    }



    public static void putLongValue(String key, long value) {
        sharePutter.putLong(key, value);
        sharePutter.commit();
    }


    /**
     * 持久化存放布尔型类型数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putBooleanValue(String key, boolean value) {
        sharePutter.putBoolean(key, value);
        sharePutter.commit();
    }


    /**
     * 持久化存放SET字符串类型数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putSetValue(String key, Set<String> value) {
        sharePutter.putStringSet(key, value);
        sharePutter.commit();
    }

    /**
     * 根据键值获取字符串数值
     *
     * @param key 键
     * @return 值
     */
    public static String getStringValue(String key) {
        return shareGetter.getString(key, DEFAULT_STRING_VALUE);
    }


    /**
     * 根据键值获取整型数值
     *
     * @param key 键
     * @return 整型值
     */
    public static int getIntegerValue(String key) {
        return shareGetter.getInt(key, DEFAULT_INTEGER_VALUE);
    }

    public static long getLongValue(String key) {
        return shareGetter.getLong(key, DEFAULT_LONG_VALUE);
    }

    /**
     * 根据键值获取整型数值
     *
     * @param key 键
     * @return 整型值
     */
    public static boolean getBooleanValue(String key) {
        return shareGetter.getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }


    /**
     * 获取Set类型的数据
     *
     * @param key 键
     * @return 值
     */
    public static Set<String> getSetValue(String key) {
        return shareGetter.getStringSet(key, new ArraySet<>());
    }

}
