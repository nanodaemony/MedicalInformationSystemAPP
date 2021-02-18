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
import com.nano.activity.devicedata.collection.MessageEntity;
import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.devicedata.collection.interfaces.FragmentOperationHandler;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataLiBangEliteV8;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 理邦EliteV8数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentLiBangV8 extends android.support.v4.app.Fragment implements FragmentDataExchanger {


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

    // 这里添加各个仪器自己的数据展示控件
    private TextView tvDataHr;
    private TextView tvDataPr;
    private TextView tvDataRr;
    private TextView tvDataSpo2;
    private TextView tvDataLap;
    private TextView tvDataCvp;
    private TextView tvDataTemp;
    private TextView tvDataNibp;
    private TextView tvDataArt;
    private TextView tvDataP2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_libang_v8, container, false);
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
        device = DeviceUtil.getMedicalDevice(DeviceEnum.LI_BANG_ELITEV8);

        // 这里是仪器特有的参数控件
        tvDataHr = root.findViewById(R.id.device_data_li_bang_elitev8_hr);
        tvDataPr = root.findViewById(R.id.device_data_li_bang_elitev8_pr);
        tvDataRr = root.findViewById(R.id.device_data_li_bang_elitev8_rr);
        tvDataSpo2 = root.findViewById(R.id.device_data_li_bang_elitev8_spo2);
        tvDataLap = root.findViewById(R.id.device_data_li_bang_elitev8_lap);
        tvDataCvp = root.findViewById(R.id.device_data_li_bang_elitev8_cvp);
        tvDataTemp = root.findViewById(R.id.device_data_li_bang_elitev8_temp);
        tvDataNibp = root.findViewById(R.id.device_data_li_bang_elitev8_nibp);
        tvDataArt = root.findViewById(R.id.device_data_li_bang_elitev8_art);
        tvDataP2 = root.findViewById(R.id.device_data_li_bang_elitev8_p2);
        return root;
    }

    /**
     * 更新数据采集状态
     * @param status 新的状态
     */
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
            tvReceiveCounter.setText("" + receiveCounter);
            // 转换为数据实体
            DataLiBangEliteV8 dataLiBangEliteV8 = (DataLiBangEliteV8) dataObject;
            if (dataLiBangEliteV8.getHr() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataHr.setText(dataLiBangEliteV8.getHr() + " bpm");
            }
            if (dataLiBangEliteV8.getPr() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataPr.setText(dataLiBangEliteV8.getPr() + " bpm");
            }
            if (dataLiBangEliteV8.getRr() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataRr.setText(dataLiBangEliteV8.getRr() + " rpm");
            }
            if (dataLiBangEliteV8.getSpo2() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataSpo2.setText(dataLiBangEliteV8.getSpo2() + " %");
            }
            if (dataLiBangEliteV8.getLapMap() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataLap.setText(dataLiBangEliteV8.getLapMap() + " mmHg");
            }
            if (dataLiBangEliteV8.getCvpMap() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataCvp.setText(dataLiBangEliteV8.getCvpMap() + " mmHg");
            }
            if (dataLiBangEliteV8.getTemp1() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataTemp.setText(dataLiBangEliteV8.getTemp1() + " ℃\n" + dataLiBangEliteV8.getTemp2() + " ℃\n" + Math.abs(dataLiBangEliteV8.getTemp1() - dataLiBangEliteV8.getTemp2()) + " ℃");
            }
            if (dataLiBangEliteV8.getNibpMap() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataNibp.setText(dataLiBangEliteV8.getNibpSys() + " mmHg\n" + dataLiBangEliteV8.getNibpDia() + " mmHg\n" + dataLiBangEliteV8.getNibpMap() + " mmHg");
            }
            if (dataLiBangEliteV8.getArtDia() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataArt.setText(dataLiBangEliteV8.getArtSys() + " mmHg\n" + dataLiBangEliteV8.getArtDia() + " mmHg\n" + dataLiBangEliteV8.getArtMap() + " mmHg");
            }
            if (dataLiBangEliteV8.getP2Dia() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataP2.setText(dataLiBangEliteV8.getP2Sys() + " mmHg\n" + dataLiBangEliteV8.getP2Dia() + " mmHg\n" + dataLiBangEliteV8.getP2Map() + " mmHg");
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
