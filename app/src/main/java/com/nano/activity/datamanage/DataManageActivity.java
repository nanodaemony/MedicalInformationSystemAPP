package com.nano.activity.datamanage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.chip.Chip;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nano.AppStatic;
import com.nano.R;
import com.nano.activity.devicedata.collection.DataCollectionActivity;
import com.nano.activity.devicedata.collection.DataCollectionUtils;
import com.nano.activity.devicedata.collection.DeviceUnity;
import com.nano.activity.devicedata.collection.MessageEntity;
import com.nano.activity.devicedata.collection.TestUtil;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationFragment;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationTable;
import com.nano.activity.devicedata.mark.MarkEventActivity;
import com.nano.activity.devicedata.mark.MarkEventUtil;
import com.nano.activity.login.LoginActivity;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ScheduleUtils;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.util.SimpleDialog;
import com.nano.common.util.ToastUtil;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.nano.http.HttpHandler;
import com.nano.http.HttpManager;
import com.nano.http.HttpMessage;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: 基于区块链的医疗数据管理界面
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/16 23:07
 */
public class DataManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HttpHandler {


    /**
     * Logger
     */
    private Logger logger = new Logger("CollectionActivity");

    /**
     * 整体采集状态
     */
    private TextView tvWholeCollectionStatus;


    /**
     * 几个仪器的布局ID
     */
    private int[] deviceLayoutIds;

    /**
     * 已经使用仪器的Map信息
     */
    private Map<Integer, DeviceUnity> usedDeviceUnityMap;


    private HttpManager httpManager;

    /**
     * 测试的布局
     */
    private MaterialCardView layoutTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceLayoutIds = new int[6];
        setContentView(R.layout.activity_data_collection);
        // 初始化生成使用仪器的Map
        usedDeviceUnityMap = DataCollectionUtils.getUsedDeviceUnityMap(DeviceUtil.getUsedDeviceList());

        // 初始化界面
        viewInit();

        functionInit();
    }


    /**
     * 视图初始化
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void viewInit() {
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.collection_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // DrawerLayout
        DrawerLayout drawer = findViewById(R.id.collection_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // 侧边导航栏的布局
        NavigationView navigationView = findViewById(R.id.collection_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 整体采集状态
        tvWholeCollectionStatus = findViewById(R.id.collection_data_whole_status);

        // 初始化几个仪器展示的布局
        deviceLayoutIds[0] = (R.id.collection_device_layout_collection_1);
        deviceLayoutIds[1] = (R.id.collection_device_layout_collection_2);
        deviceLayoutIds[2] = (R.id.collection_device_layout_collection_3);
        deviceLayoutIds[3] = (R.id.collection_device_layout_collection_4);
        deviceLayoutIds[4] = (R.id.collection_device_layout_collection_5);
        deviceLayoutIds[5] = (R.id.collection_device_layout_collection_6);

        int i = 0;
        // 根据使用的仪器初始化
        for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
            if (usedDeviceUnityMap.containsKey(device.getDeviceCode())) {
                // 将布局的ID加入Unity中
                usedDeviceUnityMap.get(device.getDeviceCode()).setDeviceLayoutIds(deviceLayoutIds[i]);
                // 初始化为本次使用的仪器信息
                replaceFragment(deviceLayoutIds[i], usedDeviceUnityMap.get(device.getDeviceCode()).getFragment());
                i++;
            }
        }

        // 展示错误日志
        Chip btnErrorLog = findViewById(R.id.collection_error_log_button);
        btnErrorLog.setOnClickListener(v -> {
            // 弹出错误日志
            showErrorLogDialog();
        });

        // 添加标记事件
        Chip btnAddMarkEvent = findViewById(R.id.material_button_add_mark_info);
        btnAddMarkEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarkEventActivity.class);
            startActivity(intent);
        });

    }


    /**
     * 功能初始化
     */
    private void functionInit() {

        // 打印出采集的基本信息：含手术信息和仪器信息
        logger.info(JSON.toJSONString("本次采集信息:" + AppStatic.collectionBasicInfoEntity));

        // 注册EventBus
        EventBus.getDefault().register(this);

        // 初始化网络管理器
        httpManager = new HttpManager(this);

        // 请求手术场次号
        // requestOperationNumber();

        // 此处设置定时任务 10S为周期 延迟10秒再执行
        ScheduleUtils.executeTask(commonFixedTimeTask, 5, 10, TimeUnit.SECONDS);
        // 上传标记信息的定时任务
        ScheduleUtils.executeTask(this::updateMarkEventList, 5, 20, TimeUnit.SECONDS);
    }

    /**
     * 处理成功的HTTP请求
     * <p>
     * 1. 只有完成采集才能进行Abandon.
     * 2. 完成采集后选择是否需要Abandon以及是否进行评价
     * 3.
     *
     * @param message 数据
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        // 根据响应号进行处理
        switch (message.getCode()) {
            // 上传手术信息
            case POST_BASIC_OPERATION_INFO:
                logger.info("获取到的HTTPMessage:" + message.toString());
                try {
                    // 解析手术场次号
                    AppStatic.operationNumber = Integer.parseInt(message.getData());
                    // (必须在UI线程改变UI)
                    this.runOnUiThread(() -> {
                        // 改变采集状态
                        changeCollectionStatus("手术场次(" + AppStatic.operationNumber + ")" + "等待开始采集", getColor(R.color.titleColor));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("手术场次号解析失败:" + message.getData());
                    ToastUtil.toast(this, "手术场次号获取失败", TastyToast.ERROR);
                }
                break;

            // 上传仪器开始采集信息
            case POST_DEVICE_COLLECTION_START:
                // 获取仪器号
                int startDeviceCode = Integer.parseInt(message.getData());
                DeviceUnity startUnity = usedDeviceUnityMap.get(startDeviceCode);
                if (startUnity != null) {
                    // 开启这个仪器的采集线程
                    startUnity.getUsedCollector().startCollection();
                    // 修改仪器采集状态
                    startUnity.getMedicalDevice().setStatusEnum(CollectionStatusEnum.COLLECTING);
                    // 更改显示的采集状态
                    this.runOnUiThread(() -> {
                        // 设置为开始采集
                        startUnity.getFragmentDataExchanger().updateCollectionStatus(CollectionStatusEnum.COLLECTING);
                        // 如果当前是第一个仪器开始采集
                        if (DataCollectionUtils.isFirstDeviceToStartCollection(usedDeviceUnityMap)) {
                            // 改变采集状态
                            changeCollectionStatus("手术场次(" + AppStatic.operationNumber + ")" + "正在采集", getColor(R.color.colorAccent));
                        }
                    });
                }

                break;

            // 上传仪器停止采集信息
            case POST_DEVICE_COLLECTION_STOP:
                // 获取仪器号
                int stopDeviceCode = Integer.parseInt(message.getData());
                DeviceUnity stopUnity = usedDeviceUnityMap.get(stopDeviceCode);
                if (stopUnity != null) {
                    // 停止采集线程
                    stopUnity.getUsedCollector().stopCollection();
                    // 修改仪器采集状态
                    stopUnity.getMedicalDevice().setStatusEnum(CollectionStatusEnum.FINISHED);
                    // 更改显示的采集状态
                    this.runOnUiThread(() -> {
                        stopUnity.getFragmentDataExchanger().updateCollectionStatus(CollectionStatusEnum.FINISHED);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        DeviceEvaluationFragment evaluationFragment = new DeviceEvaluationFragment();
                        stopUnity.setEvaluationFragment(evaluationFragment);
                        // 传递仪器序号给Fragment
                        Bundle bundle = new Bundle();
                        bundle.putInt("deviceCode", stopDeviceCode);
                        evaluationFragment.setArguments(bundle);
                        replaceFragment(stopUnity.getDeviceLayoutIds(), evaluationFragment);
                        fragmentTransaction.commit();
                    });
                }
                break;

            // 上传仪器评价信息
            case POST_DEVICE_EVALUATION_TABLE:

                try {
                    // 获取仪器号及对应操作Unity
                    List<String> tableList = JSON.parseArray(message.getData(), String.class);
                    for (String tableUniqueNumber : tableList) {
                        // 寻找满足评价表唯一号的所有信息
                        for (Map.Entry<Integer, DeviceUnity> entry : usedDeviceUnityMap.entrySet()) {
                            DeviceEvaluationTable evaluationTable = entry.getValue().getMedicalDevice().getDeviceEvaluationTable();
                            if (evaluationTable != null && evaluationTable.getUniqueNumber().equals(tableUniqueNumber)) {
                                // 将当前仪器的状态设置为评价信息已上传
                                entry.getValue().getMedicalDevice().setEvaluationTableUpdated(true);
                                runOnUiThread(() -> {
                                    // 接收评价信息成功则取消当前界面
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.remove(entry.getValue().getEvaluationFragment());
                                    fragmentTransaction.commit();
                                    ToastUtil.toastSuccess(this, "当前仪器数据采集完成");
                                });
                            }
                        }
                    }

                    runOnUiThread(() -> {
                        // 判断本次采集的仪器是否都已经全部上传完成了,如果是则弹出reboot的弹窗
                        if (isAllDeviceFinishOrAbandon()) {
                            showRestartCollectorDialog();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            // 上传仪器放弃采集信息
            case POST_DEVICE_COLLECTION_ABANDON:
                // 获取仪器号及对应操作Unity
                int abandonDeviceCode = Integer.parseInt(message.getData());
                DeviceUnity abandonUnity = usedDeviceUnityMap.get(abandonDeviceCode);
                if (abandonUnity != null) {
                    // 停止采集
                    runOnUiThread(() -> {
                        abandonUnity.getFragmentDataExchanger().updateCollectionStatus(CollectionStatusEnum.ABANDON);
                        // 隐藏EvaluationFragment界面
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(abandonUnity.getEvaluationFragment());
                        fragmentTransaction.commit();
                        // 既然已经放弃,就要删掉一些信息
                        // 将当前仪器设置为非使用状态
                        abandonUnity.getMedicalDevice().setDeviceUsed(false);
                        usedDeviceUnityMap.remove(abandonDeviceCode);
                        ToastUtil.toast(this, "已放弃该仪器数据采集", TastyToast.INFO);

                        // 判断本次采集的仪器是否都已经全部上传完成了,如果是则弹出reboot的弹窗
                        if (isAllDeviceFinishOrAbandon()) {
                            showRestartCollectorDialog();
                        }
                    });
                }
                break;

            default:

        }

    }

    /**
     * 处理失败的HTTP响应
     *
     * @param message 数据
     */
    @Override
    public void handleFailedHttpMessage(HttpMessage message) {
        logger.error("HTTP请求失败:" + message.toString());
    }

    /**
     * 处理网络异常
     */
    @Override
    public void handleNetworkFailedMessage() {
        logger.error("网络可能出现异常");
        runOnUiThread(() -> ToastUtil.toast(this, "网络可能出现异常...", TastyToast.ERROR));
    }


    /**
     * 弹出日志消息的弹窗
     */
    private void showErrorLogDialog() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> logString = Logger.getErrorList();
        for (String string : logString) {
            stringBuilder.append(string).append("\n");
        }
        SimpleDialog.show(this, "错误日志信息", stringBuilder.toString(), R.mipmap.error_info);
    }

    /**
     * 重新采集的弹窗
     */
    private void showRestartCollectorDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("重新开始")
                .setMessage("本次数据采集完成,确定要重新开始新的手术监测吗？")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> resetApp())
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }


    /**
     * 判断当前是否全部仪器都完成采集或者放弃了
     */
    private boolean isAllDeviceFinishOrAbandon() {
        for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
            if (device.getStatusEnum() == CollectionStatusEnum.COLLECTING || device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                return false;
            }

            if (device.getStatusEnum() == CollectionStatusEnum.FINISHED && !device.isEvaluationTableUpdated()) {
                return false;
            }
        }

        return true;
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
        tvExitMessage.setText("本次采集尚未完成，确定退出？");

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
            // 本次使用的仪器全部Abandon
            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                httpManager.postDeviceCollectionAbandon(device.getDeviceEnum());
            }
            ThreadPoolUtils.executeNormalTask(() -> {
                try {
                    Thread.sleep(1000);
                    // 重启采集器
                    resetApp();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            dialog.dismiss();           // 关掉弹窗
        });

        // 重新选择的按钮注册
        btnReChoose.setOnClickListener(arg0 -> {
            // 清理选择结果，防止不断缓存关掉弹窗
            dialog.dismiss();
        });
    }


    /**
     * 改变采集显示状态
     */
    private void changeCollectionStatus(String status, int textColor) {
        tvWholeCollectionStatus.setText(status);
        tvWholeCollectionStatus.setTextColor(textColor);
    }


    /**
     * 接收仪器数据的EventBus回调
     *
     * @param message 仪器数据类
     */
    @SuppressLint("SetTextI18n")
    @Subscribe
    public void onEventMainThread(String message) {
        // 暂时不用
    }


    /**
     * 上传标记事件列表
     */
    private void updateMarkEventList() {
        // 如果列表没有全部上传完成且采集状态为采集中则进行上传
        if (!MarkEventUtil.isAllMarkEventUpdated()) {
            // 上传标记事件列表
            httpManager.postMarkEvent(JSON.toJSONString(MarkEventUtil.getNotUpdateMarkEvent()));
        }
    }


    /**
     * 目录相关
     *
     * @param menu 目录
     * @return 是否成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_nav_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * ToolBar的目录条目
     *
     * @param item 条目
     * @return 布尔值
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // 测试
            case R.id.action_test:
                // 显示与隐藏测试发送数据的按钮
                if (layoutTest.getVisibility() == View.VISIBLE) {
                    layoutTest.setVisibility(View.GONE);
                } else if (layoutTest.getVisibility() == View.GONE) {
                    layoutTest.setVisibility(View.VISIBLE);
                }
                break;

            // 维护
            case R.id.action_maintain:

                break;


            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 抽屉布局的导航目录
     *
     * @param item 导航条目
     * @return none use
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            // 弹出工作日志
            case R.id.nav_collection_working_info:
                // showLogMessageDialog();
                break;

            case R.id.nav_collection_maintain:

                break;

            case R.id.nav_collection_setting:

                break;

            default:

        }

        DrawerLayout drawer = findViewById(R.id.collection_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 通用定时任务
     */
    private Runnable commonFixedTimeTask = () -> {


    };

    /**
     * 更换布局
     */
    private void replaceFragment(int originLayout, Fragment targetFragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(originLayout, targetFragment);
        transaction.commit();
    }

    /**
     * 重置采集器 实现APP的重启
     */
    public void resetApp() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
