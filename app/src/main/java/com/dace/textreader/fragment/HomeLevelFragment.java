package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.HomeLevelAdapter;
import com.dace.textreader.adapter.LevelFragmentRecyclerViewAdapter;
import com.dace.textreader.bean.LevelFragmentBean;
import com.dace.textreader.bean.ReaderLevelBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeLevelFragment extends Fragment implements PullListener {
    private View view;
    private PullRecyclerView mRecycleView;
    private String url = HttpUrlPre.HTTP_URL_ + "/select/reading/py/list";
    private String level_url = HttpUrlPre.HTTP_URL_ + "/select/article/level/list";
    private int pageNum = 1;
    private boolean isRefresh = true;
    private String grade = "-1";
    private HomeLevelAdapter homeLevelAdapter;
    private List<ReaderLevelBean.DataBean.ArticleListBean> mData = new ArrayList<>();
    private boolean isVisibleToUser = false;
    private List<LevelFragmentBean.DataBean> levelBeanList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reader_level, container, false);

        initView();
        loadLevelData();

        return view;
    }

    private void loadData() {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID + "");
            params.put("gradeId","111");
            params.put("grade",grade);
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

                        homeLevelAdapter.setData(mData);

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        mRecycleView.onPullComplete();
                    }
                });
    }

    private void initView() {

        NewHomeFragment newHomeFragment = (NewHomeFragment) getParentFragment();
        newHomeFragment.setOnTabLevelClickListener(new NewHomeFragment.OnTabLevelClickListener() {
            @Override
            public void onClick() {
                if(isVisibleToUser)
                    showLevelDialog();
            }
        });
        mRecycleView = view.findViewById(R.id.rlv_reader_level);
        homeLevelAdapter = new HomeLevelAdapter(mData,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecycleView.setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(homeLevelAdapter);

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

    private void loadLevelData() {
        JSONObject params = new JSONObject();
//        try {
//            params.put("studentId","7826");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        OkHttpManager.getInstance(getContext()).requestAsyn(level_url, OkHttpManager.TYPE_GET, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        LevelFragmentBean readerLevelBean = GsonUtil.GsonToBean(result.toString(),LevelFragmentBean.class);
                        List<LevelFragmentBean.DataBean> data = readerLevelBean.getData();
                            if(levelBeanList != null) {
                                levelBeanList.addAll(data);
                                if (levelBeanList.size() > 0) {
                                    grade = levelBeanList.get(0).getGradename();
                                    loadData();
                                }
                            }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        mRecycleView.onPullComplete();
                    }
                });
    }


    private void showLevelDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_fragment_level_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_doubt = holder.getView(R.id.ll_doubt_level_dialog);
                        final RecyclerView recyclerView = holder.getView(R.id.recycler_view_level_dialog);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                                3);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        LevelFragmentRecyclerViewAdapter adapter =
                                new LevelFragmentRecyclerViewAdapter(getContext(), levelBeanList);
                        recyclerView.setAdapter(adapter);
                        ll_doubt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                turnToDoubtView();
                                dialog.dismiss();
                            }
                        });
                        adapter.setOnItemClickListener(new LevelFragmentRecyclerViewAdapter.OnLevelItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int pos = recyclerView.getChildAdapterPosition(view);
                                if (levelBeanList.size() > 0) {
                                    grade = levelBeanList.get(pos).getGradename();
                                    loadData();
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    /**
     * 前往疑问解释视图
     */
    private void turnToDoubtView() {
        Intent intent = new Intent(getContext(), EventsActivity.class);
        intent.putExtra("pageName", "py_activity");
        startActivity(intent);
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
