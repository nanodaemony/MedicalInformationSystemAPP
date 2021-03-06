package com.nano.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nano.R;
import com.nano.WorkingModeEnum;
import com.nano.activity.datamanage.DataManageActivity;
import com.nano.activity.heartblood.HeartBloodActivity;
import com.nano.common.logger.Logger;
import com.nano.AppStatic;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.threadpool.core.TaskExecutor;
import com.nano.common.util.PersistUtil;
import com.nano.common.util.SimpleDialog;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.nano.activity.devicedata.healthrecord.HealthRecordActivity;
import com.nano.common.logger.LoggerFactory;
import com.nano.http.ServerPathEnum;
import com.nano.http.entity.CommonResult;
import com.nano.http.entity.ParamMedical;
import com.nano.share.rsa.RsaUtils;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.hutool.http.HttpUtil;


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

    private ImageView ivRegisterDoctor;

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

            // 进行登录
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.USER_LOGIN;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                TaskExecutor.executeHttpTask(() -> {
                    try {
                        logger.info("用户登录.");
                        String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(userName, password)));
                        logger.info(res);
                        CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                        if (commonResult != null && commonResult.getCode() == 200) {
                            logger.info("用户登录:" + commonResult.getData());

                            runOnUiThread(() -> {
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
                            });

                            logger.info(AppStatic.user.toString());

                        } else {
                            runOnUiThread(() -> ToastUtil.toastWarn(this, "登录失败..."));
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> ToastUtil.toastWarn(this, "登录失败..."));
                    }
                });

            });

            // 校验用户名和密码
            if (verifyUser(userName, password)) {

            } else {
                ToastUtil.toast(getApplicationContext(), "用户名或密码错误", TastyToast.ERROR);
            }

        });




//        // 生成模拟文件到本地
//        ThreadPoolUtils.executeNormalTask(() -> {
//
//            StringBuilder data = new StringBuilder();
//            for (int i = 0; i < 10000; i++) {
//                data.append("@1@324@34@324#32#78#78#87").append(System.currentTimeMillis()).append("\n");
//                if (i % 10000 == 0) {
//                    logger.info(i + "");
//                }
//            }
//            saveDataToFile("Test1234.txt", data.toString());
//            logger.info("长度:" + data.toString().length());
//        });
//

        // 初始加载上次的信息
        edUserName.setText(PersistUtil.getStringValue("PID"));
        AppStatic.user.setPid(PersistUtil.getStringValue("PID"));
        edPassword.setText(PersistUtil.getStringValue("Password"));



        // 注册按钮
        Button btnRegister = findViewById(R.id.login_button_register);
        btnRegister.setOnClickListener(view -> {
            // 获取用户名与密码
            String userName = edUserName.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            if ("".equals(userName) || "".equals(password)) {
                ToastUtil.toastWarn(this, "注册时请输入完整的用户名与密码!!!");
                return;
            }
            // 进行注册
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.USER_REGISTER;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                TaskExecutor.executeHttpTask(() -> {
                    try {
                        logger.info("用户注册.");
                        String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(1, userName, password)));
                        logger.info(res);
                        CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                        if (commonResult != null && commonResult.getCode() == 200) {
                            logger.info("用户注册:" + commonResult.getData());
                            runOnUiThread(() -> {
                                ToastUtil.toastSuccess(this, "您已注册成功,点击登录即可登录.");
                                edUserName.setText(commonResult.getData());
                            });
                            // 注册成功之后将上述信息缓存到本地
                            PersistUtil.putStringValue("UserName", userName);
                            PersistUtil.putStringValue("Password", password);
                            PersistUtil.putStringValue("PID", commonResult.getData());
                            // 更新PID
                            AppStatic.pid = commonResult.getData();
                            AppStatic.user.setPid(commonResult.getData());

                        } else {
                            runOnUiThread(() -> ToastUtil.toastWarn(this, "用户注册失败..."));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> ToastUtil.toastError(this, "注册失败..."));
                    }
                });

            });

        });

        // 注册医生的PID
        ivRegisterDoctor = findViewById(R.id.login_image_register_doctor);
        ivRegisterDoctor.setOnClickListener(view -> {
            // 进行注册
            ThreadPoolUtils.executeNormalTask(() -> {
                ServerPathEnum pathEnum = ServerPathEnum.USER_REGISTER;
                String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
                TaskExecutor.executeHttpTask(() -> {
                    try {
                        logger.info("注册医生.");
                        String res = HttpUtil.post(path, JSON.toJSONString(new ParamMedical(1, "111" + System.currentTimeMillis(), "password")));
                        logger.info(res);
                        CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                        if (commonResult != null && commonResult.getCode() == 200) {
                            logger.info("注册医生:" + commonResult.getData());
                            // 注册成功之后将上述信息缓存到本地
                            PersistUtil.putStringValue("PIDDoctor", commonResult.getData());
                            AppStatic.doctorPseudoId = commonResult.getData();
                        } else {
                        }
                    } catch (Exception e) {
                    }
                });

            });
        });

        // 忘记密码
        tvForgetPassword.setOnClickListener(v -> SimpleDialog.show(LoginActivity.this, "忘记密码", "请联系QQ：1174520425", R.mipmap.help));
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