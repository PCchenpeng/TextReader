package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.dace.textreader.adapter.HomeRecommendationAdapter;
import com.dace.textreader.adapter.WritingGradeFilterAdapter;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.bean.LevelBean;
import com.dace.textreader.listen.OnUserInfoClickListen;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 优秀作文
 */
public class WritingSortListActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/writing/recommend";
    private static final String url_grade = HttpUrlPre.HTTP_URL + "/composition/grade/list";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_search;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private RelativeLayout rl_grade_filter;
    private ImageView iv_grade_filter;
    private RecyclerView recyclerView_grade_filter;
    private View view_grade_filter;

    private WritingSortListActivity mContext;

    private List<HomeRecommendationBean> mList = new ArrayList<>();
    private HomeRecommendationAdapter adapter;
    private LinearLayoutManager layoutManager;

    private List<LevelBean> mList_grade_filter = new ArrayList<>();
    private WritingGradeFilterAdapter adapter_grade_filter;
    private int oldGradeSelectedPosition = -1;

    private int pageNum = 1;
    private boolean isLoading = false;  //是否正在加载中
    private boolean isEndData = false;  //是否没有数据了

    private String gradeId = "-1";  //筛选等级

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_sort_list);

        mContext = this;

        initView();
        initData();
        initGradeData();
        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_search.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initGradeData() {
        new GetGradeData(mContext).execute(url_grade);
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEndData && !isLoading) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnItemClickListen(new HomeRecommendationAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (!isLoading) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    Intent intent = new Intent(mContext, CompositionDetailActivity.class);
                    intent.putExtra("writingId", mList.get(pos).getCompositionId());
                    intent.putExtra("area", 0);
                    intent.putExtra("orderNum", "");
                    startActivity(intent);
                    //增加阅读数
                    String views = mList.get(pos).getViews();
                    mList.get(pos).setViews(DataUtil.increaseViews(views));
                    adapter.notifyItemChanged(pos);
                }
            }
        });
        adapter.setOnUserInfoClickListen(new OnUserInfoClickListen() {
            @Override
            public void onClick(long userId) {
                turnToUserHomepage(userId);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_grade_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView_grade_filter.getVisibility() == View.VISIBLE) {
                    recyclerView_grade_filter.setVisibility(View.GONE);
                    view_grade_filter.setVisibility(View.GONE);
                    iv_grade_filter.setImageResource(R.drawable.icon_writing_sort_hide);
                } else {
                    recyclerView_grade_filter.setVisibility(View.VISIBLE);
                    view_grade_filter.setVisibility(View.VISIBLE);
                    iv_grade_filter.setImageResource(R.drawable.icon_writing_sort_show);
                }
            }
        });
        view_grade_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_grade_filter.setVisibility(View.GONE);
                view_grade_filter.setVisibility(View.GONE);
                iv_grade_filter.setImageResource(R.drawable.icon_writing_sort_hide);
            }
        });
        adapter_grade_filter.setOnWritingGradeFilterItemClickListen(new WritingGradeFilterAdapter.OnWritingGradeFilterItemClick() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (!mList_grade_filter.get(pos).isSelected()) {
                    if (oldGradeSelectedPosition >= 0 && oldGradeSelectedPosition < mList_grade_filter.size()) {
                        mList_grade_filter.get(oldGradeSelectedPosition).setSelected(false);
                    }
                    mList_grade_filter.get(pos).setSelected(true);
                    adapter_grade_filter.notifyDataSetChanged();
                    gradeId = String.valueOf(mList_grade_filter.get(pos).getGrade());
                    oldGradeSelectedPosition = pos;
                    recyclerView_grade_filter.setVisibility(View.GONE);
                    view_grade_filter.setVisibility(View.GONE);
                    iv_grade_filter.setImageResource(R.drawable.icon_writing_sort_hide);
                    initData();
                }
            }
        });
    }

    /**
     * 前往用户首页
     *
     * @param userId
     */
    private void turnToUserHomepage(long userId) {
        if (userId != -1) {
            Intent intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId", userId);
            mContext.startActivity(intent);
        }
    }

    /**
     * 前往作文搜索界面
     */
    private void turnToWritingSearch() {
        Intent intent = new Intent(mContext, WritingSearchActivity.class);
        intent.putExtra("searchType", "");
        intent.putExtra("taskId", "");
        startActivity(intent);
    }

    private void initData() {
        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(true);
        isLoading = true;
        isEndData = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();

        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                gradeId, String.valueOf(pageNum));
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
            isLoading = true;
            new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                    gradeId, String.valueOf(pageNum));
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("优秀作文");
        rl_search = findViewById(R.id.rl_search_writing_sort_list);
        frameLayout = findViewById(R.id.frame_writing_sort_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_writing_sort_list);
        recyclerView = findViewById(R.id.recycler_view_writing_sort_list);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeRecommendationAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        rl_grade_filter = findViewById(R.id.rl_grade_filter);
        iv_grade_filter = findViewById(R.id.iv_sort_grade_filter);
        recyclerView_grade_filter = findViewById(R.id.recycler_view_grade_filter);
        view_grade_filter = findViewById(R.id.view_grade_filter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerView_grade_filter.setLayoutManager(gridLayoutManager);
        adapter_grade_filter = new WritingGradeFilterAdapter(mList_grade_filter, mContext);
        recyclerView_grade_filter.setAdapter(adapter_grade_filter);
    }

    /**
     * 分析获取的作文推荐列表数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        HomeRecommendationBean writingBean = new HomeRecommendationBean();
                        JSONObject object = array.getJSONObject(i);
                        writingBean.setType(0);
                        writingBean.setCompositionId(object.getString("compositionId"));
                        writingBean.setCompositionArea(0);
                        writingBean.setCompositionScore(String.valueOf(object.optInt("mark", 0)));
                        writingBean.setCompositionPrize(object.getString("prize"));
                        writingBean.setCompositionAvgScore(object.getString("avgScore"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setImage(object.getString("cover"));
                        if (object.getString("saveTime").equals("")
                                || object.getString("saveTime").equals("null")) {
                            writingBean.setDate("2018-01-01");
                        } else {
                            writingBean.setDate(DateUtil.time2Format(object.getString("saveTime")));
                        }
                        writingBean.setViews(object.getString("pv"));
                        writingBean.setUserId(object.optLong("studentId", -1));
                        writingBean.setUserName(object.getString("username"));
                        writingBean.setUserImage(object.getString("userimg"));
                        writingBean.setUserGrade(DataUtil.gradeCode2Chinese(
                                object.optInt("gradeid", 111)));
                        mList.add(writingBean);
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
        isEndData = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("没有找到相关作文");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络
     */
    private void noConnect() {
        showTips("获取数据失败");
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
        }
    }

    /**
     * 显示提示
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取作文列表
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, WritingSortListActivity> {

        protected GetData(WritingSortListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingSortListActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                if (!strings[2].equals("-1")) {
                    object.put("gradeId", strings[2]);
                }
                object.put("pageNum", strings[3]);
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
        protected void onPostExecute(WritingSortListActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
            activity.isLoading = false;
        }
    }

    /**
     * 获取年级数据
     */
    private static class GetGradeData
            extends WeakAsyncTask<String, Integer, String, WritingSortListActivity> {

        protected GetGradeData(WritingSortListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingSortListActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingSortListActivity activity, String s) {
            if (s == null) {
                activity.addLocalGradeData();
            } else {
                activity.analyzeGradeData(s);
            }
        }
    }

    private void addLocalGradeData() {
        mList_grade_filter.clear();

        LevelBean levelBean_1 = new LevelBean();
        levelBean_1.setGrade(110);
        levelBean_1.setGradeName("一年级");
        levelBean_1.setSelected(false);
        mList_grade_filter.add(levelBean_1);

        LevelBean levelBean_2 = new LevelBean();
        levelBean_2.setGrade(120);
        levelBean_2.setGradeName("二年级");
        levelBean_2.setSelected(false);
        mList_grade_filter.add(levelBean_2);

        LevelBean levelBean_3 = new LevelBean();
        levelBean_3.setGrade(130);
        levelBean_3.setGradeName("三年级");
        levelBean_3.setSelected(false);
        mList_grade_filter.add(levelBean_3);

        LevelBean levelBean_4 = new LevelBean();
        levelBean_4.setGrade(140);
        levelBean_4.setGradeName("四年级");
        levelBean_4.setSelected(false);
        mList_grade_filter.add(levelBean_4);

        LevelBean levelBean_5 = new LevelBean();
        levelBean_5.setGrade(150);
        levelBean_5.setGradeName("五年级");
        levelBean_5.setSelected(false);
        mList_grade_filter.add(levelBean_5);

        LevelBean levelBean_6 = new LevelBean();
        levelBean_6.setGrade(160);
        levelBean_6.setGradeName("六年级");
        levelBean_6.setSelected(false);
        mList_grade_filter.add(levelBean_6);

        LevelBean levelBean_7 = new LevelBean();
        levelBean_7.setGrade(210);
        levelBean_7.setGradeName("初一");
        levelBean_7.setSelected(false);
        mList_grade_filter.add(levelBean_7);

        LevelBean levelBean_8 = new LevelBean();
        levelBean_8.setGrade(220);
        levelBean_8.setGradeName("初二");
        levelBean_8.setSelected(false);
        mList_grade_filter.add(levelBean_8);

        LevelBean levelBean_9 = new LevelBean();
        levelBean_9.setGrade(230);
        levelBean_9.setGradeName("初三");
        levelBean_9.setSelected(false);
        mList_grade_filter.add(levelBean_9);

        LevelBean levelBean_10 = new LevelBean();
        levelBean_10.setGrade(310);
        levelBean_10.setGradeName("高一");
        levelBean_10.setSelected(false);
        mList_grade_filter.add(levelBean_10);

        LevelBean levelBean_11 = new LevelBean();
        levelBean_11.setGrade(320);
        levelBean_11.setGradeName("高二");
        levelBean_11.setSelected(false);
        mList_grade_filter.add(levelBean_11);

        LevelBean levelBean_12 = new LevelBean();
        levelBean_12.setGrade(330);
        levelBean_12.setGradeName("高三");
        levelBean_12.setSelected(false);
        mList_grade_filter.add(levelBean_12);

        LevelBean levelBean_13 = new LevelBean();
        levelBean_13.setGrade(-1);
        levelBean_13.setGradeName("随机");
        levelBean_13.setSelected(true);
        mList_grade_filter.add(levelBean_13);

        oldGradeSelectedPosition = mList_grade_filter.size() - 1;
        adapter_grade_filter.notifyDataSetChanged();
    }

    private void analyzeGradeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.optInt("status", -1) == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    LevelBean levelBean = new LevelBean();
                    levelBean.setGrade(object.optInt("gradeid", 110));
                    levelBean.setGradeName(object.getString("gradename"));
                    levelBean.setSelected(false);
                    mList_grade_filter.add(levelBean);
                }
                mList_grade_filter.get(mList_grade_filter.size() - 1).setSelected(true);
                oldGradeSelectedPosition = mList_grade_filter.size() - 1;
                adapter_grade_filter.notifyDataSetChanged();
            } else {
                addLocalGradeData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLocalGradeData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
