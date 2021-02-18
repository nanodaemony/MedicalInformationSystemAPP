package com.nano.activity.test;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.nano.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Description: 测试用
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 0:15
 */
public class TestActivity extends AppCompatActivity {

    LinearLayout rightLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button button = findViewById(R.id.button);
        rightLayout = findViewById(R.id.rightLayout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replace();
            }
        });


        WheelView<String> wheelView = findViewById(R.id.test_wheel);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
        wheelView.setSkin(WheelView.Skin.Holo); // common皮肤
        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");
        data.add("6");
        data.add("7");
        wheelView.setWheelData(data);  // 数据集合

//        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
//                .setCallBack(new OnDateSetListener() {
//                    @Override
//                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
//
//                    }
//                })
//                .setCancelStringId("Cancel")
//                .setSureStringId("Sure")
//                .setTitleStringId("TimePicker")
//                .setYearText("Year")
//                .setMonthText("Month")
//                .setDayText("Day")
//                .setHourText("Hour")
//                .setMinuteText("Minute")
//                .setCyclic(false)
//                .setMinMillseconds(System.currentTimeMillis())
//                .setMaxMillseconds(System.currentTimeMillis() + 10)
//                .setCurrentMillseconds(System.currentTimeMillis())
//                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
//                .setType(Type.ALL)
//                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
//                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
//                .setWheelItemTextSize(12)
//                .build();


        SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.sample1);

        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        ImageView add = findViewById(R.id.add_test);
        ImageView delete = findViewById(R.id.delete_test);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click Add Here");
                // 关掉这个
                swipeLayout.close(true);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Click Delete Here");
            }
        });




    }

    private void replace() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.rightLayout, new AnotherRightFragment());
        transaction.commit();
    }


}
