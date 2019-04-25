package com.dace.textreader.view.weight.pullrecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class HorizontalRecycleView extends RecyclerView {
    private float startx;
    private float starty;
    private float offsetx;
    private float offsety;
    public HorizontalRecycleView(@NonNull Context context) {
        super(context);
    }

    public HorizontalRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                getParent().requestDisallowInterceptTouchEvent(true);
//                startx = event.getX();
//                starty = event.getY();
//                Log.e("MotionEvent", "webview按下");
//                break;
//            case MotionEvent.ACTION_MOVE:
////                getParent().requestDisallowInterceptTouchEvent(true);
//                Log.e("MotionEvent", "webview滑动");
//                offsetx = Math.abs(event.getX() - startx);
//                offsety = Math.abs(event.getY() - starty);
//                Log.e("MotionEvent", "starty = "+String.valueOf(starty));
//                Log.e("MotionEvent", "event.getY() = "+String.valueOf(event.getY()));
//                Log.e("MotionEvent", "offsety = "+String.valueOf(offsety));
////                if (offsetx > offsety ) {
////                    getParent().requestDisallowInterceptTouchEvent(true);
////                    Log.e("MotionEvent", "屏蔽了父控件");
////                } else {
////                    getParent().requestDisallowInterceptTouchEvent(false);
////                    Log.e("MotionEvent", "事件传递给父控件");
////                }
//
//            default:
//                break;
//        }
//        return false;
//    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //返回false，则把事件交给子控件的onInterceptTouchEvent()处理
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //返回true,则后续事件可以继续传递给该View的onTouchEvent()处理
        return true;
    }

}
