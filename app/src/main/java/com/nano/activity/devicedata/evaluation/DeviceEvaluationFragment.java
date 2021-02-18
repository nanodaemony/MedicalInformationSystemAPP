package com.nano.activity.devicedata.evaluation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nano.R;
import com.nano.activity.devicedata.collection.MessageEntity;
import com.nano.common.logger.Logger;
import com.nano.common.util.PersistUtil;
import com.nano.common.util.ToastUtil;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.sdsmdg.tastytoast.TastyToast;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Description: 仪器评价Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class DeviceEvaluationFragment extends Fragment implements DeviceEvaluationHandler {


    private Logger logger = new Logger("FragmentDeviceEvaluation");

    /**
     * 对应的仪器
     */
    private MedicalDevice device;

    /**
     * 使用科室
     */
    private EditText etDeviceDepartment;

    /**
     * 体验度滚轮
     */
    private WheelView<String> wheelExperienceLevel;
    private WheelView<String> wheelReliabilityLevel;

    /**
     * 故障情况相关
     */
    private RadioButton rbHasErrorYes;
    private RadioButton rbHasErrorNo;
    private AppCompatCheckBox checkBoxComponentError;
    private AppCompatCheckBox checkBoxSoftwareError;
    private AppCompatCheckBox checkBoxOperationError;
    private AppCompatCheckBox checkBoxEnvironmentError;
    private EditText etOtherError;
    private LinearLayout layoutErrorReason;


    private EditText etRemark;
    private EditText etRecordName;

    private MaterialButton btnCommit;
    private MaterialButton btnAbandon;

    // 操作CollectionActivity的处理器
    private FragmentEvaluationHandler handler;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_evaluation, container, false);

        // 仪器名称
        TextView tvDeviceName = root.findViewById(R.id.device_evaluation_device_name);

        // 获取当前操作的仪器信息
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            device = DeviceUtil.getMedicalDevice(bundle.getInt("deviceCode"));
        }

        tvDeviceName.setText(device.getDeviceEnum().getCompanyName() + " " + device.getDeviceEnum().getDeviceName());

        etDeviceDepartment = root.findViewById(R.id.collection_evaluation_collection_department);
        etRemark = root.findViewById(R.id.collection_evaluation_remark_info);
        etRecordName = root.findViewById(R.id.collection_evaluation_record_name);
        // 如果缓存有记录则直接输入好
        etDeviceDepartment.setText(PersistUtil.getStringValue("DeviceEvaluationDepartment"));
        etRecordName.setText(PersistUtil.getStringValue("DeviceEvaluationRecordName"));

        wheelExperienceLevel = root.findViewById(R.id.device_evaluation_wheel_use_experience_level);
        wheelReliabilityLevel = root.findViewById(R.id.device_evaluation_wheel_reliable_level);
        wheelExperienceLevel.setWheelAdapter(new ArrayWheelAdapter(getContext())); // 文本数据源
        wheelReliabilityLevel.setWheelAdapter(new ArrayWheelAdapter(getContext())); // 文本数据源
        wheelExperienceLevel.setSkin(WheelView.Skin.Holo);  // common皮肤
        wheelReliabilityLevel.setSkin(WheelView.Skin.Holo); // common皮肤
        List<String> levelName = Arrays.asList("非常满意", "满意", "一般", "不满意", "非常不满意");
        wheelExperienceLevel.setWheelData(levelName);   // 数据集合
        wheelReliabilityLevel.setWheelData(levelName);  // 数据集合

        rbHasErrorYes = root.findViewById(R.id.collection_evaluation_has_error_yes);
        rbHasErrorNo = root.findViewById(R.id.collection_evaluation_has_error_no);
        checkBoxComponentError = root.findViewById(R.id.collection_evaluation_error_component_error);
        checkBoxSoftwareError = root.findViewById(R.id.collection_evaluation_error_software_error);
        checkBoxOperationError = root.findViewById(R.id.collection_evaluation_error_operation_error);
        checkBoxEnvironmentError = root.findViewById(R.id.collection_evaluation_error_environment_error);
        etOtherError = root.findViewById(R.id.collection_evaluation_error_other_error);
        layoutErrorReason = root.findViewById(R.id.device_evaluation_layout_error_reason);

        // 选择了无故障
        rbHasErrorNo.setOnClickListener(view -> {
            // 隐藏故障信息框
            layoutErrorReason.setVisibility(View.GONE);
            // 同时将各种故障取消选择
            checkBoxComponentError.setChecked(false);
            checkBoxSoftwareError.setChecked(false);
            checkBoxOperationError.setChecked(false);
            checkBoxEnvironmentError.setChecked(false);
            etOtherError.setText("");
        });

        // 选择了有故障
        rbHasErrorYes.setOnClickListener(view -> layoutErrorReason.setVisibility(View.VISIBLE));

        // 提交按钮
        btnCommit = root.findViewById(R.id.device_evaluation_button_commit);
        btnCommit.setOnClickListener(view -> {
            // 如果检查评价信息通过则通知CollectionActivity进行上传
            if (checkAndStoreEvaluationInfo()) {
                handler.handleUpdateEvaluationInfo(new MessageEntity(device.getDeviceCode()));
            }
        });

        // 放弃数据的按钮
        btnAbandon = root.findViewById(R.id.device_evaluation_button_abandon);
        btnAbandon.setOnClickListener(view -> {
            handler.handleAbandonDeviceData(new MessageEntity(device.getDeviceCode()));
        });

        return root;
    }

    /**
     * 检查并持久化仪器评价信息(如果输入内容不合格则弹出提示,同时返回false)
     */
    @Override
    public boolean checkAndStoreEvaluationInfo() {

        DeviceEvaluationTable evaluationTable = new DeviceEvaluationTable();

        // 使用科室
        String department = etDeviceDepartment.getText().toString().trim();
        if (department.length() == 0) {
            ToastUtil.toast(getContext(), "尚未填写使用科室信息:" + device.getDeviceEnum().getCompanyName() + " " + device.getDeviceEnum().getDeviceName(), TastyToast.WARNING);
            return false;
        }
        evaluationTable.setDeviceDepartment(department);

        // 设置体验满意度
        evaluationTable.setExperienceLevel(wheelExperienceLevel.getCurrentPosition() + 1);
        evaluationTable.setReliabilityLevel(wheelReliabilityLevel.getCurrentPosition() + 1);

        // 选择的是无故障
        if (rbHasErrorNo.isChecked()) {
            evaluationTable.setHasError(false);
        } else {
            // 说明有故障
            evaluationTable.setHasError(true);
            // 有故障但是故障信息都没填
            if ("0".equals(getErrorReason()) && etOtherError.getText().toString().trim().length() == 0) {
                ToastUtil.toast(getContext(), "尚未填写故障信息:" + device.getDeviceEnum().getCompanyName() + " " + device.getDeviceEnum().getDeviceName(), TastyToast.WARNING);
                return false;
            }

        }
        // 获取已知故障原因
        evaluationTable.setKnownError(getErrorReason());
        // 获取其他故障原因
        evaluationTable.setOtherError(getOtherError());

        // 备注信息
        String remark = etRemark.getText().toString().trim();
        if (remark.length() == 0) {
            evaluationTable.setRemark("无");
        } else {
            evaluationTable.setRemark(remark);
        }

        // 记录人签名
        String recordName = etRecordName.getText().toString().trim();
        if (recordName.length() == 0) {
            ToastUtil.toast(getContext(), "尚未填写记录人签名信息:" + device.getDeviceEnum().getCompanyName() + " " + device.getDeviceEnum().getDeviceName(), TastyToast.WARNING);
            return false;
        }
        evaluationTable.setRecordName(recordName);
        // 补充信息
        evaluationTable.setDeviceCode(device.getDeviceCode());
        evaluationTable.setSerialNumber(device.getSerialNumber());

        // 上述信息都设置完成,持久化到MedicalDevice对象中
        device.setDeviceEvaluationTable(evaluationTable);

        // 持久化信息到本地缓存中
        PersistUtil.putStringValue("DeviceEvaluationDepartment", department);
        PersistUtil.putStringValue("DeviceEvaluationRecordName", recordName);
        logger.info("当前评价信息:" + evaluationTable.toString());
        return true;
    }






    /**
     * 获取错误信息
     * @return 错误信息
     */
    private String getErrorReason() {

        if (rbHasErrorYes.isChecked()) {
            StringBuilder errorMessage = new StringBuilder();

            if (checkBoxComponentError.isChecked()) {
                errorMessage.append("1");
            } else {
                errorMessage.append("0");
            }

            if (checkBoxSoftwareError.isChecked()) {
                errorMessage.append("1");
            } else {
                errorMessage.append("0");
            }

            if (checkBoxOperationError.isChecked()) {
                errorMessage.append("1");
            } else {
                errorMessage.append("0");
            }

            if (checkBoxEnvironmentError.isChecked()) {
                errorMessage.append("1");
            } else {
                errorMessage.append("0");
            }

            int errorInfoCode = 0;
            try {
                errorInfoCode = Integer.parseInt(errorMessage.toString(), 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorInfoCode + "";
        } else {
            // 没错误直接返回0000
            return "0";
        }

    }



    /**
     * 获取其他错误信息
     * @return 其他错误
     */
    private String getOtherError() {
        if (rbHasErrorYes.isChecked()) {
            return etOtherError.getText().toString().trim();
        } else if (rbHasErrorNo.isChecked()) {
            return "";
        }
        return "";
    }


    /**
     * 将CollectionActivity以Handler的方式传入,可以实现回调
     *
     * @param context 上下文
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 获取实现接口的activity,将其转换为一个Handler
        handler = (FragmentEvaluationHandler) getActivity();
    }
}
