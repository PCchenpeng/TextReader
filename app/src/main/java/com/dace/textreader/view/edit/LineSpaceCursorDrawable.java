package com.dace.textreader.view.edit;

import android.graphics.drawable.ShapeDrawable;

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

public class LineSpaceCursorDrawable extends ShapeDrawable {
    private int mHeight;

    public LineSpaceCursorDrawable(int cursorColor, int cursorWidth, int cursorHeight) {
        mHeight = cursorHeight;
        setDither(false);
        getPaint().setColor(cursorColor);
        setIntrinsicWidth(cursorWidth);
    }

    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.setBounds(paramInt1, paramInt2, paramInt3, this.mHeight + paramInt2);
    }
}
