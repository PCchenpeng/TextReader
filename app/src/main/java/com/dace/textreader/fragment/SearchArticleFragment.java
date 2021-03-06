package com.dace.textreader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.SearchArticleAdapter;
import com.dace.textreader.bean.SearchItemBean;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/10/29 0029 下午 4:03.
 * Version   1.0;
 * Describe :  搜索文章
 * History:
 * ==============================================================================
 */
public class SearchArticleFragment extends Fragment implements View.OnClickListener {
    private String url = HttpUrlPre.SEARCHE_URL + "/search/search/full/text/more";
    private Context mContext;
    private View view;
    private RecyclerView rcl_author;
    private SearchArticleAdapter searchArticleAdapter;
    private List<SubListBean> mData = new ArrayList<>();
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private String searchWord;
    private boolean isAccure;

    public static SearchArticleFragment newInstance(String searchResult, String word,boolean isAccure) {
        SearchArticleFragment f = new SearchArticleFragment();
        Bundle args = new Bundle();
        args.putString("word", word);
        args.putString("searchResult",searchResult);
        args.putBoolean("isAccure",isAccure);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_author, container, false);

        initView();
        initData();
        initEvents();

        return view;
    }

    private void initData() {
        if(getArguments() != null){
            isAccure = getArguments().getBoolean("isAccure");
            if(isAccure){
                url = HttpUrlPre.SEARCHE_URL + "/search/search/full/text";
            }else {
                url = HttpUrlPre.SEARCHE_URL + "/search/search/full/text/more";
            }
            searchWord = getArguments().getString("word");
            String searchResult = getArguments().getString("searchResult");
            if(searchWord != null && searchResult !=null && !searchWord.equals("") && !searchResult.equals("")){
                SearchResultBean searchResultBean = GsonUtil.GsonToBean(searchResult,SearchResultBean.class);
                List<SubListBean> subListBeans = null;
                if(isAccure){
                    subListBeans= searchResultBean.getData().getRet_array().get(0).getSubList();
                }else {
                    for (int i=0;i<searchResultBean.getData().getRet_array().size();i++){
                        int type = searchResultBean.getData().getRet_array().get(i).getType();
                        if(type == 5){
                            subListBeans = searchResultBean.getData().getRet_array().get(i).getSubList();
                        }
                    }
                }
                if(subListBeans != null)
                    setData(subListBeans,searchWord);
            }
        }
    }

    private void initView() {
        rcl_author = view.findViewById(R.id.rcl_search_item);
        frameLayout = view.findViewById(R.id.frame);

        refreshLayout = view.findViewById(R.id.smart);
//        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setEnableRefresh(false);
        searchArticleAdapter = new SearchArticleAdapter(mData,mContext);
        LinearLayoutManager layoutManager_user = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rcl_author.setLayoutManager(layoutManager_user);
        rcl_author.setAdapter(searchArticleAdapter);
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
                    if(isAccure){
                        getAccureMoreData();
                    }else {
                        getVagueMoreData();
                    }
                }
            }
        });
    }

    /**
     *
     */
    private void getAccureMoreData() {
        pageNum ++;
        JSONObject params = new JSONObject();
        try {
            params.put("query",searchWord);
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

                SearchResultBean searchResultBean = GsonUtil.GsonToBean(result.toString(),SearchResultBean.class);
                if(searchResultBean != null && searchResultBean.getData()!= null){
                    List<SubListBean> subListBeans = searchResultBean.getData().getRet_array().get(0).getSubList();
                    searchArticleAdapter.addData(subListBeans);
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    private void getVagueMoreData() {
        pageNum ++;
        JSONObject params = new JSONObject();
        try {
            params.put("query",searchWord);
            params.put("type","5");
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
                    searchArticleAdapter.addData(subListBeans);
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<SubListBean> subListBean,String word){
        this.searchWord = word;
        searchArticleAdapter.refreshData(subListBean);
    }
}
