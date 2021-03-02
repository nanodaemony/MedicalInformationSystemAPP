package com.nano.activity.devicedata.collection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.nano.R;
import com.nano.activity.devicedata.collection.interfaces.FragmentOperationHandler;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationFragment;
import com.nano.activity.devicedata.evaluation.DeviceEvaluationTable;
import com.nano.activity.devicedata.evaluation.FragmentEvaluationHandler;
import com.nano.activity.devicedata.mark.MarkEvent;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.activity.login.LoginActivity;
import com.nano.activity.devicedata.mark.MarkEventUtil;
import com.nano.common.util.SimpleDialog;
import com.nano.device.DeviceEnum;
import com.nano.AppStatic;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ScheduleUtils;
import com.nano.common.util.ToastUtil;
import com.nano.activity.devicedata.mark.MarkEventActivity;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Description: 数据采集Activity
 * <p>
 * 整体流程：
 * 1. 打开界面就弹出信息提示框，用以告诉用户的使用注意事项。
 * 2. 根据仪器选择的结果初始化展示的界面。
 * 3. 请求手术场次号，请求成功则进行展示，否则重新请求。请求到则进入可采集阶段。
 * 4. 医生根据仪器的情况分别点击仪器图片开始数据采集。首先需要向服务器发送开始请求，服务器响应后正式开启采集线程。
 * 5. 数据采集过程中可以进行手术标记，标记分为简单标记和复杂标记。标记的切换直接左右拖动即可。标记的结果同步展示在下方。
 * 6. 如果医生判断不再需要采集数据，那么此时分别点击仪器图片，确认后发送结束请求到服务器，然后结束这个仪器的采集。
 * 7. 当某台仪器采集完成之后，需要判断当前是否是最后一台完成采集的仪器，如果是则进入到仪器评价界面。
 * 8. 进行仪器评价,采集完成。
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/27 22:41
 */
public class DataCollectionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentOperationHandler, HttpHandler, FragmentEvaluationHandler {

    /**
     * Logger
     */
    private Logger logger = new Logger("CollectionActivity");

    /**
     * 几个仪器的布局ID
     */
    private int[] deviceLayoutIds;

    /**
     * 已经使用仪器的Map信息
     */
    private Map<Integer, DeviceUnity> usedDeviceUnityMap;

    private Chip btnControlAllDevice;

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
            Intent intent = new Intent(DataCollectionActivity.this, MarkEventActivity.class);
            startActivity(intent);
        });

        // 控制所有仪器的开始与结束
        btnControlAllDevice = findViewById(R.id.material_button_controll_all_collection);
        btnControlAllDevice.setOnClickListener(view -> {

            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                // 如果有等待采集的那就弹出
                if (device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                    // 弹出全部开始日志
                    showAllDeviceStartCollectionDialog();
                    return;
                }
            }
            for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                if (device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                    ToastUtil.toastWarn(this, "当前有仪器还未开始采集,无法全部停止.");
                    return;
                }
            }
            // 说明全部都已经开始或者停止了
            // 弹出全部开始日志
            showAllDeviceFinishCollectionDialog();
        });


        // 测试按钮
        layoutTest = findViewById(R.id.collection_textView_test_card);
        TextView tvTestNuoHe = findViewById(R.id.collection_textView_test_nuoHe);
        tvTestNuoHe.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.NUO_HE_NW9002S.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.NUO_HE_NW9002S.getDeviceCode())));
        TextView tvTestPuKe = findViewById(R.id.collection_textView_test_puke);
        tvTestPuKe.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.PU_KE_YY106.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.PU_KE_YY106.getDeviceCode())));
        TextView tvTestBaoLaiTe = findViewById(R.id.collection_textView_test_baolaite);
        tvTestBaoLaiTe.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.BAO_LAI_TE_A8.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.BAO_LAI_TE_A8.getDeviceCode())));
        TextView tvYiAn8700A = findViewById(R.id.collection_textView_test_yian_8700a);
        tvYiAn8700A.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.YI_AN_8700A.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.YI_AN_8700A.getDeviceCode())));
        TextView tvTestMaiRuiT8 = findViewById(R.id.collection_textView_test_mairui_t8);
        tvTestMaiRuiT8.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.MAI_RUI_T8.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.MAI_RUI_T8.getDeviceCode())));
        TextView tvTestMaiRuiWatoex65 = findViewById(R.id.collection_textView_test_mairui_watoex_65);
        tvTestMaiRuiWatoex65.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.MAI_RUI_WATOEX65.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.MAI_RUI_WATOEX65.getDeviceCode())));
        TextView tvTestLiBangEliteV8 = findViewById(R.id.collection_textView_test_libang_elite_v8);
        tvTestLiBangEliteV8.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.LI_BANG_ELITEV8.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.LI_BANG_ELITEV8.getDeviceCode())));
        TextView tvTestAiQinEgos600A = findViewById(R.id.collection_textView_test_aiqin600a);
        tvTestAiQinEgos600A.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.AI_QIN_EGOS600A.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.AI_QIN_EGOS600A.getDeviceCode())));
        TextView tvTestAiQinEgos600B = findViewById(R.id.collection_textView_test_aiqin600b);
        tvTestAiQinEgos600B.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.AI_QIN_EGOS600B.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.AI_QIN_EGOS600B.getDeviceCode())));
        TextView tvTestAiQinEgos600C = findViewById(R.id.collection_textView_test_aiqin600c);
        tvTestAiQinEgos600C.setOnClickListener(view -> DeviceUtil.getMedicalDevice(DeviceEnum.AI_QIN_EGOS600C.getDeviceCode()).parseAndUploadDeviceData(TestUtil.getTestData(DeviceEnum.AI_QIN_EGOS600C.getDeviceCode())));
    }


    /**
     * 功能初始化
     */
    private void functionInit() {

        logger.info(DeviceUtil.getUsedDeviceList().size() + "");
        // 打印出采集的基本信息：含手术信息和仪器信息
        for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
            logger.info(device.toString());
            logger.info("本次采集仪器:" + device.getDeviceCode() + ", " + device.getDeviceName());
            requestCollectionNumber(device);
        }

        // 注册EventBus
        EventBus.getDefault().register(this);

        // 初始化网络管理器
        httpManager = new HttpManager(this);

        // 请求手术场次号
        //requestCollectionNumber();

        // 此处设置定时任务 10S为周期 延迟10秒再执行
        ScheduleUtils.executeTask(commonFixedTimeTask, 5, 10, TimeUnit.SECONDS);
        // 上传标记信息的定时任务
        ScheduleUtils.executeTask(this::updateMarkEventList, 5, 20, TimeUnit.SECONDS);
    }

    /**
     * 延时1.5秒之后开始请求采集场次号
     */
    private void requestCollectionNumber(MedicalDevice device) {
        ThreadPoolUtils.executeNormalTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 逐个上传信息
            httpManager.postMedicalDeviceInfoAndGetCollectionNumber(device);
        });
    }


    /**
     * 仪器开始采集
     *
     * @param messageEntity 仪器实体
     */
    @Override
    public void handleDeviceStartCollection(MessageEntity messageEntity) {
        // 获取当前正在操作的仪器
        MedicalDevice device = DeviceUtil.getMedicalDevice(messageEntity.getDeviceCode());
        if (device.getCollectionNumber() == 0) {
            ToastUtil.toastError(this, "采集场次号为空,正在重新获取...");
            httpManager.postMedicalDeviceInfoAndGetCollectionNumber(device);
            return;
        }
        // 如果不是等待采集状态
        if (device.getStatusEnum() != CollectionStatusEnum.WAITING_START) {
            ToastUtil.toast(getApplicationContext(), "当前仪器不是等待采集状态,无法开始采集.", TastyToast.WARNING);
            return;
        }
        // 这里弹出确定开始的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("开始采集")
                .setMessage("确定开始采集仪器" + device.getDeviceName() + "的数据?")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 发送开始请求
                    httpManager.postDeviceCollectionStart(device);
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }

    /**
     * 仪器结束采集
     *
     * @param messageEntity 仪器实体
     */
    @Override
    public void handleDeviceStopCollection(MessageEntity messageEntity) {
        // 获取当前正在操作的仪器
        MedicalDevice device = DeviceUtil.getMedicalDevice(messageEntity.getDeviceCode());
        // 如果不是正在采集状态
        if (device.getStatusEnum() != CollectionStatusEnum.COLLECTING) {
            ToastUtil.toast(getApplicationContext(), "当前仪器不是正在采集状态,无法结束采集.", TastyToast.WARNING);
            return;
        }
        // 这里弹出确定结束的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("结束采集")
                .setMessage("确定结束采集仪器" + device.getDeviceName() + "的数据?")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 发送结束请求
                    httpManager.postDeviceCollectionStop(device);
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }


    /**
     * 处理放弃仪器数据的信息
     */
    @Override
    public void handleAbandonDeviceData(MessageEntity messageEntity) {
        // 获取当前正在操作的仪器
        MedicalDevice device = DeviceUtil.getMedicalDevice(messageEntity.getDeviceCode());
        if (device.getStatusEnum() != CollectionStatusEnum.FINISHED) {
            ToastUtil.toastWarn(this, "仅采集完成状态才能放弃采集.");
            return;
        }
        // 这里弹出确定重新开始的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("放弃本次该仪器采集")
                .setMessage("确定放弃本次采集仪器" + device.getDeviceName() + "的采集信息及采集的数据?请三思!!!")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 发送结束请求
                    httpManager.postDeviceCollectionAbandon(device);
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }

    /**
     * 处理上传仪器评价信息
     */
    @Override
    public void handleUpdateEvaluationInfo(MessageEntity messageEntity) {
        // 获取当前正在操作的仪器
        MedicalDevice device = DeviceUtil.getMedicalDevice(messageEntity.getDeviceCode());
        // 这里弹出确定重新开始的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("上传评价信息")
                .setMessage("确定上传仪器评价信息?确定则代表着本次数据采集有效.")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 下面进行评价信息上传
                    httpManager.postDeviceEvaluationInfo(device.getDeviceEvaluationTable());
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
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
        switch (message.getPathEnum()) {
            // 上传仪器信息并获取采集场次号
            case POST_MEDICAL_DEVICE_INFO:
                logger.info(message.toString());
                try {
                    // 获取到采集场次号
                    int deviceCode = Integer.parseInt(message.getData());
                    int collectionNumber = Integer.parseInt(message.getData2());
                    // 添加到列表中
                    AppStatic.collectionNumberList.add(collectionNumber);
                    // (必须在UI线程改变UI)
                    this.runOnUiThread(() -> {
                        DeviceUnity unity = usedDeviceUnityMap.get(deviceCode);
                        if (unity != null) {
                            unity.getMedicalDevice().setCollectionNumber(collectionNumber);
                            // 通知可以进行开始了
                            unity.getFragmentDataExchanger().updateCollectionStatus(CollectionStatusEnum.WAITING_START);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("采集场次号解析失败:" + message.getData());
                    ToastUtil.toast(DataCollectionActivity.this, "采集场次号解析失败", TastyToast.ERROR);
                }
                break;

            // 上传仪器开始采集信息
            case DEVICE_START_COLLECTION:
                try {
                    int deviceCode = Integer.parseInt(message.getData());
                    // 获取仪器号
                    DeviceUnity startUnity = usedDeviceUnityMap.get(deviceCode);
                    if (startUnity != null) {
                        // 开启这个仪器的采集线程
                        startUnity.getUsedCollector().startCollection();
                        // 修改仪器采集状态
                        startUnity.getMedicalDevice().setStatusEnum(CollectionStatusEnum.COLLECTING);
                        // 更改显示的采集状态
                        this.runOnUiThread(() -> {
                            // 设置为开始采集
                            startUnity.getFragmentDataExchanger().updateCollectionStatus(CollectionStatusEnum.COLLECTING);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // 上传仪器停止(完成)采集信息
            case DEVICE_FINISH_COLLECTION:
                try {
                    int deviceCode = Integer.parseInt(message.getData());
                    DeviceUnity stopUnity = usedDeviceUnityMap.get(deviceCode);
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
                            bundle.putInt("deviceCode", deviceCode);
                            evaluationFragment.setArguments(bundle);
                            replaceFragment(stopUnity.getDeviceLayoutIds(), evaluationFragment);
                            fragmentTransaction.commit();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // 上传仪器评价信息
            case AFTER_COLLECTION_EVALUATION_TABLE_ADD:
                try {

                    String tableUniqueNumber = message.getData();
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
            case DEVICE_ABANDON_COLLECTION:
                int deviceCode = Integer.parseInt(message.getData());
                // 获取仪器号及对应操作Unity
                DeviceUnity abandonUnity = usedDeviceUnityMap.get(deviceCode);
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
                        usedDeviceUnityMap.remove(deviceCode);
                        ToastUtil.toast(DataCollectionActivity.this, "已放弃该仪器数据采集", TastyToast.INFO);

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
        runOnUiThread(() -> ToastUtil.toast(DataCollectionActivity.this, "网络可能出现异常...", TastyToast.ERROR));
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
     * 弹出全部仪器开始采集的弹窗
     */
    private void showAllDeviceStartCollectionDialog() {
        // 这里弹出确定开始的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("开始采集")
                .setMessage("确定开始所有采集仪器的数据采集?")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 处于等待状态的就开始
                    for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                        if (device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                            // 发送开始请求
                            httpManager.postDeviceCollectionStart(device);
                        }
                    }
                    btnControlAllDevice.setText("全部完成采集");
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }


    /**
     * 弹出全部仪器完成采集的弹窗
     */
    private void showAllDeviceFinishCollectionDialog() {
        // 这里弹出确定开始的弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("完成采集")
                .setMessage("确定完成所有采集仪器的数据采集?")
                .setIcon(R.mipmap.question)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 处于等待状态的就开始
                    for (MedicalDevice device : DeviceUtil.getUsedDeviceList()) {
                        if (device.getStatusEnum() == CollectionStatusEnum.COLLECTING) {
                            // 发送开始请求
                            httpManager.postDeviceCollectionStop(device);
                        }
                    }
                    btnControlAllDevice.setText("全部完成采集");
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
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
                httpManager.postDeviceCollectionAbandon(device);
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
            List<MarkEvent> notUpdateMarkEventList = MarkEventUtil.getNotUpdateMarkEvent();
            for (MarkEvent event : notUpdateMarkEventList) {
                // 上传标记事件列表
                httpManager.postMarkEvent(event);
            }
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

            case R.id.action_collection_help:

                String msg = "1. 数据采集与控制. 本次采集的每个仪器都有一个仪器卡片用于单独的展示其采集的数据," +
                        "通过点击仪器卡片左边的仪器图标并点击确认可以实现单个仪器的\"开始采集\"与\"结束采集\",如果多个仪器" +
                        "是同时开始采集的,可以点击左下方的\"全部开始采集\"简化操作.\n" +
                        "2. 术中事件标记. 点击右下角的\"添加标记\"进行手术事件的添加.\n" +
                        "3. 采集评价. 每个仪器采集完成后会弹出评价弹窗,进行仪器采集的评价.";

                SimpleDialog.show(this, "使用说明", msg, R.mipmap.help);

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
