package com.dace.textreader.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.PreferencesUtil;

public class BaseFragment extends Fragment {
    protected void showDefaultView(FrameLayout frameLayout, int imageResource, String tipsText, boolean isGif, boolean isButton, String buttonText, final OnButtonClick onButtonClick){
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_default_layout, null);

        ImageView imageView = view.findViewById(R.id.iv_state);
        if (isGif) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = DensityUtil.dip2px(getContext(),110);
            layoutParams.height = DensityUtil.dip2px(getContext(),110);
            imageView.setLayoutParams(layoutParams);
            GlideUtils.loadGIFImageWithNoOptions(getContext(), R.drawable.image_loading, imageView);
        } else {
            GlideUtils.loadImageWithNoOptions(getContext(), imageResource, imageView);
        }

            TextView tv_tips = view.findViewById(R.id.tv_tips);
            TextView tv_operation = view.findViewById(R.id.tv_operation);

            tv_tips.setText(tipsText);
            tv_operation.setText(buttonText);
            if (isButton) {
                tv_operation.setVisibility(View.VISIBLE);
                if (onButtonClick != null) {
                    tv_operation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onButtonClick.onButtonClick();
                        }
                    });
                }
            } else {
                tv_operation.setVisibility(View.GONE);
            }

            frameLayout.removeAllViews();
            frameLayout.addView(view,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            frameLayout.setVisibility(View.VISIBLE);
            frameLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

    }

    protected void showNetFailView(FrameLayout frameLayout,final OnButtonClick onButtonClick){
        showDefaultView(frameLayout, R.drawable.image_state_netfail, "加载数据失败，请重试～", false, true, "重新加载", onButtonClick);
    }
    protected void showLoadingView(FrameLayout frameLayout){
        showDefaultView(frameLayout, R.drawable.image_loading, "", true, false, "", null);
    }

    protected void showEmptyView(FrameLayout frameLayout){
        showDefaultView(frameLayout, R.drawable.image_state_empty, "暂无内容", false, false, "", null);
    }

    public interface OnButtonClick{
        void onButtonClick();
    }

    protected void setOnScrollListener(RecyclerView recyclerView){
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && ((NewMainActivity)getActivity()).getRl_tab().getVisibility() == View.GONE){
                    Log.d("111","444");
                    ((NewMainActivity)getActivity()).getRl_tab().setVisibility(View.VISIBLE);
                } else if (dy > 0 && ((NewMainActivity)getActivity()).getRl_tab().getVisibility() == View.VISIBLE){
                    Log.d("111","555");
                    ((NewMainActivity)getActivity()).getRl_tab().setVisibility(View.GONE);
                }
            }
        });
    }
    protected void setOnScrollListener(NestedScrollView nestedScrollView){
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY && ((NewMainActivity)getActivity()).getRl_tab().getVisibility() == View.GONE){
                    Log.d("111","444");
                    ((NewMainActivity)getActivity()).getRl_tab().setVisibility(View.VISIBLE);
                } else if (scrollY > oldScrollY && ((NewMainActivity)getActivity()).getRl_tab().getVisibility() == View.VISIBLE){
                    Log.d("111","555");
                    ((NewMainActivity)getActivity()).getRl_tab().setVisibility(View.GONE);
                }
            }
        });
    }

    protected boolean isLogin(){
        Object studeenObj = PreferencesUtil.getData(getContext(),"studentId","-1");
        if(studeenObj == null)
            return false;
        String studentId = studeenObj.toString();

        return !studentId.equals("-1");
    }
}
