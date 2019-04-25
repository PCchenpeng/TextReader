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
import com.dace.textreader.adapter.SettingsNewsRecyclerViewAdapter;
import com.dace.textreader.bean.SettingsNews;
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
 * 系统消息
 */
public class SystemNewsActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/system/message/query";
    private static final String markUrl = HttpUrlPre.HTTP_URL + "/system/message/view";
    private static final String markAllUrl = HttpUrlPre.HTTP_URL + "/message/view/all";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private TextView tv_all_viewed;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private SystemNewsActivity mContext;

    private LinearLayoutManager layoutManager;
    private List<SettingsNews> mList = new ArrayList<>();
    private SettingsNewsRecyclerViewAdapter adapter;

    private String curTime;  //当前时间

    private boolean refreshing = false;  //是否正在加载数据
    private boolean isEnd = false;  //是否加载到最后
    private int pageNum = 1;  //页码
    private boolean hasNewsUnread = false;  //是否有消息未读

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_news);

        mContext = this;

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
        tv_all_viewed.setOnClickListener(this);
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
        adapter.setOnSettingsNewsItemClickListen(new SettingsNewsRecyclerViewAdapter.OnSettingsNewsItemClick() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    markHasRead(pos);
                    turnToView(pos);
                }
            }
        });
    }

    /**
     * 前往界面前判断消息类型
     *
     * @param pos
     */
    private void turnToView(int pos) {
        SettingsNews settingsNews = mList.get(pos);
        String id = settingsNews.getCargoId();
        int type = settingsNews.getType();
        if (type == 2) {
            turnToMicroLesson(Long.valueOf(id));
        } else if (type == 3) {
            turnToEvents(id);
        }
    }

    /**
     * 前往微课界面
     *
     * @param id
     */
    private void turnToMicroLesson(long id) {
        Intent intent = new Intent(mContext, MicroLessonActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    /**
     * 前往活动界面
     *
     * @param name
     */
    private void turnToEvents(String name) {
        Intent intent = new Intent(this, EventsActivity.class);
        intent.putExtra("pageName", name);
        startActivity(intent);
    }

    /**
     * 标记消息已读
     */
    private void markHasRead(int pos) {
        if (!mList.get(pos).isViewOrNot()) {
            mList.get(pos).setViewOrNot(true);
            adapter.notifyItemChanged(pos);
            int type = mList.get(pos).getType();
            String id = mList.get(pos).getCargoId();
            new MarkHasRead(mContext).execute(markUrl, String.valueOf(type), id);
        }
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
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        refreshing = true;
        isEnd = false;
        curTime = DateUtil.getTodayDateTime();
        new GetData(mContext).execute(url, String.valueOf(pageNum));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("系统消息");

        frameLayout = findViewById(R.id.frame_settings_news);
        tv_all_viewed = findViewById(R.id.tv_all_viewed_system_news);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_settings_news);
        recyclerView = findViewById(R.id.recycler_view_settings_news);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SettingsNewsRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.frame_settings_news:
                //不响应，只是占据点击事件
                break;
            case R.id.tv_all_viewed_system_news:
                markAllViewed();
                break;
        }
    }

    /**
     * 设置全部已读
     */
    private void markAllViewed() {
        new MarkAllViewed(mContext).execute(markAllUrl, "system");
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setViewOrNot(true);
        }
        adapter.notifyDataSetChanged();
        tv_all_viewed.setVisibility(View.GONE);
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
                        SettingsNews settingsNews = new SettingsNews();
                        JSONObject object = array.getJSONObject(i);
                        settingsNews.setId(object.getString("id"));
                        settingsNews.setTitle(object.getString("title"));
                        settingsNews.setContent(object.getString("content"));
                        settingsNews.setType(object.optInt("type", -1));
                        settingsNews.setCargoId(object.getString("cargoId"));
                        String updateTime = object.getString("updateTime");
                        String t;
                        if (updateTime.equals("") || updateTime.equals("null")) {
                            t = "2018-01-01 00:00";
                        } else {
                            t = DateUtil.timeYMD(updateTime);
                        }
                        settingsNews.setTime(DateUtil.getTimeDiff_(t, curTime));
                        if (object.getString("status").equals("null") ||
                                object.getString("status").equals("")) {
                            settingsNews.setViewOrNot(false);
                            hasNewsUnread = true;
                        } else {
                            if (1 == object.optInt("status", -1)) {
                                settingsNews.setViewOrNot(true);
                            } else {
                                hasNewsUnread = true;
                                settingsNews.setViewOrNot(false);
                            }
                        }
                        mList.add(settingsNews);
                    }
                    adapter.notifyDataSetChanged();
                    if (hasNewsUnread) {
                        tv_all_viewed.setVisibility(View.VISIBLE);
                    } else {
                        tv_all_viewed.setVisibility(View.GONE);
                    }
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
            tv_tips.setText("暂无系统消息哦~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
            tv_all_viewed.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据失败
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
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
            tv_all_viewed.setVisibility(View.GONE);
        } else {
            showTips("获取数据失败，请稍后再试");
        }
    }


    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, SystemNewsActivity> {

        protected GetData(SystemNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SystemNewsActivity activity, String[] strings) {
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
        protected void onPostExecute(SystemNewsActivity activity, String s) {
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
            extends WeakAsyncTask<String, Void, String, SystemNewsActivity> {

        protected MarkHasRead(SystemNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SystemNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", strings[1]);
                object.put("messageId", strings[2]);
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
        protected void onPostExecute(SystemNewsActivity activity, String s) {

        }
    }

    /**
     * 标记全部已读
     */
    private static class MarkAllViewed
            extends WeakAsyncTask<String, Void, String, SystemNewsActivity> {

        protected MarkAllViewed(SystemNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SystemNewsActivity activity, String[] strings) {
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
        protected void onPostExecute(SystemNewsActivity activity, String s) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
