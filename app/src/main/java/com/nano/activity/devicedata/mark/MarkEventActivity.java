package com.nano.activity.devicedata.mark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nano.R;
import com.nano.datacollection.util.TimeStampUtils;
import com.nano.common.logger.Logger;
import com.nano.common.util.ToastUtil;
import com.nano.http.HttpHandler;
import com.nano.http.HttpMessage;
import com.nano.http.HttpManager;
import com.sdsmdg.tastytoast.TastyToast;

import org.angmarch.views.NiceSpinner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;

/**
 * Description: 事件标记Activity
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/1 21:15
 */
public class MarkEventActivity extends Activity implements HttpHandler {

    /**
     * Logger
     */
    private Logger logger = new Logger("MarkActivity");

    private HttpManager httpManager = new HttpManager(this);

    /**
     * 返回
     */
    private ImageView ivBack;


    /**
     * 添加其他新的标记信息的按钮
     */
    private MaterialButton btnAddSomeOtherMarkEvent;

    /**
     * 当前选择的标记
     */
    private TextView tvCurrentChooseMark;

    /**
     * 搜索框相关
     */
    private EditText etInputSearchKeyWord;
    private ImageView ivDeleteKeyWord;
    private ImageView ivGetOftenUseMark;
    private ImageView ivSearchMarkEvent;

    /**
     * 事件展示结果的列表
     */
    private ListView lvEventList;

    /**
     * 搜索事件的结果Adapter
     */
    private ArrayAdapter<String> eventAdapter;

    /**
     * 暂存当前查询事件的列表
     */
    private List<MarkEvent> currentQueryList = new ArrayList<>();
    private MarkEvent currentChooseEvent;

    private LinearLayout layoutGiveMethod;
    private LinearLayout layoutGiveVolume;
    private NiceSpinner spGiveMedicineMethod;
    private NiceSpinner spGiveFluidMethod;
    private EditText etGiveVolume;
    private NiceSpinner spGiveVolumeUnit;

    private EditText etSideEffect;

    /**
     * 时间选择控件
     */
    private LinearLayout layoutChooseTimeMethod;
    private LinearLayout layoutTimePicker;
    private EditText etTimePickerHour;
    private EditText etTimePickerMinute;
    private EditText etTimePickerSecond;
    private boolean useDefaultTime = true;


    /**
     * 确认标记的按钮
     */
    private MaterialButton btnConfirmMark;

    /**
     * 选择结果的列表
     */
    private ListView lvChooseResultList;

    private ArrayAdapter<String> chooseResultAdapter;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_mark);

        tvCurrentChooseMark = findViewById(R.id.mark_event_textView_current_choosed_mark);
        btnAddSomeOtherMarkEvent = findViewById(R.id.mark_event_button_add_some_other_event);

        etInputSearchKeyWord = findViewById(R.id.mark_event_edit_text_input_key_word);
        ivDeleteKeyWord = findViewById(R.id.mark_event_imageView_delete_key_word);
        ivGetOftenUseMark = findViewById(R.id.mark_event_imageView_get_often_use_event);
        ivSearchMarkEvent = findViewById(R.id.mark_event_imageView_search);

        // 查询常用列表
        ivGetOftenUseMark.setOnClickListener(view -> {
            etInputSearchKeyWord.setText("");
            httpManager.getOftenUseMarkEventList();
        });

        // 点击删除按钮
        ivDeleteKeyWord.setOnClickListener(view -> {
            etInputSearchKeyWord.setText("");
            refreshAdditionalInfoWidget();
        });

        // 展示选项的列表
        lvEventList = findViewById(R.id.mark_event_listView_event_search_result);
        eventAdapter = new ArrayAdapter<>(MarkEventActivity.this, R.layout.item_log_message, new ArrayList<String>());
        lvEventList.setAdapter(eventAdapter);
        // 点击了事件列表
        lvEventList.setOnItemClickListener((parent, view, position, id) -> {
            // 获取事件
            currentChooseEvent = MarkEventUtil.copyMarkEvent(currentQueryList.get(position));

            // 修改当前选择
            tvCurrentChooseMark.setText(currentChooseEvent.generateListViewString());
            logger.info("当前点击:" + currentChooseEvent.toString());
            // 获取当前事件的类型
            MarkEventTypeEnum currentEventType = MarkEventTypeEnum.getEventType(currentChooseEvent);
            // 刷新额外信息的控件
            refreshAdditionalInfoWidget();
            // 如果是事件与操作
            if (currentEventType == MarkEventTypeEnum.SHI_JIAN || currentEventType == MarkEventTypeEnum.CAO_ZUO) {
                layoutGiveVolume.setVisibility(View.GONE);
                layoutGiveMethod.setVisibility(View.GONE);
            } else if (currentEventType == MarkEventTypeEnum.YONG_YAO) {
                layoutGiveMethod.setVisibility(View.VISIBLE);
                layoutGiveVolume.setVisibility(View.VISIBLE);
                spGiveMedicineMethod.setVisibility(View.VISIBLE);
                spGiveFluidMethod.setVisibility(View.GONE);
            } else if (currentEventType == MarkEventTypeEnum.BU_YE_SHU_XUE) {
                layoutGiveMethod.setVisibility(View.VISIBLE);
                layoutGiveVolume.setVisibility(View.VISIBLE);
                spGiveMedicineMethod.setVisibility(View.GONE);
                spGiveFluidMethod.setVisibility(View.VISIBLE);
            }
        });

        // 当前标记被长按时可以设置是否为常用标记
        lvEventList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            // TODO:
            return false;
        });


        // 返回按钮
        ivBack = findViewById(R.id.mark_event_imageView_back);
        ivBack.setOnClickListener(view -> finish());

        // 添加其他标记事件
        btnAddSomeOtherMarkEvent.setOnClickListener(view -> {
            Intent intent = new Intent(MarkEventActivity.this, AddSomeOtherNewEventMarkActivity.class);
            startActivity(intent);
        });

        etInputSearchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyWord = etInputSearchKeyWord.getText().toString().trim();
                if (keyWord.length() != 0) {

                    httpManager.getMatchedMarkEventList(keyWord);
                } else {
                    // 直接置空列表
                    refreshEventList(new ArrayList<>());
                }

            }
        });

        layoutGiveMethod = findViewById(R.id.mark_event_layout_add_info_give_method);
        layoutGiveVolume = findViewById(R.id.mark_event_layout_add_info_give_volume);
        spGiveMedicineMethod = findViewById(R.id.mark_event_add_give_method_give_medicine);
        spGiveFluidMethod = findViewById(R.id.mark_event_add_give_method_give_fluid);
        etGiveVolume = findViewById(R.id.mark_event_add_give_volume);
        spGiveVolumeUnit = findViewById(R.id.mark_event_add_give_volume_unit);
        etSideEffect = findViewById(R.id.mark_event_side_effect);

        // 给药途径的集合
        spGiveMedicineMethod.attachDataSource(MarkEventUtil.giveMedicineMethodList);
        spGiveFluidMethod.attachDataSource(MarkEventUtil.giveFluidMethodList);
        spGiveVolumeUnit.attachDataSource(MarkEventUtil.giveVolumeUnitList);

        layoutChooseTimeMethod = findViewById(R.id.mark_event_choose_mark_time_method);
        layoutTimePicker = findViewById(R.id.mark_event_layout_time_picker);
        etTimePickerHour = findViewById(R.id.mark_event_time_choose_hour);
        etTimePickerMinute = findViewById(R.id.mark_event_time_choose_minute);
        etTimePickerSecond = findViewById(R.id.mark_event_time_choose_second);

        // 选择标记时间的方式
        layoutChooseTimeMethod.setOnClickListener(view -> {
            // 由默认的时间变成自定义时间
            if (useDefaultTime) {
                useDefaultTime = false;
                layoutTimePicker.setVisibility(View.VISIBLE);
                // 设置选择的时间
                LocalDateTime currentTime = TimeStampUtils.parseTimeStampToLocalDateTime(System.currentTimeMillis());
                etTimePickerHour.setText("" + currentTime.getHour());
                etTimePickerMinute.setText("" + currentTime.getMinute());
                etTimePickerSecond.setText("" + currentTime.getSecond());
                // 由自定义时间变成默认时间
            } else {
                useDefaultTime = true;
                layoutTimePicker.setVisibility(View.GONE);
            }
        });

        // 展示标记事件结果的列表
        lvChooseResultList = findViewById(R.id.mark_event_listView_show_mark_info);
        lvChooseResultList.setOnItemClickListener((adapterView, view, position, l) -> {
            // 展示修改标记的弹窗
            showModifyMarkEventDialog(position);
        });

        // 确定选择并提交的按钮
        btnConfirmMark = findViewById(R.id.mark_event_sure_choose_button);
        btnConfirmMark.setOnClickListener(view -> {
            if (currentChooseEvent == null) {
                ToastUtil.toast(MarkEventActivity.this, "当前没有选择事件,无法添加", TastyToast.WARNING);
            } else {
                // 然后根据事件类别进行不同判断
                MarkEventTypeEnum currentEventType = MarkEventTypeEnum.getEventType(currentChooseEvent);
                if (currentEventType == MarkEventTypeEnum.SHI_JIAN || currentEventType == MarkEventTypeEnum.CAO_ZUO) {
                    // 这两种情况无需补充信息
                    // 说明都需要输入剂量
                } else {
                    // 说明没有输入剂量
                    if (etGiveVolume.getText().toString().trim().length() == 0) {
                        ToastUtil.toast(MarkEventActivity.this, "尚未输入剂量", TastyToast.WARNING);
                        return;
                    }
                    // 剂量由数字+单位构成
                    String volume = etGiveVolume.getText().toString().trim() + MarkEventUtil.giveVolumeUnitList.get(spGiveVolumeUnit.getSelectedIndex());
                    currentChooseEvent.setGiveMedicineVolume(volume);
                    if (currentEventType == MarkEventTypeEnum.YONG_YAO) {
                        // 说明没有选择给药途径
                        if (spGiveMedicineMethod.getSelectedIndex() == 0) {
                            ToastUtil.toast(MarkEventActivity.this, "尚未选择给药途径", TastyToast.WARNING);
                            return;
                        }
                        // 获取给药途径
                        String giveMedicineMethod = MarkEventUtil.giveMedicineMethodList.get(spGiveMedicineMethod.getSelectedIndex());
                        currentChooseEvent.setGiveMedicineMethod(giveMedicineMethod);
                    } else if (currentEventType == MarkEventTypeEnum.BU_YE_SHU_XUE) {
                        // 说明没有选择补液途径
                        if (spGiveFluidMethod.getSelectedIndex() == 0) {
                            ToastUtil.toast(MarkEventActivity.this, "尚未选择补液/输血途径", TastyToast.WARNING);
                            return;
                        }
                        String giveFluidMethod = MarkEventUtil.giveFluidMethodList.get(spGiveFluidMethod.getSelectedIndex());
                        currentChooseEvent.setGiveMedicineMethod(giveFluidMethod);
                    }
                }

                // 设置不良反应
                String sideEffect = etSideEffect.getText().toString().trim();
                if (sideEffect.length() == 0) {
                    currentChooseEvent.setSideEffect("无");
                } else {
                    currentChooseEvent.setSideEffect(sideEffect);
                }
                // 设置标记时间
                // 设置标记时间
                if (useDefaultTime) {
                    currentChooseEvent.setMarkTime(System.currentTimeMillis());
                } else {
                    // 读取自定义时间

                    String hour = etTimePickerHour.getText().toString().trim();
                    String minute = etTimePickerMinute.getText().toString().trim();
                    String second = etTimePickerSecond.getText().toString().trim();

                    if (!verifyInputTime(hour, minute, second)) {
                        ToastUtil.toast(MarkEventActivity.this, "输入的时间格式错误", TastyToast.WARNING);
                        return;
                    }
                    LocalDate localDate = LocalDate.now();
                    LocalTime localTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
                    LocalDateTime markTime = LocalDateTime.of(localDate, localTime);
                    // 转换为秒数
                    currentChooseEvent.setMarkTime(markTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                }

                // 设置采集场次号列表
                currentChooseEvent.setCollectionNumberList(MarkEventUtil.getCollectionNumberList());

                logger.info("添加标记:" + currentChooseEvent.toString());

                // 加入到当前选择的列表总
                MarkEventUtil.markEventList.add(currentChooseEvent);

                // 刷新已选择事件列表
                refreshChooseResultList(MarkEventUtil.markEventList);
                // 上传当前的标记列表
                updateEventMarkList();
                // 刷新界面
                currentChooseEvent = null;
                tvCurrentChooseMark.setText("");
                layoutGiveMethod.setVisibility(View.GONE);
                layoutGiveVolume.setVisibility(View.GONE);
                useDefaultTime = true;
                layoutTimePicker.setVisibility(View.GONE);
            }

        });

        // 一来刷新一下
        refreshChooseResultList(MarkEventUtil.markEventList);

        // 一开启就查询常用列表
        httpManager.getOftenUseMarkEventList();
    }


    /**
     * 处理HTTP成功的消息
     *
     * @param message 数据
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleSuccessfulHttpMessage(HttpMessage message) {

        switch (message.getPathEnum()) {
            // 查询常用列表
            case GET_OFTEN_USE_MARK_EVENT_LIST:
                // 解析到当前查询结果列表
                currentQueryList = JSON.parseArray(message.getData(), MarkEvent.class);
                // 在这里对事件进行初始化
                ArrayList<String> oftenUseEventStringList = new ArrayList<>();
                for (MarkEvent event : currentQueryList) {
                    logger.info("获取到的常用事件:" + event.generateListViewString());
                    oftenUseEventStringList.add(event.generateListViewString());
                }
                runOnUiThread(() -> refreshEventList(oftenUseEventStringList));
                break;

            // 查询匹配列表
            case SEARCH_MATCH_MARK_EVENT_LIST:
                // 解析到当前查询结果列表
                currentQueryList = JSON.parseArray(message.getData(), MarkEvent.class);
                // 在这里对事件进行初始化
                ArrayList<String> matchEventList = new ArrayList<>();
                for (MarkEvent event : currentQueryList) {
                    logger.info("获取到的匹配事件:" + event.generateListViewString());
                    matchEventList.add(event.generateListViewString());
                }
                runOnUiThread(() -> refreshEventList(matchEventList));
                break;

            // 成功上传事件信息
            case OPERATION_MARK_EVENT_ADD:
                // 解析上传成功的标记事件的唯一代号
                for (MarkEvent event : MarkEventUtil.markEventList) {
                    if (event.getUniqueNumber().equals(message.getData())) {
                        event.setUpdated(true);
                    }
                }
                // 刷新一下结果列表
                runOnUiThread(() -> refreshChooseResultList(MarkEventUtil.markEventList));
                break;


            // 更新标记事件的时间成功
            case OPERATION_MARK_EVENT_UPDATE:
                logger.info("成功同步时间:" + message.getData());
                runOnUiThread(() -> {
                    ToastUtil.toast(MarkEventActivity.this, "同步时间成功", TastyToast.SUCCESS);
                    refreshChooseResultList(MarkEventUtil.markEventList);
                });
                break;

            // 删除标记事件成功
            case OPERATION_MARK_EVENT_DELETE:
                runOnUiThread(() -> {
                    refreshChooseResultList(MarkEventUtil.markEventList);
                    ToastUtil.toast(MarkEventActivity.this, "删除成功", TastyToast.SUCCESS);
                });
        }


    }

    @Override
    public void handleFailedHttpMessage(HttpMessage message) {

    }

    @Override
    public void handleNetworkFailedMessage() {

    }


    /**
     * 刷新事件选择列表
     *
     * @param dataList 事件列表
     */
    private void refreshEventList(ArrayList<String> dataList) {
        eventAdapter = new ArrayAdapter<>(MarkEventActivity.this, R.layout.item_log_message, dataList);
        lvEventList.setAdapter(eventAdapter);
    }


    /**
     * 刷新事件选择结果列表
     *
     * @param eventList 事件列表
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshChooseResultList(ArrayList<MarkEvent> eventList) {
        ArrayList<String> dataList = new ArrayList<>();
        for (MarkEvent event : eventList) {
            dataList.add(event.generateResultListViewString());
            logger.info("添加事件:" + event.generateResultListViewString());
        }
        logger.info("刷新结果展示");
        chooseResultAdapter = new ArrayAdapter<>(MarkEventActivity.this, R.layout.item_log_message, dataList);
        lvChooseResultList.setAdapter(chooseResultAdapter);
    }


    /**
     * 刷新填写额外信息的控件
     */
    private void refreshAdditionalInfoWidget() {
        layoutGiveMethod.setVisibility(View.GONE);
        layoutGiveVolume.setVisibility(View.GONE);
        layoutTimePicker.setVisibility(View.GONE);
        useDefaultTime = true;
        spGiveVolumeUnit.setSelectedIndex(0);
        spGiveFluidMethod.setSelectedIndex(0);
        spGiveMedicineMethod.setSelectedIndex(0);
        etGiveVolume.setText("");
        etSideEffect.setText("无");
    }


    /**
     * 上传标记信息到服务器
     */
    private void updateEventMarkList() {

        // 遍历找到全部没有上传的列表
        List<MarkEvent> updateList = new ArrayList<>();
        for (MarkEvent event : MarkEventUtil.markEventList) {
            if (!event.isUpdated()) {
                updateList.add(event);
                logger.info("当前上传标记:" + event.toString());
                httpManager.postMarkEvent(event);
            }
        }
    }


    /**
     * 判断输入的时间是否是对的
     */
    private boolean verifyInputTime(String hourStr, String minuteStr, String secondStr) {
        if (!(isInteger(hourStr) && isInteger(minuteStr) && isInteger(secondStr))) {
            return false;
        }

        int hour = Integer.parseInt(hourStr);
        int minute = Integer.parseInt(minuteStr);
        int second = Integer.parseInt(secondStr);

        if (hour < 0 || hour > 23) {
            return false;
        }
        if (minute < 0 || minute > 59) {
            return false;
        }
        if (second < 0 || second > 59) {
            return false;
        }
        return true;
    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 弹出显示修改事件的弹窗
     *
     * @param position 点击条目的位置
     */
    private void showModifyMarkEventDialog(final int position) {
        // 弹出操作的弹窗 新建Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MarkEventActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MarkEventActivity.this);
        // 绑定自定义弹窗布局
        View v = inflater.inflate(R.layout.dialog_mark_event_change_content, null);

        // 弹窗布局中的控件注册
        TextView tvChangeTime = v.findViewById(R.id.mark_event_change_timeToTheSame);
        TextView tvDeleteMark = v.findViewById(R.id.mark_event_delete_mark);

        final Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        // 显示圆角的关键代码
        window.setBackgroundDrawable(new BitmapDrawable());
        dialog.show();
        dialog.getWindow().setContentView(v);
        // 可以设置显示的位置
        dialog.getWindow().setGravity(Gravity.CENTER);

        // 改变时间的按钮
        tvChangeTime.setOnClickListener(v1 -> {
            if (position == 0) {
                ToastUtil.toast(MarkEventActivity.this, "当前是第一条标记无法同步时间", TastyToast.WARNING);
            } else {
                // 设置时间
                MarkEventUtil.markEventList.get(position).setMarkTime(MarkEventUtil.markEventList.get(position - 1).getMarkTime());
                httpManager.postModifyMarkEventTime(MarkEventUtil.markEventList.get(position));
            }
            dialog.dismiss();           // 关掉弹窗
        });

        // 删除标记的按钮
        tvDeleteMark.setOnClickListener(arg0 -> {
            // 上传删除请求
            httpManager.postDeleteMarkEvent(MarkEventUtil.markEventList.get(position));
            MarkEventUtil.markEventList.remove(position);
            dialog.dismiss();
            // 更新显示
        });
    }

}
