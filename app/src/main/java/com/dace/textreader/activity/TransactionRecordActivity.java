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
import com.dace.textreader.adapter.TransactionChildRecyclerViewAdapter;
import com.dace.textreader.bean.TransactionBean;
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
 * 交易记录
 */
public class TransactionRecordActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/bill/query";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private TransactionRecordActivity mContext;

    private LinearLayoutManager mLayoutManager;

    private List<TransactionBean> mList = new ArrayList<>();
    private TransactionChildRecyclerViewAdapter adapter;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);

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
                if (!refreshing && !isEnd) {
                    getMoreData(newState);
                }
            }
        });
        adapter.setOnOrderChildClick(new TransactionChildRecyclerViewAdapter.OnOrderChildClick() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    turnToOrderDetail(pos);
                }
            }
        });
    }

    /**
     * 前往订单详情
     */
    private void turnToOrderDetail(int position) {
        String orderNum = mList.get(position).getId();
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra("orderNum", orderNum);
        mContext.startActivity(intent);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        mList.clear();
        adapter.notifyDataSetChanged();
        isEnd = false;
        pageNum = 1;
        new GetData(mContext).execute(url, String.valueOf(pageNum));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("交易记录");
        frameLayout = findViewById(R.id.frame_transaction_record);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_transaction_record);
        recyclerView = findViewById(R.id.recycler_view_transaction_record);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new TransactionChildRecyclerViewAdapter(mContext, mList);
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
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TransactionBean transactionBean = new TransactionBean();
                        transactionBean.setId(object.getString("orderId"));
                        transactionBean.setContent(object.getString("productName"));
                        transactionBean.setCost(object.getString("discountPrice"));
                        transactionBean.setCategory(object.optInt("category", 4));
                        transactionBean.setPayChannel(object.optInt("payChannel", -1));
                        transactionBean.setStatus(object.optInt("status", -1));
                        String time = object.getString("updateTime");
                        if (time.equals("") || time.equals("null")) {
                            time = "2018-01-01 00:00";
                        } else {
                            time = DateUtil.timeYMD(object.getString("updateTime"));
                        }
                        transactionBean.setTime(time);
                        mList.add(transactionBean);
                    }
                    //通知列表适配器更新数据
                    adapter.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 数据为空
     */
    private void emptyData() {
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
            tv_tips.setText("暂无交易记录");
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
        adapter.notifyDataSetChanged();
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
            extends WeakAsyncTask<String, Void, String, TransactionRecordActivity> {

        protected GetData(TransactionRecordActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(TransactionRecordActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("userId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(TransactionRecordActivity activity, String s) {
            activity.refreshing = false;
            activity.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }
}
