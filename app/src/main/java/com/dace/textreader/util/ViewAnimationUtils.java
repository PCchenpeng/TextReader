package com.dace.textreader.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/12/20 0020 下午 4:54.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class ViewAnimationUtils {

    /**
     * View渐隐动画效果
     */
    public static void setHideAnimation(final ViewGroup view, int duration) {
        if (null == view || duration < 0) {
            return;
        }

        // 监听动画结束的操作
        Animation mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(mHideAnimation);
    }

    /**
     * View渐现动画效果
     */
    public static void setShowAnimation(final ViewGroup view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        Animation mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
        view.startAnimation(mShowAnimation);
    }

}
