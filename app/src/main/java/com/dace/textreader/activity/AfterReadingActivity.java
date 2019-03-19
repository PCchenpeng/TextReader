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
import com.dace.textreader.adapter.AfterReadingRecyclerViewAdapter;
import com.dace.textreader.bean.AfterReading;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 读后感列表
 */
public class AfterReadingActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/essay/feeling?";
    private static final String updateLikeUrl = HttpUrlPre.HTTP_URL + "/personal/feeling_num/update?";

    private RelativeLayout rl_back;  //返回按钮
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private AfterReadingActivity mContext;

    private LinearLayoutManager mLayoutManager;
    private AfterReadingRecyclerViewAdapter adapter;
    private List<AfterReading> mList = new ArrayList<>();

    private int mSelectedPos = -1;

    private long essayId = -1;  //文章ID
    private int essayType = -1;
    private String essayTitle = "";
    private int pageNum = 1;  //页码
    private boolean refreshing = false;
    private boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_reading);

        mContext = this;

        essayId = getIntent().getLongExtra("essayId", -1);
        essayType = getIntent().getIntExtra("essayType", -1);
        essayTitle = getIntent().getStringExtra("essayTitle");

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshing && !isEnd) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new AfterReadingRecyclerViewAdapter.OnAfterReadingItemClick() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    turnToAfterReadingDetail(pos);
                }
            }
        });
        adapter.setOnItemLikeClickListener(new AfterReadingRecyclerViewAdapter.OnAfterReadingLikeItemClick() {
            @Override
            public void onItemClick(int position) {
                if (!refreshing) {
                    if (mList.get(position).isLiked() != 1) {
                        new UpdateLikeData(AfterReadingActivity.this)
                                .execute(updateLikeUrl +
                                        "id=" + mList.get(position).getId() +
                                        "&studentId=" + NewMainActivity.STUDENT_ID +
                                        "&essayId=" + essayId + "&type=" + essayType +
                                        "&title=" + essayTitle);
                        int likerNum = mList.get(position).getLikeNum() + 1;
                        mList.get(position).setLikeNum(likerNum);
                        mList.get(position).setLiked(1);

                        Bundle bundle = new Bundle();
                        bundle.putInt("isLiked", 1);
                        bundle.putInt("likeNum", likerNum);
                        adapter.notifyItemChanged(position, bundle);

                    } else {
                        MyToastUtil.showToast(AfterReadingActivity.this, "已点赞，不能再点");
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
     * 进入读后感详情页
     *
     * @param pos
     */
    private void turnToAfterReadingDetail(int pos) {
        mSelectedPos = pos;
        String id = mList.get(pos).getId();
        Intent intent = new Intent(mContext, AfterReadingDetailActivity.class);
        intent.putExtra("afterReadingId", id);
        intent.putExtra("essayId", essayId);
        intent.putExtra("essayType", essayType);
        intent.putExtra("essayTitle", essayTitle);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                boolean isClickLike = data.getBooleanExtra("clickLikeOrNot", false);
                if (isClickLike && mSelectedPos != -1) {
                    int likeNum = mList.get(mSelectedPos).getLikeNum() + 1;
                    mList.get(mSelectedPos).setLikeNum(likeNum);
                    adapter.notifyItemChanged(mSelectedPos);
                    mSelectedPos = -1;
                }
            }
        }
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            new GetData(AfterReadingActivity.this)
                    .execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                            "&essayId=" + essayId +
                            "&pageNum=" + pageNum +
                            "&pageSize=10");
        }
    }

    /**
     * 刷新数据
     */
    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData(AfterReadingActivity.this)
                .execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId +
                        "&pageNum=" + pageNum +
                        "&pageSize=10");
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("读后感");
        frameLayout = findViewById(R.id.frame_after_reading);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_after_reading);
        recyclerView = findViewById(R.id.recycler_view_after_reading);
        mLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new AfterReadingRecyclerViewAdapter(this, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 处理数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject json = jsonObject.getJSONObject("data");
                JSONArray data = json.getJSONArray("feelings");
                if (data.length() == 0) {
                    noAfterReading();
                } else {
                    for (int i = 0; i < data.length(); i++) {
                        AfterReading afterReading = new AfterReading();
                        JSONObject feeling = data.getJSONObject(i);
                        afterReading.setId(feeling.getString("id"));
                        afterReading.setUserImg(feeling.getString("userimg"));
                        afterReading.setUsername(feeling.getString("username"));
                        afterReading.setLikeNum(feeling.optInt("feelingLikeNum", 0));
                        afterReading.setFeeling(feeling.getString("feeling"));
                        afterReading.setLiked(feeling.optInt("likeOrNot", 0));
                        if (feeling.getString("feelingTime").equals("")
                                || feeling.getString("feelingTime").equals("null")) {
                            afterReading.setDate("2018-01-01 00:00");
                        } else {
                            afterReading.setDate(DateUtil.time2MD(feeling.getString("feelingTime")));
                        }
                        mList.add(afterReading);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                noAfterReading();
            }
        } catch (Exception e) {
            e.printStackTrace();
            noAfterReading();
        }
    }

    /**
     * 暂无读后感
     */
    private void noAfterReading() {
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
            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("暂无读后感");
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
        if (isDestroyed()) {
            return;
        }
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

    private void showTips(String s) {
        MyToastUtil.showToast(mContext, s);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, AfterReadingActivity> {

        protected GetData(AfterReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AfterReadingActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AfterReadingActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            activity.refreshing = false;
            if (s == null) {
                activity.errorConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 点赞
     */
    private static class UpdateLikeData
            extends WeakAsyncTask<String, Integer, String, AfterReadingActivity> {

        protected UpdateLikeData(AfterReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AfterReadingActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AfterReadingActivity activity, String s) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
