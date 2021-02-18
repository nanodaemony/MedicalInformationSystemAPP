package com.nano.common.util;

import android.content.Context;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

/**
 * 用于Toast消息的共用类
 * @author cz
 */
public class ToastUtil {

    /**
     * 根据上下文弹出Toast
     * @param context 上下文
     * @param content 内容
     */
    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }


    /**
     * 根据上下文弹出高级的Toast
     * @param context 上下文
     */
    public static void toast(Context context, String msg, int type) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, type);
    }

    /**
     * 根据上下文弹出高级的Toast
     * @param context 上下文
     */
    public static void toastWarn(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.WARNING);
    }

    /**
     * 根据上下文弹出高级的Toast
     * @param context 上下文
     */
    public static void toastSuccess(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
    }

    /**
     * 根据上下文弹出高级的Toast
     * @param context 上下文
     */
    public static void toastError(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.ERROR);
    }

}
