package com.nano.activity.devicedata.devicechoose;

import android.app.Dialog;
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
import com.nano.AppStatic;
import com.nano.R;
import com.nano.activity.devicedata.collection.DataCollectionActivity;
import com.nano.common.logger.Logger;
import com.nano.common.util.PersistUtil;
import com.nano.common.util.ToastUtil;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.nano.http.HttpHandler;
import com.nano.http.HttpManager;
import com.nano.http.HttpMessage;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Description: 仪器选择的Fragment的界面
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/10 20:41
 */
public class DeviceChooseActivity extends AppCompatActivity implements HttpHandler {

    private HttpManager httpManager = new HttpManager(DeviceChooseActivity.this);

    private Logger logger = new Logger("DeviceChooseActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choose);

        httpManager.getNetworkStatus();

        // 初始化几个仪器展示的布局(几个仪器的布局ID)
        int[] deviceLayoutIds = new int[28];
        deviceLayoutIds[0] = (R.id.device_choose_device_layout_1);
        deviceLayoutIds[1] = (R.id.device_choose_device_layout_2);
        deviceLayoutIds[2] = (R.id.device_choose_device_layout_3);
        deviceLayoutIds[3] = (R.id.device_choose_device_layout_4);
        deviceLayoutIds[4] = (R.id.device_choose_device_layout_5);
        deviceLayoutIds[5] = (R.id.device_choose_device_layout_6);
        deviceLayoutIds[6] = (R.id.device_choose_device_layout_7);
        deviceLayoutIds[7] = (R.id.device_choose_device_layout_8);
        deviceLayoutIds[8] = (R.id.device_choose_device_layout_9);
        deviceLayoutIds[9] = (R.id.device_choose_device_layout_10);
        deviceLayoutIds[10] = (R.id.device_choose_device_layout_11);
        deviceLayoutIds[11] = (R.id.device_choose_device_layout_12);
        deviceLayoutIds[12] = (R.id.device_choose_device_layout_13);
        deviceLayoutIds[13] = (R.id.device_choose_device_layout_14);
        deviceLayoutIds[14] = (R.id.device_choose_device_layout_15);
        deviceLayoutIds[15] = (R.id.device_choose_device_layout_16);
        deviceLayoutIds[16] = (R.id.device_choose_device_layout_17);
        deviceLayoutIds[17] = (R.id.device_choose_device_layout_18);
        deviceLayoutIds[18] = (R.id.device_choose_device_layout_19);
        deviceLayoutIds[19] = (R.id.device_choose_device_layout_20);
        deviceLayoutIds[20] = (R.id.device_choose_device_layout_21);
        deviceLayoutIds[21] = (R.id.device_choose_device_layout_22);
        deviceLayoutIds[22] = (R.id.device_choose_device_layout_23);
        deviceLayoutIds[23] = (R.id.device_choose_device_layout_24);
        deviceLayoutIds[24] = (R.id.device_choose_device_layout_25);
        deviceLayoutIds[25] = (R.id.device_choose_device_layout_26);
        deviceLayoutIds[26] = (R.id.device_choose_device_layout_27);
        deviceLayoutIds[27] = (R.id.device_choose_device_layout_28);


        // 获取仪器信息列表
        List<MedicalDevice> medicalDeviceList = DeviceUtil.getMedicalDeviceList();
        // 将仪器按照使用时间排序
        medicalDeviceList.sort((device1, device2) -> {
            if (device1.getDeviceLastUseTime() < device2.getDeviceLastUseTime()) {
                return 1;
            } else if (device1.getDeviceLastUseTime() == device2.getDeviceLastUseTime()) {
                return 0;
            } else {
                return -1;
            }
        });

        // 根据使用的仪器初始化
        int i = 0;
        for (MedicalDevice device : medicalDeviceList) {
            DeviceChooseFragment deviceChooseFragment = new DeviceChooseFragment();
            // 传递仪器序号给Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("deviceCode", device.getDeviceCode());
            deviceChooseFragment.setArguments(bundle);
            replaceFragment(deviceLayoutIds[i], deviceChooseFragment);
            i++;
        }

        // 提交按钮
        MaterialButton btnCommit = findViewById(R.id.device_choose_button_commit);
        btnCommit.setOnClickListener(view -> {
            // 检查是不是选用的仪器都填了序列号
            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                if (device.getSerialNumber() == null || device.getSerialNumber().length() == 0) {
                    ToastUtil.toast(this, "仪器尚未填写序列号:" + device.getDeviceName(), TastyToast.WARNING);
                    return;
                }
            }
            int totalChoosedDeviceNumber = DeviceUtil.getUsedDeviceList().size();
            if (totalChoosedDeviceNumber > 6) {
                ToastUtil.toast(this, "选则的仪器数量不能大于6", TastyToast.ERROR);
            } else if (totalChoosedDeviceNumber == 0) {
                ToastUtil.toast(this, "您尚未选择使用的仪器", TastyToast.ERROR);
            } else {
                // 显示弹框，提示确认选择
                showConfirmChooseDialog();
            }
        });


    }


    /**
     * 弹出仪器选择的确认弹窗，会将传入的数据显示出来
     */
    private void showConfirmChooseDialog() {
        // 新建Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        // 绑定自定义弹窗布局
        View v = inflater.inflate(R.layout.dialog_confirm_device_choose, null);

        // 弹窗布局中的控件注册
        TextView deviceInfoContent = v.findViewById(R.id.dialog_content);
        Button btnConfirmChoose = v.findViewById(R.id.dialog_btn_sure);
        Button btnReChoose = v.findViewById(R.id.dialog_btn_cancel);
        // 获取仪器选择的展示信息
        deviceInfoContent.setText(DeviceUtil.getConfirmDeviceChooseDialogInfo());
        final Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        assert window != null;
        // 显示圆角的关键代码
        window.setBackgroundDrawable(new BitmapDrawable());
        dialog.show();
        // 自定义布局应该在这里添加，要在dialog.show()的后面
        dialog.getWindow().setContentView(v);
        // 可以设置显示的位置
        dialog.getWindow().setGravity(Gravity.CENTER);

        // 确认选择的按钮注册
        btnConfirmChoose.setOnClickListener(v1 -> {
            // 关掉弹窗
            dialog.dismiss();
            logger.info("本次仪器选择:" + DeviceUtil.getConfirmDeviceChooseDialogInfo());
            // 将仪器选择信息持久化到本地,同时更新这个仪器上次使用的时间
            for (MedicalDevice device : AppStatic.medicalDeviceMap.values()) {
                if (device.isDeviceUsed()) {
                    // 更新仪器使用时间为当前
                    PersistUtil.putLongValue("DeviceLastUseTime" + device.getDeviceCode(), System.currentTimeMillis());
                }
            }
            // 构造上传到服务器的仪器信息
            List<InfoDevice> infoDeviceList = new ArrayList<>(6);
            for (MedicalDevice medicalDevice : AppStatic.medicalDeviceMap.values()) {
                if (medicalDevice.isDeviceUsed()) {
                    infoDeviceList.add(new InfoDevice("" + medicalDevice.getDeviceCode(), medicalDevice.getSerialNumber(), (double) medicalDevice.getServiceLife()));
                }
            }
            AppStatic.collectionBasicInfoEntity.setUsedDeviceInfo(JSON.toJSONString(infoDeviceList));
            Intent intent = new Intent(this, DataCollectionActivity.class);
            startActivity(intent);
        });

        // 重新选择的按钮注册
        btnReChoose.setOnClickListener(arg0 -> {
            // 清理选择结果，防止不断缓存关掉弹窗
            dialog.dismiss();
        });
    }

    /**
     * 处理成功消息(需要在UI线程执行)
     * @param message 数据
     */
    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        runOnUiThread(() -> ToastUtil.toastSuccess(this, "网络良好"));
    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {
        runOnUiThread(() -> ToastUtil.toastSuccess(this, "网络异常"));
    }

    @Override
    public void handleNetworkFailedMessage() {
        runOnUiThread(() -> ToastUtil.toastSuccess(this, "网络异常"));
    }


    /**
     * 用于上传到服务器的仪器信息类
     * @author cz
     */
    @Data
    private  static class InfoDevice implements Serializable {

        private static final long serialVersionUID = 233410313766289238L;
        // 仪器号
        private String deviceCode;
        // 序列号
        private String deviceSerialNumber;
        // 生产日期
        private LocalDate deviceProduceDate;
        // 服务年限
        private Double deviceServiceLife;

        public InfoDevice(String deviceCode, String deviceSerialNumber, Double deviceServiceLife) {
            this.deviceCode = deviceCode;
            this.deviceSerialNumber = deviceSerialNumber;
            this.deviceServiceLife = deviceServiceLife;
        }
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
