package com.dace.textreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.adapter.WritingRecommendAdapter;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.SmartScrollView;

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
 * 作文活动结果展示
 */
public class WritingEventDetailsActivity extends BaseActivity {

    private static final String url_event = HttpUrlPre.HTTP_URL + "/composition/match/detail";
    private static final String url_award = HttpUrlPre.HTTP_URL + "/select/match/winner";
    private static final String url_enter = HttpUrlPre.HTTP_URL + "/select/match/other";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_search;
    private FrameLayout frameLayout;
    private SmartScrollView scrollView;
    private ImageView iv_event;
    private RelativeLayout rl_status_event;
    private TextView tv_status_event;
    private TextView tv_title_event;
    private TextView tv_content_event;
    private TextView tv_time_event;
    private LinearLayout ll_award;
    private RecyclerView recyclerView_award;
    private LinearLayout ll_enter;
    private RecyclerView recyclerView_enter;

    private WritingEventDetailsActivity mContext;

    private LinearLayoutManager layoutManager_award;
    private List<WritingBean> mList_award = new ArrayList<>();
    private WritingRecommendAdapter adapter_award;

    private LinearLayoutManager layoutManager_enter;
    private List<WritingBean> mList_enter = new ArrayList<>();
    private WritingRecommendAdapter adapter_enter;

    private String taskId = "";
    private String image_event;
    private int status;
    private String title_event;
    private String content_event;
    private String time_event;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_event_details);

        mContext = this;

        taskId = getIntent().getStringExtra("taskId");

        initView();
        initData();
        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_search.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToWritingSearch();
            }
        });
        scrollView.setScanScrollChangedListener(new SmartScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if (!refreshing && !isEnd && scrollView.isScrolledToBottom()) {
                    getMoreData();
                }
            }

            @Override
            public void onScrolledToTop() {

            }
        });
        adapter_award.setOnWritingRecommendItemClick(new WritingRecommendAdapter.OnWritingRecommendItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView_award.getChildAdapterPosition(view);
                String id = mList_award.get(pos).getId();
                turnToWritingH5(id);
                addAwardWritingViews(pos);

            }
        });
        adapter_enter.setOnWritingRecommendItemClick(new WritingRecommendAdapter.OnWritingRecommendItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView_enter.getChildAdapterPosition(view);
                String id = mList_enter.get(pos).getId();
                turnToWritingH5(id);
                addEnterWritingViews(pos);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 为获奖作文增加阅读数
     */
    private void addAwardWritingViews(int position) {
        int views = Integer.valueOf(mList_award.get(position).getViews()) + 1;
        mList_award.get(position).setViews(String.valueOf(views));
        adapter_award.notifyItemChanged(position);
    }

    /**
     * 为参赛作文增加阅读数
     */
    private void addEnterWritingViews(int position) {
        int views = Integer.valueOf(mList_enter.get(position).getViews()) + 1;
        mList_enter.get(position).setViews(String.valueOf(views));
        adapter_enter.notifyItemChanged(position);
    }

    /**
     * 前往作文搜索界面
     */
    private void turnToWritingSearch() {
        Intent intent = new Intent(mContext, WritingSearchActivity.class);
        intent.putExtra("searchType", "");
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }

    /**
     * 获取更多参赛列表数据
     */
    private void getMoreData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetEnterData(mContext).execute(url_enter, taskId, String.valueOf(pageNum));
    }

    /**
     * 前往作文详情界面
     */
    private void turnToWritingH5(String id) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", id);
        intent.putExtra("orderNum", "");
        intent.putExtra("area", 0);
        startActivity(intent);
    }

    private void initData() {
        showLoadingView();
        pageNum = 1;
        refreshing = true;
        mList_award.clear();
        adapter_award.notifyDataSetChanged();
        mList_enter.clear();
        adapter_enter.notifyDataSetChanged();
        new GetCompetitionData(mContext).execute(url_event, taskId);
        new GetAwardData(mContext).execute(url_award, taskId);
        new GetEnterData(mContext).execute(url_enter, taskId, String.valueOf(pageNum));
    }

    /**
     * 显示加载等待视图
     */
    private void showLoadingView() {
        if (isDestroyed()) {
            return;
        }
        frameLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
        frameLayout.removeAllViews();
        frameLayout.addView(view);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("活动详情");

        rl_search = findViewById(R.id.rl_search_writing_event_detail);
        frameLayout = findViewById(R.id.frame_writing_event_detail);
        scrollView = findViewById(R.id.scroll_view_writing_event_detail);
        iv_event = findViewById(R.id.iv_writing_event_detail);
        rl_status_event = findViewById(R.id.rl_status_writing_event_detail);
        tv_status_event = findViewById(R.id.tv_status_writing_event_detail);
        tv_title_event = findViewById(R.id.tv_title_writing_event_detail);
        tv_content_event = findViewById(R.id.tv_content_writing_event_detail);
        tv_time_event = findViewById(R.id.tv_time_writing_event_detail);
        ll_award = findViewById(R.id.ll_award_writing_event_detail);
        recyclerView_award = findViewById(R.id.recycler_view_award_writing_event_detail);
        recyclerView_award.setNestedScrollingEnabled(false);
        ll_enter = findViewById(R.id.ll_enter_writing_event_detail);
        recyclerView_enter = findViewById(R.id.recycler_view_enter_writing_event_detail);
        recyclerView_enter.setNestedScrollingEnabled(false);

        //得到AssetManager
        AssetManager mgr = getAssets();
        //根据路径得到Typeface
        Typeface score = Typeface.createFromAsset(mgr, "css/GB2312.ttf");

        layoutManager_award = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_award.setLayoutManager(layoutManager_award);
        adapter_award = new WritingRecommendAdapter(mContext, mList_award, score);
        recyclerView_award.setAdapter(adapter_award);

        layoutManager_enter = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_enter.setLayoutManager(layoutManager_enter);
        adapter_enter = new WritingRecommendAdapter(mContext, mList_enter, score);
        recyclerView_enter.setAdapter(adapter_enter);
    }

    private void analyzeCompetitionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                taskId = object.getString("id");
                title_event = object.getString("title");
                status = object.optInt("status", -1);
                if (object.getString("startTime").equals("") ||
                        object.getString("startTime").equals("null")) {
                    time_event = "2018-01-01 00:00";
                } else {
                    time_event = DateUtil.timeYMD(object.getString("startTime"));
                }
                image_event = object.getString("image");
                content_event = object.getString("content");
                updateUi();
            } else {
                showErrorContent(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorContent(false);
        }
    }

    /**
     * 更新UI
     */
    private void updateUi() {
        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_state_404);
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .load(image_event)
                    .apply(options)
                    .into(iv_event);
        }
        rl_status_event.setSelected(false);
        tv_status_event.setText("已结束");
//        if (status == 0) {
//            rl_status_event.setSelected(false);
//            tv_status_event.setText("已结束");
//        } else {
//            rl_status_event.setVisibility(View.GONE);
//        }
        tv_title_event.setText(title_event);
        tv_content_event.setText(content_event);
        tv_time_event.setText(time_event);
        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 显示获取数据失败页面
     */
    private void showErrorContent(boolean noConnect) {
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        if (noConnect) {
            tv_reload.setText("无网络，请连接网络后重试");
        }
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
    }

    /**
     * 分析获取的作文获奖列表数据
     */
    private void analyzeAwardData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorAwardData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        WritingBean writingBean = new WritingBean();
                        JSONObject object = array.getJSONObject(i);
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setComment(object.getString("comment"));
                        writingBean.setType(object.optInt("type", -1));
                        writingBean.setStatus(object.optInt("status", -1));
                        writingBean.setMaterialId(object.getString("materialId"));
//                        if (object.getString("mark").equals("")
//                                || object.getString("mark").equals("null")) {
                        writingBean.setMark(-1);
//                        } else {
//                            writingBean.setMark(object.getInt("mark"));
//                        }
                        writingBean.setUserId(object.getString("studentId"));
                        writingBean.setUsername(object.getString("username"));
                        writingBean.setUserGrade(DataUtil.gradeCode2Chinese(object.optInt("gradeid", 110)));
                        writingBean.setUserImg(object.getString("userimg"));
                        String pv = object.getString("pv");
                        if (pv.equals("") || pv.equals("null")) {
                            writingBean.setViews("0");
                        } else {
                            writingBean.setViews(pv);
                        }
                        writingBean.setPrize(object.getString("prize"));
                        writingBean.setTaskId(object.getString("taskId"));
                        writingBean.setTaskName(object.getString("matchName"));
                        mList_award.add(writingBean);
                    }
                    adapter_award.notifyDataSetChanged();
                }
            } else {
                errorAwardData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorAwardData();
        }
    }

    /**
     * 获取作文获奖列表失败
     */
    private void errorAwardData() {
        if (mList_award.size() == 0) {
            ll_award.setVisibility(View.GONE);
        } else {
            adapter_award.notifyDataSetChanged();
        }
    }

    /**
     * 分析获取的作文参赛列表数据
     */
    private void analyzeEnterData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorEnterData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        WritingBean writingBean = new WritingBean();
                        JSONObject object = array.getJSONObject(i);
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setComment(object.getString("comment"));
                        writingBean.setType(object.optInt("type", 5));
                        writingBean.setStatus(object.optInt("status", -1));
                        writingBean.setMaterialId(object.getString("materialId"));
//                        if (object.getString("mark").equals("")
//                                || object.getString("mark").equals("null")) {
                        writingBean.setMark(-1);
//                        } else {
//                            writingBean.setMark(object.getInt("mark"));
//                        }
                        writingBean.setUserId(object.getString("studentId"));
                        writingBean.setUsername(object.getString("username"));
                        writingBean.setUserGrade(DataUtil.gradeCode2Chinese(object.optInt("gradeid", 110)));
                        writingBean.setUserImg(object.getString("userimg"));
                        String pv = object.getString("pv");
                        if (pv.equals("") || pv.equals("null")) {
                            writingBean.setViews("0");
                        } else {
                            writingBean.setViews(pv);
                        }
                        writingBean.setPrize(object.getString("prize"));
                        writingBean.setTaskId(object.getString("taskId"));
                        writingBean.setTaskName(object.getString("matchName"));
                        mList_enter.add(writingBean);
                    }
                    adapter_enter.notifyDataSetChanged();
                    scrollView.setScrolledToBottom(false);
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                isEnd = true;
                errorEnterData();
            } else {
                errorEnterData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorEnterData();
        }
    }

    /**
     * 获取作文推荐列表失败
     */
    private void errorEnterData() {
        if (mList_enter.size() == 0) {
            ll_enter.setVisibility(View.GONE);
        } else {
            adapter_enter.notifyDataSetChanged();
            scrollView.setScrolledToBottom(false);
        }
    }

    /**
     * 获取比赛、活动数据
     */
    private static class GetCompetitionData
            extends WeakAsyncTask<String, Void, String, WritingEventDetailsActivity> {

        protected GetCompetitionData(WritingEventDetailsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingEventDetailsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("taskId", strings[1]);
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
        protected void onPostExecute(WritingEventDetailsActivity activity, String s) {
            if (s == null) {
                activity.showErrorContent(true);
            } else {
                activity.analyzeCompetitionData(s);
            }
        }
    }

    /**
     * 获取获奖作文列表
     */
    private static class GetAwardData
            extends WeakAsyncTask<String, Void, String, WritingEventDetailsActivity> {

        protected GetAwardData(WritingEventDetailsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingEventDetailsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("taskId", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
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
        protected void onPostExecute(WritingEventDetailsActivity activity, String s) {
            if (s == null) {
                activity.errorAwardData();
            } else {
                activity.analyzeAwardData(s);
            }
        }
    }

    /**
     * 获取参赛作文列表
     */
    private static class GetEnterData
            extends WeakAsyncTask<String, Void, String, WritingEventDetailsActivity> {

        protected GetEnterData(WritingEventDetailsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingEventDetailsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("taskId", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
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
        protected void onPostExecute(WritingEventDetailsActivity activity, String s) {
            if (s == null) {
                activity.errorEnterData();
            } else {
                activity.analyzeEnterData(s);
            }
            activity.refreshing = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
