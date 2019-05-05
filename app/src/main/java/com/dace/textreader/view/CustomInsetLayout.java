package com.dace.textreader.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomInsetLayout extends RelativeLayout {

    private int[] mInsets = new int[4];

    public CustomInsetLayout(Context context) {
        super(context);
    }

    public CustomInsetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomInsetLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public final int[] getInsets() {
        return mInsets;
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            // TODO: Figure out why.

            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }

        return super.fitSystemWindows(insets);
    }

}
