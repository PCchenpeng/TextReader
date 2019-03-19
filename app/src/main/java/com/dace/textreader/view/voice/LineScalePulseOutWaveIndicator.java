package com.dace.textreader.view.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view.voice
 * Created by Administrator.
 * Created time 2018/12/25 0025 下午 7:07.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class LineScalePulseOutWaveIndicator extends LineScaleWaveIndicator {

    public LineScalePulseOutWaveIndicator(int[] heightFloats) {
        super(heightFloats);
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList<>();
        long[] delays = new long[]{500, 100, 200, 300};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);
            scaleAnim.setDuration(900);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }

}
