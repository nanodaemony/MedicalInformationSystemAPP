package com.nano.activity.datamanage;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;

import com.nano.R;
import com.nano.common.logger.Logger;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.sdsmdg.tastytoast.TastyToast;

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
    private Logger logger = new Logger("DataManageActivity");


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



}
