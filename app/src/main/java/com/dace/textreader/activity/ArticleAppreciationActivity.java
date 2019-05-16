package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.adapter.AppreciationAdapter;
import com.dace.textreader.bean.AppreciationBean;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
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

public class ArticleAppreciationActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private FrameLayout fly_exception;
    private ImageView iv_edit;
    private String essayId;
    private String studentId;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private int pageNum = 1;
    private AppreciationAdapter appreciationAdapter;
    private List<AppreciationBean.DataBean.MyselfBean> mData = new ArrayList<>();
    private boolean isRefresh = true;
    private boolean hasMyself;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rl_back;
    private String title;
    private String content = "";
    private String noteId = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appreciation);

        initData();
        initView();
        initEvents();
        getData();
        EventBus.getDefault().register(this);
//        showLoading(fm_exception);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        if(messageEvent.getMessage().equals("update_appreciation")){
            pageNum = 1;
            getData();
        }
    }


    private void initData() {
        essayId = getIntent().getExtras().getString("essayId");
        title = getIntent().getExtras().getString("title");
        studentId = PreferencesUtil.getData(this,"studentId","-1").toString();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rcl_appreciation);
        fly_exception = findViewById(R.id.rly_exception);
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setRefreshHeader(new MyRefreshHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(true);
        iv_edit = findViewById(R.id.iv_edit);
        rl_back = findViewById(R.id.rl_back);

        appreciationAdapter = new AppreciationAdapter(this, mData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(appreciationAdapter);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        iv_edit.setOnClickListener(this);

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
    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("essayId",essayId);
            params.put("studentId",studentId);
            params.put("pageNum",pageNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AppreciationBean appreciationBean = GsonUtil.GsonToBean(result.toString(),AppreciationBean.class);
                if(appreciationBean.getData()!= null){
                    AppreciationBean.DataBean.MyselfBean myselfBean = appreciationBean.getData().getMyself();
                    List<AppreciationBean.DataBean.MyselfBean> itemData = appreciationBean.getData().getAppreciationList();
//                    List<AppreciationBean.DataBean.MyselfBean> refreshData = new ArrayList<>();
                    if(isRefresh){
                        if(myselfBean != null){
                            content = myselfBean.getNote();
                            noteId = myselfBean.getId();
//                            refreshData.add(myselfBean);
                            hasMyself = true;
                            fly_exception.setVisibility(View.GONE);
                        }
                        if(itemData != null){
//                            refreshData.addAll(itemData);
                            fly_exception.setVisibility(View.GONE);
                        }else {
                            if(!hasMyself){
                                //没有数据
                                showEmptyView(fly_exception);
                            }
                        }
                        appreciationAdapter.refreshData(itemData);
                    }else {
                        if(itemData != null){
                            appreciationAdapter.addData(itemData);
                        }else {
                            MyToastUtil.showToast(ArticleAppreciationActivity.this,"没有更多了");
                        }
                    }

                }else {
                    //没有数据
                    if(isRefresh){
                        showEmptyView(fly_exception);
                    }else {
                        MyToastUtil.showToast(ArticleAppreciationActivity.this,"没有更多了");
                    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_edit:
                if(!isLogin()){
                    toLogin();
                    return;
                }
                Intent intent = new Intent(this,EditAppreciationActivity.class);
                intent.putExtra("essayId",essayId);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("noteId",noteId);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
