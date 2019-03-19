package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.MaterialRecyclerViewAdapter;
import com.dace.textreader.bean.MaterialBean;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
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
import okhttp3.Response;

/**
 * 素材列表
 */
public class MaterialListActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/material?";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SmartRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private MaterialListActivity mContext;

    private LinearLayoutManager mLayoutManager;
    private List<MaterialBean> mList = new ArrayList<>();
    private MaterialRecyclerViewAdapter adapter;

    private boolean refreshing = false;  //是否正在刷新
    private boolean isEnd = false;

    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);

        mContext = this;

        initView();
        swipeRefreshLayout.autoRefresh();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing) {
                    swipeRefreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEnd && mList.size() != 0) {
                    getMoreData();
                } else {
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });
        adapter.setOnMaterialItemDeleteClick(new MaterialRecyclerViewAdapter.OnMaterialItemDeleteClick() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    turnToMaterialDetail(pos);
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
     * 查看素材详情
     *
     * @param pos
     */
    private void turnToMaterialDetail(int pos) {
        MaterialBean materialBean = mList.get(pos);
        Intent intent = new Intent(mContext, MaterialDetailActivity.class);
        intent.putExtra("materialId", materialBean.getId());
        intent.putExtra("essayTitle", materialBean.getTitle());
        startActivity(intent);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        pageNum++;
        refreshing = true;
        new GetData(mContext).execute(url +
                "studentId=" + NewMainActivity.STUDENT_ID + "&pageNum=" + pageNum + "&pageSize=10");
    }

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            pageNum = 1;
            mList.clear();
            isEnd = false;
            adapter.notifyDataSetChanged();
            new GetData(mContext).execute(url +
                    "studentId=" + NewMainActivity.STUDENT_ID + "&pageNum=" + pageNum + "&pageSize=10");
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("学以致用");
        frameLayout = findViewById(R.id.frame_material_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_material_list);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = findViewById(R.id.recycler_view_material_list);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MaterialRecyclerViewAdapter(mContext, mList);
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
                        MaterialBean materialBean = new MaterialBean();
                        JSONObject object = jsonArray.getJSONObject(i);
                        materialBean.setId(object.getString("materialid"));
                        materialBean.setEssayId(object.optLong("essayid", -1L));
                        materialBean.setEssayType(object.optInt("type", -1));
                        materialBean.setTitle(object.getString("title"));
                        materialBean.setImage(object.getString("image"));
                        if (object.getString("score").equals("") ||
                                object.getString("score").equals("null")) {
                            materialBean.setScore("0");
                        } else {
                            materialBean.setScore(object.getString("score"));
                        }
                        if (object.getString("updated").equals("")
                                || object.getString("updated").equals("null")) {
                            materialBean.setTime("2018-01-01 00:00");
                        } else {
                            materialBean.setTime(DateUtil.time2YMD(object.getString("updated")));
                        }
                        mList.add(materialBean);
                    }
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
     * 获取数据为空
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
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_write, imageView);
            String text = "暂无笔记~\n可在阅读文章中添加摘抄/生词/读后感/想法";
            tv_tips.setText(text);
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
                    frameLayout.removeAllViews();
                    swipeRefreshLayout.autoRefresh();
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
    private class GetData
            extends WeakAsyncTask<String, Void, String, MaterialListActivity> {

        protected GetData(MaterialListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MaterialListActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MaterialListActivity activity, String s) {
            activity.refreshing = false;
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
            activity.swipeRefreshLayout.finishRefresh();
            activity.swipeRefreshLayout.finishLoadMore();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
