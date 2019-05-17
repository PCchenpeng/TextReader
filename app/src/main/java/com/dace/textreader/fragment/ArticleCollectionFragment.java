package com.dace.textreader.fragment;

import android.content.Intent;
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
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.HomeAudioDetailActivity;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewCollectionActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.ArticleCollectionRecyclerViewAdapter;
import com.dace.textreader.bean.Article;
import com.dace.textreader.bean.CollectArticleBean;
import com.dace.textreader.bean.FollowBean;
import com.dace.textreader.listen.OnCollectionEditorListen;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
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
 * Created time 2018/3/19 0019 上午 10:41.
 * Version   1.0;
 * Describe :  文章收藏页
 * History:
 * ==============================================================================
 */

public class ArticleCollectionFragment extends Fragment {

    private final String url = HttpUrlPre.HTTP_URL_ + "/select/essay/collect";
    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/essays/collect/delete";

    private View view;

    private FrameLayout frameLayout;

    private SmartRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;

    private List<CollectArticleBean.DataBean> mList = new ArrayList<>();
    private List<CollectArticleBean.DataBean> mSelectedList = new ArrayList<>();
    private int selectedNum = 0;  //选中的item数量

    private ArticleCollectionRecyclerViewAdapter adapter;

    private boolean isEditor = false;  //是否处于编辑模式
    private boolean hasSelected = false;
    private boolean refreshing = false;  //是否正在刷新
    private boolean isEnd = false;
    private int pageNum = 1;

    private int position = -1; //被点击进行跳转的索引，用于改变浏览次数和点赞次数

    private NewCollectionActivity activity;

    private boolean hidden = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_article_new_collection, container, false);

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
                    if (tag.equals("article")) {
                        editorOrCancel(true);
                    }
                }

                @Override
                public void OnEditorCancel(String tag) {
                    if (tag.equals("article")) {
                        editorOrCancel(false);
                    }
                }

                @Override
                public void OnSelectAll(String tag, boolean selectAll) {
                    if (tag.equals("article")) {
                        selectAllOrNot(selectAll);
                    }
                }

                @Override
                public void OnDeleteData(String tag) {
                    if (tag.equals("article")) {
                        deleteArticle();
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
                if (tag.equals("article")) {
                    editorOrCancel(true);
                }
            }

            @Override
            public void OnEditorCancel(String tag) {
                if (tag.equals("article")) {
                    editorOrCancel(false);
                }
            }

            @Override
            public void OnSelectAll(String tag, boolean selectAll) {
                if (tag.equals("article")) {
                    selectAllOrNot(selectAll);
                }
            }

            @Override
            public void OnDeleteData(String tag) {
                if (tag.equals("article")) {
                    deleteArticle();
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
        adapter.setOnItemClickListener(new ArticleCollectionRecyclerViewAdapter.OnCollectionArticleItemClick() {
            @Override
            public void onItemClick(int pos) {
                if (isEditor) {
                    selectedOrNot(pos);
                } else {
                    if (mList.get(pos).getFlag() == 0) {
                        turnToArticleDetail(pos);
                    } else if (mList.get(pos).getFlag() == 1){
                        turnToHomeAudioDetail(pos);
                    }
                }
            }
        });
        adapter.setOnDeleteArticleItemClick(new ArticleCollectionRecyclerViewAdapter.OnDeleteArticleItemClick() {
            @Override
            public void onClick(int position) {
                long id = mList.get(position).getId();
                deleteSingleArticle(id);
                mList.remove(position);
                adapter.notifyDataSetChanged();
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
        this.position = pos;
        CollectArticleBean.DataBean article = mList.get(pos);
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("essayId", article.getId() + "");
        intent.putExtra("imgUrl", article.getImage());
//        intent.putExtra("type", type);
        startActivityForResult(intent, 0);
    }

        //跳转绘本
    /**
     * 查看文章详细内容
     *
     * @param pos
     */
    private void turnToHomeAudioDetail(int pos) {
        int py = mList.get(pos).getScore();
        int id = mList.get(pos).getId();
        Intent intent = new Intent(getContext(), HomeAudioDetailActivity.class);
        intent.putExtra("id", id + "");
        intent.putExtra("py", py);
        getContext().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (position != -1) {
                if (data.getBooleanExtra("clickLike", false)) {
                    int likeNum = mList.get(position).getLikeNum();
                    mList.get(position).setLikeNum(likeNum + 1);
                }
                int views = mList.get(position).getPv();
                mList.get(position).setPv(views + 1);
                adapter.notifyItemChanged(position);
                position = -1;
            }
        }
    }

    /**
     * 选中或取消选中
     *
     * @param pos
     */
    private void selectedOrNot(int pos) {
        CollectArticleBean.DataBean article = mList.get(pos);
        if (article.isSelected()) {
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
        if (mHasArticleItemSelected != null) {
            mHasArticleItemSelected.hasItemSelected(hasSelected);
        }
    }

    /**
     * 删除单个文章
     */
    private void deleteSingleArticle(long id) {
        JSONArray array = new JSONArray();
        array.put(id);
        new DeleteData(this).execute(deleteUrl, array.toString());
    }

    /**
     * 删除文章
     */
    private void deleteArticle() {
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
        new GetData(this)
                .execute(url,NewMainActivity.STUDENT_ID + "",pageNum + "","750","420");
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
            new GetData(this)
                    .execute(url,NewMainActivity.STUDENT_ID + "",pageNum + "","750","420");
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_article_new_collection);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_article_new_collection);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        recyclerView = view.findViewById(R.id.recycler_view_article_new_collection);
        mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ArticleCollectionRecyclerViewAdapter(getContext(), mList);
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
                    CollectArticleBean collectArticleBean = GsonUtil.GsonToBean(s,CollectArticleBean.class);
                    mList.addAll(collectArticleBean.getData());
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject essay = jsonArray.getJSONObject(i);
//                        Article article = new Article();
//                        article.setId(essay.optLong("id", -1));
//                        article.setType(essay.optInt("type", -1));
//                        article.setTitle(essay.getString("title"));
//                        article.setContent(essay.getString("content"));
//                        article.setGrade(essay.optInt("grade", 110));
//                        article.setPyScore(essay.getString("score"));
//                        article.setViews(essay.optInt("pv", 0));
//                        article.setImagePath(essay.getString("image"));
//                        article.setEditor(isEditor);
//                        article.setSelected(false);
//                        mList.add(article);
//                    }
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
            tv_tips.setText("暂无文章收藏");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
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
            showTips("获取数据失败，请稍后重试");
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
            extends WeakAsyncTask<String, Integer, String, ArticleCollectionFragment> {

        protected GetData(ArticleCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleCollectionFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", params[1]);
                object.put("pageNum", params[2]);
                object.put("width", params[3]);
                object.put("height", params[4]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(params[0])
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
        protected void onPostExecute(ArticleCollectionFragment fragment, String s) {
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
            extends WeakAsyncTask<String, Integer, String, ArticleCollectionFragment> {

        protected DeleteData(ArticleCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleCollectionFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("essayIds", params[1]);
                json.put("status", 0);
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
        protected void onPostExecute(ArticleCollectionFragment fragment, String s) {

        }
    }

    public interface HasArticleItemSelected {
        void hasItemSelected(boolean hasSelected);
    }

    private HasArticleItemSelected mHasArticleItemSelected;

    public void setHasArticleItemSelectedListen(HasArticleItemSelected hasArticleItemSelected) {
        this.mHasArticleItemSelected = hasArticleItemSelected;
    }
}
