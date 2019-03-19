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
import com.dace.textreader.adapter.PointsNewsRecyclerViewAdapter;
import com.dace.textreader.bean.PointsNews;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

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
 * 点赞消息
 */
public class PointsNewsActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/like/query/list";
    private static final String markUrl = HttpUrlPre.HTTP_URL + "/message/view/all";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private FrameLayout frameLayout;

    private PointsNewsActivity mContext;

    private LinearLayoutManager layoutManager;
    private PointsNewsRecyclerViewAdapter adapter;
    private List<PointsNews> mList = new ArrayList<>();

    private String curTime;  //当前时间

    private int pageNum = 1;  //加载数据的页数
    private boolean refreshing = false;  //是否正在加载数据
    private boolean isEnd = false;  //是否已经加载到底部

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_news);

        mContext = this;

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing) {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isEnd && !refreshing) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnPointsNewsItemClick(new PointsNewsRecyclerViewAdapter.OnPointsNewsItemClick() {
            @Override
            public void onClick(int position) {
                if (!refreshing) {
                    if (mList.get(position).getContentType().equals("0")) {
                        turnToEssay(position);
                    } else if (mList.get(position).getContentType().equals("1")) {
                        turnToDetail(position);
                    }
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 前往文章详情
     */
    private void turnToEssay(int position) {
        long id = Long.valueOf(mList.get(position).getId());
        int type = Integer.valueOf(mList.get(position).getType());
        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * 标记消息已读
     */
    private void markHasRead() {
        new MarkHasRead(mContext).execute(markUrl, "like");
    }

    /**
     * 前往作文详情
     */
    private void turnToDetail(int position) {
        String id = mList.get(position).getId();
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", id);
        intent.putExtra("orderNum", "");
        intent.putExtra("area", 0);
        startActivity(intent);
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            curTime = DateUtil.getTodayDateTime();
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        pageNum = 1;
        isEnd = false;
        mList.clear();
        adapter.notifyDataSetChanged();
        curTime = DateUtil.getTodayDateTime();
        new GetData(mContext).execute(url, String.valueOf(pageNum));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("收到的赞");

        frameLayout = findViewById(R.id.frame_points_news);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_points_news);
        recyclerView = findViewById(R.id.recycler_view_points_news);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PointsNewsRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
        }
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
                    noData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        PointsNews pointsNews = new PointsNews();
                        pointsNews.setId(object.getString("likeId"));
                        String updateTime = object.getString("time");
                        String t;
                        if (updateTime.equals("") || updateTime.equals("null")) {
                            t = "2018-01-01 00:00";
                        } else {
                            t = DateUtil.timeYMD(updateTime);
                        }
                        pointsNews.setDuration(DateUtil.getTimeDiff_(t, curTime));
                        pointsNews.setTitle(object.getString("title"));
                        pointsNews.setUserId(object.optLong("studentid", -1));
                        pointsNews.setUsername(object.getString("username"));
                        pointsNews.setUserImg(object.getString("userimg"));
                        pointsNews.setContentType(object.getString("type"));
                        pointsNews.setType(object.getString("essayType"));
                        mList.add(pointsNews);
                    }
                    adapter.notifyDataSetChanged();
                    markHasRead();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                noData();
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 无数据返回
     */
    private void noData() {
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
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_msg, imageView);
            tv_tips.setText("暂无点赞消息哦~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
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
            showTips("获取数据失败");
        }
    }

    /**
     * 显示提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, PointsNewsActivity> {

        protected GetData(PointsNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(PointsNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", strings[1]);
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
        protected void onPostExecute(PointsNewsActivity activity, String s) {
            activity.refreshing = false;
            activity.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 标记已读
     */
    private static class MarkHasRead
            extends WeakAsyncTask<String, Void, String, PointsNewsActivity> {

        protected MarkHasRead(PointsNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(PointsNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", strings[1]);
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
        protected void onPostExecute(PointsNewsActivity activity, String s) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
