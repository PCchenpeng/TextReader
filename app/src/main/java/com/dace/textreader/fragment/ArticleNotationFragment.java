package com.dace.textreader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.NotationRecyclerViewAdapter;
import com.dace.textreader.bean.NotationBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/4/12 0012 上午 10:39.
 * Version   1.0;
 * Describe :  文章详情的注释部分
 * History:
 * ==============================================================================
 */

public class ArticleNotationFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/guwen/translation?";

    private View view;

    private RelativeLayout rl_back;
    private View view_top;
    private ImageView iv_notation;
    private NestedScrollView scrollView;
    private LinearLayout ll_title;
    private TextView tv_title;
    private LinearLayout ll_author;
    private TextView tv_author;
    private RecyclerView recyclerView;
    private ImageView iv_to_top;

    private FrameLayout frameLayout;

    private long essayId = -1;
    private int essayType = -1;
    private String title;
    private String author;
    private String dynasty;  //作者的朝代
    private List<NotationBean> mList = new ArrayList<>();
    private NotationRecyclerViewAdapter adapter;

    private boolean isShowAllNotation = true;
    private boolean isClickToTop = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_article_notation, container, false);

        essayId = NewArticleDetailActivity.essayId;
        essayType = NewArticleDetailActivity.essayType;

        initView();
        initData();
        initEvents();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (essayId != NewArticleDetailActivity.essayId) {
                essayId = NewArticleDetailActivity.essayId;
                initData();
            }
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnArticleNotationBackClick != null) {
                    mOnArticleNotationBackClick.onClick();
                }
            }
        });
        view_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop();
            }
        });
        iv_notation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotationOrNot();
            }
        });
        iv_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop();
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isClickToTop) {
                    if (scrollY > 10) {
                        iv_to_top.setVisibility(View.VISIBLE);
                    } else {
                        iv_to_top.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    //滚动到顶部
    private void scrollToTop() {
        isClickToTop = true;
        iv_to_top.setVisibility(View.INVISIBLE);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                isClickToTop = false;
            }
        });
    }

    /**
     * 显示或隐藏注释
     */
    private void showNotationOrNot() {
        if (isShowAllNotation) {
            hideNotation();
        } else {
            showNotation();
        }
    }

    /**
     * 显示注释
     */
    private void showNotation() {
        iv_notation.setImageResource(R.drawable.icon_notation_sel);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setExpand(true);
            adapter.notifyDataSetChanged();
        }
        isShowAllNotation = true;
    }

    /**
     * 隐藏注释
     */
    private void hideNotation() {
        iv_notation.setImageResource(R.drawable.icon_notation_nor);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setExpand(false);
            adapter.notifyDataSetChanged();
        }
        isShowAllNotation = false;
    }

    private void initData() {
        mList.clear();
        adapter.notifyDataSetChanged();
        ll_author.setVisibility(View.GONE);
        ll_title.setVisibility(View.GONE);
        new GetData(this).execute(url + "essayId=" + essayId +
                "&type=" + essayType + "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    private void initView() {
        rl_back = view.findViewById(R.id.rl_back_article_notation_fragment);
        view_top = view.findViewById(R.id.view_top_article_notation_fragment);
        iv_notation = view.findViewById(R.id.iv_notation_article_notation_fragment);
        scrollView = view.findViewById(R.id.scroll_view_article_notation_fragment);
        ll_title = view.findViewById(R.id.ll_title_article_notation_fragment);
        tv_title = view.findViewById(R.id.tv_title_article_notation_fragment);
        ll_author = view.findViewById(R.id.ll_author_article_notation_fragment);
        tv_author = view.findViewById(R.id.tv_author_article_notation_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_article_notation_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotationRecyclerViewAdapter(getContext(), mList);
        recyclerView.setAdapter(adapter);
        iv_to_top = view.findViewById(R.id.iv_to_top_article_notation_fragment);

        frameLayout = view.findViewById(R.id.frame_article_notation_fragment);
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
                JSONObject data = jsonObject.getJSONObject("data");
                title = data.getString("title");
                author = data.getString("author");
                dynasty = data.getString("dynasty");
                JSONArray array = data.getJSONArray("parse");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    NotationBean notationBean = new NotationBean();
                    notationBean.setOriginal(object.getString("original"));
                    notationBean.setNotation(object.getString("annotation"));
                    notationBean.setExpand(true);
                    mList.add(notationBean);
                }
                updateUi();
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 更新视图
     */
    private void updateUi() {
        tv_title.setText(title);
        ll_title.setVisibility(View.VISIBLE);
        String authorInfo = author + " 【" + dynasty + "】";
        tv_author.setText(authorInfo);
        ll_author.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        View errorView = LayoutInflater.from(getContext())
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty, imageView);
        tv_tips.setText("暂无该文章注释");
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据失败
     */
    private void errorConnect() {
        View errorView = LayoutInflater.from(getContext())
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

    /**
     * 获取注释数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, ArticleNotationFragment> {

        protected GetData(ArticleNotationFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleNotationFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArticleNotationFragment fragment, String s) {
            if (s == null) {
                fragment.errorConnect();
            } else {
                fragment.analyzeData(s);
            }
        }
    }

    public interface OnArticleNotationBackClick {
        void onClick();
    }

    private OnArticleNotationBackClick mOnArticleNotationBackClick;

    public void setOnArticleNotationBackClick(OnArticleNotationBackClick onArticleNotationBackClick) {
        this.mOnArticleNotationBackClick = onArticleNotationBackClick;
    }
}
