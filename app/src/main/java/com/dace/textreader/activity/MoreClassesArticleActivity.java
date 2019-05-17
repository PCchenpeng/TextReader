package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.MoreClassesArticleRecyclerViewAdapter;
import com.dace.textreader.bean.MoreArticle;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 更多阅读推荐列表
 */
public class MoreClassesArticleActivity extends BaseActivity implements View.OnClickListener {

    private final String url = HttpUrlPre.HTTP_URL + "/recommendation/more?";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FrameLayout frameLayout;

    private MoreClassesArticleRecyclerViewAdapter adapter;
    private List<MoreArticle> mList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private MoreClassesArticleActivity mContext;

    private long essayId = -1;
    private int essayType = -1;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_classes_article);

        mContext = this;

        essayId = getIntent().getLongExtra("essayId", -1);
        essayType = getIntent().getIntExtra("essayType", -1);

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        adapter.setOnItemClickListener(new MoreClassesArticleRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    turnToArticleDetail(position);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (refreshing) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEnd && !refreshing) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        frameLayout.setOnClickListener(this);
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                linearLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            new GetData(mContext).execute(url + "essayId=" + essayId + "&type=" + essayType +
                    "&studentId=" + NewMainActivity.STUDENT_ID + "&pageNum=" + pageNum);
        }
    }

    /**
     * 跳转到文章详情
     *
     * @param position
     */
    private void turnToArticleDetail(int position) {
//        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
//        intent.putExtra("id", mList.get(position).getId());
//        int type = mList.get(position).getType();
//        intent.putExtra("type", type);
//        startActivityForResult(intent, 0);
    }

    private void initData() {
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        mList.clear();
        pageNum = 1;
        isEnd = false;
        adapter.notifyDataSetChanged();
        new GetData(mContext).execute(url + "essayId=" + essayId + "&type=" + essayType +
                "&studentId=" + NewMainActivity.STUDENT_ID + "&pageNum=" + pageNum);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("推荐文章");

        frameLayout = findViewById(R.id.frame_more_classes_article);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_more_classes_article);
        recyclerView = findViewById(R.id.recycler_view_more_classes_article);
        linearLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MoreClassesArticleRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.frame_more_classes_article:
                break;
        }
    }

    private static class GetData
            extends WeakAsyncTask<String, Integer, String, MoreClassesArticleActivity> {

        protected GetData(MoreClassesArticleActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MoreClassesArticleActivity activity, String[] strings) {
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
        protected void onPostExecute(MoreClassesArticleActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            activity.refreshing = false;
            if (s == null) {
                activity.noSearchResult();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 分析数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    noSearchResult();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject essay = jsonArray.getJSONObject(i);
                        MoreArticle article = new MoreArticle();
                        article.setId(essay.optLong("id", -1));
                        article.setType(essay.optInt("type", -1));
                        article.setTitle(essay.getString("title"));
                        article.setContent(essay.getString("content"));
                        article.setImagePath(essay.getString("image"));
                        if (essay.getString("status").equals("") ||
                                essay.getString("status").equals("null")) {
                            article.setCorrelation("无相似处");
                        } else {
                            if (essay.optInt("status", 0) == 0) {
                                article.setCorrelation("内容相似");
                            } else {
                                article.setCorrelation("结构相似");
                            }
                        }
                        article.setGrade(essay.optInt("grade", 110));
                        article.setPyScore(essay.getString("score"));
                        article.setViews(essay.optInt("pv", 180));
                        mList.add(article);
                    }
                    adapter.notifyDataSetChanged();
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
     * 获取失败
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
                    initData();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            MyToastUtil.showToast(mContext, "获取数据失败");
        }
    }

    /**
     * 没有搜索到内容
     */
    private void noSearchResult() {
        if (isDestroyed()) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("暂无更多推荐文章");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }
}
