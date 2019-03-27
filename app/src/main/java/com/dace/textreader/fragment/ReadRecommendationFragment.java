package com.dace.textreader.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.adapter.ReadRecommendationAdapter;
import com.dace.textreader.bean.ReaderChoiceBean;
import com.dace.textreader.bean.ReaderRecommendationBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2019/3/22 0027 上午 10:18.
 * Version   1.0;
 * Describe : 阅读推荐fragment
 * History:
 * ==============================================================================
 */
public class ReadRecommendationFragment extends Fragment implements PullListener {

    private View view;
    private PullRecyclerView mRecycleView;
    private ReadRecommendationAdapter readRecommendationAdapter;

    private String url_choice = HttpUrlPre.HTTP_URL_ + "/select/reading/index/choice/list";
    private String url_list = HttpUrlPre.HTTP_URL_ + "/select/reading/index/recommend/list";

    private int pageNum = 1;
    private boolean isRefresh = true;

    private List<ReaderRecommendationBean.DataBean> mListData = new ArrayList<>();
    private List<ReaderChoiceBean.DataBean.EssayListBean> mChoiceData = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_read_recommendation, container, false);

        initView();

        return view;
    }

    private void initView() {
        mRecycleView = view.findViewById(R.id.rcv_recommend);
        readRecommendationAdapter = new ReadRecommendationAdapter(mListData,mChoiceData,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecycleView.setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(readRecommendationAdapter);

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int childCount = recyclerView.getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (firstVisibleItem + childCount == (itemCount+1)) {
                        isRefresh = false;
                        pageNum++;
                        loadListData();
                    }
                }
            }
        });


        loadChoiceData();
        loadListData();
    }

    private void loadChoiceData() {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId","-1");
            params.put("gradeId","-1");
            params.put("py","100");
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url_choice, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReaderChoiceBean readerChoiceBean = GsonUtil.GsonToBean(result.toString(),ReaderChoiceBean.class);
                        mChoiceData = readerChoiceBean.getData().getEssayList();
                        readRecommendationAdapter.setChoiceData(mChoiceData);
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    private void loadListData(){

        JSONObject params = new JSONObject();
        try {
            params.put("studentId","-1");
            params.put("gradeId","-1");
            params.put("py","100");
            params.put("pageNum",String.valueOf(pageNum));
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url_list, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReaderRecommendationBean readerRecommendationBean = GsonUtil.GsonToBean(result.toString(),ReaderRecommendationBean.class);
                        mListData = readerRecommendationBean.getData();
                        if(isRefresh){
//                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
                            if(mListData != null)
                                readRecommendationAdapter.refreshData(mListData);
                            mRecycleView.onPullComplete();
                        } else{
                            if(mListData != null)
                                readRecommendationAdapter.addData(mListData);
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        mRecycleView.onPullComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        pageNum = 1;
        loadChoiceData();
        loadListData();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        pageNum++;
        loadListData();
    }
}
