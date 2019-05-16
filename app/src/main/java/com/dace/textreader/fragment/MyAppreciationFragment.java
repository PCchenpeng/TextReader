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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.EditAppreciationActivity;
import com.dace.textreader.adapter.AppreciationAdapter;
import com.dace.textreader.bean.AppreciationBean;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.MyRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAppreciationFragment extends BaseFragment{
    private View view;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private String studentId;
    private FrameLayout fly_exception;
    private String title;
    private String content;
    private String noteId;
    private String essayId;
    private int pageNum = 1;
    private RecyclerView recyclerView;
    private AppreciationAdapter appreciationAdapter;
    private List<AppreciationBean.DataBean.MyselfBean> mData = new ArrayList<>();
    private boolean isRefresh = true;
    private boolean hasMyself;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rl_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        if(messageEvent.getMessage().equals("update_appreciation")){
            getData();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myappreciation, container, false);
        initData();
        initView();
        getData();
        return view;
    }



    private void initData() {
        studentId = PreferencesUtil.getData(getContext(),"studentId","-1").toString();
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rcl_appreciation);
        fly_exception = view.findViewById(R.id.rly_exception);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        refreshLayout.setRefreshHeader(new MyRefreshHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);

        appreciationAdapter = new AppreciationAdapter(getContext(), mData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(appreciationAdapter);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                isRefresh = false;
                pageNum++;
                getData();
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNum = 1;
                getData();

            }
        });

//        iv_edit.setOnClickListener(this);
    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",studentId);
            params.put("pageNum",pageNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AppreciationBean appreciationBean = GsonUtil.GsonToBean(result.toString(),AppreciationBean.class);
                if(appreciationBean.getData() != null && appreciationBean.getData().getAppreciationList() != null){
                    List<AppreciationBean.DataBean.MyselfBean> itemData = appreciationBean.getData().getAppreciationList();
                    if(isRefresh){
                        fly_exception.setVisibility(View.GONE);
                        appreciationAdapter.refreshData(itemData);
                    }else {
                        appreciationAdapter.addData(itemData);
                    }
                }else {
                    if(isRefresh)
                    showEmptyView(fly_exception);
                }

                if(isRefresh)
                    refreshLayout.finishRefresh();
                else
                    refreshLayout.finishLoadMore();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                if(isRefresh)
                    refreshLayout.finishRefresh();
                else
                    refreshLayout.finishLoadMore();
            }
        });
    }

}
