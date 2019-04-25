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
import com.dace.textreader.adapter.MyselfNewsAdapter;
import com.dace.textreader.bean.MyselfNewsBean;
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
 * 我的消息列表
 */
public class MyselfNewsActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/my/message";
    private static final String markUrl = HttpUrlPre.HTTP_URL + "/my/message/view";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private List<MyselfNewsBean> mList = new ArrayList<>();
    private MyselfNewsAdapter adapter;

    private MyselfNewsActivity mContext;

    private boolean refreshing = false;  //是否正在加载数据
    private boolean isEnd = false;  //是否加载到最后
    private int pageNum = 1;  //页码

    private boolean hasNewsUnread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_news);

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
        adapter.setOnItemClickListen(new MyselfNewsAdapter.OnMyselfNewsItemClick() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    int type = mList.get(pos).getType();
                    if (type == 0 && mList.get(pos).getStatus() == 1) {
                        turnToTeacherList();
                    } else if (type == 1) {
                        turnToCard();
                    } else if (type == 2) {
                        turnToCoupon();
                    } else if (type == 3) {
                        turnToWallet();
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
     * 前往卡包
     */
    private void turnToCard() {
        startActivity(new Intent(mContext, CardActivity.class));
    }

    /**
     * 前往优惠券
     */
    private void turnToCoupon() {
        startActivity(new Intent(mContext, CouponActivity.class));
    }

    /**
     * 前往钱包
     */
    private void turnToWallet() {
        startActivity(new Intent(mContext, WalletActivity.class));
    }

    /**
     * 跳转到老师列表
     */
    private void turnToTeacherList() {
        Intent intent = new Intent(mContext, WritingWorkActivity.class);
        intent.putExtra("isSubmit", false);
        startActivity(intent);
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData(mContext).execute(url, String.valueOf(pageNum));
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            refreshing = true;
            pageNum++;
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("我的消息");

        frameLayout = findViewById(R.id.frame_myself_news);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_myself_news);
        recyclerView = findViewById(R.id.recycler_view_myself_news);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyselfNewsAdapter(mContext, mList);
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
     * 设置全部已读
     */
    private void markAllViewed() {
        new MarkAllViewed(mContext).execute(markUrl);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setIsViewed(1);
        }
        adapter.notifyDataSetChanged();
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
                        MyselfNewsBean newsBean = new MyselfNewsBean();
                        newsBean.setId(object.optLong("id", -1L));
                        newsBean.setTitle(object.getString("title"));
                        newsBean.setType(object.optInt("type", -1));
                        newsBean.setStatus(object.optInt("status", -1));
                        String time = object.getString("time");
                        if (time.equals("") || time.equals("null")) {
                            newsBean.setTime("2018-01-01");
                        } else {
                            newsBean.setTime(DateUtil.time2Format(time));
                        }
                        newsBean.setIsViewed(object.optInt("isView", -1));
                        mList.add(newsBean);
                        if (newsBean.getIsViewed() != 1) {
                            hasNewsUnread = true;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (hasNewsUnread) {
                        markAllViewed();
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
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_msg, imageView);
            tv_tips.setText("暂无我的消息哦~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
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
        } else {
            showTips("获取数据失败，请稍后再试");
        }
    }


    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, MyselfNewsActivity> {

        protected GetData(MyselfNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MyselfNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", Integer.valueOf(strings[1]));
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
        protected void onPostExecute(MyselfNewsActivity activity, String s) {
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
     * 标记作文已读
     */
    private static class MarkViewed
            extends WeakAsyncTask<String, Void, String, MyselfNewsActivity> {

        protected MarkViewed(MyselfNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MyselfNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("status", 0);
                object.put("id", strings[1]);
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
        protected void onPostExecute(MyselfNewsActivity activity, String s) {

        }
    }

    /**
     * 标记全部已读
     */
    private static class MarkAllViewed
            extends WeakAsyncTask<String, Void, String, MyselfNewsActivity> {

        protected MarkAllViewed(MyselfNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MyselfNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("status", "1");
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
        protected void onPostExecute(MyselfNewsActivity activity, String s) {

        }
    }
}
