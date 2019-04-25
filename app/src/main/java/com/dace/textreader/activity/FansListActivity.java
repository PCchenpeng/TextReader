package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.UserVerticalListAdapter;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
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
 * 用户列表
 * 包括粉丝、关注、系统推荐用户
 */
public class FansListActivity extends BaseActivity {

    private static final String url_guess = HttpUrlPre.HTTP_URL + "/follow/recommend";
    private static final String url_fans = HttpUrlPre.HTTP_URL + "/follower/query";
    private static final String url_follow = HttpUrlPre.HTTP_URL + "/following/query";

    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private static final String TYPE_FANS_GUESS = "guess";  //猜你喜欢
    private static final String TYPE_FANS_USER = "fans";  //粉丝列表
    private static final String TYPE_FANS_OTHER = "follow";  //关注列表

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private FansListActivity mContext;

    private String type_fans;  //列表数据类型，fans粉丝，follow关注
    private long userId;
    private String url = "";

    private List<UserBean> mList = new ArrayList<>();
    private UserVerticalListAdapter adapter;
    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_list);

        mContext = this;

        type_fans = getIntent().getStringExtra("type");
        userId = getIntent().getLongExtra("userId", -1);

        if (type_fans.equals(TYPE_FANS_USER)) {
            url = url_fans;
        } else if (type_fans.equals(TYPE_FANS_OTHER)) {
            url = url_follow;
        } else if (type_fans.equals(TYPE_FANS_GUESS)) {
            url = url_guess;
        }

        initView();
        refreshLayout.autoRefresh();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing) {
                    refreshLayout.finishRefresh();
                } else {
                    if (type_fans.equals(TYPE_FANS_GUESS)) {
                        initGuessData();
                    } else {
                        initData();
                    }
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing) {
                    if (isEnd) {
                        refreshLayout.finishLoadMore();
                    } else {
                        if (type_fans.equals(TYPE_FANS_GUESS)) {
                            getMoreGuessData();
                        } else {
                            getMoreData();
                        }
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

    /**
     * 初始化“猜你喜欢”数据
     */
    private void initGuessData() {
        if (!refreshing) {
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetGuessData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(pageNum));
        }
    }

    /**
     * 获取更多“猜你喜欢”数据
     */
    private void getMoreGuessData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetGuessData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(pageNum));
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

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(mContext).execute(url, String.valueOf(userId), String.valueOf(pageNum),
                    String.valueOf(NewMainActivity.STUDENT_ID));
        }
    }

    private void getMoreData() {
        refreshing = true;
        pageNum = pageNum + 1;
        new GetData(mContext).execute(url, String.valueOf(userId), String.valueOf(pageNum),
                String.valueOf(NewMainActivity.STUDENT_ID));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        if (type_fans.equals(TYPE_FANS_USER)) {
            tv_title.setText("粉丝");
        } else if (type_fans.equals(TYPE_FANS_OTHER)) {
            tv_title.setText("关注");
        } else if (type_fans.equals(TYPE_FANS_GUESS)) {
            tv_title.setText("猜你喜欢");
        }

        frameLayout = findViewById(R.id.frame_fans_list);

        refreshLayout = findViewById(R.id.smart_refresh_fans_list);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));

        recyclerView = findViewById(R.id.recycler_view_fans_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserVerticalListAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            refreshLayout.autoRefresh();
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        JSONObject user = object.getJSONObject("student");
                        UserBean userBean = new UserBean();
                        userBean.setUserId(user.optLong("studentId", -1L));
                        userBean.setUsername(user.getString("username"));
                        userBean.setUserImage(user.getString("userImg"));
                        userBean.setUserGrade(DataUtil.gradeCode2Chinese(
                                user.optInt("gradeId", 110)));
                        userBean.setCompositionNum(user.getString("composition_num"));
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
     * 分析“猜你喜欢”数据
     *
     * @param s
     */
    private void analyzeGuessData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
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
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            if (type_fans.equals(TYPE_FANS_USER)) {
                tv_tips.setText("暂无粉丝");
            } else if (type_fans.equals(TYPE_FANS_OTHER)) {
                tv_tips.setText("暂无关注");
            } else if (type_fans.equals(TYPE_FANS_GUESS)) {
                tv_tips.setText("暂无内容");
            }
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
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            if (type_fans.equals(TYPE_FANS_USER)) {
                tv_tips.setText("获取粉丝列表失败");
            } else if (type_fans.equals(TYPE_FANS_OTHER)) {
                tv_tips.setText("获取关注列表失败");
            } else if (type_fans.equals(TYPE_FANS_GUESS)) {
                tv_tips.setText("暂无列表失败");
            }
            tv_reload.setVisibility(View.VISIBLE);
            tv_reload.setText("重新获取");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshLayout.autoRefresh();
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
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
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, FansListActivity> {

        protected GetData(FansListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(FansListActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                object.put("viewerId", strings[3]);
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
        protected void onPostExecute(FansListActivity activity, String s) {
            activity.refreshLayout.finishRefresh();
            activity.refreshLayout.finishLoadMore();
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
            activity.refreshing = false;
        }
    }

    /**
     * 获取“猜你喜欢”数据
     */
    private static class GetGuessData
            extends WeakAsyncTask<String, Void, String, FansListActivity> {

        protected GetGuessData(FansListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(FansListActivity activity, String[] strings) {
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
        protected void onPostExecute(FansListActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeGuessData(s);
            }
            activity.refreshing = false;
            activity.refreshLayout.finishRefresh();
            activity.refreshLayout.finishLoadMore();
        }
    }

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, FansListActivity> {

        protected FollowUser(FansListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(FansListActivity activity, String[] strings) {
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
        protected void onPostExecute(FansListActivity activity, String s) {
            if (s == null) {
                activity.errorFollowUser();
            } else {
                activity.analyzeFollowUserData(s);
            }
        }
    }

}
