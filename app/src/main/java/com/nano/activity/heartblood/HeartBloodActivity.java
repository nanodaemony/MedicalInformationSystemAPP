package com.nano.activity.heartblood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nano.AppStatic;
import com.nano.R;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.threadpool.core.TaskExecutor;
import com.nano.common.util.ToastUtil;
import com.nano.http.ServerPathEnum;
import com.nano.http.entity.CommonResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.hutool.http.HttpUtil;

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

    /**
     * 接收数据相关
     */
    private ServerSocket serverSocket;
    private ArrayList<SocketThread> socketThreadList = new ArrayList<>();
    private boolean isFinished;

    // 当前的连接对象
    private Socket currentSocket;

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
    private StringBuilder dataBuilder = new StringBuilder();
    private StringBuilder heartDataBuilder = new StringBuilder();
    private StringBuilder bloodDataBuilder = new StringBuilder();

    /**
     * 数据上传日志列表
     */
    private ListView lvDataUploadLog;
    private ArrayAdapter<String> logAdapter;
    private List<String> logList = new ArrayList<>();


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

        btnDoDataProccesing = findViewById(R.id.heart_blood_do_data_processing);
        lvDataUploadLog = findViewById(R.id.heart_blood_data_upload_log);

        // 注册EventBus
        EventBus.getDefault().register(this);

        // simulator();
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
                    tvWholeCollectionStatus.setTextColor(getColor(R.color.collectingData));
                    tvCollectionStatusBlood.setText("采集中");
                    tvCollectionStatusHeart.setText("采集中");
                    btnControl.setText("完成采集");
                } else if (wholeCollectionStatus == PortableCollectionStatusEnum.COLLECTING) {
                    // 发送控制命令
                    sendCommandToMedicalDevice("#4");
                    Thread.sleep(1000);
                    sendCommandToMedicalDevice("#6");
                    // 修改状态显示
                    wholeCollectionStatus = PortableCollectionStatusEnum.FINISH;
                    tvWholeCollectionStatus.setText("数据完成采集");
                    tvWholeCollectionStatus.setTextColor(getColor(R.color.colorPrimary));
                    tvCollectionStatusBlood.setText("采集完成");
                    tvCollectionStatusHeart.setText("采集完成");
                    // 弹出数据处理的按钮
                    btnDoDataProccesing.setVisibility(View.VISIBLE);
                    btnControl.setText("不处理数据");

                    // 此时逐一关闭线程
                    for (SocketThread thread : socketThreadList) {
                        thread.close();
                    }
                    // 说明此时是选择了不处理数据,此时相当于进行初始化，重新等待采集数据
                } else if (wholeCollectionStatus == PortableCollectionStatusEnum.FINISH) {
                    // 直接回到登录界面
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 控制血氧采集与暂停的按钮(仅切换开始与等待的两种状态)
        tvCollectionStatusBlood.setOnClickListener(view1 -> {
            try {
                if (wholeCollectionStatus == PortableCollectionStatusEnum.FINISH) {
                    ToastUtil.toastWarn(this, "当前已完成采集");
                } else {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 控制心电采集与暂停的按钮(仅切换开始与等待的两种状态)
        tvCollectionStatusHeart.setOnClickListener(view1 -> {
            try {
                if (wholeCollectionStatus == PortableCollectionStatusEnum.FINISH) {
                    ToastUtil.toastWarn(this, "当前已完成采集");
                } else {
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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 进行数据处理
        btnDoDataProccesing.setOnClickListener(view -> {

            // TODO: 这里判断是否采集并将数据保存到了本地，如果没有则无法进行数据处理
            lvDataUploadLog.setVisibility(View.VISIBLE);
            refreshDataUploadLogList("准备开始数据上链...");

            // 1. 对文件进行加密处理
            refreshDataUploadLogList("加载文件中...");
            String data = loadDataFromFile(HeartBloodUtil.getNewFileName(currentCollectionNumber));
            refreshDataUploadLogList("文件加载完成...");
            refreshDataUploadLogList("初始文件大小:" + data.length() + "字节.");
            refreshDataUploadLogList("开始进行数据加密...");
            // 获取加密后的数据
            String enData = doDataEncryption(data);
            saveDataToFile("HeartBloodEncryption" + currentCollectionNumber + ".txt", enData);
            refreshDataUploadLogList("数据加密完成");
            refreshDataUploadLogList("加密后文件大小:" + enData.length() + "字节.");
            // 2. 将文件上传到OSS服务器上，并得到文件存储的URL地址
            refreshDataUploadLogList("上传数据至OSS服务器...");
            ThreadPoolUtils.executeNormalTask(() -> {
                // uploadFileToOss(getFileStreamPath("HeartBloodEncryption" + currentCollectionNumber + ".txt"), "http://192.168.8.120:10086/eval/oss/uploadAudio");
            });

            // 上传之后获取文件存储的地址
            String fileUrl = null;
            int cnt = 5;
            while (cnt-- > 0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshDataUploadLogList("查询上传状态...");
                fileUrl = getFileStorageUrlFromServer("HeartBloodEncryption" + currentCollectionNumber + ".txt");
                if (fileUrl.length() > 10) {
                    refreshDataUploadLogList("文件已成功上传,URL: " + fileUrl);
                    break;
                } else {
                    refreshDataUploadLogList("文件尚未上传完成,重试中...");
                }
            }
            if (fileUrl.length() > 10) {
                // 3. 请求后端进行数据上链
                // 构造数据上传的数据结构
                MedicalDataEntity dataEntity = new MedicalDataEntity();
                dataEntity.setTimestamp(System.currentTimeMillis());
                dataEntity.setDataSaveUrl(fileUrl);
                dataEntity.setDataType("PHR");
                dataEntity.setPatientPseudonymId(AppStatic.pseudoId);
                refreshDataUploadLogList("计算数据摘要...");
                String messageDigest = getMessageDigest(loadDataFromFile("HeartBloodEncryption" + currentCollectionNumber + ".txt"));
                refreshDataUploadLogList("数据摘要: " + messageDigest);
                refreshDataUploadLogList("计算数据签名...");
                // 下面进行数据处理
                String patientSignature = getPatientSignature(messageDigest);
                refreshDataUploadLogList("数据签名: " + patientSignature);
                dataEntity.setTreatmentId(AppStatic.treatmentId);



            }




        });

//        long time = System.currentTimeMillis();
//        ThreadPoolUtils.executeNormalTask(() -> {
//            uploadFileToOss(getFileStreamPath("Test.txt"), "http://192.168.8.120:10086/eval/oss/uploadAudio");
//        });
//
//        logger.info("文件URL:" + getFileStorageUrlFromServer("Test.txt"));



    }


    private void functionInit() {
        isFinished = false;

        // 线程池内执行任务
        ThreadPoolUtils.executeNormalTask(() -> {
            try {
                logger.info("开启端口监听：10087");
                // 创建服务器套接字,绑定到指定的端口
                serverSocket = new ServerSocket(10087);
                // 等待客户端连接
                while (!isFinished) {
                    // 接受连接
                    Socket socket = serverSocket.accept();
                    currentSocket = socket;
                    logger.info("连接成功.");
                    runOnUiThread(() -> {
                        tvWholeCollectionStatus.setText("已连接仪器");
                        tvWholeCollectionStatus.setTextColor(getColor(R.color.colorBlue));
                    });
                    // 创建数据处理线程
                    SocketThread socketThread = new SocketThread(socket);
                    logger.info(new String(socket.getInetAddress().getAddress()));
                    socketThreadList.add(socketThread);
                    socketThread.start();
                }
            } catch (IOException e) {
                isFinished = true;
            }
        });
        tvWholeCollectionStatus.setText("连接监听中...");
    }


    /**
     * 接收仪器数据的EventBus回调
     *
     * @param dataEntity 仪器数据类
     */
    @SuppressLint("SetTextI18n")
    @Subscribe
    public void onEventMainThread(DataEntity dataEntity) {

        // 将数据持久化到文件中
        dataBuilder.append(dataEntity.getData()).append("\n");
        saveDataToFile("HeartBloodNew" + currentCollectionNumber + ".txt", dataBuilder.toString());
        // 清空数据缓存
        dataBuilder.setLength(0);

        // 下面解析数据并展示
        switch (dataEntity.getType()) {
            // 心电数据
            case 1:
                // 做数据展示
                String[] heartValues = dataEntity.getData().split("#");
                for (String data : heartValues) {
                    if (data.length() > 0 && data.length() <= 4) {
                        // 进行反转然后解析后传入绘制图形
                        EcgView.addEcgData0(3000 + Integer.parseInt(HeartBloodUtil.reverseHeartValue(data)));
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            // 血氧数据
            case 2:
                String data = dataEntity.getData();
                String[] values = data.split("@");
                // 血氧数据展示
                if (values.length >= 4) {
                    int hr = Integer.parseInt(values[1]);
                    int hrValid = Integer.parseInt(values[2]);
                    int spo2 = Integer.parseInt(values[3]);
                    int spo2Valid = Integer.parseInt(values[4]);
                    logger.info("" + hr);
                    logger.info("" + spo2);
                    if (hrValid == 1 && hr >= 30 && hr <= 150) {
                        tvDataHr.setText("" + hr);
                    } else {
                        tvDataHr.setText("--");
                    }

                    if (spo2Valid == 1 && spo2 >= 0 && spo2 <= 120) {
                        tvDataSpo2.setText("" + spo2);
                    } else {
                        tvDataSpo2.setText("--");
                    }
                }
                break;

            default:
                break;
        }

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
                            logger.info(data);
                            // 将采集的数据传出
                            if (data.contains("#")) {
                                EventBus.getDefault().post(new DataEntity(1, data));
                            } else if (data.startsWith("@")) {
                                EventBus.getDefault().post(new DataEntity(2, data));
                            }
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
     *
     * @param data 控制数据
     * @return 是否成功
     */
    private boolean sendCommandToMedicalDevice(String data) {
        ThreadPoolUtils.executeNormalTask(() -> {
            try {
                currentSocket.getOutputStream().write(data.getBytes());
                currentSocket.getOutputStream().flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
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
     * 将文件上传到OSS
     *
     * @param file       文件
     * @param serverPath 地址
     * @return 是否成功
     */
    public boolean uploadFileToOss(File file, String serverPath) {
        logger.info("上传文件到服务器");
        String BOUNDARY = "letv"; // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        //String CONTENT_TYPE = "application/json"; // json
        String CONTENT_TYPE = "multipart/form-data";
        try {
            URL url = new URL(serverPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
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
                sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
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
                if (res == 200) {
                    logger.info("文件上传OSS成功...");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * 通过服务器获取文件存储地址
     * @return 文件存储地址
     */
    private String getFileStorageUrlFromServer(String fileName) {
        // "HeartBloodEncryption" + currentCollectionNumber + ".txt"
        Future<String> future = TaskExecutor.taskExecutor.submit(new GetFileStorageThread(fileName));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private class GetFileStorageThread implements Callable<String> {

        private String fileName;

        public GetFileStorageThread(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call() throws Exception {

            Thread.sleep(5000);

            ServerPathEnum pathEnum = ServerPathEnum.GET_FILE_STORAGE_URL;
            String path = AppStatic.serverIpEnum.getPath() + pathEnum.getPath();
            try {
                logger.info("获取文件存储地址: " + fileName);
                logger.info("查询URL:" + path + "/" + fileName);
                // 携带本次开始的采集场次号
                String res = HttpUtil.get(path + "?filename=" + fileName);
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    return commonResult.getData();
                } else {
                    return "";
                }
            } catch (Exception e) {
                return "";
            }
        }
    }


    /**
     * 通过OSS下载文件
     *
     * @param docUrl 文件OSS地址
     */
    public String downloadFileFromOss(String docUrl) throws Exception {                           /***加载正文***/
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
        Log.d("Test", "下载成功" + fileName);
        return fileName;
    }


    /**
     * 刷新数据上传事件列表日志
     *
     * @param data 数据
     */
    private void refreshDataUploadLogList(String data) {
        logList.add(data);
        logAdapter = new ArrayAdapter<>(HeartBloodActivity.this, R.layout.item_log_message, logList);
        lvDataUploadLog.setAdapter(logAdapter);
        lvDataUploadLog.setSelection(logList.size() - 1);
    }


    /**
     * 获取默认文件路径下所有文件的名称
     */
    public File[] getCurFilesList() {
        File path = new File(getApplicationContext().getFilesDir().getAbsolutePath());
        File[] fileList = path.listFiles(pathname -> {
            if (pathname.isFile()) {
                return true;
            } else {
                return false;
            }
        });
        return fileList;
    }


    /**
     * 进行数据加密
     */
    private String doDataEncryption(String data) {

        return data;
    }


    /**
     * 获取数据消息摘要
     * @param data 数据
     * @return 消息摘要
     */
    private String getMessageDigest(String data) {
        return data;
    }


    private String getPatientSignature(String messageDigest) {
        return messageDigest;
    }
}
