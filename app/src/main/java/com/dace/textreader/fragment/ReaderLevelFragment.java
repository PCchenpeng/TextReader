package com.dace.textreader.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.adapter.ReaderLevelAdapter;
import com.dace.textreader.bean.ReaderLevelBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderLevelFragment extends Fragment implements PullListener {
    private View view;
    private PullRecyclerView mRecycleView;
    private String url = HttpUrlPre.HTTP_URL_ + "/select/reading/py/list";
    private int pageNum = 1;
    private boolean isRefresh = true;
    private ReaderLevelAdapter readerLevelAdapter;
    private List<ReaderLevelBean.DataBean.ArticleListBean> mData = new ArrayList<>();
    private boolean isVisibleToUser = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reader_level, container, false);

        initView();
        loadData();

        return view;
    }

    private void loadData() {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId","7826");
            params.put("gradeId","112");
            params.put("py","100");
            params.put("grade","2");
            params.put("pageNum",String.valueOf(pageNum));
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReaderLevelBean readerLevelBean = GsonUtil.GsonToBean(result.toString(),ReaderLevelBean.class);
                        List<ReaderLevelBean.DataBean.ArticleListBean> data = readerLevelBean.getData().getArticleList();
                        if(isRefresh){
//                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
                            if(mData != null){
                                mData.clear();
                                mData.addAll(data);

                            }
                            mRecycleView.onPullComplete();
                        } else{
                            if(mData != null)
                                mData.addAll(data);
                        }

                        readerLevelAdapter.setData(mData);

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        mRecycleView.onPullComplete();
                    }
                });
    }

    private void initView() {

        NewReaderFragment newReaderFragment = (NewReaderFragment) getParentFragment();
        newReaderFragment.setOnTabLevelClickListener(new NewReaderFragment.OnTabLevelClickListener() {
            @Override
            public void onClick() {
                if(isVisibleToUser)
                Toast.makeText(getContext(),"4545",Toast.LENGTH_SHORT).show();
            }
        });
        mRecycleView = view.findViewById(R.id.rlv_reader_level);
        readerLevelAdapter = new ReaderLevelAdapter(mData,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecycleView.setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(readerLevelAdapter);

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int childCount = recyclerView.getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (firstVisibleItem + childCount == (itemCount+1)) {
                        isRefresh = false;
                        pageNum++;
                        loadData();
                    }
                }
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            //相当于OnResume(),可以做相关逻辑
            this.isVisibleToUser = true;
        }else {
            //相当于OnPause()
            this.isVisibleToUser = false;
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        pageNum = 1;
        loadData();
    }

    @Override
    public void onLoadMore() {

    }

}
