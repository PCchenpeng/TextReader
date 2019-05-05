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

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.HomeAudioDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.NewSearchActivity;
import com.dace.textreader.adapter.HomeRecommendAdapter;
import com.dace.textreader.bean.RecommendBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendFragment extends Fragment implements PullListener {

    private View view;
    private PullRecyclerView mRecycleView;

    private HomeRecommendAdapter mHomeRecommendAdapter;
    private int pageNum = 1;

    private String url = HttpUrlPre.HTTP_URL_+"/select/index/recommend/list";

    private List<RecommendBean.DataBean.ArticleListBean> data = new ArrayList<>();

    private boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_recommend, container, false);

        initView();

        return view;
    }

    private void initView() {
        mRecycleView = view.findViewById(R.id.rcv_recommend);
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
            public void onClick(int type, String id, String imgUrl,int flag) {
                Intent intent;
                switch (type){
                    case HomeRecommendAdapter.AUDIO_PIC:

                        if(flag == 1){
                            intent = new Intent(getContext(), HomeAudioDetailActivity.class);
                            intent.putExtra("id", id);
                        }else {
                            intent = new Intent(getContext(), ArticleDetailActivity.class);
                            intent.putExtra("essayId", id);
                            intent.putExtra("imgUrl", imgUrl);
                        }
                        startActivity(intent);
                        break;

                    case HomeRecommendAdapter.TOP:
                         intent = new Intent(getContext(), NewSearchActivity.class);
                        startActivity(intent);
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

        new GetRecommendData(RecommendFragment.this).execute(url,String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(NewMainActivity.GRADE_ID), String.valueOf(pageNum));

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
    private static class GetRecommendData
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
            if (s == null) {
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

        if(recommendBean != null && recommendBean.getData() != null)
            data = recommendBean.getData().getArticleList();
        else
            return;
        if(isRefresh){
            mHomeRecommendAdapter.refreshData(data);
            mRecycleView.onPullComplete();
        } else{
            mHomeRecommendAdapter.addData(data);
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
