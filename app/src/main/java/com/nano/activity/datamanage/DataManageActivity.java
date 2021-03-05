package com.nano.activity.datamanage;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nano.R;
import com.nano.activity.heartblood.HeartBloodActivity;
import com.nano.common.logger.Logger;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.share.DataSharingUtil;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class DataManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HttpHandler {

    /**
     * Logger
     */
    private Logger logger = new Logger("DataManageActivity");


    /**
     * 请求分享医疗数据的Card
     */
    private MaterialCardView cardRequestMedicalData;
    private MaterialButton btnRequestMedicalData;
    private EditText etSendPid;


    private ListView lvHistoryMedicalData;
    private ListView lvMedicalDataUsage;

    private MaterialCardView cardHandleDataRequest;
    private MaterialButton btnHandleDataRequest;
    private MaterialButton btnHandleDataRequestNo;
    private MaterialButton btnHandleDataRequestYes;
    private TextView tvReceiverPid;
    private TextView tvRequestTid;
    private ListView lvDataSharingLog;
    private ArrayAdapter<String> dataSharingAdapter;
    private List<String> dataSharingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manage);
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

        lvHistoryMedicalData = findViewById(R.id.data_manage_list_view_history_medical_data);
        lvMedicalDataUsage = findViewById(R.id.data_manage_list_view_medical_data_usage_list);

        cardRequestMedicalData = findViewById(R.id.data_manage_card_request_medical_data);
        etSendPid = findViewById(R.id.data_manage_edit_text_input_request_data_pid);
        btnRequestMedicalData = findViewById(R.id.data_manage_btn_request_data);

        cardHandleDataRequest = findViewById(R.id.data_manage_card_handle_medical_data_request);
        btnHandleDataRequest = findViewById(R.id.data_manage_btn_handle_data_sharing_request);

        lvDataSharingLog = findViewById(R.id.data_manage_list_view_do_data_share_log);
        tvReceiverPid = findViewById(R.id.data_manage_text_view_request_receiver_pid);
        tvRequestTid = findViewById(R.id.data_manage_text_view_request_tid);

        btnHandleDataRequestNo = findViewById(R.id.data_manage_btn_handle_data_sharing_request_no);
        btnHandleDataRequestYes = findViewById(R.id.data_manage_btn_handle_data_sharing_request_yes);


        // 忽略别人的数据分享请求
        btnHandleDataRequestNo.setOnClickListener(view -> {
            tvRequestTid.setText("");
            tvReceiverPid.setText("");
            cardHandleDataRequest.setVisibility(View.GONE);
        });
        // 同意别人的数据分享请求
        btnHandleDataRequestYes.setOnClickListener(view -> {
            refreshDataSharingLogList("开始共享数据...");

            refreshDataSharingLogList("查询区块链数据中...");
            // TODO: 这里根据TID以及PID查询医疗数据索引记录,进而获取到数据存储的URL地址.
            String fileUrl = getDataSaveUrlFromOss("PID", "TID");
            if (fileUrl.length() < 10) {
                ToastUtil.toastWarn(this, "数据不存在...");
                refreshDataSharingLogList("数据不存在...");
                return;
            }

            refreshDataSharingLogList("成功获取数据存储URL:" + fileUrl);
            // 从OSS下载数据文件
            String fileName = downloadFileFromOss(fileUrl);
            if ("".equals(fileName)) {
                ToastUtil.toastWarn(this, "OSS文件不存在...");
                refreshDataSharingLogList("OSS文件不存在...");
                return;
            }
            refreshDataSharingLogList("成功获取加密数据.");
            // 下面从文件中加载加密数据
            String originEncryptedMedicalData = loadDataFromFile(fileName);
            refreshDataSharingLogList("成功获取加密数据.");
            // TODO: 解密数据获取明文数据

            String originMedicalData = "";

            // 生成AES秘钥(也可以用户输入)
            String originAesKey = DataSharingUtil.getKey();
            refreshDataSharingLogList("生成原始AES秘钥:" + originAesKey);
            refreshDataSharingLogList("原始数据加密中...");
            // TODO: 利用AES秘钥对原始医疗数据和PID与TID进行加密(具体见论文)
            // 加密后的分享数据
            String shareEncryptedData = "";

            refreshDataSharingLogList("加密AES秘钥中...");


            refreshDataSharingLogList("生成转换秘钥中...");


            // 这里将数据上传给代理服务器,然后等待分享完成...
            refreshDataSharingLogList("数据分享中...");


            refreshDataSharingLogList("数据分享完成!!");

            // 这里弹出确定完成的弹窗，然后关闭数据分享卡片
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("上传完成")
                    .setMessage("您的数据已经完成分享!!!")
                    .setIcon(R.mipmap.post_success)
                    //添加"Yes"按钮
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        cardHandleDataRequest.setVisibility(View.GONE);
                    })
                    // 添加取消
                    .setNegativeButton("取消", (dialogInterface, i) -> {
                        cardHandleDataRequest.setVisibility(View.GONE);
                    })
                    .create();
            alertDialog.show();
        });


    }


    /**
     * 功能初始化
     */
    private void functionInit() {

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
     * 重新采集的弹窗
     */
    private void showRestartCollectorDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("重新开始")
                .setMessage("本次数据采集完成,确定要重新开始新的手术监测吗？")
                .setIcon(R.mipmap.question)
                // 添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 确认后的逻辑
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {

                })
                .create();
        alertDialog.show();
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
            String fileName = list[list.length - 1];
            fileName = dirName + "/" + fileName;
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
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
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
