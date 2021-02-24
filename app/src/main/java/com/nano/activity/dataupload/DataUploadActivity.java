package com.nano.activity.dataupload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nano.AppStatic;
import com.nano.R;
import com.nano.WorkingModeEnum;
import com.nano.common.logger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.provider.Telephony.Mms.Part.CHARSET;

/**
 * Description: 各种医疗数据处理与上传界面
 * 作用：
 * 1. 根据传入的代号选择是处理何种类型的数据：EMR、SHR、PHR等。
 * 2. 对数据进行统计并展示。
 * 3. 将数据进行加密，然后上传至OSS并获取到数据存储的URL。
 * 4. 调用后端接口将数据上链，成功后展示区块链相关的信息。
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/2/23 10:45
 */
public class DataUploadActivity extends AppCompatActivity {

    private Logger logger = new Logger("DataUploadActivity");

    /**
     * 数据上传日志列表
     */
    private ListView lvDataUploadLog;
    private ArrayAdapter<String> logAdapter;
    private List<String> logList = new ArrayList<>();

    private TextView tvDataType;

    private MaterialButton btnUploadData;





    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_upload);
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_data_upload);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        lvDataUploadLog = findViewById(R.id.list_view_data_upload_log);
        btnUploadData = findViewById(R.id.button_data_upload_to_block_chain);
        tvDataType = findViewById(R.id.text_data_upload_data_type);

        // 进行数据上传的按钮
        btnUploadData.setOnClickListener(view -> {
            refreshDataUploadLogList("" + System.currentTimeMillis());


        });

        // 说明是进行数据采集的EMR与SHR数据
        if (AppStatic.workingMode == WorkingModeEnum.DEVICE_DATA) {
            tvDataType.setText("EMR/SHR数据");
            // 说明是PHR数据
        } else if (AppStatic.workingMode == WorkingModeEnum.HEART_BLOOD) {
            tvDataType.setText("PHR数据");
        }


    }


    private void uploadFileToOSS() {

    }



    /**
     * 刷新数据上传事件列表日志
     * @param data 数据
     */
    private void refreshDataUploadLogList(String data) {
        logList.add(data);
        logAdapter = new ArrayAdapter<>(DataUploadActivity.this, R.layout.item_log_message, logList);
        lvDataUploadLog.setAdapter(logAdapter);
        lvDataUploadLog.setSelection(logList.size() - 1);
    }




}
