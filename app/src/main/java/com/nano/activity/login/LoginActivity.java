package com.nano.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nano.R;
import com.nano.WorkingModeEnum;
import com.nano.activity.datamanage.DataManageActivity;
import com.nano.activity.heartblood.HeartBloodActivity;
import com.nano.common.logger.Logger;
import com.nano.AppStatic;
import com.nano.common.util.SimpleDialog;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.nano.activity.devicedata.healthrecord.HealthRecordActivity;
import com.nano.common.logger.LoggerFactory;
import com.nano.http.ServerPathEnum;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Description: 登录界面
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/22 22:12
 */
public class LoginActivity extends AppCompatActivity implements HttpHandler {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger("[LoginActivity]");

    private EditText edUserName;
    private EditText edPassword;

    // 当前profile的计数器
    private int profileCounter = 0;

    private RadioButton btnHeartAndBloodCollection;
    private RadioButton btnDeviceDataCollection;
    private RadioButton btnDataManage;

    private HttpManager httpManager = new HttpManager(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        // Init widgets
        edUserName = findViewById(R.id.login_editText_userName);
        edPassword = findViewById(R.id.login_editText_password);
        Button btnLogin = findViewById(R.id.login_button_login);
        // 忘记密码
        TextView tvForgetPassword = findViewById(R.id.login_textView_forgetPassword);
        btnHeartAndBloodCollection = findViewById(R.id.login_ratioButton_heartandblood_mode);
        btnDeviceDataCollection = findViewById(R.id.login_ratioButton_device_data_mode);
        btnDataManage = findViewById(R.id.login_ratioButton_datamanage_mode);

        // 查询网络状态
        httpManager.getNetworkStatus();

        // 登录按钮
        btnLogin.setOnClickListener(v -> {
            // 解决登录时通过空格输入的问题
            // 用户名
            String userName = edUserName.getText().toString();
            // 登录密码
            String password = edPassword.getText().toString();
            // 校验用户名和密码
            if (verifyUser(userName, password)) {
                if (btnDataManage.isChecked()) {
                    Intent intent = new Intent(LoginActivity.this, DataManageActivity.class);
                    startActivity(intent);
                    AppStatic.workingMode = WorkingModeEnum.DATA_MANAGEMENT;
                } else if (btnHeartAndBloodCollection.isChecked()) {
                    Intent intent = new Intent(LoginActivity.this, HeartBloodActivity.class);
                    startActivity(intent);
                    AppStatic.workingMode = WorkingModeEnum.HEART_BLOOD;
                } else if (btnDeviceDataCollection.isChecked()) {
                    Intent intent = new Intent(LoginActivity.this, HealthRecordActivity.class);
                    startActivity(intent);
                    AppStatic.workingMode = WorkingModeEnum.DEVICE_DATA;
                }
            } else {
                ToastUtil.toast(getApplicationContext(), "用户名或密码错误", TastyToast.ERROR);
            }

        });

        // 忘记密码
        tvForgetPassword.setOnClickListener(v -> SimpleDialog.show(LoginActivity.this, "忘记密码", "请联系QQ：1174520425", R.mipmap.help));
    }


    /**
     * 判断用户是否合格
     * @return 是否合格
     */
    private boolean verifyUser(String user, String password) {
        if (AppStatic.debug) {
            return true;
        }
        if (user.length() == 4 && password.length() == 6 && "root".equals(user) && "123456".equals(password)) {
            return true;
        }
        if (user.length() == 4 && password.length() == 6 && "Root".equals(user) && "123456".equals(password)) {
            return true;
        }
        return true;
    }



    /**
     * 提示当前的网络状态不对
     */
    private void toastRetryConnectServer() {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("提示")
                .setMessage("无法连接服务器,是否刷新网络?")
                .setIcon(R.mipmap.question)
                // 添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // 发送结束请求
                    httpManager.getNetworkStatus();
                })
                // 添加取消
                .setNegativeButton("取消", (dialogInterface, i) -> {
                    // DO Nothing.
                })
                .create();
        alertDialog.show();
    }

    /**
     * 网络成功回调接口
     * @param message 数据
     */
    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        runOnUiThread(() -> {
            AppStatic.isNetworkConnected = true;
            ToastUtil.toast(LoginActivity.this, "网络正常", TastyToast.SUCCESS);
        });
    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {

    }

    /**
     * 网络失败接口
     */
    @Override
    public void handleNetworkFailedMessage() {
        runOnUiThread(() -> ToastUtil.toast(LoginActivity.this, "网络异常", TastyToast.ERROR));
    }


    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    public File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}