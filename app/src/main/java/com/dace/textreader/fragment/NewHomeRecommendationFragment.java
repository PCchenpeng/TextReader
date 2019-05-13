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
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.EventListActivity;
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.activity.FansListActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.MemberCentreActivity;
import com.dace.textreader.activity.MicroLessonActivity;
import com.dace.textreader.activity.MyCompositionActivity;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewDailySentenceActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.activity.WritingActivity;
import com.dace.textreader.activity.WritingEventDetailsActivity;
import com.dace.textreader.activity.WritingSortListActivity;
import com.dace.textreader.adapter.HomeRecommendationAdapter;
import com.dace.textreader.adapter.UserHorizontalListAdapter;
import com.dace.textreader.bean.BannerBean;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.diff.BannerDiffCallBack;
import com.dace.textreader.diff.HomeDiffCallBack;
import com.dace.textreader.listen.OnUserInfoClickListen;
import com.dace.textreader.util.BannerGlideImageLoader;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.MyRefreshHeader;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

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
 * Created time 2018/12/18 0018 上午 10:10.
 * Version   1.0;
 * Describe :  首页推荐页
 * History:
 * ==============================================================================
 */
public class NewHomeRecommendationFragment extends Fragment {

    private static final String bannerUrl = HttpUrlPre.HTTP_URL + "/home/banner";
    private static final String userUrl = HttpUrlPre.HTTP_URL + "/follow/recommend";
    private static final String url = HttpUrlPre.HTTP_URL + "/recommend/public/list";
    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private View view;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private Banner banner;
    private LinearLayout ll_excellent_composition;
    private LinearLayout ll_composition_correction;
    private LinearLayout ll_daily_sentence;
    private LinearLayout ll_selected_events;
    private RecyclerView recyclerView_first;
    private LinearLayout ll_user;
    private LinearLayout ll_user_refresh;
    private RecyclerView recyclerView_user;
    private RecyclerView recyclerView;

    private Context mContext;

    private List<BannerBean> mList_banner = new ArrayList<>();
    private List<HomeRecommendationBean> mList_first = new ArrayList<>();
    private List<UserBean> mList_user = new ArrayList<>();
    private List<HomeRecommendationBean> mList = new ArrayList<>();

    private HomeRecommendationAdapter adapter_first;
    private UserHorizontalListAdapter adapter_user;
    private HomeRecommendationAdapter adapter;

    private boolean isRefreshAll = false;

    private int pageNum = 1;
    private boolean isLoading = false;  //是否正在加载中
    private boolean isEndData = false;  //是否没有数据了
    private boolean isRefresh = false;  //是否是刷新数据

    private int pageNum_user = 1;
    private boolean isLoading_user = false;
    private boolean isEndData_user = false;
    private int mPosition_user = -1;

    private boolean isNeedSave = false;

    private List<BannerBean> newBannerList = new ArrayList<>();
    private List<HomeRecommendationBean> newFirstList = new ArrayList<>();
    private List<HomeRecommendationBean> newList = new ArrayList<>();
    private DiffUtil.DiffResult diffResult_first;
    private DiffUtil.DiffResult diffResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recommendation_home, container, false);

        initView();
        initEvents();
        initLocalData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        //轮播图开始自动轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //轮播图结束轮播
        banner.stopAutoPlay();
    }

    /**
     * 初始化本地数据
     */
    private void initLocalData() {
        LitePal.findAllAsync(BannerBean.class).listen(new FindMultiCallback<BannerBean>() {
            @Override
            public void onFinish(List<BannerBean> list) {
                mList_banner.addAll(list);
                if (mList_banner.size() != 0) {
                    updateBannerUi();
                }
                initLocalRecommendationData();
            }
        });
    }

    /**
     * 初始化本地推荐数据
     */
    private void initLocalRecommendationData() {
        LitePal.findAllAsync(HomeRecommendationBean.class)
                .listen(new FindMultiCallback<HomeRecommendationBean>() {
                    @Override
                    public void onFinish(List<HomeRecommendationBean> list) {
                        if (list.size() != 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (i < 2) {
                                    newFirstList.add(list.get(i));
                                } else {
                                    newList.add(list.get(i));
                                }
                            }
                            diffRecommendThread();
                        }
                        initData();
                    }
                });
    }

    // An highlighted block
    public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        public  String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

        public int previousTotal = 0; // The total number of items in the dataset after the last load  总数据
        private boolean loading = true; // True if we are still waiting for the last set of data to load. 是否提前加载
        public int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;
        private int current_page = 1;
        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        public abstract void onLoadMore(int current_page);
        public void reset(int previousTotal, boolean loading) {
            this.previousTotal = previousTotal;
            this.loading = loading;
        }
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
                if (isLoading || isEndData) {
                    refreshLayout.finishLoadMore();
                } else {
                    getMoreRecommendData();
                }
            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    int childCount = recyclerView.getChildCount();
//                    int itemCount = recyclerView.getLayoutManager().getItemCount();
//                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//                    if (firstVisibleItem + childCount == itemCount) {
////                        if (!loadingMore) {
////                            loadingMore = true
//                        getMoreRecommendData();
////                        }
//                    }
//                }
//
//            }
//        });

//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager)recyclerView.getLayoutManager()){
//            @Override
//            public void onLoadMore(int current_page) {
//
//                getMoreRecommendData();
//            }
//        });
        ll_excellent_composition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToExcellentComposition();
            }
        });
        ll_composition_correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCorrectionDialog();
            }
        });
        ll_daily_sentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToDailySentence();
            }
        });
        ll_selected_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToEventList();
            }
        });
        ll_user_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading_user) {
                    showTips("正在刷新，请稍候~");
                } else {
                    if (mPosition_user == -1) {
                        if (isEndData_user) {
                            showTips("没有更多了~");
                        } else {
                            getMoreUserData();
                        }
                    } else {
                        showTips("另一个操作进行中，请稍候...");
                    }
                }
            }
        });
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                BannerBean bannerBean = mList_banner.get(position);
                int sourceType = bannerBean.getSourceType();
                if (sourceType == 0) {  //H5页面
                    String status = bannerBean.getTaskStatus();
                    String name = bannerBean.getName();
                    if (status.equals("0")) {
                        turnToEventDetail(name);
                    } else {
                        turnToEventsH5(name);
                    }
                } else if (sourceType == 1) {  //作文详情
                    String compositionId = bannerBean.getName();
                    turnToWritingDetail(compositionId);
                } else if (sourceType == 2) {  //阅读
                    long essayId = Long.valueOf(bannerBean.getName());
                    int essayType = Integer.valueOf(bannerBean.getType());
                    turnToArticle(essayId, essayType);
                } else if (sourceType == 3) {  //微课
                    long lessonId = Long.valueOf(bannerBean.getName());
                    turnToLesson(lessonId);
                } else if (sourceType == 4) {
                    String cardId = bannerBean.getName();
                    turnToMemberCentre(cardId);
                }
            }
        });
        adapter_first.setOnItemClickListen(new HomeRecommendationAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_first.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList_first.size()) {
                    int type = mList_first.get(pos).getType();
                    if (type == 0) {
                        String writingId = mList_first.get(pos).getCompositionId();
                        turnToWritingDetail(writingId);
                        //增加阅读数
                        String views = mList_first.get(pos).getViews();
                        mList_first.get(pos).setViews(DataUtil.increaseViews(views));
                        adapter_first.notifyItemChanged(pos);
                    }
                }
            }
        });
        adapter_first.setOnUserInfoClickListen(new OnUserInfoClickListen() {
            @Override
            public void onClick(long userId) {
                turnToUserHomepage(userId);
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
    }

    /**
     * 前往会员中心
     */
    private void turnToMemberCentre(String memberCardId) {
        Intent intent = new Intent(getActivity(), MemberCentreActivity.class);
        intent.putExtra("id", memberCardId);
        intent.putExtra("code", "");
        startActivity(intent);
    }

    private void initData() {
        if (!isLoading) {
            isNeedSave = true;
            isRefreshAll = true;
            isLoading = true;
            new GetBannerData(this).execute(bannerUrl);
        }
    }

    private void initUserData() {
        if (!isLoading_user) {
            isLoading_user = true;
            isEndData_user = false;
            pageNum_user = 1;
            new GetUserData(this).execute(userUrl,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum_user));
        }
    }

    /**
     * 获取更多用户数据
     */
    private void getMoreUserData() {
        if (!isLoading_user) {
            isLoading_user = true;
            isRefreshAll = false;
            pageNum_user = pageNum_user + 1;
            new GetUserData(this).execute(userUrl,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum_user));
        }
    }

    private void initRecommendData() {
        isLoading = true;
        isEndData = false;
        isRefresh = true;
        pageNum = 1;
        new GetRecommendData(this).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));
    }

    private void getMoreRecommendData() {
        if (!isLoading) {
            isLoading = true;
            isRefreshAll = false;
            isRefresh = false;
            pageNum = pageNum + 1;
            new GetRecommendData(this).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_home_recommend_fragment);

        refreshLayout = view.findViewById(R.id.smart_refresh_home_recommendation);
        refreshLayout.setRefreshHeader(new MyRefreshHeader(mContext));
//        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);

        banner = view.findViewById(R.id.banner_home_recommendation);
        banner.setImageLoader(new BannerGlideImageLoader());
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.isAutoPlay(true);
        banner.setDelayTime(3000);

        ll_excellent_composition = view.findViewById(R.id.ll_excellent_composition_home_recommendation);
        ll_composition_correction = view.findViewById(R.id.ll_composition_correction_home_recommendation);
        ll_daily_sentence = view.findViewById(R.id.ll_daily_sentence_home_recommendation);
        ll_selected_events = view.findViewById(R.id.ll_selected_events_home_recommendation);

        recyclerView_first = view.findViewById(R.id.recycler_view_writing_first_home_recommendation);
        recyclerView_first.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager_first = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_first.setLayoutManager(layoutManager_first);
        adapter_first = new HomeRecommendationAdapter(mContext, mList_first);
        recyclerView_first.setAdapter(adapter_first);

        ll_user = view.findViewById(R.id.ll_user_home_recommendation_fragment);
        ll_user_refresh = view.findViewById(R.id.ll_refresh_user_home_recommendation_fragment);
        recyclerView_user = view.findViewById(R.id.recycler_view_user_home_recommendation);
        LinearLayoutManager layoutManager_user = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_user.setLayoutManager(layoutManager_user);
        adapter_user = new UserHorizontalListAdapter(mContext, mList_user);
        recyclerView_user.setAdapter(adapter_user);

        recyclerView = view.findViewById(R.id.recycler_view_writing_home_recommendation);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeRecommendationAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置轮播图高度
     */
    private void setBannerLayoutParams() {
        int width = DensityUtil.getScreenWidth(mContext);
        int height = (int) (width * 0.4533);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
    }

    /**
     * 前往微课
     *
     * @param lessonId
     */
    private void turnToLesson(long lessonId) {
        Intent intent = new Intent(mContext, MicroLessonActivity.class);
        intent.putExtra("id", lessonId);
        startActivity(intent);
    }

    /**
     * 前往文章详情
     *
     * @param essayId
     * @param essayType
     */
    private void turnToArticle(long essayId, int essayType) {
        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
        intent.putExtra("id", essayId);
        intent.putExtra("type", essayType);
        startActivity(intent);
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
     * 显示作文批改对话框
     */
    private void showCorrectionDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_correction_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_write = holder.getView(R.id.ll_write_composition_correction_dialog);
                        LinearLayout ll_choose = holder.getView(R.id.ll_choose_composition_correction_dialog);
                        RelativeLayout rl_close = holder.getView(R.id.rl_close_writing_correction_dialog);

                        ll_write.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, WritingActivity.class);
                                intent.putExtra("id", "");
                                intent.putExtra("taskId", "");
                                intent.putExtra("area", 5);
                                intent.putExtra("type", 5);
                                intent.putExtra("isCorrection", true);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        ll_choose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NewMainActivity.STUDENT_ID == -1) {
                                    turnToLogin();
                                } else {
                                    Intent intent = new Intent(getActivity(), MyCompositionActivity.class);
                                    intent.putExtra("index", 0);
                                    intent.putExtra("isCorrection", true);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            }
                        });
                        rl_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getChildFragmentManager());
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
     * 前往每日一句
     */
    private void turnToDailySentence() {
        startActivity(new Intent(mContext, NewDailySentenceActivity.class));
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 前往查看活动详情H5
     *
     * @param name
     */
    private void turnToEventsH5(String name) {
        Intent intent = new Intent(getContext(), EventsActivity.class);
        intent.putExtra("pageName", name);
        startActivity(intent);
    }

    /**
     * 前往活动详情
     */
    private void turnToEventDetail(String taskId) {
        Intent intent = new Intent(getContext(), WritingEventDetailsActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }

    /**
     * 前往优秀作文
     */
    private void turnToExcellentComposition() {
        startActivity(new Intent(mContext, WritingSortListActivity.class));
    }

    /**
     * 前往活动列表
     */
    private void turnToEventList() {
        startActivity(new Intent(mContext, EventListActivity.class));
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
                    mList_banner.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        BannerBean bannerBean = new BannerBean();
                        bannerBean.setImagePath(object.getString("banner"));
                        bannerBean.setSourceType(object.optInt("sourceType"));
                        bannerBean.setName(object.getString("name"));
                        bannerBean.setType(object.getString("type"));
                        bannerBean.setTitle(object.getString("title"));
                        bannerBean.setType(object.getString("status"));
                        bannerBean.setTaskStatus(object.getString("taskStatus"));
                        mList_banner.add(bannerBean);
                    }
                    initUserData();
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
                    DiffUtil.DiffResult bannerDiffResult = (DiffUtil.DiffResult) msg.obj;
                    isBannerSame = true;
                    bannerDiffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                        @Override
                        public void onInserted(int i, int i1) {
                            isBannerSame = false;
                        }

                        @Override
                        public void onRemoved(int i, int i1) {
                            isBannerSame = false;
                        }

                        @Override
                        public void onMoved(int i, int i1) {
                            isBannerSame = false;
                        }

                        @Override
                        public void onChanged(int i, int i1, @Nullable Object o) {
                            isBannerSame = false;
                        }
                    });
                    updateBannerUi();
                    break;
                case 1:
                    diffResult_first.dispatchUpdatesTo(adapter_first);
                    mList_first.clear();
                    mList_first.addAll(newFirstList);
                    adapter_first.setList(mList_first);

                    if (isRefreshAll) {
                        updateUserUi();
                    }

                    diffResult.dispatchUpdatesTo(adapter);
                    mList.clear();
                    mList.addAll(newList);
                    adapter.setList(mList);
                    break;
            }
        }
    };

    private boolean isBannerSame = false;

    /**
     * 更新轮播图ui
     */
    private void updateBannerUi() {
        if (!isBannerSame) {
            setBannerLayoutParams();
            List<String> images = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            for (int i = 0; i < mList_banner.size(); i++) {
                images.add(mList_banner.get(i).getImagePath());
                titles.add(mList_banner.get(i).getTitle());
            }
            banner.update(images, titles);
            if (banner.getVisibility() == View.GONE) {
                banner.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取轮播图数据失败
     */
    private void errorBannerData() {
        if (isRefreshAll) {
            initUserData();
        } else {
            mList_banner.clear();
            banner.setVisibility(View.GONE);
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
                    emptyUserData();
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
                    if (isRefreshAll) {
                        initRecommendData();
                    } else {
                        updateUserUi();
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyUserData();
            } else {
                errorUserData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorUserData();
        }
    }

    /**
     * 更新用户Ui
     */
    private void updateUserUi() {
        adapter_user.notifyDataSetChanged();
        if (ll_user.getVisibility() == View.GONE) {
            ll_user.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取用户数据为空
     */
    private void emptyUserData() {
        isEndData_user = true;
        if (isRefreshAll) {
            initRecommendData();
        } else {
            if (mList_user.size() == 0) {
                ll_user.setVisibility(View.GONE);
            } else {
                showTips("没有更多了~");
            }
        }
    }

    /**
     * 获取用户数据失败
     */
    private void errorUserData() {
        if (isRefreshAll) {
            initRecommendData();
        } else {
            if (mList_user.size() == 0) {
                ll_user.setVisibility(View.GONE);
            } else {
                showTips("获取用户失败，请稍后重试~");
            }
        }
    }

    /**
     * 分析推荐数据
     *
     * @param s
     */
    private void analyzeRecommendData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyRecommendData();
                } else {
                    newFirstList.clear();
                    newList.clear();
                    List<HomeRecommendationBean> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        JSONObject object = json.getJSONObject("product");
                        HomeRecommendationBean bean = new HomeRecommendationBean();
                        bean.setType(json.optInt("productType", 0));
                        if (json.optInt("productType", 0) == 0) {  //作文分类
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
                            if (isRefresh) {
                                if (i < 2) {
                                    newFirstList.add(bean);
                                } else {
                                    newList.add(bean);
                                }
                            } else {
                                list.add(bean);
                            }
                        }
                    }
                    if (isRefreshAll) {
                        diffBannerThread();
                    }
                    if (isRefresh) {
                        diffRecommendThread();
                    } else {
                        adapter.addData(list);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyRecommendData();
            } else {
                errorRecommendData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorRecommendData();
        }
    }

    /**
     * 计算推荐数据的DiffResult
     */
    private void diffRecommendThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                diffResult_first = DiffUtil.calculateDiff(
                        new HomeDiffCallBack(mList_first, newFirstList), true);
                diffResult = DiffUtil.calculateDiff(
                        new HomeDiffCallBack(mList, newList), true);
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }

//    /**
//     * 更新推荐UI
//     */
//    private void updateRecommendUi() {
//        if (mList_first.size() == 0) {
//            if (mList.size() > 2) {
//                mList_first.add(mList.get(0));
//                mList_first.add(mList.get(1));
//                mList.remove(1);
//                mList.remove(0);
//            } else {
//                mList_first.addAll(mList);
//            }
//            adapter_first.notifyDataSetChanged();
//        }
//
//        adapter.notifyDataSetChanged();
//
//        isRefreshAll = false;
//    }

    /**
     * 获取推荐数据为空
     */
    private void emptyRecommendData() {
        isEndData = true;
        if (mList_first.size() == 0 && mList.size() == 0) {
            if (mList_banner.size() == 0 && mList_user.size() == 0) {
                emptyData();
            }
        } else {
            showTips("没有更多了~");
        }
        isRefreshAll = false;
    }

    /**
     * 获取推荐数据失败
     */
    private void errorRecommendData() {
        if (mList_first.size() != 0 || mList.size() != 0) {
            showTips("获取数据失败，请稍后再试~");
        } else {
            if (mList_banner.size() == 0 && mList_user.size() == 0) {
                errorData();
            }
        }
        isRefreshAll = false;
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
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
        tv_tips.setText("没有内容喔~");
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
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
    }

    /**
     * 显示吐丝
     *
     * @param s
     */
    private void showTips(String s) {
        MyToastUtil.showToast(mContext, s);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isNeedSave) {
            isNeedSave = false;
            LitePal.deleteAll(BannerBean.class);
            for (int i = 0; i < mList_banner.size(); i++) {
                mList_banner.get(i).save();
            }

            LitePal.deleteAll(HomeRecommendationBean.class);
            for (int i = 0; i < mList_first.size(); i++) {
                mList_first.get(i).save();
            }
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).save();
            }
        }

    }

    /**
     * 获取轮播图数据
     */
    private static class GetBannerData
            extends WeakAsyncTask<String, Void, String, NewHomeRecommendationFragment> {

        protected GetBannerData(NewHomeRecommendationFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewHomeRecommendationFragment fragment, String[] strings) {
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
        protected void onPostExecute(NewHomeRecommendationFragment fragment, String s) {
            if (s == null) {
                fragment.errorBannerData();
            } else {
                fragment.analyzeBannerData(s);
            }
        }
    }

    /**
     * 获取用户数据
     */
    private static class GetUserData
            extends WeakAsyncTask<String, Void, String, NewHomeRecommendationFragment> {

        protected GetUserData(NewHomeRecommendationFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewHomeRecommendationFragment fragment, String[] strings) {
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
        protected void onPostExecute(NewHomeRecommendationFragment fragment, String s) {
            if (s == null) {
                fragment.errorUserData();
            } else {
                fragment.analyzeUserData(s);
            }
            fragment.isLoading_user = false;
        }
    }

    /**
     * 获取推荐数据
     */
    private static class GetRecommendData
            extends WeakAsyncTask<String, Void, String, NewHomeRecommendationFragment> {

        protected GetRecommendData(NewHomeRecommendationFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewHomeRecommendationFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("gradeId", strings[2]);
                object.put("pageNum", strings[3]);
                object.put("pageSize", 6);
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
        protected void onPostExecute(NewHomeRecommendationFragment fragment, String s) {
            if (s == null) {
                fragment.errorRecommendData();
            } else {
                fragment.analyzeRecommendData(s);
            }
            fragment.isLoading = false;
            if (fragment.isRefresh) {
                fragment.refreshLayout.finishRefresh();
            } else {
                fragment.refreshLayout.finishLoadMore();
            }
        }
    }

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, NewHomeRecommendationFragment> {

        protected FollowUser(NewHomeRecommendationFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewHomeRecommendationFragment fragment, String[] strings) {
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
        protected void onPostExecute(NewHomeRecommendationFragment fragment, String s) {
            if (s == null) {
                fragment.errorFollowUser();
            } else {
                fragment.analyzeFollowUserData(s);
            }
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

}
