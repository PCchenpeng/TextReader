package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewClassesActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.NewReaderActivity;
import com.dace.textreader.adapter.ReaderFragmentAdapter;
import com.dace.textreader.adapter.RecyclerViewAdapter;
import com.dace.textreader.bean.Article;
import com.dace.textreader.bean.ReaderSortBean;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.HttpUrlPre;
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
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/9/25 0025 上午 10:52.
 * Version   1.0;
 * Describe :  阅读页
 * History:
 * ==============================================================================
 */

public class ReaderFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/essay/category";
    private static final String recommendUrl = HttpUrlPre.HTTP_URL + "/index/default/essays?";

    private View view;

    private SmartRefreshLayout refreshLayout;
    private FrameLayout frameLayout;
    private RelativeLayout rl_back;
    private TextView tv_title;
    private LinearLayout ll_classes;
    private ImageView iv_classes;
    private LinearLayout ll_reader;
    private RecyclerView recyclerView;
    private LinearLayout ll_recommend;
    private RecyclerView recyclerView_recommend;

    private Context mContext;

    private ReaderSortBean classesReaderSortBean;
    private List<ReaderSortBean> mList = new ArrayList<>();
    private ReaderFragmentAdapter adapter;

    private boolean isRefresh = false;
    private boolean refreshing = false;

    private List<Article> mList_recommend = new ArrayList<>();
    private RecyclerViewAdapter adapter_recommend;
    private int pageNum = 1;
    private boolean isEnd = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reader, container, false);

        initView();
        initLocalData();
        initEvents();

        return view;
    }

    private void initLocalData() {
        List<ReaderSortBean> list = LitePal.findAll(ReaderSortBean.class);
        if (list.isEmpty()) {
            refreshLayout.autoRefresh();
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus() == 0) {
                    classesReaderSortBean = list.get(i);
                } else if (list.get(i).getStatus() == 1) {
                    mList.add(list.get(i));
                }
            }
            updateUi();
        }
    }

    /**
     * 获取推荐数据
     */
    private void initRecommendData() {
        isRefresh = true;
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        new GetRecommendData(this).execute(recommendUrl +
                "studentId=" + NewMainActivity.STUDENT_ID + "&essayGrade=" + NewMainActivity.GRADE
                + "&pageNum=" + pageNum + "&pageSize=10");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
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
                    getMoreRecommendData();
                }
            }
        });
        iv_classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewClassesActivity.class));
            }
        });
        adapter.setOnItemClick(new ReaderFragmentAdapter.OnReaderFragmentSortItemClick() {
            @Override
            public void OnItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    turnToReader(pos);
                }
            }
        });
        adapter_recommend.setOnItemClickListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView_recommend.getChildAdapterPosition(view);
                if (position != -1 && position < mList_recommend.size()) {
                    turnToArticleDetail(position);
                }
            }
        });
    }

    /**
     * 跳转到文章详情
     *
     * @param position
     */
    private void turnToArticleDetail(int position) {
        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
        intent.putExtra("id", mList_recommend.get(position).getId());
        intent.putExtra("type", mList_recommend.get(position).getType());
        startActivity(intent);
    }

    /**
     * 获取更多推荐数据
     */
    private void getMoreRecommendData() {
        isRefresh = false;
        refreshing = true;
        pageNum = pageNum + 1;
        new GetRecommendData(this).execute(recommendUrl +
                "studentId=" + NewMainActivity.STUDENT_ID + "&essayGrade=" + NewMainActivity.GRADE
                + "&pageNum=" + pageNum + "&pageSize=10");
    }

    /**
     * 前往课外阅读
     *
     * @param pos
     */
    private void turnToReader(int pos) {
        Intent intent = new Intent(mContext, NewReaderActivity.class);
        intent.putExtra("index", pos);
        startActivity(intent);
    }

    private void initData() {
        if (!refreshing) {
            new GetData(this).execute(url);
        }
    }

    private void initView() {
//        rl_back = view.findViewById(R.id.rl_page_back_top_layout);
//        rl_back.setVisibility(View.GONE);
//        tv_title = view.findViewById(R.id.tv_page_title_top_layout);
//        tv_title.setText("阅读");

        refreshLayout = view.findViewById(R.id.smart_refresh_layout_reader_fragment);
        frameLayout = view.findViewById(R.id.frame_reader_fragment);
        ll_classes = view.findViewById(R.id.ll_classes_reader_fragment);
        iv_classes = view.findViewById(R.id.iv_classes_reader_fragment);
        ll_reader = view.findViewById(R.id.ll_reader_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_reader_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReaderFragmentAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        ll_recommend = view.findViewById(R.id.ll_recommend_reader_fragment);
        recyclerView_recommend = view.findViewById(R.id.recycler_view_recommend_reader_fragment);
        recyclerView_recommend.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_recommend.setLayoutManager(layoutManager_recommend);
        adapter_recommend = new RecyclerViewAdapter(mContext, mList_recommend);
        recyclerView_recommend.setAdapter(adapter_recommend);

        ll_recommend.setVisibility(View.GONE);
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorData();
                } else {
                    classesReaderSortBean = null;
                    mList.clear();
                    LitePal.deleteAll(ReaderSortBean.class);
                    for (int i = 0; i < array.length(); i++) {
                        ReaderSortBean readerSortBean = new ReaderSortBean();
                        JSONObject object = array.getJSONObject(i);
                        readerSortBean.setType(object.optInt("type", -1));
                        readerSortBean.setName(object.getString("name"));
                        readerSortBean.setStatus(object.optInt("status", -1));
                        readerSortBean.setImage(object.getString("image"));
                        readerSortBean.save();
                        if (readerSortBean.getStatus() == 1) {
                            mList.add(readerSortBean);
                        } else if (readerSortBean.getStatus() == 0) {
                            classesReaderSortBean = readerSortBean;
                        }
                    }
                    updateUi();
                }
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新界面
     */
    private void updateUi() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        if (classesReaderSortBean == null) {
            ll_classes.setVisibility(View.GONE);
        } else {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.image_placeholder_rectangle)
                    .error(R.drawable.image_placeholder_rectangle)
                    .transform(new GlideRoundImage(mContext, 8));
            Glide.with(mContext)
                    .load(classesReaderSortBean.getImage())
                    .apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            iv_classes.setImageDrawable(resource);
                        }
                    });
        }
        adapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            ll_reader.setVisibility(View.GONE);
        }
        if (ll_classes.getVisibility() == View.GONE && ll_reader.getVisibility() == View.GONE) {
            errorData();
        }
        initRecommendData();
    }

    /**
     * 获取数据出错
     */
    private void errorData() {
        View errorView = LayoutInflater.from(getContext())
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        tv_tips.setText("获取数据失败，请稍后重试");
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                refreshLayout.autoRefresh();
            }
        });
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        View errorView = LayoutInflater.from(getContext())
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        tv_tips.setText("暂无网络，请连接网络后重试");
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                refreshLayout.autoRefresh();
            }
        });
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, ReaderFragment> {

        protected GetData(ReaderFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReaderFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ReaderFragment fragment, String s) {
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
            fragment.refreshLayout.finishRefresh();
        }
    }

    /**
     * 获取推荐数据
     */
    private static class GetRecommendData
            extends WeakAsyncTask<String, Void, String, ReaderFragment> {

        protected GetRecommendData(ReaderFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReaderFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ReaderFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeRecommendData(s);
            }
            fragment.refreshing = false;
            fragment.refreshLayout.finishLoadMore();
        }
    }

    /**
     * 分析推荐数据
     *
     * @param s
     */
    private void analyzeRecommendData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    if (isRefresh) {
                        mList_recommend.clear();
                    }
                    List<Article> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject essay = jsonArray.getJSONObject(i);
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
                            mList_recommend.add(article);
                        } else {
                            list.add(article);
                        }
                    }
                    if (isRefresh) {
                        adapter_recommend.notifyDataSetChanged();
                    } else {
                        adapter_recommend.addData(list);
                    }
                    if (ll_recommend.getVisibility() == View.GONE) {
                        ll_recommend.setVisibility(View.VISIBLE);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
                emptyData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void emptyData() {
        isEnd = true;
    }

}
