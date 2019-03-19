package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewClassesActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.ClassesArticleRecyclerViewAdapter;
import com.dace.textreader.adapter.ClassesChooseRecyclerViewAdapter;
import com.dace.textreader.bean.Classes;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;

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
 * Copyright (c) 2017 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2017/12/7 0007 下午 1:41.
 * Version   1.0;
 * Describe :  小学
 * History:
 * ==============================================================================
 */

public class PrimarySchoolFragment extends Fragment {

    private String url = HttpUrlPre.HTTP_URL + "/kewen?";

    //获取版本信息
    private final String versionGradeUrl = HttpUrlPre.HTTP_URL + "/kewen/grade";

    private View view;

    private RecyclerView recyclerView_version;
    private RecyclerView recyclerView_grade;

    private FrameLayout frameLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private GridLayoutManager mGridLayoutManager;

    private ClassesChooseRecyclerViewAdapter adapter_version;
    private ClassesChooseRecyclerViewAdapter adapter_grade;
    private ClassesArticleRecyclerViewAdapter adapter;

    private List<String> mList_version = new ArrayList<>();
    private List<String> mList_grade = new ArrayList<>();
    private List<Classes> mList = new ArrayList<>();

    private int version_position = 0;  //当前的版本位置
    private int grade_position = 0;  //当前的年级位置

    private String version = "人教版";
    private String grade = "一年级上";
    private int gradeId = 111;  //年级对应的ID
    private int pageNum = 1;  //页码
    private int pageSize = 15;  //每页的文章数量

    private boolean refreshing = false;
    private boolean isNoMoreData = false;

    private boolean hidden = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_primary_school, container, false);

        initView();
        initData();
        initEvents();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
    }

    private void initEvents() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshing && !isNoMoreData) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (!isNoMoreData
                && newState == RecyclerView.SCROLL_STATE_IDLE
                && mGridLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            new GetData().execute(url + "gradeId=" + gradeId +
                    "&level1=" + version +
                    "&pageNum=" + pageNum +
                    "&pageSize=" + pageSize +
                    "&studentId=" + NewMainActivity.STUDENT_ID);
        }
    }

    /**
     * 更新数据
     */
    private void updateData() {
        pageNum = 1;
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);
        }
        isNoMoreData = false;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData().execute(url + "gradeId=" + gradeId +
                "&level1=" + version +
                "&pageNum=" + pageNum +
                "&pageSize=" + pageSize +
                "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    private void initData() {
        mList_version.clear();
        mList_version.add("人教版");
        adapter_version.notifyDataSetChanged();

        mList_grade.clear();
        mList_grade.add("一年级上");
        mList_grade.add("一年级下");
        mList_grade.add("二年级上");
        mList_grade.add("二年级下");
        mList_grade.add("三年级上");
        mList_grade.add("三年级下");
        mList_grade.add("四年级上");
        mList_grade.add("四年级下");
        mList_grade.add("五年级上");
        mList_grade.add("五年级下");
        mList_grade.add("六年级上");
        mList_grade.add("六年级下");
        adapter_grade.notifyDataSetChanged();

        if (NewClassesActivity.GRADE_CODE == 0) {
            grade = DataUtil.gradeCode2Grade(NewMainActivity.GRADE_ID, 0);
            if (!grade.equals("")) {
                for (int i = 0; i < mList_grade.size(); i++) {
                    if (grade.equals(mList_grade.get(i))) {
                        grade_position = i;
                        break;
                    }
                }
                gradeId = DataUtil.grade2GradeId(grade, 0);
                adapter_grade.setItemSelected(grade_position);
                recyclerView_grade.smoothScrollToPosition(grade_position);
            }
        }

        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        mList.clear();
        adapter.notifyDataSetChanged();
        pageNum = 1;
        new GetData().execute(url + "gradeId=" + gradeId +
                "&level1=" + version +
                "&pageNum=" + pageNum +
                "&pageSize=" + pageSize +
                "&studentId=" + NewMainActivity.STUDENT_ID);
        new GetVersionData().execute(versionGradeUrl);
    }

    private void initView() {
        recyclerView_version = view.findViewById(R.id.recycler_view_classes_version);
        recyclerView_grade = view.findViewById(R.id.recycler_view_classes_grade);

        frameLayout = view.findViewById(R.id.frame_primary_school);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_classes_primary);
        recyclerView = view.findViewById(R.id.recycler_view_classes_primary);

        LinearLayoutManager mLayoutManager_version = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager mLayoutManager_grade = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);

        recyclerView_version.setLayoutManager(mLayoutManager_version);
        recyclerView_grade.setLayoutManager(mLayoutManager_grade);
        recyclerView.setLayoutManager(mGridLayoutManager);

        adapter_version = new ClassesChooseRecyclerViewAdapter(getContext(), mList_version);
        adapter_grade = new ClassesChooseRecyclerViewAdapter(getContext(), mList_grade);
        adapter = new ClassesArticleRecyclerViewAdapter(getContext(), mList);

        adapter_version.setOnItemClickListener(
                new ClassesChooseRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        int position = recyclerView_version.getChildAdapterPosition(view);
                        if (position != version_position) {
                            version_position = position;
                            adapter_version.setItemSelected(position);
                            version = mList_version.get(position);
                            moveToMiddle(0, view);
                            updateData();
                        }
                    }
                });
        adapter_grade.setOnItemClickListener(
                new ClassesChooseRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        int position = recyclerView_grade.getChildAdapterPosition(view);
                        if (position != grade_position) {
                            grade_position = position;
                            adapter_grade.setItemSelected(position);
                            grade = mList_grade.get(position);
                            //code是年级代码，0表示小学，1表示初中，2表示高中
                            gradeId = DataUtil.grade2GradeId(grade, 0);
                            moveToMiddle(1, view);
                            updateData();
                        }
                    }
                });
        adapter.setOnItemClickListener(new ClassesArticleRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                turnToArticleDetail(position);
            }
        });

        recyclerView_version.setAdapter(adapter_version);
        recyclerView_grade.setAdapter(adapter_grade);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 滚动到中间位置
     */
    public void moveToMiddle(int i, View view) {
        int itemWidth = view.getWidth();
        int screenWidth = getResources().getDisplayMetrics().widthPixels
                - DensityUtil.dip2px(getContext(), 48);
        int scrollWidth = view.getLeft() - (screenWidth / 2 - itemWidth / 2);
        if (i == 0) {
            recyclerView_version.scrollBy(scrollWidth, 0);
        } else {
            recyclerView_grade.scrollBy(scrollWidth, 0);
        }
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

    /**
     * 获取数据失败，请稍后重试
     */
    private void noContent() {
        if (getContext() == null){
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            tv_tips.setText("获取数据失败，请稍后重试");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    updateData();
                }
            });
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showToast("获取数据失败，请稍后重试");
        }
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
                noMoreData();
            } else {
                noContent();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noContent();
        }
    }

    /**
     * 没有更多数据
     */
    private void noMoreData() {
        isNoMoreData = true;
    }

    /**
     * 显示吐丝
     *
     * @param toast 内容
     */
    private void showToast(String toast) {
        if (!hidden) {
            MyToastUtil.showToast(getContext(), toast);
        }
    }

    /**
     * 分析版本数据
     *
     * @param s
     */
    private void analyzeVersionData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            int status = json.getInt("status");
            if (status == 200) {
                JSONObject jsonObject = json.getJSONObject("data");
                JSONArray primaryArray = jsonObject.getJSONArray("小学");
                if (primaryArray.length() != 0) {
                    mList_version.clear();
                    for (int i = 0; i < primaryArray.length(); i++) {
                        String primaryVersion = primaryArray.getString(i);
                        mList_version.add(primaryVersion);
                    }
                    adapter_version.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据
     */
    private class GetData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                noContent();
            } else {
                analyzeData(s);
            }
            refreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 获取版本数据
     */
    private class GetVersionData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                analyzeVersionData(s);
            }
        }
    }

}
