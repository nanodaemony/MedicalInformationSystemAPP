package com.nano.activity.devicedata.mark;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.nano.R;
import com.nano.common.util.SimpleDialog;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * Description: 添加其他标记事件的界面
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/5 10:53
 */
public class AddSomeOtherNewEventMarkActivity extends AppCompatActivity implements HttpHandler {

    private WheelView<String> wheelMarkMainType;

    private EditText etMarkSubType;

    private EditText etMarkEvent;

    private HttpManager httpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_mark_add_some_other_mark_event);

        httpManager = new HttpManager(this);

        ImageView ivBack = findViewById(R.id.add_some_new_event_mark_back);
        ivBack.setOnClickListener((view) -> finish());

        wheelMarkMainType = findViewById(R.id.add_some_new_event_mark_wheel_main_type);
        wheelMarkMainType.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
        wheelMarkMainType.setSkin(WheelView.Skin.Holo);  // common皮肤
        List<String> mainTypeList = Arrays.asList("事件", "操作", "用药", "补液/输血");
        wheelMarkMainType.setWheelData(mainTypeList);   // 数据集合
        etMarkSubType = findViewById(R.id.add_some_new_event_mark_sub_type);
        etMarkEvent = findViewById(R.id.add_some_new_event_mark_concrete_event);

        // 提交按钮
        MaterialButton btnCommit = findViewById(R.id.add_some_new_event_mark_commit);
        btnCommit.setOnClickListener(view -> {

            String markMainType = wheelMarkMainType.getSelectionItem();
            String markSubType = etMarkSubType.getText().toString().trim();
            String markEvent = etMarkEvent.getText().toString().trim();

            if (markSubType.length() == 0) {
                ToastUtil.toastWarn(this, "请填写标记事件的子类");
                return;
            }

            if (markEvent.length() == 0) {
                ToastUtil.toastWarn(this, "请填写具体标记事件");
                return;
            }
            // 上传事件
            httpManager.postAddSomeOtherNewMarkEvent(JSON.toJSONString(new MarkEventInServer(markMainType, markSubType, markEvent)));
        });

    }


    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {
        switch (message.getCode()) {

            case POST_ADD_SOME_OTHER_NEW_MARK_EVENT:
                // 弹出添加成功的消息
                runOnUiThread(() -> {
                    SimpleDialog.show(this, "上传成功",
                            "当前事件已经成功新增到服务器中,请返回上一界面进行搜索标记", R.mipmap.post_success);
                    // 输入框重置
                    etMarkEvent.setText("");
                    etMarkSubType.setText("");
                });

                break;
        }
    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {

    }

    @Override
    public void handleNetworkFailedMessage() {

    }


    /**
     * 对应服务器上的事件类
     */
    @Data
    private static class MarkEventInServer {

        private static final long serialVersionUID = -4892589808312433198L;

        /**
         * 事件大类
         */
        private String markMainType;


        /**
         * 事件小类
         */
        private String markSubType;

        /**
         * 具体事件
         */
        private String markEvent;

        /**
         * 是否为常用事件
         */
        private Integer oftenUse = 0;

        /**
         * 使用此标记的频率
         */
        private Integer markFrequency = 0;

        /**
         * 是否是新增的标记信息(默认为新增事件)
         */
        private Integer whetherNewAdd = 1;

        public MarkEventInServer(String markMainType, String markSubType, String markEvent) {
            this.markMainType = markMainType;
            this.markSubType = markSubType;
            this.markEvent = markEvent;
        }
    }
}
