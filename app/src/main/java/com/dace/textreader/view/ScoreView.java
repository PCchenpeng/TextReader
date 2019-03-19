package com.dace.textreader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.example.test.myapplication
 * Created by Administrator.
 * Created time 2018/11/1 0001 上午 10:30.
 * Version   1.0;
 * Describe :  自定义打分view
 * History:
 * ==============================================================================
 */
public class ScoreView extends View {

    private Context mContext;

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private int mColor = -1;
    private int mStartColor = -1;
    private int mEndColor = -1;

    private int minValue;
    private int maxValue;
    private int middleValue;
    private Paint mValuePaint;
    private int mValueSize;
    private int mValueColor;

    private int mCurValue;
    private Paint mCurPaint;
    private int mCurValueSize;
    private int mCurValueColor;

    private Paint mCurBgPaint;
    private int mCurBgColor;

    private Paint mSeekPaint;
    private int mSeekRadius;
    private int mSeekColor;

    private float mCurX;
    private boolean isTouch;

    private int mLeftRightPadding;

    private RectF arc;
    private RectF roundRect;
    private RectF rectF;

    private Bitmap bitmap;
    private Rect src;
    private RectF dst;

    public ScoreView(Context context) {
        super(context);
        init(context);
    }

    public ScoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        mColor = Color.parseColor("#FFA76D");
        mStartColor = Color.parseColor("#FFA76D");
        mEndColor = Color.parseColor("#FF8159");

        minValue = 60;
        maxValue = 100;
        middleValue = (maxValue - minValue) / 2 + minValue;
        mValueColor = Color.parseColor("#333333");
        mValueSize = DensityUtil.sp2px(mContext, 14);

        mSeekColor = Color.parseColor("#ffffff");
        mSeekRadius = DensityUtil.dip2px(mContext, 13);

        mCurValueSize = DensityUtil.sp2px(mContext, 15);
        mCurValueColor = Color.parseColor("#ffffff");

        //左右留有padding防止显示当前值的view越界
        mLeftRightPadding = DensityUtil.dip2px(mContext, 28);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setTextSize(mValueSize);

        mSeekPaint = new Paint();
        mSeekPaint.setAntiAlias(true);
        mSeekPaint.setColor(mSeekColor);

        mCurBgColor = Color.parseColor("#5C6AFC");
        mCurBgPaint = new Paint();
        mCurBgPaint.setAntiAlias(true);
        mCurBgPaint.setColor(mCurBgColor);

        mCurPaint = new Paint();
        mCurPaint.setAntiAlias(true);
        mCurPaint.setTextSize(mCurValueSize);
        mCurPaint.setColor(mCurValueColor);

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_score_indicator);
        src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mCurX = mWidth / 2;

        if (mStartColor != -1 && mEndColor != -1) {
            LinearGradient gradient = new LinearGradient(mLeftRightPadding, 0,
                    mWidth - mLeftRightPadding, 0,
                    mStartColor, mEndColor, Shader.TileMode.MIRROR);
            mPaint.setShader(gradient);
        } else if (mColor != -1) {
            mPaint.setColor(mColor);
        } else {
            mColor = Color.parseColor("#FFA76D");
            mPaint.setColor(mColor);
        }

        updateRect();
    }

    private void updateRect() {
        rectF = new RectF(mLeftRightPadding,
                mHeight / 2 - DensityUtil.dip2px(mContext, 10),
                mWidth - mLeftRightPadding,
                mHeight / 2 + DensityUtil.dip2px(mContext, 10));

        arc = new RectF(mCurX - DensityUtil.dip2px(mContext, 7),
                mHeight / 2 - DensityUtil.dip2px(mContext, 30),
                mCurX + DensityUtil.dip2px(mContext, 7),
                mHeight / 2 - DensityUtil.dip2px(mContext, 16));

        roundRect = new RectF(mCurX - mLeftRightPadding,
                mHeight / 2 - DensityUtil.dip2px(mContext, 57),
                mCurX + mLeftRightPadding,
                mHeight / 2 - DensityUtil.dip2px(mContext, 27));

        dst = new RectF(mCurX - DensityUtil.dip2px(mContext, 15),
                mHeight / 2 - DensityUtil.dip2px(mContext, 15),
                mCurX + DensityUtil.dip2px(mContext, 15),
                mHeight / 2 + DensityUtil.dip2px(mContext, 15));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(rectF, DensityUtil.dip2px(mContext, 10),
                DensityUtil.dip2px(mContext, 10), mPaint);

        canvas.drawText(String.valueOf(minValue), mLeftRightPadding,
                mHeight / 2 + DensityUtil.dip2px(mContext, 30), mValuePaint);
        canvas.drawText(String.valueOf(middleValue),
                mWidth / 2 - mValuePaint.measureText(String.valueOf(middleValue)) / 2,
                mHeight / 2 + DensityUtil.dip2px(mContext, 30), mValuePaint);
        canvas.drawText(String.valueOf(maxValue),
                mWidth - mValuePaint.measureText(String.valueOf(maxValue)) - mLeftRightPadding,
                mHeight / 2 + DensityUtil.dip2px(mContext, 30), mValuePaint);


        canvas.drawBitmap(bitmap, src, dst, null);

        canvas.drawArc(arc, 225, 90, true, mCurBgPaint);

        canvas.drawRoundRect(roundRect, DensityUtil.dip2px(mContext, 2),
                DensityUtil.dip2px(mContext, 2), mCurBgPaint);

        mCurValue = xToValue(mCurX);

        if (onValueChangeListen != null) {
            onValueChangeListen.onValueChange(xToValue(mCurValue));
        }

        String value = mCurValue + "分";
        //计算坐标使文字居中
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(value, mCurX - mCurPaint.measureText(value) / 2,
                mHeight / 2 - DensityUtil.dip2px(mContext, 42) + fontHeight, mCurPaint);
    }

    private int xToValue(float x) {

        x = x - mLeftRightPadding;

        float d = mWidth - mLeftRightPadding * 2;

        float unit = (maxValue - minValue) / d;

        float value = (x * unit) + minValue;

        int xValue = Math.round(value);

        if (xValue < 60) {
            xValue = 60;
        } else if (xValue > 100) {
            xValue = 100;
        }
        return xValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x >= DensityUtil.dip2px(mContext, 28)
                        && x <= mWidth - DensityUtil.dip2px(mContext, 28)) {
                    if (Math.abs(x - mCurX) < DensityUtil.dip2px(mContext, 13)
                            && Math.abs(y - mHeight / 2) < DensityUtil.dip2px(mContext, 20)) {
                        isTouch = true;
                    } else {
                        mCurX = x;
                        updateRect();
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouch) {
                    if (event.getX() >= DensityUtil.dip2px(mContext, 28)
                            && event.getX() <= mWidth - DensityUtil.dip2px(mContext, 28)) {
                        mCurX = event.getX();
                        updateRect();
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
        }
        return true;
    }

    /**
     * 获取当前值
     *
     * @return
     */
    public int getCurValue() {
        return mCurValue;
    }

    public interface OnValueChangeListen {
        void onValueChange(int value);
    }

    private OnValueChangeListen onValueChangeListen;

    public void setOnValueChangeListen(OnValueChangeListen onValueChangeListen) {
        this.onValueChangeListen = onValueChangeListen;
    }

}
