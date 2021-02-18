package com.nano.activity.devicedata.collection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nano.R;
import com.nano.activity.devicedata.collection.interfaces.FragmentDataExchanger;
import com.nano.datacollection.CollectionStatusEnum;

import androidx.annotation.NonNull;

/**
 * Description: Drager Fabius数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentDragerFabius extends android.support.v4.app.Fragment implements FragmentDataExchanger {

    private TextView tvCollectionStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_drager_fabius, container, false);
        tvCollectionStatus = root.findViewById(R.id.textView_collection_status_drager_fabius_plus);

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
            tvCollectionStatus.setTextColor(getResources().getColor(R.color.collection_status_collecting));
        } else if (status == CollectionStatusEnum.FINISHED) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是完成采集则修改颜色为绿色
            tvCollectionStatus.setTextColor(getResources().getColor(R.color.collection_status_finished));
        }
    }

    @Override
    public void updateSuccessfulUploadCounter(Integer successfulUploadCounter) {

    }

    @Override
    public void updateReceiveCounterAndDeviceData(Integer receiveCounter, Object dataObject) {

    }

}
