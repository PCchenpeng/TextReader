package com.dace.textreader.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PullRecyclerView extends RecyclerView {

    int pullDistance = 300;//下拉高度达到这个的时候，松开手才会刷新
    int originalFirstItemHeight = 0;
    int originalFirstItemWeight = 0;
    int downY = -1;
    //down之后下次up之前，这个值不变，用来实现loading的缩放比例
    int constDownY = -1;
    float constUpY = -1f;
    boolean canRefresh = false;
    boolean isFirstMove = true;
    int tempWidth = -1;
    int dx = 0;
//    var homeBanner: HomeBanner? = null

    boolean willRefresh = false;//松手后可刷新

    float mLastMotionY = 0f;
    float mLastMotionX = 0f;
    float deltaY = 0f;
    float deleaX = 0f;

    public PullRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean resume = super.onInterceptTouchEvent(e);
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 发生down事件时,记录y坐标
                mLastMotionY = e.getY();
                mLastMotionX = e.getX();
                downY = (int)e.getY();
                constDownY = (int)e.getY();
                resume = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                deltaY = e.getY()-mLastMotionY;
                deleaX = e.getY()-mLastMotionX;

                if (Math.abs(deleaX) > Math.abs(deltaY)) {
                    resume = false;
                } else {
                    //当前正处于滑动
                    if (!canScrollVertically(-1) && !willRefresh) {
                        canRefresh = true;
                    }
                    resume = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                resume = false;
        }

        return resume;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = (int)e.getY();
                constDownY = (int)e.getY();
                if (!canScrollVertically(-1) && !willRefresh) {
                    canRefresh = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFirstMove) {
                    isFirstMove = false;
                    if (canRefresh) {
                        canRefresh = e.getY() - downY > 0;
                    }

                }


//                if (canRefresh) {
//                    if (getChildAt(0) is HomeBanner) {
//
//                        val firstView = getChildAt(0) as HomeBanner
//                        if (!hasShow) {
//                            showLoading(firstView)
//                        }
//
//
//                        var fl = e.y - constDownY//fl从1-pullDistance   缩放比例从0-1
//                        if ((fl <= 0)) {
//                            return true
//                        }
//                        setLoadingScale(fl)
//
//
//                        val layoutParams = firstView.layoutParams
//                        if (layoutParams.height < 0 || tempWidth < 0) {
//                            originalFirstItemHeight = getChildViewHolder(firstView).itemView.height
//                            originalFirstItemWeight = getChildViewHolder(firstView).itemView.width
//                            layoutParams.height = originalFirstItemHeight
//                            tempWidth = originalFirstItemWeight
//                            firstView.layoutParams = layoutParams
//                        } else {
//
//                            var dY = e.y - downY
//                            val fl1 = e.y - constDownY
//
//
//                            val ratio = (1f / (0.004 * fl1 + 1)).toFloat()//实现阻尼效果
//                            dY = dY * ratio
//                            layoutParams.height = (Math.max((layoutParams.height + dY).toInt(), originalFirstItemHeight))
//                            tempWidth = (Math.max((tempWidth + dY * originalFirstItemWeight / originalFirstItemHeight).toInt(), originalFirstItemWeight))
//                            downY = e.y.toInt()
//                            firstView.layoutParams = layoutParams
//
//                            val viewpager = firstView.getChildAt(0) as ViewPager
//                            val viewpagerLayoutParams = viewpager.layoutParams
//
//
//
//                            viewpagerLayoutParams.height = layoutParams.height
//                            viewpagerLayoutParams.width = tempWidth
//                            viewpager.layoutParams = viewpagerLayoutParams
//
//                            dx = viewpagerLayoutParams.width - originalFirstItemWeight
//
//                            adjustViewPager(viewpager, dx)
//                        }
//                        return true
//
//                    }
//                }
                break;
            case MotionEvent.ACTION_UP:
                canRefresh = false;
                isFirstMove = true;
                constUpY = e.getY();
//                if (getChildAt(0) is HomeBanner) {
//                getChildAt(0)?.let {
//                    smoothRecover()
//                }
//            }
//        }
                break;

        }



        return super.onTouchEvent(e);
    }

    interface OnRefreshListener{
        void onRefresh();
    }

    OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }

}
