package com.dace.textreader.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.LevelFragmentRecyclerViewAdapter;
import com.dace.textreader.bean.LevelFragmentBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/7/12 0012 上午 10:36.
 * Version   1.0;
 * Describe : 弹窗提示工具类
 * History:
 * ==============================================================================
 */

public class TipsUtil {

    private PopupWindow popupWindow;
    private Context mContext;

    public TipsUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 显示在视图之上
     */
    public void showTipAboveView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_above_view_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_above_view);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 130),
                DensityUtil.dip2px(mContext, 80));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = view.getWidth() / 2 - DensityUtil.dip2px(mContext, 130) / 2;
        int offsetY = -(view.getHeight() + DensityUtil.dip2px(mContext, 80));
        popupWindow.showAsDropDown(view, offsetX, offsetY);
        this.popupWindow = popupWindow;
    }

    /**
     * 显示在视图之下
     */
    public void showTipBelowView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_below_view_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_below_view);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 130),
                DensityUtil.dip2px(mContext, 80));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = view.getWidth() / 2 - DensityUtil.dip2px(mContext, 130) / 2;
        popupWindow.showAsDropDown(view, offsetX, 0);
        this.popupWindow = popupWindow;
    }

    public void showTipBelowOffsetView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_below_offset_view_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_below_offset_view);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 130),
                DensityUtil.dip2px(mContext, 80));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = view.getWidth() - DensityUtil.dip2px(mContext, 130);
        popupWindow.showAsDropDown(view, offsetX, 0);
        this.popupWindow = popupWindow;
    }

    /**
     *
     */
    public void showTipWebView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_below_view_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_below_view);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 130),
                DensityUtil.dip2px(mContext, 80));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = DensityUtil.getScreenWidth(mContext) / 2 - DensityUtil.dip2px(mContext, 65);
        int offsetY = -DensityUtil.dip2px(mContext, 240) / 2;
        popupWindow.showAsDropDown(view, offsetX, offsetY);
        this.popupWindow = popupWindow;
    }

    /**
     * 显示在视图右侧
     */
    public void showTipToLeftView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_to_left_view_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_to_left_view);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 130),
                DensityUtil.dip2px(mContext, 80));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = DensityUtil.getScreenWidth(mContext) / 2 - DensityUtil.dip2px(mContext, 65);
        int offsetY = -DensityUtil.getScreenHeight(mContext) / 2 - DensityUtil.dip2px(mContext, 40);
        popupWindow.showAsDropDown(view, offsetX, offsetY);
        this.popupWindow = popupWindow;
    }

    /**
     * 在屏幕中间显示支付失败的视图
     */
    public void showPayFailedView(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_pay_fail_layout, null);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 120),
                DensityUtil.dip2px(mContext, 120));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
//        int offsetX = DensityUtil.getScreenWidth(mContext) / 2 - DensityUtil.dip2px(mContext, 60);
//        int offsetY = DensityUtil.getScreenHeight(mContext) / 2 - DensityUtil.dip2px(mContext, 60);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        this.popupWindow = popupWindow;
    }



    /**
     * 写作界面显示倒计时提示
     */
    public void showWritingCountdownTips(View view, String tips) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()){
            return;
        }
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_writing_countdown_layout, null);
        TextView tv_tips = popupView.findViewById(R.id.tv_tips_popup_writing_countdown);
        tv_tips.setText(tips);
        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(mContext, 200),
                DensityUtil.dip2px(mContext, 40));
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        int offsetX = -DensityUtil.dip2px(mContext, 100) + view.getWidth() / 2;
        int offsetY = DensityUtil.dip2px(mContext, 56);
        popupWindow.showAsDropDown(view, offsetX, offsetY);
        this.popupWindow = popupWindow;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
