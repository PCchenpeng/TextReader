package com.dace.textreader.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.FansListActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.adapter.HomeRecommendationAdapter;
import com.dace.textreader.adapter.UserHorizontalListAdapter;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.diff.HomeDiffCallBack;
import com.dace.textreader.listen.OnUserInfoClickListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.VersionInfoUtil;
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
 * Created time 2018/10/25 0025 上午 10:01.
 * Version   1.0;
 * Describe :  首页关注页
 * History:
 * ==============================================================================
 */
public class HomeFollowFragment extends BaseFragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/my/release/composition";
    private static final String userUrl = HttpUrlPre.HTTP_URL + "/follow/recommend";
    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private View view;
    private SmartRefreshLayout refreshLayout;
    private LinearLayout ll_user;
    private LinearLayout ll_user_refresh;
    private RecyclerView recyclerView_user;
    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;
    private FrameLayout frameLayout;

    private Context mContext;

    private List<HomeRecommendationBean> mList = new ArrayList<>();
    private HomeRecommendationAdapter adapter;
    private int pageNum = 1;
    private boolean isLoading = false;  //是否正在加载中
    private boolean isEndData = false;  //是否没有数据了

    private List<UserBean> mList_user = new ArrayList<>();
    private UserHorizontalListAdapter adapter_user;
    private LinearLayoutManager layoutManager_user;
    private int pageNum_user = 1;
    private int mPosition_user = -1;
    private boolean isRefreshing_user = false;

    private boolean isRefresh = false;
    private List<HomeRecommendationBean> newList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_follow, container, false);

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
                if (isLoading) {
                    refreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!isEndData && !isLoading && mList.size() != 0) {
                    getMoreData();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
        ll_user_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRefreshing_user) {
                    if (mPosition_user == -1) {
                        getMoreUserData();
                    } else {
                        showTips("另一个操作进行中，请稍候...");
                    }
                }
            }
        });
        adapter.setOnItemClickListen(new HomeRecommendationAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList.size()) {
                    int type = mList.get(pos).getType();
                    if (type == 0) {
                        String writingId = mList.get(pos).getCompositionId();
                        turnToWritingDetail(writingId);
                        //增加阅读数
                        String views = mList.get(pos).getViews();
                        mList.get(pos).setViews(DataUtil.increaseViews(views));
                        adapter.notifyItemChanged(pos);
                    }
                }
            }
        });
        adapter.setOnUserInfoClickListen(new OnUserInfoClickListen() {
            @Override
            public void onClick(long userId) {
                turnToUserHomepage(userId);
            }
        });
        adapter_user.setOnItemClickListen(new UserHorizontalListAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_user.getChildAdapterPosition(view);

                if (pos != -1 && pos < mList_user.size()) {
                    long userId = mList_user.get(pos).getUserId();
                    turnToUserHomepage(userId);
                } else if (pos == mList_user.size() && mList_user.size() != 0) {
                    turnToMoreUser();
                }
            }
        });
        adapter_user.setOnItemFollowClickListen(new UserHorizontalListAdapter.OnItemFollowClickListen() {
            @Override
            public void onFollowClick(int position) {
                if (position != -1 && position < mList_user.size()) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        if (mPosition_user == -1) {
                            if (mList_user.get(position).getUserId() == NewMainActivity.STUDENT_ID) {
                                showTips("自己不能关注自己喔~");
                            } else {
                                int followed = mList_user.get(position).getFollowed();
                                if (followed == 1) {
                                    unFollowUser(position);
                                } else {
                                    followUser(position);
                                }
                            }
                        } else {
                            showTips("另一个操作进行中，请稍候...");
                        }
                    }
                }
            }
        });
        setOnScrollListener(nestedScrollView);
    }

    /**
     * 前往更多用户
     */
    private void turnToMoreUser() {
        Intent intent = new Intent(mContext, FansListActivity.class);
        intent.putExtra("type", "guess");
        intent.putExtra("userId", -1);
        startActivity(intent);
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
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        isLoading = true;
        isRefresh = false;
        pageNum = pageNum + 1;
        new GetData(this).execute(url,
                String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum));
    }

    /**
     * 前往作文详情
     *
     * @param id
     */
    private void turnToWritingDetail(String id) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", id);
        intent.putExtra("area", 0);
        intent.putExtra("orderNum", "");
        startActivity(intent);
    }

    /**
     * 关注用户
     *
     * @param position
     */
    private void followUser(int position) {
        mPosition_user = position;
        long userId = mList_user.get(position).getUserId();
        new FollowUser(this).execute(userFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 取消关注用户
     *
     * @param position
     */
    private void unFollowUser(int position) {
        mPosition_user = position;
        long userId = mList_user.get(position).getUserId();
        new FollowUser(this).execute(userUnFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    private void initData() {
        if (!isLoading) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.setVisibility(View.GONE);
            }
            isRefresh = true;
            isLoading = true;
            isEndData = false;
            pageNum = 1;
            new GetData(this).execute(url,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum));
        }
    }

    private void initUserData() {
        if (!isRefreshing_user) {
            isRefreshing_user = true;
            pageNum_user = 1;
            new GetUserData(this).execute(userUrl,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum_user));
        }
    }

    /**
     * 获取更多用户数据
     */
    private void getMoreUserData() {
        isRefreshing_user = true;
        pageNum_user = pageNum_user + 1;
        new GetUserData(this).execute(userUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum_user));
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_home_follow_fragment);
        refreshLayout = view.findViewById(R.id.smart_refresh_home_follow_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext),0,0);
        refreshLayout.setEnableAutoLoadMore(true);

        ll_user = view.findViewById(R.id.ll_user_home_follow_fragment);
        ll_user_refresh = view.findViewById(R.id.ll_refresh_user_home_follow_fragment);
        recyclerView_user = view.findViewById(R.id.recycler_view_user_home_follow_fragment);
        layoutManager_user = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_user.setLayoutManager(layoutManager_user);
        adapter_user = new UserHorizontalListAdapter(mContext, mList_user);
        recyclerView_user.setAdapter(adapter_user);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        recyclerView = view.findViewById(R.id.recycler_view_home_follow_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new HomeRecommendationAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
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
                    newList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        HomeRecommendationBean bean = new HomeRecommendationBean();
                        JSONObject object = array.getJSONObject(i);
                        bean.setType(0);
                        bean.setCompositionId(object.getString("compositionId"));
                        bean.setCompositionArea(0);
                        bean.setCompositionScore(String.valueOf(object.optInt("mark", 0)));
                        bean.setCompositionPrize(object.getString("prize"));
                        bean.setCompositionAvgScore(object.getString("avgScore"));
                        bean.setTitle(object.getString("article"));
                        bean.setContent(object.getString("content"));
                        bean.setImage(object.getString("cover"));
                        if (object.getString("saveTime").equals("")
                                || object.getString("saveTime").equals("null")) {
                            bean.setDate("2018-01-01");
                        } else {
                            bean.setDate(DateUtil.time2Format(object.getString("saveTime")));
                        }
                        bean.setViews(object.getString("pv"));
                        bean.setUserId(object.optLong("studentId", -1));
                        bean.setUserName(object.getString("username"));
                        bean.setUserImage(object.getString("userimg"));
                        bean.setUserGrade(DataUtil.gradeCode2Chinese(
                                object.optInt("gradeid", 111)));
                        newList.add(bean);
                    }
                    if (isRefresh) {
                        diffResultThread();
                    } else {
                        adapter.addData(newList);
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
     * 计算DiffResult线程
     */
    private void diffResultThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                        new HomeDiffCallBack(mList, newList), true);
                Message message = mHandler.obtainMessage(0);
                message.obj = diffResult;//obj存放DiffResult
                message.sendToTarget();
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //取出Result
                    DiffUtil.DiffResult diffResult = (DiffUtil.DiffResult) msg.obj;
                    updateListUi(diffResult);
                    break;
            }
        }
    };

    /**
     * 更新列表
     *
     * @param diffResult
     */
    private void updateListUi(DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                adapter.notifyItemRangeInserted(position, count);
                if (position == 0) {
                    recyclerView.scrollToPosition(0);
                }
            }

            @Override
            public void onRemoved(int position, int count) {
                adapter.notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count, @Nullable Object payload) {
                adapter.notifyItemRangeChanged(position, count, payload);
            }
        });
        mList.clear();
        mList.addAll(newList);
        adapter.setList(mList);
    }

    /**
     * 数据为空
     */
    private void emptyData() {
        isEndData = true;
        if (mList.size() == 0) {
            initUserData();
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (mList.size() == 0) {
            initUserData();
        } else {
            showTips("获取数据失败，请稍后再试~");
        }
    }

    /**
     * 分析用户数据
     */
    private void analyzeUserData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorUserData();
                } else {
                    mList_user.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject user = array.getJSONObject(i);
                        UserBean userBean = new UserBean();
                        userBean.setUserId(user.optLong("studentId", -1L));
                        userBean.setUsername(user.getString("username"));
                        userBean.setUserImage(user.getString("userImg"));
                        userBean.setUserGrade(DataUtil.gradeCode2Chinese(
                                user.optInt("gradeId", 110)));
                        userBean.setCompositionNum(user.getString("composition_num"));
                        userBean.setFollowed(user.optInt("followed", 0));
                        mList_user.add(userBean);
                    }
                    adapter_user.notifyDataSetChanged();
                    if (ll_user.getVisibility() == View.GONE) {
                        ll_user.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                errorUserData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorUserData();
        }
    }

    /**
     * 获取用户数据失败
     */
    private void errorUserData() {
        if (mList_user.size() == 0) {
            showErrorView();
        }
    }

    /**
     * 显示错误界面
     */
    private void showErrorView() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_tips.setText("获取内容为空，下拉可以刷新哦~~");
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
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
                if (mList_user.size() != 0 && mPosition_user < mList_user.size()) {

                    Bundle bundle = new Bundle();

                    if (mList_user.get(mPosition_user).getFollowed() == 1) {
                        mList_user.get(mPosition_user).setFollowed(0);
                        bundle.putInt("followed", 0);
                    } else {
                        mList_user.get(mPosition_user).setFollowed(1);
                        bundle.putInt("followed", 1);
                    }

                    adapter_user.notifyItemChanged(mPosition_user, bundle);
                }
                mPosition_user = -1;
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
        if (mList_user.size() != 0 && mPosition_user < mList_user.size()) {
            if (mList_user.get(mPosition_user).getFollowed() == 1) {
                showTips("取消关注失败，请稍后重试~");
            } else {
                showTips("关注失败，请稍后重试~");
            }
        }
        mPosition_user = -1;
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
            extends WeakAsyncTask<String, Void, String, HomeFollowFragment> {

        protected GetData(HomeFollowFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(HomeFollowFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", strings[2]);
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
        protected void onPostExecute(HomeFollowFragment fragment, String s) {
            fragment.refreshLayout.finishRefresh();
            fragment.refreshLayout.finishLoadMore();
            if (s == null) {
                fragment.errorData();
            } else {
                fragment.analyzeData(s);
            }
            fragment.isLoading = false;
        }
    }

    /**
     * 获取用户数据
     */
    private static class GetUserData
            extends WeakAsyncTask<String, Void, String, HomeFollowFragment> {

        protected GetUserData(HomeFollowFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(HomeFollowFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                object.put("appVersion",VersionInfoUtil.getVersionName(fragment.mContext));
                object.put("platform","android");
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
        protected void onPostExecute(HomeFollowFragment fragment, String s) {
            if (s == null) {
                fragment.errorUserData();
            } else {
                fragment.analyzeUserData(s);
            }
            fragment.isRefreshing_user = false;
        }
    }

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, HomeFollowFragment> {

        protected FollowUser(HomeFollowFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(HomeFollowFragment fragment, String[] strings) {
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
        protected void onPostExecute(HomeFollowFragment fragment, String s) {
            if (s == null) {
                fragment.errorFollowUser();
            } else {
                fragment.analyzeFollowUserData(s);
            }
        }
    }

}
