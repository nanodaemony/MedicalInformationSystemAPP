package com.nano.activity.datamanage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.nano.AppStatic;
import com.nano.R;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ScheduleUtils;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.util.ToastUtil;
import com.nano.http.ServerPathEnum;
import com.nano.http.entity.CommonResult;
import com.nano.http.entity.ParamMedical;
import com.nano.share.DataSharingUtil;
import com.nano.share.MessageEntity;
import com.nano.share.rsa.RsaUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.http.HttpUtil;

/**
 * Description: 基于区块链的医疗数据管理界面
 *
 * 1. 定时任务持续查询后端接口判断是否有新的数据分享请求，如果有则更新底部按钮的显示文本，点击按钮即弹出数据分享的框框。（一次只更新一个请求，处理完成再继续请求）
 *
 *
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/16 23:07
 */
public class DataManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Logger
     */
    private Logger logger = new Logger("DataManageActivity");

    /**
     * 请求分享医疗数据的Card
     */
    private MaterialCardView cardRequestMedicalData;
    private MaterialButton btnRequestMedicalData;
    private EditText etSenderPid;


    // 历史数据记录
    private ListView lvHistoryMedicalIndexData;
    private ArrayAdapter<String> historyDataAdapter;
    private List<PatientDataEntity> historyIndexDataList = new ArrayList<>();
    private List<String> historyIndexDataStringList = new ArrayList<>(16);

    // 数据使用记录
    private ListView lvDataUsageRecord;
    private ArrayAdapter<String> dataUsageRecordAdapter;
    private List<DataUsageEntity> dataUsageRecordList = new ArrayList<>();
    private List<String> dataUsageRecordStringList = new ArrayList<>(16);



    private MaterialCardView cardHandleDataRequest;
    private MaterialButton btnHandleDataRequestNo;
    private MaterialButton btnHandleDataRequestYes;
    private TextView tvReceiverPid;
    private ListView lvDataSharingLog;
    private ArrayAdapter<String> dataSharingAdapter;
    private List<String> dataSharingList = new ArrayList<>();


    /**
     * 当前选择的用于分享的Entity
     */
    private PatientDataEntity currentChooseIndexDataEntity;

    private DataShareEntity currentHandlingEntity;

    private TextView tvUserPid;
    private TextView tvHistoryDataNumber;
    private TextView tvDataUsageRecordNumber;
    private TextView tvBlockchainHeight;
    private TextView tvNewestBlockHash;
    private TextView tvPreviousBlockHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manage);
        // 初始化界面
        viewInit();

        functionInit();

        // 通用定时任务
        ScheduleUtils.executeTask(commonFixedTimeTask, 5, 10, TimeUnit.SECONDS);
    }


    /**
     * 视图初始化
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void viewInit() {
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_data_manage);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // DrawerLayout
        DrawerLayout drawer = findViewById(R.id.drawer_layout_data_manage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // 侧边导航栏的布局
        NavigationView navigationView = findViewById(R.id.nav_view_data_manage);
        navigationView.setNavigationItemSelectedListener(this);

        lvHistoryMedicalIndexData = findViewById(R.id.data_manage_list_view_history_medical_data);
        lvDataUsageRecord = findViewById(R.id.data_manage_list_view_medical_data_usage_list);

        cardRequestMedicalData = findViewById(R.id.data_manage_card_request_medical_data);
        etSenderPid = findViewById(R.id.data_manage_edit_text_input_request_data_pid);
        btnRequestMedicalData = findViewById(R.id.data_manage_btn_request_data);

        cardHandleDataRequest = findViewById(R.id.data_manage_card_handle_medical_data_request);

        lvDataSharingLog = findViewById(R.id.data_manage_list_view_do_data_share_log);
        tvReceiverPid = findViewById(R.id.data_manage_text_view_request_receiver_pid);

        btnHandleDataRequestNo = findViewById(R.id.data_manage_btn_handle_data_sharing_request_no);
        btnHandleDataRequestYes = findViewById(R.id.data_manage_btn_handle_data_sharing_request_yes);

        tvUserPid = findViewById(R.id.data_manage_user_pid);
        tvUserPid.setText(getShortPid(AppStatic.user.getPid()));

        tvHistoryDataNumber = findViewById(R.id.data_manage_history_data_number);
        tvDataUsageRecordNumber = findViewById(R.id.data_manage_history_data_usage_record_number);
        tvBlockchainHeight = findViewById(R.id.data_manage_blockchain_height);
        tvNewestBlockHash = findViewById(R.id.data_manage_newest_block_hash);
        tvPreviousBlockHash = findViewById(R.id.data_manage_previous_block_hash);

        // 进行数据请求的按钮
        btnRequestMedicalData.setOnClickListener(view -> {
            String senderPid = etSenderPid.getText().toString().trim();
            if (senderPid.length() == 0) {
                ToastUtil.toastWarn(this, "数据发送方伪身份ID为空");
                return;
            }
            // 根据PID发起数据分享请求
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.DATA_SHARE_REQUEST;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                try {
                    logger.info("接收方发起数据分享请求...");

                    // 构造数据分享请求体
                    DataShareEntity dataShareEntity = new DataShareEntity();
                    // 设置状态为新请求
                    dataShareEntity.setHandled(false);
                    // 初始化是否同意为false
                    dataShareEntity.setAgree(false);
                    // 将自己的PID设置为接收方PID
                    dataShareEntity.setReceiverPid(AppStatic.user.getPid());
                    // 根据输入获取发送方PID
                    dataShareEntity.setSenderPid(senderPid);

                    // 构造上传的参数，第一个参数为请求放PID，第二个参数为分享实体字符串
                    String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(DataShareRequestCode.RECEIVER_REQUEST_DATA,
                            AppStatic.user.getPid(), JSON.toJSONString(dataShareEntity))));
                    logger.info(res);
                    CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                    if (commonResult != null && commonResult.getCode() == 200) {
                        logger.info("发起数据分享请求成功!!");
                        // 逐一解析得到数据
                        runOnUiThread(() -> {
                            ToastUtil.toastSuccess(this, "发起数据分享请求成功!!请等待对方处理.");
                            // 清空显示并隐藏显示
                            etSenderPid.setText("");
                            cardRequestMedicalData.setVisibility(View.GONE);
                        });
                    } else {
                        runOnUiThread(() -> ToastUtil.toastError(this, "接收方发起数据分享请求失败..."));
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> ToastUtil.toastError(this, "接收方发起数据分享请求失败..."));
                }
            });

        });

        // 拒绝别人的数据分享请求
        btnHandleDataRequestNo.setOnClickListener(view -> {
            tvReceiverPid.setText("");
            cardHandleDataRequest.setVisibility(View.GONE);
            // 表示已经处理
            currentHandlingEntity.setHandled(true);
            // 不同意进行数据分享
            currentHandlingEntity.setAgree(false);
            // 处理完成,继续查询
            isHaveSharingRequestToHandle = false;

            // 将处理后的实体发送到服务器
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.DATA_SHARE_REQUEST;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                try {
                    logger.info("发送方处理后的分享数据到服务器(拒绝).");
                    String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(DataShareRequestCode.SENDER_HANDLE_DATA_SHARE_REQUEST,
                            AppStatic.user.getPid(), JSON.toJSONString(currentHandlingEntity))));
                    logger.info(res);
                    CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                    if (commonResult != null && commonResult.getCode() == 200) {
                        logger.info("发送方处理后的分享数据到服务器: " + commonResult.getData());
                        // 说明获取到新的分享请求
                        if (commonResult.getData().length() > 0) {
                            runOnUiThread(() -> {
                                ToastUtil.toastSuccess(this, "已忽略该分享请求.");
                                // 去掉卡片展示
                                cardHandleDataRequest.setVisibility(View.GONE);
                                currentHandlingEntity = null;
                            });
                        }
                    } else {
                        logger.info("发送方处理后的分享数据到服务器(拒绝):失败");
                    }
                } catch (Exception e) {
                    logger.info("发送方处理后的分享数据到服务器(拒绝):失败");
                }
            });

        });
        // 同意别人的数据分享请求
        btnHandleDataRequestYes.setOnClickListener(view -> {
            if (currentChooseIndexDataEntity == null) {
                ToastUtil.toastWarn(this, "当前未选择需要分享的数据...");
                return;
            }
            // 处理完成继续查询
            isHaveSharingRequestToHandle = false;
            // 同意进行数据分享
            currentHandlingEntity.setAgree(true);
            // 表示已经处理
            currentHandlingEntity.setHandled(true);
            // 设置TID
            currentHandlingEntity.setDataTid(currentChooseIndexDataEntity.getTid());

            refreshDataSharingLogList("开始共享数据...");
            refreshDataSharingLogList("从OSS服务器下载加密数据...");

            ThreadPoolUtils.executeNormalTask(() -> {
                String fileName = downloadFileFromOss(currentChooseIndexDataEntity.getUrl());
                if ("".equals(fileName)) {
                    ToastUtil.toastWarn(this, "OSS文件不存在...");
                    refreshDataSharingLogList("OSS文件不存在...");
                    return;
                }
                try {
                    // 下面从文件中加载加密数据
                    String originEncryptedMedicalData = loadDataFromFile(fileName);
                    refreshDataSharingLogList("成功获取加密数据.");
                    logger.info("加密数据为：" + originEncryptedMedicalData);
                    // 解密获取明文数据
                    String originMedicalData = RsaUtils.decryptDataLong(originEncryptedMedicalData, AppStatic.user.getRsaKeyPair().getPrivate());
                    logger.info("明文数据为: " + originMedicalData);
                    refreshDataSharingLogList("成功解密数据, 共" + originMedicalData.length() + "字节.");
                    // 随机生成AES秘钥(也可以用户输入)
                    String originAesKey = DataSharingUtil.getKey();
                    refreshDataSharingLogList("随机生成原始AES秘钥:" + originAesKey);
                    refreshDataSharingLogList("原始数据加密中...");
                    // 需要加密的原始数据明文
                    String shareData = originMedicalData + "#" + AppStatic.user.getPid() + "#" + currentChooseIndexDataEntity.getTid();
                    currentHandlingEntity.setShareData(shareData);
                    refreshDataSharingLogList("数据分享中...");
                    // 将处理后的实体发送到服务器
                    ServerPathEnum pathEnum = ServerPathEnum.DATA_SHARE_REQUEST;
                    String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                    try {
                        logger.info("发送处理后的分享数据到服务器(同意).");
                        String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(DataShareRequestCode.SENDER_HANDLE_DATA_SHARE_REQUEST,
                                AppStatic.user.getPid(), JSON.toJSONString(currentHandlingEntity))));
                        logger.info("数据处理上传响应:" + res);
                        CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                        if (commonResult != null && commonResult.getCode() == 200) {
                            logger.info("处理结果上传成功: " + commonResult.getData());
                            runOnUiThread(() -> {
                                refreshDataSharingLogList("数据分享完成!!");
                                // 这里弹出确定完成的弹窗，然后关闭数据分享卡片
                                AlertDialog alertDialog = new AlertDialog.Builder(this)
                                        .setTitle("上传完成")
                                        .setMessage("您的数据已经完成分享!!!")
                                        .setIcon(R.mipmap.post_success)
                                        //添加"Yes"按钮
                                        .setPositiveButton("确定", (dialogInterface, i) -> {
                                            cardHandleDataRequest.setVisibility(View.GONE);
                                            tvReceiverPid.setText("");
                                            currentHandlingEntity = null;
                                            currentChooseIndexDataEntity = null;
                                        })
                                        // 添加取消
                                        .setNegativeButton("取消", (dialogInterface, i) -> {
                                            cardHandleDataRequest.setVisibility(View.GONE);
                                            tvReceiverPid.setText("");
                                            currentHandlingEntity = null;
                                            currentChooseIndexDataEntity = null;
                                        })
                                        .create();
                                alertDialog.show();
                            });
                        } else {
                            logger.info("数据处理结果上传失败...");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("数据处理结果上传失败.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        // 历史数据记录列表
        lvHistoryMedicalIndexData.setOnItemClickListener((adapterView, view, position, id) -> {
            view.setBackgroundColor(getColor(R.color.toolBarColor));
            currentChooseIndexDataEntity = historyIndexDataList.get(position);
            logger.info("当前分享数据:" + currentChooseIndexDataEntity.toString());
        });

        lvHistoryMedicalIndexData.setOnItemLongClickListener((parent, view, position, id) -> {
            PatientDataEntity dataEntity = historyIndexDataList.get(position);
            AlertDialog alertDialog = new AlertDialog.Builder(DataManageActivity.this)
                    .setTitle("详细历史数据")
                    .setMessage("数据类型:" + dataEntity.getDataType() + "  User PID: " + (dataEntity.getPatientPseudonymId())
                            + "\n数字签名:" + (dataEntity.getDataSignaturePatient())
                            + "存储地址: " + (dataEntity.getUrl()))
                    .setIcon(R.mipmap.post_success)
                    //添加"Yes"按钮
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                    })
                    // 添加取消
                    .setNegativeButton("取消", (dialogInterface, i) -> {

                    })
                    .create();
            alertDialog.show();

            return false;
        });


        lvDataUsageRecord.setOnItemClickListener((parent, view, position, id) -> {
            DataUsageEntity dataUsageEntity = dataUsageRecordList.get(position);
            AlertDialog alertDialog = new AlertDialog.Builder(DataManageActivity.this)
                    .setTitle("详细数据使用记录")
                    .setMessage("发送方PID: " + (dataUsageEntity.getSenderPseudonymId()) + "\n接收方PID: " + (dataUsageEntity.getReceiverPseudonymId())
                            + "\nData TID: " + dataUsageEntity.getTreatmentId() + "\nTimestamp: " + dataUsageEntity.getTimestamp())
                    .setIcon(R.mipmap.post_success)
                    //添加"Yes"按钮
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                    })
                    // 添加取消
                    .setNegativeButton("取消", (dialogInterface, i) -> {

                    })
                    .create();
            alertDialog.show();
        });

    }


    /**
     * 功能初始化
     */
    private void functionInit() {

        // 查询区块链信息
        queryLedgerInfo();

        // 根据PID请求当前用户的全部历史医疗数据
        queryAllHistoryMedicalIndexRecord();

        // 查询数据使用记录
        queryAllDataUsageRecord();
    }

    /**
     * 根据PID请求当前用户的全部历史医疗数据
     */
    @SuppressLint("SetTextI18n")
    private void queryAllHistoryMedicalIndexRecord() {
        // 根据PID请求当前用户的全部历史医疗数据
        ThreadPoolUtils.executeNormalTask(() -> {
            ServerPathEnum pathEnum = ServerPathEnum.QUERY_ALL_HISTORY_MEDICAL_INDEX_DATA;
            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
            try {
                logger.info("根据PID查询历史IndexRecord.");
                String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(AppStatic.user.getPid())));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null && commonResult.getCode() == 200) {
                    logger.info("历史IndexRecord:" + commonResult.getData());
                    String historyIndexData = commonResult.getData();
                    // 主要下面的切分符号
                    String[] datas = historyIndexData.split("\\^\\*\\$");
                    // 逐一解析得到数据
                    runOnUiThread(() -> {
                        for (String data : datas) {
                            // 解析数据
                            PatientDataEntity dataEntity = JSON.parseObject(data, PatientDataEntity.class);
                            historyIndexDataStringList.add("数据类型:" + dataEntity.getDataType() + "  User PID: " + getShortPid(dataEntity.getPatientPseudonymId())
                                    + "\n数字签名:" + getShortDisplayString(dataEntity.getDataSignaturePatient())
                                    + "存储地址: " + getShortDisplayString(dataEntity.getUrl()));
                            // 加入存储列表中
                            historyIndexDataList.add(dataEntity);
                        }
                        tvHistoryDataNumber.setText("" + historyIndexDataList.size());
                        // 刷新列表展示
                        refreshHistoryDataList();
                    });
                } else {
                    logger.info("查询历史Medical Index Record失败...");
                }
            } catch (Exception e) {
                logger.info("查询历史Medical Index Record失败...");
            }
        });
    }

    /**
     * 根据PID请求当前用户的全部数据使用记录
     */
    @SuppressLint("SetTextI18n")
    private void queryAllDataUsageRecord() {
        // 根据PID请求当前用户的全部历史医疗数据
        ThreadPoolUtils.executeNormalTask(() -> {
            ServerPathEnum pathEnum = ServerPathEnum.QUERY_DATA_USAGE_INFO_BY_PID;
            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
            try {
                logger.info("根据PID查询历史数据使用记录.");
                String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(AppStatic.user.getPid())));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null && commonResult.getCode() == 200) {
                    logger.info("DataUsageRecord:" + commonResult.getData());
                    String dataUsageRecord = commonResult.getData();
                    // 主要下面的切分符号
                    String[] records = dataUsageRecord.split("\\^\\*\\$");
                    // 逐一解析得到数据
                    runOnUiThread(() -> {
                        for (String data : records) {
                            // 解析数据
                            DataUsageEntity dataUsageEntity = JSON.parseObject(data, DataUsageEntity.class);
                            // 加入展示列表

                            String displayString = "发送方PID: " + getShortPid(dataUsageEntity.getSenderPseudonymId()) + "\n接收方PID: " + getShortPid(dataUsageEntity.getReceiverPseudonymId())
                            + "\nData TID: " + dataUsageEntity.getTreatmentId() + "\nTimestamp: " + dataUsageEntity.getTimestamp();
                            dataUsageRecordStringList.add(displayString);
                            // 加入存储列表中
                            dataUsageRecordList.add(dataUsageEntity);
                        }
                        tvDataUsageRecordNumber.setText("" + dataUsageRecordList.size());
                        // 刷新列表展示
                        refreshDataUsageRecordList();
                    });
                } else {
                    logger.info("查询历史Data usage Record失败...");
                }
            } catch (Exception e) {
                logger.info("查询历史Data usage Record失败......");
            }
        });
    }

    /**
     * 查询区块链信息
     */
    @SuppressLint("SetTextI18n")
    private void queryLedgerInfo() {
        ThreadPoolUtils.executeNormalTask(() -> {
            ServerPathEnum pathEnum = ServerPathEnum.QUERY_LEDGER_INFO;
            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
            try {
                logger.info("查询区块链信息.");
                String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(1)));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null && commonResult.getCode() == 200) {
                    // 逐一解析得到数据
                    runOnUiThread(() -> {

                        logger.info("区块链信息:" + commonResult.getData());
                        String[] values = commonResult.getData().split("#");
                        // 更新区块信息
                        tvBlockchainHeight.setText("区块高度: " + values[1]);
                        tvNewestBlockHash.setText("当前区块Hash: " + getShortHashValue(values[2]));
                        tvPreviousBlockHash.setText("前一个区块Hash: " + getShortHashValue(values[3]));
                    });
                } else {
                    logger.info("查询区块链信息失败...");
                }
            } catch (Exception e) {
                logger.info("查询区块链信息失败......");
            }
        });
    }


    private String getShortHashValue(String hash) {
        return hash.substring(0, 10) + "*****" + hash.substring(54, 64);
    }


    private String getShortDisplayString(String data) {
        int len = data.length();
        return data.substring(0, 10) + "*****" + data.substring(len - 10, len);
    }


    /**
     * 通用定时任务
     */
    private Runnable commonFixedTimeTask = () -> {

        // 如果没有新的数据请求则进行
        if (!isHaveSharingRequestToHandle) {
            // 查询是否有新的数据请求
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.DATA_SHARE_REQUEST;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                try {
                    logger.info("发送方检查是否有新的数据分享请求.");
                    String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(DataShareRequestCode.SENDER_CHECK_DATA_SHARE_REQUEST,
                            AppStatic.user.getPid())));
                    logger.info("发送方检查是否有新的数据分享请求: " + res);
                    CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                    if (commonResult != null && commonResult.getCode() == 200) {
                        if (commonResult.getData().length() < 2) {
                            logger.info("发送方定时查询请求,当前无数据分享请求...");

                        // 说明获取到新的分享请求
                        } else if (commonResult.getData().length() >= 2) {
                            // 说明当前已经有请求了,暂时不查询了,处理完成后再查询
                            isHaveSharingRequestToHandle = true;
                            runOnUiThread(() -> {
                                logger.info("发送方定时查询请求,获取到新的数据请求!!!!!!");
                                // 展示数据处理卡片
                                cardHandleDataRequest.setVisibility(View.VISIBLE);
                                // 存储到当前分享请求实体中
                                currentHandlingEntity = JSON.parseObject(commonResult.getData(), DataShareEntity.class);
                                // 接收方PID简略显示
                                String receiverPid = currentHandlingEntity.getReceiverPid().substring(0, 6) + "******" +
                                        currentHandlingEntity.getSenderPid().substring(90, 96);
                                tvReceiverPid.setText(receiverPid);
                                ToastUtil.toastSuccess(this, "您有新的数据分享请求,请及时处理!");
                            });
                        }
                    } else {
                        logger.info("查询失败...");
                    }
                } catch (Exception e) {
                    logger.info("查询失败...");
                }
            });
        }


        // 定时查询是否有新的处理结果
        ThreadPoolUtils.executeNormalTask(() -> {
            ServerPathEnum pathEnum = ServerPathEnum.DATA_SHARE_REQUEST;
            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
            try {
                logger.info("接收方检查数据处理结果.");
                String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(DataShareRequestCode.RECEIVER_CHECK_HANDLE_RESULT,
                        AppStatic.user.getPid())));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null && commonResult.getCode() == 200) {
                    DataShareEntity dataShareEntity = JSON.parseObject(commonResult.getData(), DataShareEntity.class);
                    logger.info("接收方查询成功: " + commonResult.getData());
                    // 说明获取到新的分享请求
                    if (commonResult.getData().length() > 0) {
                        // 下面进行数据解密
                        runOnUiThread(() -> {
                            String result = dataShareEntity.isAgree() ? "同意" : "拒绝";

                            // 将数据存入本地
                            if (dataShareEntity.isAgree()) {
                                saveDataToFile("Share-" + dataShareEntity.getSenderPid() + "-" + dataShareEntity.getDataTid() + ".txt",
                                        dataShareEntity.getShareData());
                            }

                            // 这里弹出有数据处理结果的卡片
                            AlertDialog alertDialog = new AlertDialog.Builder(this)
                                    .setTitle("新的数据分享结果")
                                    .setMessage("您的数据分享请求已完成. 发送方(PID:" + getShortPid(dataShareEntity.getSenderPid()) + ")已经: " + result + "了您的数据请求.")
                                    .setIcon(R.mipmap.post_success)
                                    //添加"Yes"按钮
                                    .setPositiveButton("确定", (dialogInterface, i) -> {
                                        // 说明同意了数据分享
                                        if (dataShareEntity.isAgree()) {



                                        }
                                    })
                                    // 添加取消
                                    .setNegativeButton("取消", (dialogInterface, i) -> {

                                    })
                                    .create();
                            alertDialog.show();
                        });
                    }
                } else {
                    logger.info("查询失败...");
                }
            } catch (Exception e) {
                logger.info("查询失败...");
            }
        });

    };


    /**
     * 返回简约的PID
     */
    private String getShortPid(String pid) {
        return pid.substring(0, 6) + "******" + pid.substring(90, 96);
    }


    /**
     * 目录相关
     *
     * @param menu 目录
     * @return 是否成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_nav_menu_data_manage, menu);
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

            // 请求医疗数据的按钮
            case R.id.data_manage_toolbar_menu_request_data:
                // 显示出请求数据的卡片
                if (cardRequestMedicalData.getVisibility() == View.GONE) {
                    cardRequestMedicalData.setVisibility(View.VISIBLE);
                } else {
                    cardRequestMedicalData.setVisibility(View.GONE);
                }
                break;

            // 维护
            case R.id.action_maintain:

                break;

            case R.id.action_test:
                ThreadPoolUtils.executeNormalTask(() -> {

                    // 说明当前有数据
                    if (pidList.size() > 0) {
                        int index = (int)(System.currentTimeMillis() % pidList.size());
                        etSenderPid.setText(pidList.get(index));
                        logger.info("当前选择用户：" + pidList.get(index));
                    } else {
                        ThreadPoolUtils.executeNormalTask(() -> {
                            ServerPathEnum pathEnum = ServerPathEnum.TEST_GET_SYSTEM_PID_LIST;
                            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                            try {
                                String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(AppStatic.user.getPid())));
                                logger.info(res);
                                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                                if (commonResult != null && commonResult.getCode() == 200) {
                                    logger.info("查询其他用户PID列表:" + commonResult.getData());
                                    runOnUiThread(() -> {
                                        // 设置到本地中
                                        pidList = JSON.parseArray(commonResult.getData(), String.class);
                                        etSenderPid.setText(pidList.get(0));
                                    });
                                } else {
                                    runOnUiThread(() -> ToastUtil.toastError(this, "PHR数据上传失败..."));
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> ToastUtil.toastError(this, "PHR数据上传失败..."));
                            }
                        });
                    }

                });

                break;


            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private List<String> pidList = new ArrayList<>();

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
     * 是否有未处理的新的数据请求
     */
    private static boolean isHaveSharingRequestToHandle = false;


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
     * 刷新数据分享时的日志列表
     *
     * @param log 日志
     */
    private void refreshDataSharingLogList(String log) {
        dataSharingList.add(log);
        dataSharingAdapter = new ArrayAdapter<>(this, R.layout.item_log_message, dataSharingList);
        lvDataSharingLog.setAdapter(dataSharingAdapter);
        lvDataSharingLog.setSelection(dataSharingList.size() - 1);
    }


    /**
     * 刷新数据分享时的日志列表
     */
    private void refreshHistoryDataList() {
        historyDataAdapter = new ArrayAdapter<>(this, R.layout.item_log_message, historyIndexDataStringList);
        lvHistoryMedicalIndexData.setAdapter(historyDataAdapter);
        lvHistoryMedicalIndexData.setSelection(historyIndexDataStringList.size() - 1);
    }


    /**
     * 刷新数据使用记录的日志列表
     */
    private void refreshDataUsageRecordList() {
        dataUsageRecordAdapter = new ArrayAdapter<>(this, R.layout.item_log_message, dataUsageRecordStringList);
        lvDataUsageRecord.setAdapter(dataUsageRecordAdapter);
        lvDataUsageRecord.setSelection(dataUsageRecordStringList.size() - 1);
    }

    /**
     * 从区块链查询数据存储的URL地址
     * @param pid PID
     * @param tid TID
     * @return 地址
     */
    private String getDataSaveUrlFromOss(String pid, String tid) {
        return "";
    }


    /**
     * 通过OSS下载文件
     *
     * @param docUrl 文件OSS地址
     */
    public String downloadFileFromOss(String docUrl) {
        try {
            /***加载正文***/
            //获取存储卡路径、构成保存文件的目标路径
            String dirName = "";
            //SD卡具有读写权限、指定附件存储路径为SD卡上指定的文件夹
            dirName = getApplicationContext().getFilesDir().getAbsolutePath();
            Log.d("Test", "Download: Dirname" + dirName);
            File f = new File(dirName);
            if (!f.exists()) {      //判断文件夹是否存在
                f.mkdir();        //如果不存在、则创建一个新的文件夹
            }
            //准备拼接新的文件名
            String[] list = docUrl.split("/");
            // 存储名称
            String storageName = list[list.length - 1];
            // 包含完成路径的名称
            String fileName = dirName + "/" + storageName;
            File file = new File(fileName);
            if (file.exists()) {    //如果目标文件已经存在
                file.delete();    //则删除旧文件
            }
            //1K的数据缓冲
            byte[] bs = new byte[1024];
            //读取到的数据长度
            int len;
            try {
                //通过文件地址构建url对象
                URL url = new URL(docUrl);
                //获取链接
                //URLConnection conn = url.openConnection();
                //创建输入流
                InputStream is = url.openStream();
                //获取文件的长度
                //int contextLength = conn.getContentLength();
                //输出的文件流
                OutputStream os = new FileOutputStream(file);
                //开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                //完毕关闭所有连接
                os.close();
                is.close();
            } catch (MalformedURLException e) {
                fileName = null;
                System.out.println("创建URL对象失败");
                throw e;
            } catch (FileNotFoundException e) {
                fileName = null;
                System.out.println("无法加载文件");
                throw e;
            } catch (IOException e) {
                fileName = null;
                System.out.println("获取连接失败");
                throw e;
            }
            logger.info("Test", "下载成功" + fileName);
            return storageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 存储数据到文件
     *
     * @param fileName 文件名 如 121212121.txt(无需路径)
     * @param data     数据
     */
    public void saveDataToFile(String fileName, String data) {
        // 通过线程池存储数据到文件
        ThreadPoolUtils.executeNormalTask(() -> {
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try {
                out = openFileOutput(fileName, Context.MODE_APPEND);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 从文件加载数据
     */
    public String loadDataFromFile(String fileName) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            in = openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

}
