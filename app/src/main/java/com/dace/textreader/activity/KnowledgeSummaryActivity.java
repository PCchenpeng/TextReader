package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.KnowledgeSummaryAdapter;
import com.dace.textreader.bean.KnowledgeBean;
import com.dace.textreader.bean.KnowledgeChildBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 知识汇总界面
 */
public class KnowledgeSummaryActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL_ + "/knowledge/point/all";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private KnowledgeSummaryActivity mContext;

    private List<KnowledgeBean> mList = new ArrayList<>();
    private KnowledgeSummaryAdapter adapter;

    private boolean refreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_summary);

        mContext = this;

        initView();
        initEvents();

        refreshLayout.autoRefresh();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        adapter.setOnItemClickListen(new KnowledgeSummaryAdapter.OnItemClickListen() {
            @Override
            public void onClick(int position, int childPosition) {
                turnToDetail(position, childPosition);
            }
        });
    }

    /**
     * 前往详情
     *
     * @param position
     * @param childPosition
     */
    private void turnToDetail(int position, int childPosition) {
        if (position == -1 || position > mList.size()) {
            return;
        }
        String title = mList.get(position).getTitle();
        if (childPosition == -1 || childPosition > mList.get(position).getList().size()) {
            return;
        }
        long id = mList.get(position).getList().get(childPosition).getKnowledgeId();
        Intent intent = new Intent(mContext, KnowledgeDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void initData() {
        if (refreshing) {
            return;
        }
        refreshing = true;
        mList.clear();
        new GetData(mContext).execute(url);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("知识汇总");

        frameLayout = findViewById(R.id.frame_knowledge_summary);

        refreshLayout = findViewById(R.id.smart_refresh_knowledge_summary);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));

        recyclerView = findViewById(R.id.rv_knowledge_summary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new KnowledgeSummaryAdapter(mContext, mList);
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    KnowledgeBean knowledgeBean = new KnowledgeBean();
                    knowledgeBean.setTitle(object.getString("category"));

                    JSONArray array = object.getJSONArray("list");
                    List<KnowledgeChildBean> list = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject json = array.getJSONObject(j);
                        KnowledgeChildBean bean = new KnowledgeChildBean();
                        bean.setKnowledgeId(json.optLong("id", -1));
                        bean.setKnowledgeIndexId(json.getString("indexId"));
                        bean.setTitle(json.getString("title"));
                        bean.setDescription(json.getString("description"));
                        bean.setCategory(json.getString("category"));
                        bean.setContent(json.getString("content"));
                        bean.setContents(json.getString("contents"));
                        list.add(bean);
                    }
                    knowledgeBean.setList(list);

                    mList.add(knowledgeBean);
                }

                //处理完数据，更新ui
                adapter.notifyDataSetChanged();

            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 获取内容失败
     */
    private void noConnect() {
        if (mContext == null) {
            return;
        }
        if (mContext.isFinishing()) {
            return;
        }
        if (mList.size() != 0) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                refreshLayout.autoRefresh();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, KnowledgeSummaryActivity> {

        protected GetData(KnowledgeSummaryActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(KnowledgeSummaryActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(KnowledgeSummaryActivity activity, String s) {
            activity.refreshLayout.finishRefresh();
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
            activity.refreshing = false;
        }
    }

}
