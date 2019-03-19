package com.dace.textreader.fragment;

import android.content.Context;
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
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.adapter.UserVerticalListAdapter;
import com.dace.textreader.bean.UserBean;
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
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/10/29 0029 下午 3:09.
 * Version   1.0;
 * Describe :  搜索用户
 * History:
 * ==============================================================================
 */
public class SearchUserFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/user/search";
    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private View view;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;

    private String mSearchContent = "";

    private List<UserBean> mList = new ArrayList<>();
    private UserVerticalListAdapter adapter;
    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private int mPosition = -1;

    public boolean isReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_user, container, false);

        initView();
        initEvents();

        isReady = true;

        if (!mSearchContent.equals("") && mList.size() == 0) {
            refreshLayout.autoRefresh();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mList.size() == 0 && !mSearchContent.equals("")) {
                if (isReady) {
                    refreshLayout.autoRefresh();
                }
            }
        }
    }

    public String getSearchContent() {
        return mSearchContent;
    }

    public void setSearchContent(String searchContent) {
        if (!searchContent.equals(mSearchContent)) {
            mSearchContent = searchContent;
            if (isReady) {
                refreshLayout.autoRefresh();
            }
        }
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing) {
                    refreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing) {
                    if (isEnd) {
                        showTips("没有更多了~");
                        refreshLayout.finishLoadMore();
                    } else {
                        getMoreData();
                    }
                }
            }
        });
        adapter.setOnItemClickListen(new UserVerticalListAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList.size()) {
                    long id = mList.get(pos).getUserId();
                    turnToUserHomepage(id);
                }
            }
        });
        adapter.setOnItemFollowClickListen(new UserVerticalListAdapter.OnItemFollowClickListen() {
            @Override
            public void onFollowClick(int position) {
                if (position != -1 && position < mList.size()) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        if (mPosition == -1) {
                            int followed = mList.get(position).getFollowed();
                            if (followed == 1) {
                                unFollowUser(position);
                            } else {
                                followUser(position);
                            }
                        } else {
                            showTips("另一个操作进行中，请稍候...");
                        }
                    }
                }
            }
        });
    }

    public void initData() {
        if (!refreshing) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.setVisibility(View.GONE);
            }
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url, mSearchContent,
                    String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(pageNum));
        }
    }

    private void getMoreData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(this).execute(url, mSearchContent,
                String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(pageNum));
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_search_user_fragment);
        refreshLayout = view.findViewById(R.id.smart_refresh_search_user_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = view.findViewById(R.id.recycler_view_search_user_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserVerticalListAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivityForResult(intent, 0);
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
            startActivity(intent);
        }
    }

    /**
     * 关注用户
     *
     * @param position
     */
    private void followUser(int position) {
        mPosition = position;
        long userId = mList.get(position).getUserId();
        new FollowUser(this).execute(userFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 取消关注用户
     *
     * @param position
     */
    private void unFollowUser(int position) {
        mPosition = position;
        long userId = mList.get(position).getUserId();
        new FollowUser(this).execute(userUnFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
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
                        JSONObject user = object.getJSONObject("student");
                        UserBean userBean = new UserBean();
                        userBean.setUserId(user.optLong("studentid", -1L));
                        userBean.setUsername(user.getString("username"));
                        userBean.setUserImage(user.getString("userimg"));
                        userBean.setUserGrade(DataUtil.gradeCode2Chinese(
                                user.optInt("gradeid", 110)));
                        userBean.setFollowed(object.optInt("followed", 0));
                        mList.add(userBean);
                    }
                    adapter.notifyDataSetChanged();
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
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_search, imageView);
            tv_tips.setText("没有搜索到此用户");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("获取用户列表失败");
            tv_reload.setVisibility(View.VISIBLE);
            tv_reload.setText("重新获取");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    refreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败,请稍后再试~");
        }
    }

    /**
     * 分析关注用户数据
     *
     * @param s
     */
    private void analyzeFollowUserData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (mList.size() != 0 && mPosition < mList.size()) {

                    Bundle bundle = new Bundle();

                    if (mList.get(mPosition).getFollowed() == 1) {
                        mList.get(mPosition).setFollowed(0);
                        bundle.putInt("followed", 0);
                    } else {
                        mList.get(mPosition).setFollowed(1);
                        bundle.putInt("followed", 1);
                    }
                    adapter.notifyItemChanged(mPosition, bundle);
                }
                mPosition = -1;
            } else {
                errorFollowUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorFollowUser();
        }
    }

    /**
     * 关注用户失败
     */
    private void errorFollowUser() {
        if (mList.size() != 0 && mPosition < mList.size()) {
            if (mList.get(mPosition).getFollowed() == 1) {
                showTips("取消关注失败，请稍后重试~");
            } else {
                showTips("关注失败，请稍后重试~");
            }
        }
        mPosition = -1;
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
            extends WeakAsyncTask<String, Void, String, SearchUserFragment> {

        protected GetData(SearchUserFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SearchUserFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("content", strings[1]);
                object.put("studentId", strings[2]);
                object.put("pageNum", strings[3]);
                object.put("pageSize", 10);
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
        protected void onPostExecute(SearchUserFragment fragment, String s) {
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

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, SearchUserFragment> {

        protected FollowUser(SearchUserFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SearchUserFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("followingId", strings[1]);
                object.put("followerId", strings[2]);
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
        protected void onPostExecute(SearchUserFragment fragment, String s) {
            if (s == null) {
                fragment.errorFollowUser();
            } else {
                fragment.analyzeFollowUserData(s);
            }
        }
    }
}
