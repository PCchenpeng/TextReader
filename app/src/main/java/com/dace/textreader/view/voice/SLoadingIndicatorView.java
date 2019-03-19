package com.dace.textreader.view.voice;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.dace.textreader.R;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view.voice
 * Created by Administrator.
 * Created time 2018/12/25 0025 下午 7:08.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class SLoadingIndicatorView extends View {

    public static final int DefaultScale = 0;
    public static final int LineScale = 1;
    public static final int LineScalePulseOut = 2;
    public static final int LineScalePulseOutThree = 3;
    public static final int LineScalePulseOutWave = 4;

    //Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 45;

    //attrs
    int mIndicatorId;
    int mIndicatorColor;

    Paint mPaint;
    BaseIndicatorController mIndicatorController;

    private boolean mHasAnimation;

    public SLoadingIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SLoadingIndicatorView);
        mIndicatorId = a.getInt(R.styleable.SLoadingIndicatorView_s_indicator, DefaultScale);
        mIndicatorColor = a.getColor(R.styleable.SLoadingIndicatorView_s_indicator_color, Color.WHITE);
        a.recycle();
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator() {
        int[] waveFloats = new int[4];

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        int height = this.getMeasuredHeight();
        for (int i = 0; i < 4; i++) {
            int intRandom = (int) (1 + Math.random() * (4 - 1 + 1));
            int v = intRandom * 100;
            waveFloats[i] = v;
        }
        mIndicatorController = new LineScalePulseOutWaveIndicator(waveFloats);
        bringToFront();
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
            } else {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
            }
        }
    }

    /**
     * onAttachedToWindow是在第一次onDraw前调用的。也就是我们写的View在没有绘制出来时调用的，但只会调用一次。
     * onDetachedFromWindow相反
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHasAnimation) {
            mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
        }
    }

    /**
     * This is called when the view is detached from a window. At this point it no longer has a surface for drawing.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.CANCEL);
    }

    void drawIndicator(Canvas canvas) {
        mIndicatorController.draw(canvas, mPaint);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    void applyAnimation() {
        mIndicatorController.initAnimation();
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
}
