package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.CompositionRecyclerViewAdapter;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.util.DataUtil;
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
 * 作文搜索
 */
public class WritingSearchActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/search/composition";
    private static final String url_events = HttpUrlPre.HTTP_URL + "/search/match/composition";

    private EditText et_search;
    private TextView tv_search;
    private ImageView iv_clear;

    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private WritingSearchActivity mContext;

    private List<WritingBean> mList = new ArrayList<>();
    private CompositionRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int pageNum = 1;
    private boolean isLoading = false;  //是否正在加载中
    private boolean isEndData = false;  //是否没有数据了

    private String searchType = "";
    private String taskId = "";
    private String searchContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_search);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);
        searchType = getIntent().getStringExtra("searchType");
        taskId = getIntent().getStringExtra("taskId");

        initView();
        initEvents();
    }

    private void initEvents() {
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOrNot();
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.isEmpty()) {
                    tv_search.setText("取消");
                    iv_clear.setVisibility(View.INVISIBLE);
                } else {
                    tv_search.setText("搜索");
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (!et_search.getText().toString().trim().isEmpty()) {
                            searchInfo();
                        }
                        break;
                }
                return true;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    searchInfo();
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
        adapter.setOnCompositionItemClick(new CompositionRecyclerViewAdapter.OnCompositionItemClick() {
            @Override
            public void onItemClick(View view) {
                if (!isLoading) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    Intent intent = new Intent(mContext, CompositionDetailActivity.class);
                    intent.putExtra("writingId", mList.get(pos).getId());
                    intent.putExtra("area", 0);
                    intent.putExtra("orderNum", "");
                    startActivity(intent);
                    addWritingViews(pos);
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
     * 为作文增加阅读数
     */
    private void addWritingViews(int position) {
        int views = Integer.valueOf(mList.get(position).getViews()) + 1;
        mList.get(position).setViews(String.valueOf(views));
        adapter.notifyItemChanged(position);
    }

    //搜索或者取消搜索
    private void searchOrNot() {
        if (tv_search.getText().toString().equals("搜索")) {
            searchInfo();
        } else {
            finish();
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
            isLoading = true;
            if (taskId.equals("")) {
                new GetData(mContext).execute(url, searchContent, String.valueOf(pageNum));
            } else {
                new GetData(mContext).execute(url_events, searchContent, String.valueOf(pageNum));
            }
        }
    }

    private void initView() {
        et_search = findViewById(R.id.et_search);
        tv_search = findViewById(R.id.tv_searchOrNot);
        iv_clear = findViewById(R.id.iv_clear_search);
        iv_clear.setVisibility(View.INVISIBLE);
        et_search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        frameLayout = findViewById(R.id.frame_writing_search);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_writing_search);
        recyclerView = findViewById(R.id.recycler_view_writing_search);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //得到AssetManager
        AssetManager mgr = getAssets();
        //根据路径得到Typeface
        Typeface score = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        adapter = new CompositionRecyclerViewAdapter(mContext, mList, score);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 搜索信息
     */
    private void searchInfo() {
        searchContent = et_search.getText().toString();
        swipeRefreshLayout.setRefreshing(true);
        isLoading = true;
        isEndData = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();

        if (taskId.equals("")) {
            new GetData(mContext).execute(url, searchContent, String.valueOf(pageNum));
        } else {
            new GetData(mContext).execute(url_events, searchContent, String.valueOf(pageNum));
        }

        //隐藏输入法
        InputMethodManager imm = (InputMethodManager) et_search.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_search.getApplicationWindowToken(), 0);
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
                        WritingBean writingBean = new WritingBean();
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setComment(object.getString("comment"));
                        writingBean.setStatus(object.optInt("status", -1));
                        if (object.getString("mark").equals("") ||
                                object.getString("mark").equals("null")) {
                            writingBean.setMark(-1);
                        } else {
                            writingBean.setMark(object.optInt("mark", -1));
                        }
                        writingBean.setMaterialId(object.getString("materialId"));
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
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_search, imageView);
            tv_tips.setText("搜索无内容哦~\n换个关键词试试~");
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
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    searchInfo();
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
     * 显示提示
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
            extends WeakAsyncTask<String, Void, String, WritingSearchActivity> {

        protected GetData(WritingSearchActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingSearchActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("keyword", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                if (activity.taskId.equals("")) {
                    if (!activity.searchType.equals("")) {
                        object.put("level", activity.searchType);
                    }
                } else {
                    object.put("taskId", activity.taskId);
                }
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
        protected void onPostExecute(WritingSearchActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
            activity.isLoading = false;
        }
    }

}
