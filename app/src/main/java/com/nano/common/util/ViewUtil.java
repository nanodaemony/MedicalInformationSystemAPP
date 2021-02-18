package com.nano.common.util;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.github.mikephil.charting.charts.LineChart;

/**
 * 视图工具类
 * @author cz
 */
public class ViewUtil {

    /**
     * 使用圆圈的方式打开CardView
     * @param view cardView
     */
    public static void showViewByCircle(View view){
        int circular = view.getHeight() / 2 > view.getWidth() / 2 ? view.getHeight() / 2 : view.getWidth() / 2;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, 0, circular);
        animator.setDuration(700);
        animator.start();
    }

    /**
     * 隐藏布局的动作 从上到下隐藏
     */
    public static void hideViewFromTopToBottomAction(View view){
        TranslateAnimation hideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1f);
        hideAction.setDuration(300);
        view.startAnimation(hideAction);
        view.setVisibility(View.GONE);
    }


    /**
     * 展示布局的动画 从底部上升
     * @param view 布局
     */
    public static void showViewFromBottomToTopAction(View view){
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        view.startAnimation(mShowAction);
        view.setVisibility(View.VISIBLE);
    }


    /**
     * 改变曲线的显示状态：显示与隐藏
     *
     * @param chart 图片
     */
    public static void changeDynamicChartShowAndHide(LineChart chart) {
        if (chart.getVisibility() == View.VISIBLE) {
            hideViewFromTopToBottomAction(chart);
        } else {
            showViewFromBottomToTopAction(chart);
        }
    }

}
