package com.dace.textreader.fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.NewCollectionActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.WritingCollectionRecyclerViewAdapter;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.listen.OnCollectionEditorListen;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/3/21 0021 上午 10:03.
 * Version   1.0;
 * Describe :作文收藏页
 * History:
 * ==============================================================================
 */

public class WritingCollectionFragment extends Fragment {

    private final String url = HttpUrlPre.HTTP_URL + "/writing/collection/query";
    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/writing/collection/delete";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;

    private List<WritingBean> mList = new ArrayList<>();
    private List<WritingBean> mSelectedList = new ArrayList<>();
    private int selectedNum = 0;  //选中的item数量

    private WritingCollectionRecyclerViewAdapter adapter;

    private boolean isEditor = false;  //是否处于编辑模式
    private boolean hasSelected = false;
    private boolean refreshing = false;  //是否正在刷新
    private boolean isEnd = false;

    private int pageNum = 1;

    private NewCollectionActivity activity;

    private boolean hidden = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_writing_new_collection, container, false);

        initView();
        swipeRefreshLayout.autoRefresh();
        initEvents();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            activity.setOnEditorClickListen(new OnCollectionEditorListen() {
                @Override
                public void OnEditorOpen(String tag) {
                    if (tag.equals("writing")) {
                        editorOrCancel(true);
                    }
                }

                @Override
                public void OnEditorCancel(String tag) {
                    if (tag.equals("writing")) {
                        editorOrCancel(false);
                    }
                }

                @Override
                public void OnSelectAll(String tag, boolean selectAll) {
                    if (tag.equals("writing")) {
                        selectAllOrNot(selectAll);
                    }
                }

                @Override
                public void OnDeleteData(String tag) {
                    if (tag.equals("writing")) {
                        deleteWriting();
                    }
                }
            });
        }
    }

    private void initEvents() {
        activity = (NewCollectionActivity) getContext();
        activity.setOnEditorClickListen(new OnCollectionEditorListen() {
            @Override
            public void OnEditorOpen(String tag) {
                if (tag.equals("writing")) {
                    editorOrCancel(true);
                }
            }

            @Override
            public void OnEditorCancel(String tag) {
                if (tag.equals("writing")) {
                    editorOrCancel(false);
                }
            }

            @Override
            public void OnSelectAll(String tag, boolean selectAll) {
                if (tag.equals("writing")) {
                    selectAllOrNot(selectAll);
                }
            }

            @Override
            public void OnDeleteData(String tag) {
                if (tag.equals("writing")) {
                    deleteWriting();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!isEditor && !refreshing) {
                    initData();
                } else {
                    swipeRefreshLayout.finishRefresh();
                }
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEnd && mList.size() != 0) {
                    getMoreData();
                } else {
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });
        adapter.setOnWritingCollectionItemClick(new WritingCollectionRecyclerViewAdapter
                .OnWritingCollectionItemClick() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (isEditor) {
                        selectedOrNot(pos);
                    } else {
                        turnToArticleDetail(pos);
                    }
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
     * 全选或者取消全选
     *
     * @param selectAll
     */
    private void selectAllOrNot(boolean selectAll) {
        for (int i = 0; i < mList.size(); i++) {
            if (selectAll) {
                checked(i);
            } else {
                unchecked(i);
            }
        }
        updateDeleteButtonBg();
    }

    /**
     * 查看文章详细内容
     *
     * @param pos
     */
    private void turnToArticleDetail(int pos) {
        Intent intent = new Intent(getContext(), CompositionDetailActivity.class);
        intent.putExtra("writingId", mList.get(pos).getId());
        intent.putExtra("orderNum", "");
        intent.putExtra("area", 0);
        startActivity(intent);
    }

    /**
     * 选中或取消选中
     *
     * @param pos
     */
    private void selectedOrNot(int pos) {
        WritingBean writingBean = mList.get(pos);
        if (writingBean.isSelected()) {
            unchecked(pos);
        } else {
            checked(pos);
        }
        updateDeleteButtonBg();
    }

    /**
     * 选中
     *
     * @param pos
     */
    private void checked(int pos) {
        mList.get(pos).setSelected(true);
        adapter.notifyItemChanged(pos);
        selectedNum++;
    }

    /**
     * 不选中
     *
     * @param pos
     */
    private void unchecked(int pos) {
        mList.get(pos).setSelected(false);
        adapter.notifyItemChanged(pos);
        selectedNum--;
    }

    /**
     * 更新删除按钮的背景
     */
    private void updateDeleteButtonBg() {
        hasSelected = false;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isSelected()) {
                hasSelected = true;
                break;
            }
        }
        if (mHasWritingItemSelected != null) {
            mHasWritingItemSelected.hasItemSelected(hasSelected);
        }
    }

    private void deleteWriting() {
        if (selectedNum != 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).isSelected()) {
                    mSelectedList.add(mList.get(i));
                    array.put(mList.get(i).getId());
                }
            }

            if (mSelectedList.size() != 0) {
                new DeleteData(this).execute(deleteUrl, array.toString());
            }

            for (int j = 0; j < mSelectedList.size(); j++) {
                mList.remove(mSelectedList.get(j));
            }
            mSelectedList.clear();

            adapter.notifyDataSetChanged();
        }

        editorOrCancel(false);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        pageNum++;
        refreshing = true;
        new GetData(this).execute(url, String.valueOf(pageNum));
    }

    /**
     * 点击了编辑按钮
     *
     * @param isEditor
     */
    private void editorOrCancel(boolean isEditor) {
        this.isEditor = isEditor;
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setEditor(isEditor);
            mList.get(i).setSelected(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        if (!refreshing) {
            pageNum = 1;
            refreshing = true;
            isEnd = false;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url, String.valueOf(pageNum));
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_writing_new_collection);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_writing_new_collection);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        recyclerView = view.findViewById(R.id.recycler_view_writing_new_collection);
        mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        //得到AssetManager
        AssetManager mgr = getContext().getAssets();
        //根据路径得到Typeface
        Typeface score = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        adapter = new WritingCollectionRecyclerViewAdapter(getContext(), mList, score);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 获取数据之后
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        WritingBean writingBean = new WritingBean();
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setUserId(object.getString("studentId"));
                        writingBean.setUsername(object.getString("username"));
                        if (object.getString("gradeid").equals("") ||
                                object.getString("gradeid").equals("null")) {
                            writingBean.setUserGrade("一年级");
                        } else {
                            writingBean.setUserGrade(DataUtil.gradeCode2Chinese(object.optInt("gradeid", 110)));
                        }
                        writingBean.setUserImg(object.getString("userimg"));
                        if (!writingBean.getContent().equals("")
                                && !writingBean.getContent().equals("null")) {
                            writingBean.setStatus(object.optInt("status", -1));
                            if (object.getString("mark").equals("")
                                    || object.getString("mark").equals("null")) {
                                writingBean.setMark(-1);
                            } else {
                                writingBean.setMark(object.optInt("mark", -1));
                            }
                            writingBean.setPrize(object.getString("prize"));
                            writingBean.setComment(object.getString("comment"));
                            String views = object.getString("pv");
                            if (views.equals("") || views.equals("null")) {
                                writingBean.setViews("0");
                            } else {
                                writingBean.setViews(views);
                            }
                            writingBean.setTaskId(object.getString("taskId"));
                            writingBean.setTaskName(object.getString("matchName"));
                        }
                        writingBean.setEditor(isEditor);
                        writingBean.setSelected(false);
                        mList.add(writingBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (mListen != null) {
                        mListen.onLoadResult(true);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
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
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            if (mListen != null) {
                mListen.onLoadResult(false);
            }
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_collect, imageView);
            tv_tips.setText("暂无作文收藏");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        if (mList.size() == 0) {
            if (mListen != null) {
                mListen.onLoadResult(false);
            }
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后再试");
        }
    }

    private OnListDataOperateListen mListen;

    public void setOnListDataOperateListen(OnListDataOperateListen onListDataOperateListen) {
        this.mListen = onListDataOperateListen;
    }

    /**
     * 显示提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        if (!hidden) {
            MyToastUtil.showToast(getContext(), tips);
        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, WritingCollectionFragment> {

        protected GetData(WritingCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingCollectionFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("pageNum", params[1]);
                json.put("pageSize", 10);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingCollectionFragment fragment, String s) {
            fragment.refreshing = false;
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.swipeRefreshLayout.finishRefresh();
            fragment.swipeRefreshLayout.finishLoadMore();
        }
    }

    /**
     * 删除文章
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Integer, String, WritingCollectionFragment> {

        protected DeleteData(WritingCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingCollectionFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("writingId", params[1]);
                json.put("studentId", NewMainActivity.STUDENT_ID);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingCollectionFragment fragment, String s) {

        }
    }

    public interface HasWritingItemSelected {
        void hasItemSelected(boolean hasSelected);
    }

    private HasWritingItemSelected mHasWritingItemSelected;

    public void setHasWritingItemSelectedListen(HasWritingItemSelected hasWritingItemSelected) {
        this.mHasWritingItemSelected = hasWritingItemSelected;
    }
}
