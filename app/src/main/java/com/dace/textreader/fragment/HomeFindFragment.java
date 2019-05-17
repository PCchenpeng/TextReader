package com.dace.textreader.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.adapter.HomeRecommendationAdapter;
import com.dace.textreader.adapter.IntroductionAdapter;
import com.dace.textreader.bean.BannerBean;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.diff.BannerDiffCallBack;
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

import java.io.IOException;
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
 * Created time 2018/10/24 0024 下午 4:50.
 * Version   1.0;
 * Describe :  首页发现页
 * History:
 * ==============================================================================
 */
public class HomeFindFragment extends BaseFragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/discover/recommend/list";
    private static final String bannerUrl = HttpUrlPre.HTTP_URL + "/introduction/banner";

    private View view;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private LinearLayout ll_introduction;
    private RecyclerView recyclerView_introduction;
    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;

    private Context mContext;

    private List<BannerBean> mList_banner = new ArrayList<>();
    private IntroductionAdapter adapter_banner;

    private List<HomeRecommendationBean> mList = new ArrayList<>();
    private HomeRecommendationAdapter adapter;

    private int pageNum = 1;
    private boolean isLoading = false;  //是否正在加载中
    private boolean isEndData = false;  //是否没有数据了
    private boolean isRefresh = false;

    private List<HomeRecommendationBean> newList = new ArrayList<>();
    private List<BannerBean> newBannerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_find, container, false);

        initView();
        refreshLayout.autoRefresh();
        initEvents();

        return view;
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
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
        adapter_banner.setOnItemClickListen(new IntroductionAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_introduction.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList_banner.size()) {
                    String name = mList_banner.get(pos).getName();
                    Intent intent = new Intent(getContext(), EventsActivity.class);
                    intent.putExtra("pageName", name);
                    startActivity(intent);
                }
            }
        });

        setOnScrollListener(nestedScrollView);
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

    private void initData() {
        if (!isLoading) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.setVisibility(View.GONE);
            }
            isLoading = true;
            isRefresh = true;
            isEndData = false;
            pageNum = 1;
            new GetBannerData(this).execute(bannerUrl);
            new GetData(this).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(pageNum));
        }
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        isLoading = true;
        isRefresh = false;
        pageNum = pageNum + 1;
        new GetData(this).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(pageNum));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_home_find_fragment);
        refreshLayout = view.findViewById(R.id.smart_refresh_home_find_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext),0,0);
        refreshLayout.setEnableAutoLoadMore(true);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        recyclerView = view.findViewById(R.id.recycler_view_home_find_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeRecommendationAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        ll_introduction = view.findViewById(R.id.ll_introduction_home_find_fragment);
        recyclerView_introduction = view.findViewById(R.id.recycler_view_introduction_home_find_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_introduction.setLayoutManager(linearLayoutManager);
        adapter_banner = new IntroductionAdapter(mContext, mList_banner);
        recyclerView_introduction.setAdapter(adapter_banner);
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
     * 分析获取的作文推荐列表数据
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
                        JSONObject json = array.getJSONObject(i);
                        JSONObject object = json.getJSONObject("product");
                        bean.setType(json.optInt("productType", 0));
                        if (json.optInt("productType", 0) == 0) {
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
                        }
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
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
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
                case 1:
                    //取出Result
                    DiffUtil.DiffResult bannerDiffResult = (DiffUtil.DiffResult) msg.obj;
                    updateBannerListUi(bannerDiffResult);
                    break;
            }
        }
    };

    /**
     * 更新banner列表
     */
    private void updateBannerListUi(DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                adapter_banner.notifyItemRangeInserted(position, count);
                if (position == 0) {
                    recyclerView_introduction.scrollToPosition(0);
                }
            }

            @Override
            public void onRemoved(int position, int count) {
                adapter_banner.notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                adapter_banner.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count, @Nullable Object payload) {
                adapter_banner.notifyItemRangeChanged(position, count, payload);
            }
        });
        mList_banner.clear();
        mList_banner.addAll(newBannerList);
        adapter_banner.setList(mList_banner);
    }

    /**
     * 更新列表
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
     * 获取数据为空
     */
    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        isEndData = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("没有搜索到相关作文");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络
     */
    private void noConnect() {
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
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
            showTips("获取数据失败");
        }
    }

    /**
     * 获取轮播图数据
     *
     * @param s
     */
    private void analyzeBannerData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorBannerData();
                } else {
                    newBannerList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        BannerBean bannerBean = new BannerBean();
                        bannerBean.setImagePath(object.getString("banner"));
                        bannerBean.setName(object.getString("name"));
                        bannerBean.setTitle("");
                        bannerBean.setType(object.getString("status"));
                        bannerBean.setTaskStatus(object.getString("taskStatus"));
                        newBannerList.add(bannerBean);
                    }
                    if (ll_introduction.getVisibility() == View.GONE) {
                        ll_introduction.setVisibility(View.VISIBLE);
                    }
                    diffBannerThread();
                }
            } else {
                errorBannerData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorBannerData();
        }
    }

    /**
     * 计算banner的DiffResult
     */
    private void diffBannerThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                        new BannerDiffCallBack(mList_banner, newBannerList), true);
                Message message = mHandler.obtainMessage(1);
                message.obj = diffResult;//obj存放DiffResult
                message.sendToTarget();
            }
        }).start();

    }

    /**
     * 获取轮播图数据失败
     */
    private void errorBannerData() {
        ll_introduction.setVisibility(View.GONE);
    }

    /**
     * 显示提示
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取作文列表
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, HomeFindFragment> {

        protected GetData(HomeFindFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(HomeFindFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("gradeId", NewMainActivity.GRADE_ID);
                object.put("appVersion",VersionInfoUtil.getVersionName(fragment.mContext));
                object.put("platform","android");
                object.put("pageNum", strings[2]);
                object.put("pageSize", 6);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HomeFindFragment fragment, String s) {
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshLayout.finishRefresh();
            fragment.refreshLayout.finishLoadMore();
            fragment.isLoading = false;
        }
    }

    /**
     * 获取轮播图数据
     */
    private static class GetBannerData
            extends WeakAsyncTask<String, Void, String, HomeFindFragment> {

        protected GetBannerData(HomeFindFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(HomeFindFragment fragment, String[] strings) {
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
        protected void onPostExecute(HomeFindFragment fragment, String s) {
            if (s == null) {
                fragment.errorBannerData();
            } else {
                fragment.analyzeBannerData(s);
            }
        }
    }
}
