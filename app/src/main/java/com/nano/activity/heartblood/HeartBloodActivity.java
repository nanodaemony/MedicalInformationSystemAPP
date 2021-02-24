package com.nano.activity.heartblood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nano.R;
import com.nano.activity.dataupload.DataUploadActivity;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ThreadPoolUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import static android.provider.Telephony.Mms.Part.CHARSET;

/**
 * Description: 心电血氧数据采集界面
 * 步骤：
 * 1. 初始化监听服务器,监听仪器的连接请求.
 * 2. 当连接成功之后,持续监听接收的内容,对接收的内容进行处理.
 * 3. 通过界面可以发送控制命令.
 * 4. 采集的数据发送到服务器中,进行临时存储.
 * 5. 采集流程控制: 底部按钮控制整体的开始与停止，过程为: 等待，开始，结束，询问数据是否丢弃与上传。
 * 单个仪器的控制过程为 暂停与开启，其控制由自己的卡片上的按钮控制。
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/10 14:14
 */
public class HeartBloodActivity extends AppCompatActivity {


    private Logger logger = new Logger("HeartBloodActivity");

    /**
     * 整体采集状态
     */
    private TextView tvWholeCollectionStatus;

    /**
     * 两种参数分别的采集状态
     */
    private TextView tvCollectionStatusBlood;
    private TextView tvCollectionStatusHeart;

    /**
     * 血氧参数
     */
    private TextView tvDataHr;
    private TextView tvDataSpo2;

    /**
     * 进行后续数据处理的按钮
     */
    private MaterialButton btnDoDataProccesing;

    private TextView tvDataTest;

    private ServerSocket serverSocket;
    private ArrayList<SocketThread> socketThreads;
    private boolean isFinished;

    // 当前的连接对象
    private Socket currentSocket;
    private InputStream currentInputStream;
    private OutputStream currentOutputStream;

    /**
     * 采集状态
     */
    private PortableCollectionStatusEnum wholeCollectionStatus = PortableCollectionStatusEnum.WAITING;
    private PortableCollectionStatusEnum heartCollectionStatus = PortableCollectionStatusEnum.WAITING;
    private PortableCollectionStatusEnum bloodCollectionStatus = PortableCollectionStatusEnum.WAITING;

    /**
     * 当前采集场次号
     */
    private long currentCollectionNumber = System.currentTimeMillis();

    /**
     * 临时存放监测数据
     */
    private StringBuilder heartDataBuilder = new StringBuilder();
    private StringBuilder bloodDataBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_blood);

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
        Toolbar toolbar = findViewById(R.id.heart_blood_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // 整体采集状态
        tvWholeCollectionStatus = findViewById(R.id.heart_blood_whole_status);
        tvCollectionStatusBlood = findViewById(R.id.heart_blood_collection_status_blood);
        tvCollectionStatusHeart = findViewById(R.id.heart_blood_collection_status_heart);

        tvDataHr = findViewById(R.id.heart_blood_data_hr);
        tvDataSpo2 = findViewById(R.id.heart_blood_data_spo2);
        tvDataTest = findViewById(R.id.heart_blood_data_test);

        btnDoDataProccesing = findViewById(R.id.heart_blood_do_data_processing);

        // 采集控制按钮
        MaterialButton btnControl = findViewById(R.id.heart_blood_control_button);
        btnControl.setOnClickListener(view -> {

            try {
                // 如果是等待状态
                if (wholeCollectionStatus == PortableCollectionStatusEnum.WAITING) {
                    // 发送控制命令
                    sendCommandToMedicalDevice("#3");
                    Thread.sleep(1000);
                    sendCommandToMedicalDevice("#5");
                    // 修改状态显示
                    wholeCollectionStatus = PortableCollectionStatusEnum.COLLECTING;
                    tvWholeCollectionStatus.setText("数据采集中...");
                    tvCollectionStatusBlood.setText("采集中");
                    // tvCollectionStatusHeart.setText("采集中");
                } else if (wholeCollectionStatus == PortableCollectionStatusEnum.COLLECTING) {
                    // 发送控制命令
                    sendCommandToMedicalDevice("#4");
                    Thread.sleep(1000);
                    sendCommandToMedicalDevice("#6");
                    // 修改状态显示
                    wholeCollectionStatus = PortableCollectionStatusEnum.FINISH;
                    tvWholeCollectionStatus.setText("数据完成采集");
                    tvCollectionStatusBlood.setText("采集完成");

                    // TODO:此时关闭线程

                    // 弹出数据处理的按钮
                    btnDoDataProccesing.setVisibility(View.VISIBLE);
                    btnControl.setText("不处理数据");

                    // 说明此时是选择了不处理数据,此时相当于进行初始化，重新等待采集数据
                } else if (wholeCollectionStatus == PortableCollectionStatusEnum.FINISH) {
                    wholeCollectionStatus = PortableCollectionStatusEnum.WAITING;
                    tvWholeCollectionStatus.setText("等待连接中...");
                    btnDoDataProccesing.setVisibility(View.GONE);
                    tvCollectionStatusBlood.setText("等待采集");
                    tvCollectionStatusHeart.setText("等待采集");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 控制血氧采集与暂停的按钮(仅切换开始与等待的两种状态)
        tvCollectionStatusBlood.setOnClickListener(view1 -> {
            try {
                // 由等待变成采集中
                if (bloodCollectionStatus == PortableCollectionStatusEnum.WAITING) {
                    bloodCollectionStatus = PortableCollectionStatusEnum.COLLECTING;
                    sendCommandToMedicalDevice("#5");
                    tvCollectionStatusBlood.setText("正在采集");

                } else if (bloodCollectionStatus == PortableCollectionStatusEnum.COLLECTING) {
                    bloodCollectionStatus = PortableCollectionStatusEnum.WAITING;
                    sendCommandToMedicalDevice("#6");
                    tvCollectionStatusBlood.setText("等待采集");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 控制心电采集与暂停的按钮(仅切换开始与等待的两种状态)
        tvCollectionStatusHeart.setOnClickListener(view1 -> {
            try {
                // 由等待变成采集中
                if (heartCollectionStatus == PortableCollectionStatusEnum.WAITING) {
                    heartCollectionStatus = PortableCollectionStatusEnum.COLLECTING;
                    sendCommandToMedicalDevice("#3");
                    tvCollectionStatusHeart.setText("正在采集");
                } else if (heartCollectionStatus == PortableCollectionStatusEnum.COLLECTING) {
                    heartCollectionStatus = PortableCollectionStatusEnum.WAITING;
                    sendCommandToMedicalDevice("#4");
                    tvCollectionStatusHeart.setText("等待采集");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 进行数据处理
        btnDoDataProccesing.setOnClickListener(view -> {

//            Intent intent = new Intent(HeartBloodActivity.this, DataUploadActivity.class);
//            startActivity(intent);

            saveDataToFile("Test123.txt", "123123123");


            ThreadPoolUtils.executeNormalTask(() -> uploadFile("Test123.txt",
                    "http://39.98.122.209:10088/eval/oss/uploadfile"));
        });

    }


    private void functionInit() {
        isFinished = false;
        // 线程池内执行任务
        ThreadPoolUtils.executeNormalTask(() -> {
            try {
                // 创建服务器套接字,绑定到指定的端口
                serverSocket = new ServerSocket(10087);
                // 等待客户端连接
                while (!isFinished) {
                    // 接受连接
                    Socket socket = serverSocket.accept();
                    currentSocket = socket;
                    System.out.println("连接成功.");
                    runOnUiThread(() -> {
                        tvWholeCollectionStatus.setText("已连接仪器");
                    });
                    // 创建数据处理线程
                    SocketThread socketThread = new SocketThread(socket);
                    System.out.println(new String(socket.getInetAddress().getAddress()));
                    socketThreads.add(socketThread);
                    socketThread.start();
                }
            } catch (IOException e) {
                isFinished = true;
            }
        });
        tvWholeCollectionStatus.setText("连接监听中...");
    }


    /**
     * 接收心电与血氧数据的线程
     */
    private class SocketThread extends Thread {

        private Socket socket;
        private InputStream in;
        private OutputStream out;

        SocketThread(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (in == null) {
                    return;
                }
                try {
                    int available = in.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        int size = in.read(buffer);
                        if (size > 0) {
                            String data = new String(buffer, 0, size);
                            // 这里看看能不能得到数据
                            runOnUiThread(() -> tvDataTest.setText(data));
                            // 进行数据处理
                            if (data.startsWith("#")) {
                                // 进行数据缓存
                                heartDataBuilder.append(data);
                                if (heartDataBuilder.length() > 10000) {
                                    // 持久化数据到文件
                                    saveDataToFile("Heart" + currentCollectionNumber + ".txt", heartDataBuilder.toString());
                                    // 清空缓存
                                    heartDataBuilder.setLength(0);
                                }
                                // 做数据展示

                            } else if (data.startsWith("@")) {
                                bloodDataBuilder.append(data).append("\n");
                                if (bloodDataBuilder.length() > 100) {
                                    // 持久化数据到文件
                                    saveDataToFile("Blood" + currentCollectionNumber + ".txt", bloodDataBuilder.toString());
                                    bloodDataBuilder.setLength(0);
                                }
                                // 做数据展示
                            }
                            System.out.println(data);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void close() {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 往医疗仪器发送控制数据
     * @param data 控制数据
     * @return 是否成功
     */
    private boolean sendCommandToMedicalDevice(String data) {
        try {
            currentSocket.getOutputStream().write(data.getBytes());
            currentSocket.getOutputStream().flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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


        // TODO: 获取数据路径!!!!!!!
        Environment.getDataDirectory();

        return builder.toString();
    }


    public Boolean uploadFile(String file, String urlStr) {
        Boolean result = false;
        String BOUNDARY = "letv"; // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        //String CONTENT_TYPE = "application/json"; // json
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);

                sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + file + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());

                InputStream is = openFileInput(file);
                byte[] bytes = new byte[1024 * 1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int res = conn.getResponseCode();
                logger.info(conn.getResponseMessage());
                if (res == 200) {
                    result = true;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }
}
