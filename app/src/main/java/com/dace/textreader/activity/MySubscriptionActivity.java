package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.KnowledgeSummaryAdapter;
import com.dace.textreader.adapter.MySubscriptionAdapter;
import com.dace.textreader.adapter.SubscriptionChildAdapter;
import com.dace.textreader.bean.KnowledgeBean;
import com.dace.textreader.bean.KnowledgeChildBean;
import com.dace.textreader.bean.SubscriptionBean;
import com.dace.textreader.bean.SubscriptionChildBean;
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
public class MySubscriptionActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL_ + "/album/subscription/query";
    private static final String url_unsubscribe= HttpUrlPre.HTTP_URL_ + "/album/unsubscribe";
    private static final String url_cancel = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private TextView tv_right;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private MySubscriptionActivity mContext;

    private List<SubscriptionBean> mList = new ArrayList<>();
    private MySubscriptionAdapter adapter;

    private boolean refreshing = false;
    public boolean firstGetData = true;
    public boolean isEditting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscription);

        mContext = this;


        initView();
        initEvents();

    }


    @Override
    protected void onResume() {
        super.onResume();
        //回来需要刷新一次数据
        if (firstGetData) {
            refreshLayout.autoRefresh();
        } else {
            initData();
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditting){
                    tv_right.setText("编辑");
                } else {
                    tv_right.setText("完成");
                }
                for (int i = 0; i < adapter.mListChildAdapter.size();i++){
                    adapter.mListChildAdapter.get(i).notifyDataSetChanged();
                }
                isEditting = !isEditting;
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        adapter.setOnItemClickListen(new MySubscriptionAdapter.OnItemClickListen() {
            @Override
            public void onClick(int position,int type, int childPosition) {
                if (type == SubscriptionChildAdapter.TYPE_CONTENT){
                    //进入编辑状态，点击两个区域都删除
                    if (isEditting) {
                        deleteSubscription(position, childPosition);
                    } else {
                        turnToDetail(position, childPosition);
                    }
                } else if (type == SubscriptionChildAdapter.TYPE_DELETE){
                    deleteSubscription(position, childPosition);
                }
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
        if (childPosition == -1 || childPosition > mList.get(position).getRetList().size()) {
            return;
        }
        Intent intent = null;
        if (mList.get(position).getRetType().equals("专辑")){
            intent = new Intent(mContext, ReaderTabAlbumDetailActivity.class);
            intent.putExtra("format",mList.get(position).getRetList().get(childPosition).getFormat());
            intent.putExtra("sentenceNum",mList.get(position).getRetList().get(childPosition).getSentenceNum());
        } else if (mList.get(position).getRetType().equals("书")) {
            intent = new Intent(mContext, KnowledgeDetailActivity.class);
            intent.putExtra("id",mList.get(position).getRetList().get(childPosition).getId());
            intent.putExtra("title",mList.get(position).getRetList().get(childPosition).getTitle());
        } else if (mList.get(position).getRetType().equals("创作者")) {
            intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId",Long.parseLong(mList.get(position).getRetList().get(childPosition).getAuthorId()));
        } else if (mList.get(position).getRetType().equals("作者")){
            intent = new Intent(mContext, AuthorDetailActivity.class);
            intent.putExtra("authorId",mList.get(position).getRetList().get(childPosition).getAuthorId());
        }
        startActivity(intent);
    }
    /**
     * 删除订阅项
     *
     * @param position
     * @param childPosition
     */
    private void deleteSubscription(int position, int childPosition) {
        if (position == -1 || position > mList.size()) {
            return;
        }
        if (childPosition == -1 || childPosition > mList.get(position).getRetList().size()) {
            return;
        }
        Log.d("111","position " + position + " childPosition " + childPosition);
        String albumId = "";
        String type = "";
        String followingId = "";
        String followerId = "";
        if (mList.get(position).getRetType().equals("专辑")){
            type = "0";
            albumId = mList.get(position).getRetList().get(childPosition).getId() + "";
            new DeleteSubscription(mContext).execute(url_unsubscribe,String.valueOf(NewMainActivity.STUDENT_ID), albumId,type);
        } else if (mList.get(position).getRetType().equals("书")) {
            type = "1";
//            albumId = mList.get(position).getRetList().get(childPosition).getId() + "";
        } else if (mList.get(position).getRetType().equals("创作者")){
            followingId = mList.get(position).getRetList().get(childPosition).getAuthorId() + "";
            followerId = String.valueOf(NewMainActivity.STUDENT_ID);
            new DeleteFollowRelation(mContext).execute(url_cancel,followingId,followerId);
        } else if (mList.get(position).getRetType().equals("作者")){
            type = "2";
            albumId = mList.get(position).getRetList().get(childPosition).getAuthorId() + "";
            new DeleteSubscription(mContext).execute(url_unsubscribe,String.valueOf(NewMainActivity.STUDENT_ID), albumId,type);
        }

    }

    private void initData() {
        if (refreshing) {
            return;
        }
        refreshing = true;
        mList.clear();
        Log.d("111","STUDENT_ID " + NewMainActivity.STUDENT_ID);
        new GetData(mContext).execute(url,String.valueOf(NewMainActivity.STUDENT_ID), "200","200");
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_right = findViewById(R.id.tv_top_right_layout);
        tv_title.setText("我的订阅");
        tv_right.setText("编辑");

        frameLayout = findViewById(R.id.frame_knowledge_summary);

        refreshLayout = findViewById(R.id.smart_refresh_knowledge_summary);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));

        recyclerView = findViewById(R.id.rv_knowledge_summary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MySubscriptionAdapter(mContext, mList);
        adapter.setHasStableIds (true);
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false); // 取消动画效果
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
                Log.d("111","jsonArray " + jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SubscriptionBean subscriptionBean = new SubscriptionBean();
                    subscriptionBean.setRetType(object.optString("retType"));

                    JSONArray array = object.getJSONArray("retList");
                    List<SubscriptionChildBean> list = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject json = array.getJSONObject(j);
                        SubscriptionChildBean bean = new SubscriptionChildBean();
                        bean.setId(json.optLong("id", -1));
                        bean.setTitle(json.optString("title"));
                        bean.setCover(json.optString("cover"));
                        bean.setTime(json.optString("time"));
                        bean.setStatus(json.optString("status"));
                        bean.setPv(json.optString("pv"));
                        bean.setCollectNum(json.optString("collectNum"));
                        bean.setShareNum(json.optString("shareNum"));
                        bean.setFormat(json.optInt("format"));
                        bean.setStartLevel(json.optString("startLevel"));
                        bean.setStopLevel(json.optString("stopLevel"));
                        bean.setCategory(json.optString("category"));
                        bean.setParentId(json.optString("parentId"));
                        bean.setSentenceNum(json.optString("sentenceNum"));
                        bean.setSearchId(json.optString("searchId"));
                        bean.setAlbumSequence(json.optString("albumSequence"));
                        bean.setSubIntroduction(json.optString("subIntroduction"));
                        bean.setAuthorId(json.optString("authorId"));
                        bean.setAuthor(json.optString("author"));
                        bean.setSupply(json.optString("supply"));
                        bean.setPlaformIndexId(json.optString("plaformIndexId"));
                        bean.setIsMap(json.optString("isMap"));
                        bean.setIntroduction(json.optString("introduction"));
                        bean.setFollow(json.optString("follow"));
                        list.add(bean);
                    }
                    subscriptionBean.setRetList(list);

                    mList.add(subscriptionBean);
                }

                //处理完数据，更新ui
                Log.d("111","notifyDataSetChanged");
                adapter.notifyDataSetChanged();

            } else {
                noConnect(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect(s);
        }
    }

    /**
     * 获取内容失败
     */
    private void noConnect(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

        if (mContext == null) {
            return;
        }
        if (mContext.isFinishing()) {
            return;
        }
        if (mList.size() != 0) {
            return;
        }
        View errorView = null;

        if (400 == jsonObject.optInt("status", -1)) {
            errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_empty_collect_layout, null);
            TextView tv_empty = errorView.findViewById(R.id.tv_tips_list_empty);
            tv_empty.setText("订阅为空哦");
        } else {
            errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    refreshLayout.autoRefresh();
                }
            });
        }
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据
     */
    private static class GetData extends WeakAsyncTask<String, Void, String, MySubscriptionActivity> {

        protected GetData(MySubscriptionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MySubscriptionActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("width", strings[2]);
                object.put("height", strings[3]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.d("111","e " + e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MySubscriptionActivity activity, String s) {
            activity.refreshLayout.finishRefresh();
            Log.d("111","s " + s);
            if (s == null) {
                activity.noConnect(s);
            } else {
                activity.analyzeData(s);
            }
            activity.firstGetData = false;
            activity.refreshing = false;
        }
    }

    /**
     * 取消关注作者或者专辑
     */
    private static class DeleteSubscription
            extends WeakAsyncTask<String, Void, String, MySubscriptionActivity> {

        protected DeleteSubscription(MySubscriptionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MySubscriptionActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("albumId", strings[2]);
                object.put("type", strings[3]);
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
        protected void onPostExecute(MySubscriptionActivity activity, String s) {
            if (s == null) {
                activity.noConnect(s);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        activity.initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    /**
     * 取消关注创作者
     */
    private static class DeleteFollowRelation
            extends WeakAsyncTask<String, Void, String, MySubscriptionActivity> {

        protected DeleteFollowRelation(MySubscriptionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MySubscriptionActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("followingId", strings[1]);
                object.put("followerId", strings[2]);
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
        protected void onPostExecute(MySubscriptionActivity activity, String s) {
            if (s == null) {
                activity.noConnect(s);
            } else {
                try {
                    Log.d("111","s " + s);
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        activity.initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
