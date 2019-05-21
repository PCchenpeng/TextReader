package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.GrammarAdapter;
import com.dace.textreader.bean.GrammarBean;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/10/31 0031 下午 2:30.
 * Version   1.0;
 * Describe :  用户语法页面
 * History:
 * ==============================================================================
 */
public class GrammarFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/grammar/list?";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;
    private TextView tv_sure;
    private ImageView iv_practice;

    private Context mContext;

    private List<GrammarBean> mList = new ArrayList<>();
    private GrammarAdapter adapter;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    private boolean isEditor = false;
    private int mSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_grammar, container, false);

        initView();
        refreshLayout.autoRefresh();
        initEvents();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing || isEditor) {
                    refreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (refreshing || isEnd) {
                    refreshLayout.finishLoadMore();
                } else {
                    getMoreData();
                }
            }
        });
        iv_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshing) {
                    showTips("正在加载数据，请稍后...");
                } else {
                    if (isEditor) {
                        editorOrNot(false);
                    } else {
                        editorOrNot(true);
                    }
                }
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPosition != -1) {
                    practice();
                }
            }
        });
        adapter.setOnItemClickListen(new GrammarAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (isEditor) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (mSelectedPosition == -1) {
                        mList.get(pos).setSelected(true);
                        adapter.notifyItemChanged(pos);
                        mSelectedPosition = pos;
                        tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
                    } else if (pos == mSelectedPosition) {
                        mList.get(pos).setSelected(false);
                        adapter.notifyItemChanged(pos);
                        mSelectedPosition = -1;
                        tv_sure.setBackgroundColor(Color.parseColor("#dddddd"));
                    } else {
                        mList.get(mSelectedPosition).setSelected(false);
                        mList.get(pos).setSelected(true);
                        adapter.notifyDataSetChanged();
                        mSelectedPosition = pos;
                        tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
                    }
                }
            }
        });
    }

    /**
     * 练习
     */
    private void practice() {
        String practice = mList.get(mSelectedPosition).getContent();
        Intent intent = new Intent();
        intent.putExtra("practiceType", 1);
        intent.putExtra("practice", practice);
        getActivity().setResult(0, intent);
        getActivity().finish();
    }

    /**
     * 进入编辑或取消编辑
     *
     * @param editor
     */
    private void editorOrNot(boolean editor) {
        isEditor = editor;
        if (editor) {
            tv_sure.setVisibility(View.VISIBLE);
            iv_practice.setImageResource(R.drawable.icon_practice_cancle);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setEditor(true);
            }
        } else {
            iv_practice.setImageResource(R.drawable.icon_practice);
            tv_sure.setVisibility(View.GONE);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setEditor(false);
                mList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                    "&pageNum=" + pageNum + "&pageSize=10");
        }
    }

    private void getMoreData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                "&pageNum=" + pageNum + "&pageSize=10");
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_grammar_fragment);

        refreshLayout = view.findViewById(R.id.smart_refresh_grammar_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = view.findViewById(R.id.recycler_view_grammar_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GrammarAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        tv_one = view.findViewById(R.id.tv_one_tips_note);
        tv_two = view.findViewById(R.id.tv_two_tips_note);
        tv_three = view.findViewById(R.id.tv_three_tips_note);
        tv_one.setText("在阅读时");
        tv_two.setText("可长按选择文本");
        tv_three.setText("");

        tv_sure = view.findViewById(R.id.tv_sure_grammar_fragment);
        iv_practice = view.findViewById(R.id.iv_practice_grammar_fragment);
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        GrammarBean grammarBean = new GrammarBean();
                        grammarBean.setGrammarId(object.getString("id"));
                        grammarBean.setGrammar(object.getString("grammar"));
                        grammarBean.setEssayId(object.optLong("essayid", -1));
                        grammarBean.setEssayType(object.optInt("type", -1));
                        grammarBean.setEssayTitle(object.getString("title"));
                        grammarBean.setContent(object.getString("content"));
                        grammarBean.setSelected(false);
                        grammarBean.setEditor(isEditor);
                        mList.add(grammarBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (iv_practice.getVisibility() == View.GONE) {
                        iv_practice.setVisibility(View.VISIBLE);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        if (mList.size() == 0) {
            iv_practice.setVisibility(View.GONE);
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.notes_list_tips_layout, null);
            TextView tv_one = errorView.findViewById(R.id.tv_one_tips_note);
            TextView tv_two = errorView.findViewById(R.id.tv_two_tips_note);
            TextView tv_three = errorView.findViewById(R.id.tv_three_tips_note);
            tv_one.setText("在阅读时");
            tv_two.setText("可长按选择文本");
            tv_three.setText("");
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (mList.size() == 0) {
            iv_practice.setVisibility(View.GONE);
            View errorView = LayoutInflater.from(getContext())
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
        } else {
            showTips("获取数据失败,请稍后重试~");
        }
    }

    /**
     * 显示吐丝
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
            extends WeakAsyncTask<String, Void, String, GrammarFragment> {

        protected GetData(GrammarFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(GrammarFragment fragment, String[] strings) {
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
        protected void onPostExecute(GrammarFragment fragment, String s) {
            if (s == null) {
                fragment.errorData();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
            fragment.refreshLayout.finishRefresh();
            fragment.refreshLayout.finishLoadMore();
        }
    }

}
