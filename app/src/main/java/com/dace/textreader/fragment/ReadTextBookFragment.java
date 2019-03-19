package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dace.textreader.R;
import com.dace.textreader.activity.KnowledgeDetailActivity;
import com.dace.textreader.activity.KnowledgeSummaryActivity;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.ClassesArticleRecyclerViewAdapter;
import com.dace.textreader.adapter.ClassesChooseRecyclerViewAdapter;
import com.dace.textreader.adapter.KnowledgeHorizontalAdapter;
import com.dace.textreader.bean.Classes;
import com.dace.textreader.bean.KnowledgeChildBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
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
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2019/2/27 0027 下午 2:06.
 * Version   1.0;
 * Describe :  阅读 -- 课文
 * History:
 * ==============================================================================
 */
public class ReadTextBookFragment extends Fragment {

    private static final String url_knowledge = HttpUrlPre.HTTP_URL_ + "/knowledge/point/all";
    private static final String url = HttpUrlPre.HTTP_URL + "/kewen?";

    private View view;

    private NestedScrollView scrollView;
    private LinearLayout ll_search;
    private LinearLayout ll_knowledge;
    private RecyclerView recyclerView_knowledge;
    private RecyclerView recyclerView_grade;
    private RecyclerView recyclerView;

    private Context mContext;

    private List<KnowledgeChildBean> mList_knowledge = new ArrayList<>();
    private KnowledgeHorizontalAdapter adapter_knowledge;

    private List<String> mList_grade = new ArrayList<>();
    private ClassesChooseRecyclerViewAdapter adapter_grade;

    private List<Classes> mList = new ArrayList<>();
    private ClassesArticleRecyclerViewAdapter adapter;

    private static final String version = "人教版";
    private String grade = "一年级上册";
    private int gradeId = 111;  //年级对应的ID
    private int pageNum = 1;  //页码
    private int pageSize = 15;  //每页的文章数量

    private int grade_position = 0;  //当前的年级位置

    private boolean refreshing = false;
    private boolean isEnd = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_read_text_book, container, false);

        initView();
        initKnowledgeData();
        initEvents();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initEvents() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (i1 > i3) {
                    getMoreData();
                }
            }
        });
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ll_knowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, KnowledgeSummaryActivity.class));
            }
        });
        adapter_knowledge.setOnItemClickListen(new KnowledgeHorizontalAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_knowledge.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList_knowledge.size()) {
                    turnToKnowledgeDetail(pos);
                }
            }
        });
        adapter_grade.setOnItemClickListener(
                new ClassesChooseRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        if (refreshing) {
                            MyToastUtil.showToast(mContext, "正在加载数据...");
                        } else {
                            int pos = recyclerView_grade.getChildAdapterPosition(view);
                            if (pos != -1 && pos < mList_grade.size() && pos != grade_position) {
                                grade_position = pos;
                                adapter_grade.setItemSelected(pos);
                                grade = mList_grade.get(pos);
                                gradeId = DataUtil.grade2GradeId(grade);
                                moveToMiddle(view);
                                initData();
                            }
                        }
                    }
                });
        adapter.setOnItemClickListener(new ClassesArticleRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                if (position != -1 && position < mList.size()) {
                    turnToArticleDetail(position);
                }
            }
        });
    }

    /**
     * 前往知识点详情
     *
     * @param pos
     */
    private void turnToKnowledgeDetail(int pos) {
        long id = mList_knowledge.get(pos).getKnowledgeId();
        Intent intent = new Intent(mContext, KnowledgeDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", "");
        startActivity(intent);
    }

    /**
     * 滚动到中间位置
     */
    public void moveToMiddle(View view) {
        int itemWidth = view.getWidth();
        int screenWidth = getResources().getDisplayMetrics().widthPixels
                - DensityUtil.dip2px(mContext, 48);
        int scrollWidth = view.getLeft() - (screenWidth / 2 - itemWidth / 2);
        recyclerView_grade.scrollBy(scrollWidth, 0);
    }

    /**
     * 跳转到文章详情
     *
     * @param position 文章
     */
    private void turnToArticleDetail(int position) {
        long essayId = mList.get(position).getId();
        int type = mList.get(position).getType();
        Intent intent = new Intent(getContext(), NewArticleDetailActivity.class);
        intent.putExtra("id", essayId);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void initKnowledgeData() {

        mList_knowledge.clear();
        new GetKnowledgeData(this).execute(url_knowledge);

    }

    private void initGradeData() {

        mList_grade.clear();
        mList_grade.add("一年级上册");
        mList_grade.add("一年级下册");
        mList_grade.add("二年级上册");
        mList_grade.add("二年级下册");
        mList_grade.add("三年级上册");
        mList_grade.add("三年级下册");
        mList_grade.add("四年级上册");
        mList_grade.add("四年级下册");
        mList_grade.add("五年级上册");
        mList_grade.add("五年级下册");
        mList_grade.add("六年级上册");
        mList_grade.add("六年级下册");
        mList_grade.add("初一上册");
        mList_grade.add("初一下册");
        mList_grade.add("初二上册");
        mList_grade.add("初二下册");
        mList_grade.add("初三上册");
        mList_grade.add("初三下册");
        mList_grade.add("高一上册");
        mList_grade.add("高一下册");
        mList_grade.add("高二上册");
        mList_grade.add("高二下册");
        mList_grade.add("高三上册");
        mList_grade.add("高三下册");
        adapter_grade.notifyDataSetChanged();

        initData();
    }

    private void initData() {
        if (refreshing) {
            return;
        }
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        mList.clear();
        new GetData(this).execute(url + "gradeId=" + gradeId +
                "&level1=" + version +
                "&pageNum=" + pageNum +
                "&pageSize=" + pageSize +
                "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        if (refreshing) {
            return;
        }
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(this).execute(url + "gradeId=" + gradeId +
                "&level1=" + version +
                "&pageNum=" + pageNum +
                "&pageSize=" + pageSize +
                "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    private void initView() {
        scrollView = view.findViewById(R.id.nested_scroll_read_text_book_fragment);
        ll_search = view.findViewById(R.id.ll_search_read_text_book_fragment);
        ll_knowledge = view.findViewById(R.id.ll_knowledge_read_text_book_fragment);
        recyclerView_knowledge = view.findViewById(R.id.rv_knowledge_read_text_book_fragment);
        LinearLayoutManager layoutManager_knowledge = new LinearLayoutManager(
                mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_knowledge.setLayoutManager(layoutManager_knowledge);
        adapter_knowledge = new KnowledgeHorizontalAdapter(mContext, mList_knowledge);
        recyclerView_knowledge.setAdapter(adapter_knowledge);

        recyclerView_grade = view.findViewById(R.id.rv_grade_read_text_book_fragment);
        LinearLayoutManager layoutManager_grade = new LinearLayoutManager(
                mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_grade.setLayoutManager(layoutManager_grade);
        adapter_grade = new ClassesChooseRecyclerViewAdapter(mContext, mList_grade);
        recyclerView_grade.setAdapter(adapter_grade);

        recyclerView = view.findViewById(R.id.rv_read_text_book_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ClassesArticleRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 分析数据
     *
     * @param s 从服务端获取的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Classes classes = new Classes();
                    classes.setId(object.optLong("id", -1));
                    classes.setType(object.optInt("type", -1));
                    classes.setTitle(object.getString("title"));
                    String author = object.getString("author");
                    if (author.equals("") || author.equals("null")) {
                        author = "佚名";
                    }
                    classes.setAuthor(author);
                    classes.setImagePath(object.getString("image"));
                    mList.add(classes);
                }
                adapter.notifyDataSetChanged();
            } else if (400 == jsonObject.optInt("status", -1)) {

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeKnowledgeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject object = jsonArray.getJSONObject(0);
                JSONArray array = object.getJSONArray("list");

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
                    mList_knowledge.add(bean);
                }

                //处理完数据，更新ui
                adapter_knowledge.notifyDataSetChanged();

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, ReadTextBookFragment> {

        protected GetData(ReadTextBookFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReadTextBookFragment fragment, String[] strings) {
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
        protected void onPostExecute(ReadTextBookFragment fragment, String s) {
            if (s == null) {

            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
        }
    }

    /**
     * 获取数据
     */
    private static class GetKnowledgeData
            extends WeakAsyncTask<String, Void, String, ReadTextBookFragment> {

        protected GetKnowledgeData(ReadTextBookFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReadTextBookFragment fragment, String[] strings) {
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
        protected void onPostExecute(ReadTextBookFragment fragment, String s) {
            fragment.initGradeData();
            if (s == null) {

            } else {
                fragment.analyzeKnowledgeData(s);
            }
        }
    }

}
