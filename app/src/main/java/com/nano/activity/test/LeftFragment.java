package com.nano.activity.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nano.R;
import com.nano.common.util.ToastUtil;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.annotation.NonNull;

/**
 * Description:
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 12:57
 */
public class LeftFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_left, container, false);
        final Button button = root.findViewById(R.id.button);
        ToastUtil.toast(getContext(), "123", TastyToast.SUCCESS);
        return root;
    }

}
