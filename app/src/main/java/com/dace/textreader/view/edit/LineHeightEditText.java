package com.dace.textreader.view.edit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.dace.textreader.R;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view.edit
 * Created by Administrator.
 * Created time 2018/9/10 0010 下午 5:55.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class LineHeightEditText extends AppCompatEditText {

    private float mSpacingMult = 1f;
    private float mSpacingAdd = 0f;
    private TextWatcher textWatcher;
    private int cursorColor = Color.RED;
    private int cursorWidth = 6;
    private int cursorHeight = 60;

    private Paint linePaint;

    public LineHeightEditText(Context context) {
        this(context, null);
    }

    public LineHeightEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public LineHeightEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // init
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.LineHeightEditText, defStyleAttr, 0);
        cursorColor = a.getColor(R.styleable.LineHeightEditText_cursorColor, getColorAccent(context));
        cursorHeight = a.getDimensionPixelSize(R.styleable.LineHeightEditText_cursorHeight, (int) (1.25 * getTextSize()));
        cursorWidth = a.getDimensionPixelSize(R.styleable.LineHeightEditText_cursorWidth, 6);
        a.recycle();

        this.linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#999999"));//设置下划线颜色

        getLineSpacingAddAndLineSpacingMult();
        setTextCursorDrawable();
        listenTextChange();
    }

    private int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    private void listenTextChange() {
        addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (textWatcher != null) {
                    textWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLineSpacing(0f, 1f);
                setLineSpacing(mSpacingAdd, mSpacingMult);
                if (textWatcher != null) {
                    textWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textWatcher != null) {
                    textWatcher.afterTextChanged(s);
                }
            }
        });
    }

    private void setTextCursorDrawable() {
        try {
            Method method = TextView.class.getDeclaredMethod("createEditorIfNeeded");
            method.setAccessible(true);
            method.invoke(this);
            Field field1 = TextView.class.getDeclaredField("mEditor");
            Field field2 = Class.forName("android.widget.Editor").getDeclaredField("mCursorDrawable");
            field1.setAccessible(true);
            field2.setAccessible(true);
            Object arr = field2.get(field1.get(this));
            Array.set(arr, 0, new LineSpaceCursorDrawable(getCursorColor(), getCursorWidth(), getCursorHeight()));
            Array.set(arr, 1, new LineSpaceCursorDrawable(getCursorColor(), getCursorWidth(), getCursorHeight()));
        } catch (Exception ignored) {
        }
    }

    private void getLineSpacingAddAndLineSpacingMult() {
        try {
            Field mSpacingAddField = TextView.class.getDeclaredField("mSpacingAdd");
            Field mSpacingMultField = TextView.class.getDeclaredField("mSpacingMult");
            mSpacingAddField.setAccessible(true);
            mSpacingMultField.setAccessible(true);
            mSpacingAdd = mSpacingAddField.getFloat(this);
            mSpacingMult = mSpacingMultField.getFloat(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDraw(Canvas paramCanvas) {
        int i = getLineCount();
        int j = getHeight();
        int k = getLineHeight();
        int m = 1 + j / k;
        if (i < m) i = m;
        int n = getCompoundPaddingTop();

        int distance_with_btm = (int) (getLineHeight() - getTextSize()) - 16;
        //这个关于距离底部的变量当不使用lineSpacingMultiplier和lineSpacingExtra参数时是不起作用的

        for (int i2 = 0; ; i2++) {

            if (i2 >= i) {
                super.onDraw(paramCanvas);
                paramCanvas.restore();
                return;
            }

            n += k;
            n -= distance_with_btm;//将线划在字体靠下面
            paramCanvas.drawLine(0.0F, n, getRight(), n, this.linePaint);
            paramCanvas.save();
            n += distance_with_btm;//还原n
        }
    }

    public int getCursorColor() {
        return cursorColor;
    }

    public void setCursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
        setTextCursorDrawable();
        invalidate();
    }

    public int getCursorHeight() {
        return cursorHeight;
    }

    public void setCursorHeight(int cursorHeight) {
        this.cursorHeight = cursorHeight;
        setTextCursorDrawable();
        invalidate();
    }

    public int getCursorWidth() {
        return cursorWidth;
    }

    public void setCursorWidth(int cursorWidth) {
        this.cursorWidth = cursorWidth;
        setTextCursorDrawable();
        invalidate();
    }

    /**
     * Adds a TextWatcher to the list of those whose methods are called
     * whenever this TextView's text changes.
     *
     * @param textWatcher TextWatcher
     */
    public void addTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

}
