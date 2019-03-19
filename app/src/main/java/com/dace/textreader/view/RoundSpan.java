package com.dace.textreader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

import com.dace.textreader.util.DensityUtil;

/**
 * 圆点前缀
 * Created by 70391 on 2017/10/18.
 */

public class RoundSpan extends ReplacementSpan {

    private Context mContext;
    private int mSize;
    private int mColor;
    private String mText;

    /**
     * 构造方法
     */
    public RoundSpan(Context context, int color, String text) {
        this.mContext = context;
        this.mColor = color;
        this.mText = text;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
        mSize = (int) (paint.measureText(text, start, end) + DensityUtil.dip2px(mContext, 10));
        //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
        //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
        //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
        return mSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int color = paint.getColor();//保存文字颜色

        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        paint.setColor(mColor);//设置背景颜色
        float r = (paint.descent() - paint.ascent()) / 4;
        RectF oval = new RectF(x, y + paint.ascent(),
                x + r, y + paint.descent());
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
        // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawCircle(x + (r * 2), y + paint.ascent() + (r * 2), r, paint);
        //绘制圆角矩形，第二个参数是x半径，第三个参数是y半径

        paint.setColor(color);//恢复画笔的文字颜色
        canvas.drawText(text, start, end, x + DensityUtil.dip2px(mContext, 20), y, paint);//绘制文字
    }
}
