package com.nano.activity.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nano.R;

import androidx.annotation.NonNull;

/**
 * Description:
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:06
 */
public class AnotherRightFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_right_another, container, false);
        final TextView textView = root.findViewById(R.id.test_text);
        return root;
    }

}
