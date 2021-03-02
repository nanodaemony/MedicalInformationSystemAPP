package com.nano.activity.devicedata.devicechoose;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.R;
import com.nano.common.logger.Logger;
import com.nano.common.util.PersistUtil;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;
import com.suke.widget.SwitchButton;

import org.angmarch.views.NiceSpinner;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Description: 仪器选择的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/10 20:51
 */
public class DeviceChooseFragment  extends Fragment {

    private Logger logger = new Logger("[DeviceChooseFragment]");

    /**
     * 对应的仪器
     */
    private MedicalDevice device;

    private ImageView ivDeviceImage;
    private TextView tvDeviceName;
    private EditText etSerialNumber;
    private NiceSpinner spServiceLife;
    private SwitchButton btnChooseDevice;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_choose_card, container, false);

        // 获取当前操作的仪器信息
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            device = DeviceUtil.getMedicalDevice(bundle.getInt("deviceCode"));
        }

        ivDeviceImage = root.findViewById(R.id.device_choose_device_image);
        tvDeviceName = root.findViewById(R.id.device_choose_device_name);
        etSerialNumber = root.findViewById(R.id.device_choose_device_serial_number);
        spServiceLife = root.findViewById(R.id.device_choose_device_service_life);
        btnChooseDevice = root.findViewById(R.id.device_choose_device_is_use);

        // 设置仪器图片及名称
        ivDeviceImage.setImageResource(device.getDeviceImageSource());
        tvDeviceName.setText(device.getDeviceEnum().getDeviceShortName());

        // 从缓存查询记录
        String historySerialNumber = PersistUtil.getStringValue("DeviceSerialNumber" + device.getDeviceCode());
        etSerialNumber.setText(historySerialNumber);
        device.setSerialNumber(historySerialNumber);
        List<String> dataSet = new LinkedList<>(Arrays.asList("0年", "1年", "2年", "3年", "4年", "5年", "6年", "7年", "8年", "9年", "10年", "11年", "12年", "13年"));
        spServiceLife.attachDataSource(dataSet);
        int historyIndex = PersistUtil.getIntegerValue("DeviceServiceLifeIndex" + device.getDeviceCode());
        // 说明找到了
        if (historyIndex != PersistUtil.DEFAULT_INTEGER_VALUE) {
            spServiceLife.setSelectedIndex(historyIndex);
            device.setServiceLife((double)historyIndex);
        } else {
            spServiceLife.setSelectedIndex(0);
            device.setServiceLife(0D);
        }

        etSerialNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String serialNumber = etSerialNumber.getText().toString().trim();
                device.setSerialNumber(serialNumber);
                PersistUtil.putStringValue("DeviceSerialNumber" + device.getDeviceCode(), serialNumber);
            }
        });

        // 监听输入使用年限并持久化
        spServiceLife.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int serviceLife = spServiceLife.getSelectedIndex();
                device.setServiceLife((double)serviceLife);
                PersistUtil.putIntegerValue("DeviceServiceLifeIndex" + device.getDeviceCode(), serviceLife);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // 仪器选择按钮
        btnChooseDevice.setOnCheckedChangeListener((view, isChecked) -> {
            // 如果当前是选择
            if (isChecked) {
                device.setDeviceUsed(true);
            } else {
                device.setDeviceUsed(false);
            }
        });

        return root;
    }

}
