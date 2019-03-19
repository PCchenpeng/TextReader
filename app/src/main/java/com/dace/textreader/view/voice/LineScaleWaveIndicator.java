package com.dace.textreader.view.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view.voice
 * Created by Administrator.
 * Created time 2018/12/25 0025 下午 7:06.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class LineScaleWaveIndicator extends BaseIndicatorController {

    public static final float SCALE = 1.0f;

    float[] scaleYFloats = new float[]{SCALE,
            SCALE,
            SCALE,
            SCALE};
    public int[] waveFloats;

    public LineScaleWaveIndicator(int[] waveFloats) {
        this.waveFloats = waveFloats;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float translateX = getWidth() / 14;
        float translateY = getHeight();
        for (int i = 0; i < 4; i++) {
            canvas.save();
            //平移
            int intRandom = (int) (1 + Math.random() * (10 - 1 + 1));
            float v = intRandom / 10f * getHeight();
            canvas.translate(((i + 1) * 2) * translateX + translateX * i, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF = new RectF(-translateX, -getHeight(), 0, 0);
            canvas.drawRoundRect(rectF, 6, 6, paint);
            canvas.restore();
        }
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList<>();
        long[] delays = new long[]{100, 200, 300, 400};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.4f, 1);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();//刷新界面
                }
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }

}
