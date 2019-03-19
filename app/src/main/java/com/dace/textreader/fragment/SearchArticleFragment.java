package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.RecyclerViewAdapter;
import com.dace.textreader.bean.Article;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
public class SearchArticleFragment extends Fragment {

    private final String url = HttpUrlPre.HTTP_URL + "/essay/search";
    private final String userUrl = HttpUrlPre.HTTP_URL + "/statistics/search/update?";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;

    private String mSearchContent = "";

    private RecyclerViewAdapter adapter;
    private List<Article> mList = new ArrayList<>();

    private boolean isRefresh = false;
    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    public boolean isReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_article, container, false);

        initView();
        initEvents();

        isReady = true;

        if (!mSearchContent.equals("") && mList.size() == 0) {
            refreshLayout.autoRefresh();
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mList.size() == 0 && !mSearchContent.equals("")) {
                if (isReady) {
                    refreshLayout.autoRefresh();
                }
            }
        }
    }

    public String getSearchContent() {
        return mSearchContent;
    }

    public void setSearchContent(String searchContent) {
        if (!searchContent.equals(mSearchContent)) {
            this.mSearchContent = searchContent;
            if (isReady) {
                refreshLayout.autoRefresh();
            }
        }
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
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);

                if (position != -1 && position < mList.size()) {
                    turnToArticleDetail(position);
                }

            }
        });
    }

    public void initData() {
        if (!refreshing) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.setVisibility(View.GONE);
            }
            isRefresh = true;
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url, mSearchContent, String.valueOf(pageNum));
        }
    }

    private void getMoreData() {
        isRefresh = false;
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(this).execute(url, mSearchContent, String.valueOf(pageNum));
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_search_article_fragment);

        refreshLayout = view.findViewById(R.id.smart_refresh_search_article_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = view.findViewById(R.id.recycler_view_search_article_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    /**
     * 跳转到文章详情
     *
     * @param position
     */
    private void turnToArticleDetail(int position) {
        if (NewMainActivity.STUDENT_ID != -1) {
            new UpdateUserData(this)
                    .execute(userUrl + "studentId=" + NewMainActivity.STUDENT_ID +
                            "&search_keyword=" + mSearchContent +
                            "&articleId=" + mList.get(position).getId() +
                            "&articleType=" + mList.get(position).getType() +
                            "&level=" + NewMainActivity.GRADE);
        }

        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
        intent.putExtra("id", mList.get(position).getId());
        intent.putExtra("type", mList.get(position).getType());
        startActivity(intent);
    }

    /**
     * 分析搜索的数据
     *
     * @param s 获取到的搜索结果
     */
    private void analyzeSearchData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    noSearchResult();
                } else {
                    if (isRefresh) {
                        mList.clear();
                    }
                    List<Article> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject essay = jsonObject.getJSONObject("essay");
                        Article article = new Article();
                        article.setId(essay.optLong("id", -1));
                        article.setType(essay.optInt("type", -1));
                        article.setTitle(essay.getString("title"));
                        article.setContent(essay.getString("content"));
                        article.setGrade(essay.optInt("grade", 110));
                        article.setPyScore(essay.getString("score"));
                        article.setViews(essay.optInt("pv", 180));
                        article.setImagePath(essay.getString("image"));
                        if (isRefresh) {
                            mList.add(article);
                        } else {
                            list.add(article);
                        }
                    }
                    if (isRefresh) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.addData(list);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
                noSearchResult();
            } else {
                errorConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 没有搜索到内容
     */
    private void noSearchResult() {
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
            tv_tips.setText("搜索无内容哦~\n换个关键词试试~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorConnect() {
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    refreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败");
        }
    }

    /**
     * 显示提示信息
     *
     * @param s
     */
    private void showTips(String s) {
        MyToastUtil.showToast(mContext, s);
    }

    /**
     * 获取搜索内容数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, SearchArticleFragment> {

        protected GetData(SearchArticleFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SearchArticleFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("content", params[1]);
                object.put("pageNum", params[2]);
                object.put("pageSize", 10);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchArticleFragment fragment, String s) {
            if (s == null) {
                fragment.errorConnect();
            } else {
                fragment.analyzeSearchData(s);
            }
            fragment.refreshing = false;
            fragment.refreshLayout.finishRefresh();
            fragment.refreshLayout.finishLoadMore();
        }
    }

    /**
     * 更新用户的搜索行为
     */
    private static class UpdateUserData
            extends WeakAsyncTask<String, Integer, String, SearchArticleFragment> {

        protected UpdateUserData(SearchArticleFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SearchArticleFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchArticleFragment fragment, String s) {

        }
    }
}
