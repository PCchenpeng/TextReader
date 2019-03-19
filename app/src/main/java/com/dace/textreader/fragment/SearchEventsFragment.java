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
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.adapter.EventsVerticalListAdapter;
import com.dace.textreader.bean.CompetitionBean;
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
 * Created time 2018/10/29 0029 下午 3:46.
 * Version   1.0;
 * Describe :  搜索活动
 * History:
 * ==============================================================================
 */
public class SearchEventsFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/activity/search";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;

    private String mSearchContent = "";

    private List<CompetitionBean> mList = new ArrayList<>();
    private EventsVerticalListAdapter adapter;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    public boolean isReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_events, container, false);

        initView();
        initEvents();

        isReady = true;

        if (!mSearchContent.equals("") && mList.size() == 0) {
            refreshLayout.autoRefresh();
        }

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
        if (isVisibleToUser) {
            if (!mSearchContent.equals("") && mList.size() == 0) {
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
        adapter.setOnItemClickListen(new EventsVerticalListAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList.size()) {
                    turnToEventH5(pos);
                }
            }
        });
    }

    /**
     * 前往活动详情
     *
     * @param position
     */
    private void turnToEventH5(int position) {
        String name = mList.get(position).getId();
        Intent intent = new Intent(mContext, EventsActivity.class);
        intent.putExtra("pageName", name);
        startActivity(intent);
    }

    public void initData() {
        if (!refreshing) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.setVisibility(View.GONE);
            }
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url, mSearchContent, String.valueOf(pageNum));
        }
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(this).execute(url, mSearchContent, String.valueOf(pageNum));
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_search_events_fragment);

        refreshLayout = view.findViewById(R.id.smart_refresh_search_events_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = view.findViewById(R.id.recycler_view_search_events_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EventsVerticalListAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    mList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CompetitionBean competitionBean = new CompetitionBean();
                        competitionBean.setId(object.getString("name"));
                        competitionBean.setTitle(object.getString("title"));
                        competitionBean.setImage(object.getString("image"));
                        competitionBean.setStatus(object.optInt("status", -1));
                        competitionBean.setContent(object.getString("message"));
                        mList.add(competitionBean);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 获取数据为空
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
            tv_tips.setText("暂无活动推荐");
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
            tv_tips.setText("获取数据失败，请稍后重试~");
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
            showTips("获取数据失败，请稍后重试~");
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
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
            tv_tips.setText("无网络连接，请连接网络后重试~");
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
            showTips("无网络连接，请稍后重试~");
        }
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取数据
     */
    private static class GetData extends
            WeakAsyncTask<String, Void, String, SearchEventsFragment> {

        protected GetData(SearchEventsFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SearchEventsFragment activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("content", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
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
        protected void onPostExecute(SearchEventsFragment fragment, String s) {
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
            fragment.refreshLayout.finishRefresh();
            fragment.refreshLayout.finishLoadMore();
        }
    }

}
