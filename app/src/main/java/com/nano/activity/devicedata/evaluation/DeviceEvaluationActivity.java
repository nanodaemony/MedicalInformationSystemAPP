package com.nano.activity.devicedata.evaluation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nano.R;
import com.nano.activity.login.LoginActivity;
import com.nano.common.logger.Logger;
import com.nano.common.util.ToastUtil;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 仪器评价活动
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/2 0:09
 */
@Deprecated
public class DeviceEvaluationActivity extends AppCompatActivity implements HttpHandler {


    private Logger logger = new Logger("DeviceEvaluationActivity");

    /**
     * 存放仪器Fragment的Map
     */
    private Map<MedicalDevice, DeviceEvaluationHandler> deviceFragmentMap = new HashMap<>();

    private HttpManager httpManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_evaluation);

        httpManager = new HttpManager(this);

        // 初始化几个仪器展示的布局(几个仪器的布局ID)
        int[] deviceLayoutIds = new int[6];
        deviceLayoutIds[0] = (R.id.device_evaluation_device_layout_1);
        deviceLayoutIds[1] = (R.id.device_evaluation_device_layout_2);
        deviceLayoutIds[2] = (R.id.device_evaluation_device_layout_3);
        deviceLayoutIds[3] = (R.id.device_evaluation_device_layout_4);
        deviceLayoutIds[4] = (R.id.device_evaluation_device_layout_5);
        deviceLayoutIds[5] = (R.id.device_evaluation_device_layout_6);

        // 如果所有仪器都被Abandon了直接退出
        if (DeviceUtil.getUsedDeviceList().size() == 0) {
            showRestartMonitorDialog();
        }

        // 根据使用的仪器初始化
        int i = 0;
        for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
            DeviceEvaluationFragment deviceEvaluationFragment = new DeviceEvaluationFragment();
            // 传递仪器序号给Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("deviceCode", device.getDeviceCode());
            deviceEvaluationFragment.setArguments(bundle);
            deviceFragmentMap.put(device, deviceEvaluationFragment);
            replaceFragment(deviceLayoutIds[i], deviceEvaluationFragment);
            i++;
        }

        // 提交按钮
        MaterialButton btnCommit = findViewById(R.id.device_evaluation_button_commit);
        btnCommit.setOnClickListener(view -> {
            for (Map.Entry<MedicalDevice, DeviceEvaluationHandler> entry : deviceFragmentMap.entrySet()) {
                // 逐个检查是否填写完全
                if (!entry.getValue().checkAndStoreEvaluationInfo()) {
                    return;
                }
                logger.info("已完成仪器评价信息:" + entry.getKey().getDeviceEvaluationTable().toString());
            }

            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                // 只添加已经上传的
                if (!device.getDeviceEvaluationTable().isEvaluated()) {

                    // 上传标记信息
                    httpManager.postDeviceEvaluationInfo(device.getDeviceEvaluationTable());
                }
            }


        });


    }




    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        List<String> uniqueNumberList = JSON.parseArray(message.getData(), String.class);
        for (String uniqueNumber : uniqueNumberList) {
            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                // 匹配成功
                if (device.getDeviceEvaluationTable().getUniqueNumber().equals(uniqueNumber)) {
                    // 设置为已经上传
                    device.getDeviceEvaluationTable().setUpdated(true);
                }
            }
        }
        runOnUiThread(() -> {
            // 说明完全上传成功
            if (uniqueNumberList.size() == DeviceUtil.getUsedDeviceList().size()) {
                // 弹窗重启弹窗
                showRestartMonitorDialog();
            } else {
                ToastUtil.toast(getCurrentContext(), "上传错误", TastyToast.ERROR);
            }
        });
    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {}

    @Override
    public void handleNetworkFailedMessage() {}

    /**
     * 重新采集的弹窗
     */
    private void showRestartMonitorDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getCurrentContext())
                .setTitle("重新开始")
                .setMessage("评价信息已经全部上传，确定要重新开始新的手术监测吗？")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> collectorReset())
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }

    /**
     * 获取当前的上下文
     *
     * @return context
     */
    public Context getCurrentContext() {
        return DeviceEvaluationActivity.this;
    }


    /**
     * 当按下返回键，防止意外退出
     */
    @Override
    public void onBackPressed() {
        // 新建Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        // 绑定自定义弹窗布局
        View v = inflater.inflate(R.layout.dialog_confirm_monitor_exit, null);

        TextView tvExitMessage = v.findViewById(R.id.collection_confirm_exit_message);
        tvExitMessage.setText("本次采集尚未完成评价,确定退出?");

        // 弹窗布局中的控件注册
        Button btnConfirmChoose = v.findViewById(R.id.dialog_btn_sure);
        Button btnReChoose = v.findViewById(R.id.dialog_btn_cancel);

        final Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        assert window != null;
        // 显示圆角的关键代码
        window.setBackgroundDrawable(new BitmapDrawable());
        dialog.show();
        dialog.getWindow().setContentView(v);
        // 可以设置显示的位置
        dialog.getWindow().setGravity(Gravity.CENTER);

        // 确认退出的按钮注册
        btnConfirmChoose.setOnClickListener(v1 -> {

            dialog.dismiss();           // 关掉弹窗
        });

        // 重新选择的按钮注册
        btnReChoose.setOnClickListener(arg0 -> {
            // 清理选择结果，防止不断缓存关掉弹窗
            dialog.dismiss();
        });
    }

    /**
     * 重置采集器 实现APP的重启
     */
    public void collectorReset(){
        Intent intent = new Intent(getCurrentContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 更换布局
     */
    private void replaceFragment(int originLayout, Fragment targetFragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(originLayout, targetFragment);
        transaction.commit();
    }

}
