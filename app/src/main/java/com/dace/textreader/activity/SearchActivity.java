package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.RecyclerViewAdapter;
import com.dace.textreader.adapter.SearchHistoryAdapter;
import com.dace.textreader.adapter.TagAdapter;
import com.dace.textreader.bean.Article;
import com.dace.textreader.bean.SearchHistoryBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.hhl.library.FlowTagLayout;
import com.hhl.library.OnTagClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文章搜索
 * 课内、课外文章搜索
 */
public class SearchActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/statistics/rank/search?";
    private final String tagUrl = HttpUrlPre.HTTP_URL + "/statistics/kenei/rank/search?";
    private final String classesUrl = HttpUrlPre.HTTP_URL + "/kenei/search?";
    private final String searchUrl = HttpUrlPre.HTTP_URL + "/index/search?";
    private final String userUrl = HttpUrlPre.HTTP_URL + "/statistics/search/update?";

    private FrameLayout frameLayout;
    private EditText et_search;
    private TextView tv_search;
    private ImageView iv_clear;
    private LinearLayout ll_search_word;
    private FlowTagLayout search_tag;
    private RecyclerView lv_history;
    private TextView tv_clear;
    private LinearLayout ll_hot_tag;
    private LinearLayout ll_history_words;

    private SearchHistoryAdapter historyAdapter;
    private List<SearchHistoryBean> history = new ArrayList<>();

    private TagAdapter tagAdapter;
    private List<String> mList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter listAdapter;
    private List<Article> list = new ArrayList<>();

    private int pageNum = 1;
    private LinearLayoutManager mLayoutManager;

    private String searchContent = "";  //搜索的内容

    private boolean isRefresh = false;
    private boolean refreshing = false;
    private boolean isNoMoreContent = false;

    private boolean isClasses = false;  //是否是课内文章

    private boolean isWordsExist = false;  //搜索历史是否存在

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(SearchActivity.this);
        StatusBarUtil.StatusBarLightMode(SearchActivity.this, flag);

        isClasses = getIntent().getBooleanExtra("isClasses", false);

        initView();
        initData();
        initEvents();

    }

    private void initData() {
        LitePal.findAllAsync(SearchHistoryBean.class).listen(new FindMultiCallback<SearchHistoryBean>() {
            @Override
            public void onFinish(List<SearchHistoryBean> list) {
                if (list.size() == 0) {
                    ll_history_words.setVisibility(View.GONE);
                } else {
                    history.addAll(list);
                    historyAdapter.notifyDataSetChanged();
                }
            }
        });

        String tag;
        if (isClasses) {
            tag = tagUrl;
        } else {
            tag = url;
        }
        new GetTagData(SearchActivity.this)
                .execute(tag + "level=" + NewMainActivity.GRADE);
    }

    private void initEvents() {
        et_search.addTextChangedListener(textChangeListener);
        tv_search.setOnClickListener(onClickListener);
        iv_clear.setOnClickListener(onClickListener);
        search_tag.setOnTagClickListener(onTagClickListener);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (list.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    update();
                }
            }
        });
        //上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (list.size() != 0) {
                    if (!isNoMoreContent && !refreshing) {
                        if (mList.size() != 0) {
                            getMoreData(newState);
                        }
                    }
                }
            }
        });
        listAdapter.setOnItemClickListener(onItemClickListener);
        historyAdapter.setOnItemClickListen(new SearchHistoryAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = lv_history.getChildAdapterPosition(view);
                searchContent = history.get(pos).getWords();
                et_search.setText(searchContent);
                et_search.setSelection(searchContent.length());

                update();
            }
        });
        historyAdapter.setOnItemDeleteClickListen(new SearchHistoryAdapter.OnItemDeleteClickListen() {
            @Override
            public void onClick(int position) {
                long id = history.get(position).getId();
                LitePal.delete(SearchHistoryBean.class, id);
                history.remove(position);
                historyAdapter.notifyDataSetChanged();
            }
        });
        tv_clear.setOnClickListener(onClickListener);
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
    }

    //加载更多
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == listAdapter.getItemCount() - 1) {
            isRefresh = false;
            refreshing = true;
            pageNum++;
            if (isClasses) {
                new GetData(SearchActivity.this)
                        .execute(classesUrl +
                                "query=" + searchContent +
                                "&studentId=" + NewMainActivity.STUDENT_ID +
                                "&pageNum=" + pageNum + "&pageNum=10");
            } else {
                new GetData(SearchActivity.this)
                        .execute(searchUrl +
                                "query=" + et_search.getText().toString() +
                                "&studentId=" + NewMainActivity.STUDENT_ID +
                                "&pageNum=" + pageNum + "&pageNum=10");
            }
        }
    }

    //刷新
    private void update() {
        isRefresh = true;
        refreshing = true;
        ll_search_word.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        pageNum = 1;
        isNoMoreContent = false;
        if (isClasses) {
            new GetData(SearchActivity.this)
                    .execute(classesUrl +
                            "query=" + searchContent +
                            "&studentId=" + NewMainActivity.STUDENT_ID +
                            "&pageNum=" + pageNum + "&pageNum=10");
        } else {
            new GetData(SearchActivity.this)
                    .execute(searchUrl +
                            "query=" + et_search.getText().toString() +
                            "&studentId=" + NewMainActivity.STUDENT_ID +
                            "&pageNum=" + pageNum + "&pageNum=10");
        }
        hideInputMethod();
    }

    private OnTagClickListener onTagClickListener = new OnTagClickListener() {
        @Override
        public void onItemClick(FlowTagLayout parent, View view, int position) {
            searchContent = mList.get(position);
            et_search.setText(searchContent);
            et_search.setSelection(searchContent.length());

            update();
        }
    };

    private RecyclerViewAdapter.OnRecyclerViewItemClickListener onItemClickListener
            = new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view) {

            int position = recyclerView.getChildAdapterPosition(view);

            if (NewMainActivity.STUDENT_ID != -1) {
                new UpdateUserData(SearchActivity.this)
                        .execute(userUrl + "studentId=" + NewMainActivity.STUDENT_ID +
                                "&search_keyword=" + et_search.getText().toString() +
                                "&articleId=" + list.get(position).getId() +
                                "&articleType=" + list.get(position).getType() +
                                "&level=" + NewMainActivity.GRADE);
            }

            turnToArticleDetail(position);
        }
    };

    //监听输入框的文字内容改变
    private TextWatcher textChangeListener = new TextWatcher() {
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
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_searchOrNot:
                    searchOrNot();
                    break;
                case R.id.tv_clear_history_search:
                    if (history.size() != 0) {
                        clearAllHistory();
                    }
                    break;
                case R.id.iv_clear_search:
                    clearContent();
                    break;
            }
        }
    };

    /**
     * 清除
     */
    private void clearContent() {
        et_search.setText("");
    }

    //清除所有历史纪录
    private void clearAllHistory() {
        LitePal.deleteAll(SearchHistoryBean.class);
        history.clear();
        historyAdapter.notifyDataSetChanged();
    }

    //搜索或者取消搜索
    private void searchOrNot() {
        if (tv_search.getText().toString().equals("搜索")) {
            searchInfo();
        } else {
            finish();
        }
    }

    //搜索内容
    private void searchInfo() {
        searchContent = et_search.getText().toString();

        saveSearchHistory();

        update();
    }

    /**
     * 保存搜索历史
     */
    private void saveSearchHistory() {
        isWordsExist = false;
        LitePal.findAllAsync(SearchHistoryBean.class).listen(new FindMultiCallback<SearchHistoryBean>() {
            @Override
            public void onFinish(List<SearchHistoryBean> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getWords().equals(searchContent)) {
                        isWordsExist = true;
                        break;
                    }
                }
            }
        });
        if (!isWordsExist) {
            SearchHistoryBean bean = new SearchHistoryBean();
            bean.setWords(searchContent);
            bean.save();
        }
    }

    private void initView() {
        frameLayout = findViewById(R.id.frame_search);

        et_search = findViewById(R.id.et_search);
        tv_search = findViewById(R.id.tv_searchOrNot);
        iv_clear = findViewById(R.id.iv_clear_search);
        ll_search_word = findViewById(R.id.ll_search_word);

        iv_clear.setVisibility(View.INVISIBLE);

        et_search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        ll_hot_tag = findViewById(R.id.ll_hot_tag);
        search_tag = findViewById(R.id.flow_tag_search);
        tagAdapter = new TagAdapter<>(this);
        search_tag.setAdapter(tagAdapter);

        ll_history_words = findViewById(R.id.ll_history_words);
        lv_history = findViewById(R.id.lv_search_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        lv_history.setLayoutManager(layoutManager);
        historyAdapter = new SearchHistoryAdapter(this, history);
        lv_history.setAdapter(historyAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_search);
        recyclerView = findViewById(R.id.recycler_view_search);
        mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        listAdapter = new RecyclerViewAdapter(this, list);
        recyclerView.setAdapter(listAdapter);

        tv_clear = findViewById(R.id.tv_clear_history_search);
    }

    //点击进入文章的索引
    private int position = -1;

    /**
     * 跳转到文章详情
     *
     * @param position
     */
    private void turnToArticleDetail(int position) {
        this.position = position;
        Intent intent = new Intent(SearchActivity.this, NewArticleDetailActivity.class);
        intent.putExtra("id", list.get(position).getId());
        int type = list.get(position).getType();
        intent.putExtra("type", type);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (position != -1) {
                if (data.getBooleanExtra("clickLike", false)) {
                    int likeNum = list.get(position).getLikeNum();
                    list.get(position).setLikeNum(likeNum + 1);
                }
                int views = list.get(position).getViews();
                list.get(position).setViews(views + 1);
                listAdapter.notifyItemChanged(position);
                position = -1;
            }
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) et_search.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_search.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 获取热门标签数据
     */
    private static class GetTagData
            extends WeakAsyncTask<String, Integer, String, SearchActivity> {

        protected GetTagData(SearchActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SearchActivity activity, String[] params) {
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
        protected void onPostExecute(SearchActivity activity, String s) {
            activity.refreshing = false;
            if (s != null) {
                activity.analyzeData(s);
            } else {
                activity.ll_hot_tag.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 分析数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String str = json.getString("searchKeyword");
                if (!str.equals("")) {
                    mList.add(str);
                }
            }
            if (mList.size() == 0) {
                ll_hot_tag.setVisibility(View.GONE);
            } else {
                tagAdapter.onlyAddAll(mList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取搜索内容数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, SearchActivity> {

        protected GetData(SearchActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SearchActivity activity, String[] params) {
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
        protected void onPostExecute(SearchActivity activity, String s) {
            if (s == null) {
                activity.errorConnect();
            } else {
                activity.analyzeSearchData(s);
            }
            activity.swipeRefreshLayout.setRefreshing(false);
            activity.refreshing = false;
        }
    }

    /**
     * 分析搜索的数据
     *
     * @param s 获取到的搜索结果
     */
    private void analyzeSearchData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    noSearchResult();
                } else {
                    if (isRefresh) {
                        mList.clear();
                    }
                    List<Article> newList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject essay = jsonObject.getJSONObject("essay");
                        Article article = new Article();
                        article.setId(essay.optLong("id", -1));
                        article.setType(essay.optInt("type", -1));
                        article.setTitle(essay.getString("title"));
                        article.setContent(essay.getString("content"));
                        article.setGrade(essay.optInt("grade", 110));
                        article.setPyScore(essay.getString("score"));
                        article.setViews(essay.optInt("pv", 180));
                        article.setImagePath(essay.getString("image"));
                        if (isRefresh) {
                            list.add(article);
                        } else {
                            newList.add(article);
                        }
                    }
                    if (isRefresh) {
                        listAdapter.notifyDataSetChanged();
                    } else {
                        listAdapter.addData(newList);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
                noSearchResult();
            } else {
                errorConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 没有搜索到内容
     */
    private void noSearchResult() {
        if (isDestroyed()) {
            return;
        }
        ll_search_word.setVisibility(View.GONE);
        isNoMoreContent = true;
        if (list.size() == 0) {
            View errorView = LayoutInflater.from(this)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_empty_search, imageView);
            tv_tips.setText("搜索无内容哦~\n换个关键词试试~");
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
        ll_search_word.setVisibility(View.GONE);
        if (list.size() == 0) {
            View errorView = LayoutInflater.from(this)
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
     * 更新用户的搜索行为
     */
    private static class UpdateUserData
            extends WeakAsyncTask<String, Integer, String, SearchActivity> {

        protected UpdateUserData(SearchActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SearchActivity activity, String[] params) {
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
        protected void onPostExecute(SearchActivity activity, String s) {

        }
    }

}
