package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.WritingNewsRecyclerViewAdapter;
import com.dace.textreader.bean.WritingNewsBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
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
 * 作文消息
 */
public class WritingNewsActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/writing/corrected/query";
    private static final String markUrl = HttpUrlPre.HTTP_URL + "/writing/corrected/message/view";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private RelativeLayout rl_all_viewed;
    private TextView tv_all_viewed;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private WritingNewsActivity mContext;

    private LinearLayoutManager layoutManager;

    private List<WritingNewsBean> mList = new ArrayList<>();
    private WritingNewsRecyclerViewAdapter adapter;

    private boolean refreshing = false;  //是否正在加载数据
    private boolean isEnd = false;  //是否加载到最后
    private int pageNum = 1;  //页码

    private boolean hasNewsUnread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_news);

        mContext = this;

        initView();
        initData();
        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_all_viewed.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
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
        adapter.setOnWritingNewsItemClickListen(new WritingNewsRecyclerViewAdapter.OnWritingNewsItemClick() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    markViewed(pos);
                    WritingNewsBean writingBean = mList.get(pos);
                    Intent intent = new Intent(mContext, CompositionDetailActivity.class);
                    intent.putExtra("writingId", writingBean.getId());
                    intent.putExtra("orderNum", writingBean.getOrderNum());
                    intent.putExtra("area", writingBean.getType());
                    startActivity(intent);
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
     * 标记作文已读
     *
     * @param position
     */
    private void markViewed(int position) {
        if (mList.get(position).getIsViewed() != 1) {
            mList.get(position).setIsViewed(1);
            adapter.notifyItemChanged(position);
            String id = mList.get(position).getId();
            int type = mList.get(position).getType();
            new MarkViewed(mContext).execute(markUrl, id, String.valueOf(type));
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
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
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

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("作文通知");

        frameLayout = findViewById(R.id.frame_writing_news);
        rl_all_viewed = findViewById(R.id.rl_all_viewed_writing_news);
        tv_all_viewed = findViewById(R.id.tv_all_viewed_writing_news);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_writing_news);
        recyclerView = findViewById(R.id.recycler_view_writing_news);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WritingNewsRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.tv_all_viewed_writing_news:
                markAllViewed();
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
                        JSONObject object = array.getJSONObject(i);
                        WritingNewsBean writingBean = new WritingNewsBean();
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setType(object.optInt("type", 1));
                        writingBean.setOrderNum(object.getString("orderNum"));
                        writingBean.setStatus(object.optInt("studentStatus", -1));
                        String time = object.getString("updateTime");
                        if (time.equals("") || time.equals("null")) {
                            writingBean.setTime("2018-01-01");
                        } else {
                            writingBean.setTime(DateUtil.time2Format(time));
                        }
                        writingBean.setIsViewed(object.optInt("isView", -1));
                        mList.add(writingBean);
                        if (writingBean.getIsViewed() != 1) {
                            hasNewsUnread = true;
                        }
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
            tv_tips.setText("暂无作文通知哦~");
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
            extends WeakAsyncTask<String, Void, String, WritingNewsActivity> {

        protected GetData(WritingNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingNewsActivity activity, String[] strings) {
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
        protected void onPostExecute(WritingNewsActivity activity, String s) {
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
            extends WeakAsyncTask<String, Void, String, WritingNewsActivity> {

        protected MarkViewed(WritingNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("compositionId", strings[1]);
                object.put("type", strings[2]);
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
        protected void onPostExecute(WritingNewsActivity activity, String s) {

        }
    }

    /**
     * 标记全部已读
     */
    private static class MarkAllViewed
            extends WeakAsyncTask<String, Void, String, WritingNewsActivity> {

        protected MarkAllViewed(WritingNewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingNewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("flag", "全部");
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
        protected void onPostExecute(WritingNewsActivity activity, String s) {

        }
    }

}
