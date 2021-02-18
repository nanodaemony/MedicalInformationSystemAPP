package com.nano.activity.devicedata.healthrecord.watcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.nano.R;

import java.util.regex.Pattern;

/**
 * Description: 医院的手术场次号输入信息的监视器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/27 0:11
 */
public class HospitalOperationNumberWatcher implements TextWatcher {

    private EditText editText;

    private Context context;

    public HospitalOperationNumberWatcher(EditText editText, Context context) {
        this.editText = editText;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /**
     * 监控文本变化之后的表现
     *
     * @param editable 编辑内容
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String data = editText.getText().toString();
        if (verifyHospitalOperationNumber(data)) {
            editText.setTextColor(context.getColor(R.color.colorPrimary));
        } else {
            editText.setTextColor(context.getColor(R.color.titleColor));
        }
    }

    /**
     * 判断输入的手麻系统序列号是否规范
     *
     * @return 是否规范
     */
    public static boolean verifyHospitalOperationNumber(String hospitalOperationNumber) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(hospitalOperationNumber).matches();
    }


}
