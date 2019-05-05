package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.WordDetailActivity;
import com.dace.textreader.bean.SearchItemBean;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.LineWrapLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/11/21 0021 上午 10:03.
 * Version   1.0;
 * Describe :  词堆搜索
 * History:
 * ==============================================================================
 */
public class SearchWordsFragment extends Fragment {

    private static final String url = HttpUrlPre.SEARCHE_URL + "/search/search/full/text/more";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private LineWrapLayout lineWrapLayout;

    private Context mContext;

    private String mSearchContent = "";

    private List<String> mList = new ArrayList<>();

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    public boolean isReady = false;
    private String searchWord;

    public static SearchWordsFragment newInstance(String searchResult, String word) {
        SearchWordsFragment f = new SearchWordsFragment();
        Bundle args = new Bundle();
        args.putString("word", word);
        args.putString("searchResult",searchResult);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_words, container, false);

        initView();
        initData();
        initEvents();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing) {
                    refreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (refreshing || isEnd) {
                    refreshLayout.finishLoadMore();
                } else {
                    getMoreData();
                }
            }
        });

    }

    private void getMoreData() {
        pageNum ++;
        JSONObject params = new JSONObject();
        try {
            params.put("query",searchWord);
            params.put("type","2");
            params.put("pageNum",pageNum);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("width","750");
            params.put("height","420");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(mContext).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                SearchItemBean searchItemBean = GsonUtil.GsonToBean(result.toString(),SearchItemBean.class);
                if(searchItemBean != null && searchItemBean.getData()!= null){
                    List<SubListBean> subListBeans = searchItemBean.getData();
                    addTextView(subListBeans);
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    private void initData() {
        if(getArguments() != null){
            searchWord = getArguments().getString("word");
            String searchResult = getArguments().getString("searchResult");
            if(searchWord != null && searchResult !=null && !searchWord.equals("") && !searchResult.equals("")){
                SearchResultBean searchResultBean = GsonUtil.GsonToBean(searchResult,SearchResultBean.class);
                List<SubListBean> subListBeans = searchResultBean.getData().getRet_array().get(0).getSubList();
                setData(subListBeans,searchWord);
            }
        }
    }



    private void initView() {
        frameLayout = view.findViewById(R.id.frame);

        refreshLayout = view.findViewById(R.id.smart);
//        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

        lineWrapLayout = view.findViewById(R.id.lwl_words);

    }


    /**
     * 数据为空
     */
    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_search, imageView);
            tv_tips.setText("没有搜索到词堆内容");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("获取词堆内容失败");
            tv_reload.setVisibility(View.VISIBLE);
            tv_reload.setText("重新获取");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    refreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后再试~");
        }
    }

    /**
     * 显示吐丝
     *
     * @param s
     */
    private void showTips(String s) {
        MyToastUtil.showToast(mContext, s);
    }

    public void setData(List<SubListBean> subListBean ,String word){
        pageNum = 1;
        this.searchWord = word;
        lineWrapLayout.removeAllViews();
        addTextView(subListBean);
    }

    private void addTextView(List<SubListBean> subListBean) {
        for (int j = 0;j <subListBean.size();j++){
            View child = View.inflate(mContext,R.layout.item_search_hot,null);
            TextView textView = child.findViewById(R.id.tv_num);
            final String hotWord = subListBean.get(j).getTitle();
            final String url = subListBean.get(j).getSource();
            textView.setText(hotWord);
            lineWrapLayout.addView(child);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                                WordDetailActivity
                    Intent intent = new Intent(getActivity(),WordDetailActivity.class);
                    intent.putExtra("url",url);
                    intent.putExtra("essayId",-1);
                    intent.putExtra("sourceType","2");
                    intent.putExtra("title",hotWord);
                    intent.putExtra("word",hotWord);
                    startActivity(intent);
                }
            });
        }
    }

}
