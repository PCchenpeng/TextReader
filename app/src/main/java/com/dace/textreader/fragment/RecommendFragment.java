package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.HomeAudioDetailActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.MySubscriptionActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.NewSearchActivity;
import com.dace.textreader.adapter.HomeRecommendAdapter;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.bean.RecommendBean;
import com.dace.textreader.bean.SearchTipsBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendFragment extends BaseFragment implements PullListener {

    private View view;
    private PullRecyclerView mRecycleView;
    private FrameLayout framelayout;

    private HomeRecommendAdapter mHomeRecommendAdapter;
    private int pageNum = 1;

    private String url = HttpUrlPre.HTTP_URL_+"/select/index/recommend/list";

    //获取搜索提示语
    private String searchTipsUrl = HttpUrlPre.SEARCHE_URL+"/search/select/index/search/word";

    private List<RecommendBean.DataBean.ArticleListBean> data = new ArrayList<>();

    private boolean isRefresh = false;
    private String tips = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_recommend, container, false);

        initView();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void Event(MessageEvent messageEvent) {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
        mRecycleView = view.findViewById(R.id.rcv_recommend);
        framelayout = view.findViewById(R.id.framelayout);
        mHomeRecommendAdapter = new HomeRecommendAdapter(data,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
//        mRecycleView.setLayoutManager(layoutManager);
//        mRecycleView.setAdapter(mHomeRecommendAdapter);


        mRecycleView.setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(mHomeRecommendAdapter);


        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int childCount = recyclerView.getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (firstVisibleItem + childCount == (itemCount+1)) {
//                        if (!loadingMore) {
//                            loadingMore = true
                        isRefresh = false;
                        getMoreRecommendData();
//                        }
                    }

//                    if(firstVisibleItem==1 || firstVisibleItem == 0){
//                        onSearchMissListener.onMiss();
//                    }else if(firstVisibleItem>1){
//                        onSearchMissListener.onShow();
//                    }
                }

            }
        });


        mHomeRecommendAdapter.setOnItemClickListener(new HomeRecommendAdapter.OnItemClickListener() {
            @Override
            public void onClick(int type, String id, String imgUrl,int flag,int py) {
                Intent intent;
                switch (type){
                    case HomeRecommendAdapter.AUDIO_PIC:

                        if(flag == 1){
                            intent = new Intent(getContext(), HomeAudioDetailActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("py",py);
                        }else {
                            intent = new Intent(getContext(), ArticleDetailActivity.class);
                            intent.putExtra("essayId", id);
                            intent.putExtra("imgUrl", imgUrl);
                        }
                        startActivity(intent);
                        break;

                    case HomeRecommendAdapter.TOP:
                         intent = new Intent(getContext(), NewSearchActivity.class);
                         intent.putExtra("tips",tips);
                        startActivity(intent);
                        break;
                    case HomeRecommendAdapter.TOP_SUB:
                        if (NewMainActivity.STUDENT_ID == -1) {
                            turnToLogin();
                        } else {
                            turnToIntensive();
                        }
                        break;
                    case HomeRecommendAdapter.IMG:
                         intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        startActivity(intent);
                        break;

                    case HomeRecommendAdapter.VIDEO:
                        intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        intent.putExtra("isVideo",true);
                        startActivity(intent);
                        break;
                        default:
                            break;
                }
            }
        });
        setOnScrollListener(mRecycleView);
        loadData();
        getSearchTips();
    }

    private void loadData(){
        if (pageNum == 1) {
            showLoadingView(framelayout);
        }
        new GetRecommendData(RecommendFragment.this).execute(url,String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));
    }

    private void getSearchTips() {
        JSONObject params = new JSONObject();
        OkHttpManager.getInstance(getContext()).requestAsyn(searchTipsUrl, OkHttpManager.TYPE_GET, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        SearchTipsBean searchTipsBean = GsonUtil.GsonToBean(result.toString(),SearchTipsBean.class);
                        if(searchTipsBean != null && searchTipsBean.getData() != null && searchTipsBean.getData().size() != 0){
                            tips = searchTipsBean.getData().get(0).getTip();
                            mHomeRecommendAdapter.setTips(tips);
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 跳转到我的订阅页面
     */
    private void turnToIntensive() {
        Intent intent = new Intent(getContext(), MySubscriptionActivity.class);
        startActivity(intent);
    }

    private void getMoreRecommendData() {
        pageNum++;
        new GetRecommendData(RecommendFragment.this).execute(url,String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));
    }

    @Override
    public void onRefresh() {
//        SystemClock.sleep(1000L);
        pageNum = 1;
        isRefresh = true;
        new GetRecommendData(RecommendFragment.this).execute(url,String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));
//        mRecycleView.onComplete(true);
//        mRecycleView.onPullComplete();
    }





    @Override
    public void onLoadMore() {

    }


    /**
     * 获取推荐数据
     */
    private class GetRecommendData
            extends WeakAsyncTask<String, Void, String, RecommendFragment> {

        protected GetRecommendData(RecommendFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(RecommendFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("gradeId", strings[2]);
                object.put("pageNum", strings[3]);
                object.put("pageSize", 6);
                object.put("width", 750);
                object.put("height", 420);

//                CacheControl cacheControl = new CacheControl().

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
        protected void onPostExecute(RecommendFragment fragment, String s) {
            framelayout.setVisibility(View.GONE);
            if (s == null) {
                if (pageNum == 1) {//如果是第一页
                    showNetFailView(framelayout, new OnButtonClick() {
                        @Override
                        public void onButtonClick() {
                            framelayout.setVisibility(View.GONE);
                            mRecycleView.onRefresh();
                        }
                    });
                } else {

                }
//                fragment.errorRecommendData();
            } else {
                fragment.analyzeRecommendData(s);
            }
//            fragment.isLoading = false;
//            if (fragment.isRefresh) {
//                fragment.refreshLayout.finishRefresh();
//            } else {
//                fragment.refreshLayout.finishLoadMore();
//            }
        }
    }


    /**
     * 分析推荐数据
     *
     * @param s
     */
    private void analyzeRecommendData(String s) {
        RecommendBean recommendBean = null;
        try{
            recommendBean = GsonUtil.GsonToBean(s,RecommendBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (recommendBean.getStatus() == 200) {
            mHomeRecommendAdapter.noMoreData = false;
            if (recommendBean != null && recommendBean.getData() != null) {
                data = recommendBean.getData().getArticleList();

            } else {
                mRecycleView.onPullComplete();
                return;
            }

            if (isRefresh) {
                mHomeRecommendAdapter.refreshData(data);
                mRecycleView.onPullComplete();
            } else {
                mHomeRecommendAdapter.addData(data);
            }
        } else if (recommendBean.getStatus() == 400) {
            if(mHomeRecommendAdapter.getItemList().size() == 0)
            showDefaultView(framelayout, R.drawable.image_state_empty, "暂无内容～", false, false, "", null);
            else {
                mHomeRecommendAdapter.noMoreData = true;
                mHomeRecommendAdapter.notifyDataSetChanged();
            }
        } else {
            if (isRefresh) {//如果是第一页
                showNetFailView(framelayout, new OnButtonClick() {
                    @Override
                    public void onButtonClick() {
                        framelayout.setVisibility(View.GONE);
                        mRecycleView.onRefresh();
                    }
                });
            } else {

            }
        }

    }

//    public interface OnSearchMissListener{
//        void onMiss();
//        void onShow();
//    }
//
//    OnSearchMissListener onSearchMissListener;
//
//    public void setOnSearchMissListener(OnSearchMissListener onSearchMissListener){
//        this.onSearchMissListener = onSearchMissListener;
//    }


}
