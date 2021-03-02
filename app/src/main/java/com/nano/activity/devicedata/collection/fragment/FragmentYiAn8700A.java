package com.nano.activity.devicedata.collection.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.R;
import com.nano.activity.devicedata.collection.MessageEntity;
import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.devicedata.collection.interfaces.FragmentOperationHandler;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.datacollection.parsedata.entity.DataYiAn8700A;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 宜安8700A数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentYiAn8700A extends android.support.v4.app.Fragment implements FragmentDataExchanger {

    /**
     * 仪器图片
     */
    private ImageView ivDeviceImage;
    private TextView tvControlMessage;

    /**
     * 采集状态
     */
    private TextView tvCollectionStatus;

    /**
     * 接收计数器与成功上传计数器
     */
    private TextView tvReceiveCounter;
    private TextView tvSuccessfulUpdateCounter;

    /**
     * 操作Fragment的操作器
     */
    private FragmentOperationHandler handler;

    /**
     * 对应的仪器信息
     */
    private MedicalDevice device;


    private TextView tvPeak;
    private TextView tvMean;
    private TextView tvPeep;
    private TextView tvMv;
    private TextView tvVte;
    private TextView tvFreq;
    private TextView tvFiO2;
    private TextView tvEtCo2;
    private TextView tvFiCo2;
    private TextView tvAir;
    private TextView tvN2o;
    private TextView tvO2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_yian_8700a, container, false);

        tvCollectionStatus = root.findViewById(R.id.device_collection_status);
        tvReceiveCounter = root.findViewById(R.id.collection_receive_counter);
        tvSuccessfulUpdateCounter = root.findViewById(R.id.successful_update_counter);

        // 仪器图片
        ivDeviceImage = root.findViewById(R.id.device_collection_device_image);
        // 控制信息
        tvControlMessage = root.findViewById(R.id.device_collection_control_message);
        ivDeviceImage.setOnClickListener(view -> {
            if (device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                handler.handleDeviceStartCollection(new MessageEntity(device.getDeviceCode()));
            } else if (device.getStatusEnum() == CollectionStatusEnum.COLLECTING) {
                handler.handleDeviceStopCollection(new MessageEntity(device.getDeviceCode()));
            } else if (device.getStatusEnum() == CollectionStatusEnum.FINISHED) {

            }
        });


        // 获取当前操作的仪器信息
        device = DeviceUtil.getMedicalDevice(DeviceEnum.YI_AN_8700A);

        // 这里是仪器特有的参数控件
        tvPeak = root.findViewById(R.id.device_data_yian_8700a_peak);
        tvMean = root.findViewById(R.id.device_data_yian_8700a_pmean);
        tvPeep = root.findViewById(R.id.device_data_yian_8700a_peep);
        tvMv = root.findViewById(R.id.device_data_yian_8700a_mv);
        tvVte = root.findViewById(R.id.device_data_yian_8700a_vte);
        tvFreq = root.findViewById(R.id.device_data_yian_8700a_freq);
        tvFiO2 = root.findViewById(R.id.device_data_yian_8700a_fio2);
        tvEtCo2 = root.findViewById(R.id.device_data_yian_8700a_etcCO2);
        tvFiCo2 = root.findViewById(R.id.device_data_yian_8700a_fico2);
        tvAir = root.findViewById(R.id.device_data_yian_8700a_air);
        tvN2o = root.findViewById(R.id.device_data_yian_8700a_n2o);
        tvO2 = root.findViewById(R.id.device_data_yian_8700a_o2);

        return root;
    }


    /**
     * 更新数据采集状态
     *
     * @param status 新的状态
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void updateCollectionStatus(CollectionStatusEnum status) {
        // 此时说明已经成功请求到采集场次号
        if (status == CollectionStatusEnum.WAITING_START) {
            tvControlMessage.setVisibility(View.VISIBLE);
            tvControlMessage.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
        } else if (status == CollectionStatusEnum.COLLECTING) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是正在采集则修改颜色为红色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
            tvControlMessage.setText("点击完成采集");
            tvControlMessage.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
        } else if (status == CollectionStatusEnum.FINISHED) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是完成采集则修改颜色为绿色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
            tvControlMessage.setText("点击放弃采集");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateSuccessfulUploadCounter(Integer successfulUploadCounter) {
        this.getActivity().runOnUiThread(() -> tvSuccessfulUpdateCounter.setText("" + successfulUploadCounter));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateReceiveCounterAndDeviceData(Integer receiveCounter, Object dataObject) {
        this.getActivity().runOnUiThread(() -> {
            tvReceiveCounter.setText("" + receiveCounter);
            // 转换为数据实体
            DataYiAn8700A dataYiAn8700A = (DataYiAn8700A)dataObject;
            // Pressure部分
            tvPeak.setText(dataYiAn8700A.getPeak() + " cmH2O");
            tvMean.setText(dataYiAn8700A.getPmean() + " cmH2O");
            tvPeep.setText(dataYiAn8700A.getPeep() + " cmH2O");
            // Volume部分
            tvMv.setText(dataYiAn8700A.getMv() + " L/min");
            tvVte.setText(dataYiAn8700A.getVte() + " mL");
            tvFreq.setText(dataYiAn8700A.getFreq() + " bpm");
            // 其他部分
            tvEtCo2.setText(dataYiAn8700A.getEtco2() + " mmHg");
            tvFiCo2.setText(dataYiAn8700A.getFico2() + " mmHg");
            tvFiO2.setText(dataYiAn8700A.getFio2() + " %");
            // 左上角部分
            tvN2o.setText(dataYiAn8700A.getN2o() + " L/min");
            tvAir.setText(dataYiAn8700A.getAir() + " L/min");
            tvO2.setText(dataYiAn8700A.getO2() + " L/min");
        });

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
        handler = (FragmentOperationHandler) getActivity();
    }


}
