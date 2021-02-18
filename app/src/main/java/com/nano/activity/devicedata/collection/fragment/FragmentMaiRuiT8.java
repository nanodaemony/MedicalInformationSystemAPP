package com.nano.activity.devicedata.collection.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.nano.R;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataMaiRuiT8;
import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.devicedata.collection.interfaces.FragmentOperationHandler;
import com.nano.activity.devicedata.collection.MessageEntity;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 迈瑞BeneViewT8数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentMaiRuiT8 extends android.support.v4.app.Fragment implements FragmentDataExchanger {

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


    /**
     * 开始与停止按钮
     */
    private LinearLayout ivCollectionStart;
    private LinearLayout ivCollectionStop;

    private TextView tvDataEcgHr;
    private TextView tvDataResp;
    private TextView tvDataCvp;
    private TextView tvDataArt;
    private TextView tvDataNibp;
    private TextView tvDataTemp;
    private TextView tvDataSpo2;
    private TextView tvDataSpo2Pr;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_mairui_bene_view_t8, container, false);

        tvCollectionStatus = root.findViewById(R.id.device_collection_status);
        tvReceiveCounter = root.findViewById(R.id.collection_receive_counter);
        tvSuccessfulUpdateCounter = root.findViewById(R.id.successful_update_counter);
        SwipeLayout swipeLayout = root.findViewById(R.id.device_collection_swiplayout);
        // 控制采集开始按钮
        ivCollectionStart = root.findViewById(R.id.device_collection_start);
        // 控制采集结束
        ivCollectionStop = root.findViewById(R.id.device_collection_stop);

        // 控制采集流程(基体逻辑判断交给Activity进行)
        ivCollectionStart.setOnClickListener(view -> {
            handler.handleDeviceStartCollection(new MessageEntity(device.getDeviceCode()));
            swipeLayout.close(true);
        });
        ivCollectionStop.setOnClickListener(view -> {
            handler.handleDeviceStopCollection(new MessageEntity(device.getDeviceCode()));
            swipeLayout.close(true);
        });

        // 获取当前操作的仪器信息
        device = DeviceUtil.getMedicalDevice(DeviceEnum.MAI_RUI_T8);

        // 仪器数据控件
        tvDataEcgHr = root.findViewById(R.id.device_data_mairui_t8_ecg);
        tvDataResp = root.findViewById(R.id.device_data_mairui_t8_resp);
        tvDataSpo2 = root.findViewById(R.id.device_data_mairui_t8_spo2);
        tvDataSpo2Pr = root.findViewById(R.id.device_data_mairui_t8_pr);
        tvDataArt = root.findViewById(R.id.device_data_mairui_t8_art);
        tvDataNibp = root.findViewById(R.id.device_data_mairui_t8_nibp);
        tvDataTemp = root.findViewById(R.id.device_data_mairui_t8_temp);
        tvDataCvp = root.findViewById(R.id.device_data_mairui_t8_cvp);
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
        if (status == CollectionStatusEnum.COLLECTING) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是正在采集则修改颜色为红色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
            // 隐藏开始采集图标
            ivCollectionStart.setVisibility(View.GONE);
            // 显示结束图标
            ivCollectionStop.setVisibility(View.VISIBLE);
        } else if (status == CollectionStatusEnum.FINISHED) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是完成采集则修改颜色为绿色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
            // 隐藏结束采集图标(此时只剩下放弃采集图标)
            ivCollectionStop.setVisibility(View.GONE);
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
            if (receiveCounter % 2 == 0) {
                tvReceiveCounter.setText("" + receiveCounter);
                // 转换为数据实体
                DataMaiRuiT8 dataMaiRuiT8 = (DataMaiRuiT8) dataObject;
                if (dataMaiRuiT8.getEcgHeartRate() != DataCons.INVALID_DATA_INTEGER) {
                    tvDataEcgHr.setText(dataMaiRuiT8.getEcgHeartRate() + " bmp");
                }
                if (dataMaiRuiT8.getRespRespirationRate() != DataCons.INVALID_DATA_INTEGER) {
                    tvDataResp.setText(dataMaiRuiT8.getRespRespirationRate() + "");
                }
                if (dataMaiRuiT8.getSpo2PulseRate() != DataCons.INVALID_DATA_INTEGER) {
                    tvDataSpo2Pr.setText("" + dataMaiRuiT8.getSpo2PulseRate());
                }
                if (dataMaiRuiT8.getArtIbpMean() != DataCons.INVALID_DATA_DOUBLE) {
                    tvDataArt.setText(
                            dataMaiRuiT8.getArtIbpSystolic() + " mmHg\n"
                                    + dataMaiRuiT8.getArtIbpDiastolic() + " mmHg\n("
                                    + dataMaiRuiT8.getArtIbpMean() + ") mmHg");
                }

                if (dataMaiRuiT8.getNibpDiastolic() != DataCons.INVALID_DATA_DOUBLE) {
                    tvDataNibp.setText(dataMaiRuiT8.getNibpSystolic() + " mmHg\n" +
                            dataMaiRuiT8.getNibpDiastolic() + " mmHg\n("
                            + dataMaiRuiT8.getNibpMean() + ") mmHg");
                }
                if (dataMaiRuiT8.getTempTemperature1() != DataCons.INVALID_DATA_DOUBLE) {
                    tvDataTemp.setText(dataMaiRuiT8.getTempTemperature1() + " ℃\n" +
                            dataMaiRuiT8.getTempTemperature2() + " ℃\n" +
                            dataMaiRuiT8.getTempTemperatureDifference() + " ℃");
                }
                if (dataMaiRuiT8.getSpo2PercentOxygenSaturation() != DataCons.INVALID_DATA_INTEGER) {
                    tvDataSpo2.setText(dataMaiRuiT8.getSpo2PercentOxygenSaturation() + " %");
                }

                if (dataMaiRuiT8.getSpo2PulseRate() != DataCons.INVALID_DATA_INTEGER) {
                    tvDataSpo2Pr.setText(dataMaiRuiT8.getSpo2PulseRate() + " bpm");
                }
            }

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
