package com.nano.common.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * 简单弹窗的模板
 *
 * @author cz
 */
public class SimpleDialog {

    /**
     * 弹出简单弹窗
     */
    public static void show(Context context, String title, String message, int icon){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }

}
