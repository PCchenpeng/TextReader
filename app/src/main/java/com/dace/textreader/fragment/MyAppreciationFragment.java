package com.dace.textreader.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import com.dace.textreader.activity.ArticleDetailActivity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAppreciationFragment extends BaseFragment{
    private View view;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private String deleteUrl = HttpUrlPre.HTTP_URL_+"/delete/article/note";
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
    private List<AppreciationBean.DataBean.MyselfBean> mSelectItemList = new ArrayList<>();

    private boolean isRefresh = true;
    private boolean hasMyself;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rl_back;

    private RelativeLayout rl_editor;
    private LinearLayout ll_select_all;
    private ImageView iv_select_all;
    private TextView tv_delete;

    private boolean isEditor = false;  //是否处于编辑状态
    private boolean isSelectAll = false;  //是否是全选
    private boolean hasSelected = false;  //是否有item被选中

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

        rl_editor = view.findViewById(R.id.rl_editor_excerpt_fragment);
        ll_select_all = view.findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = view.findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = view.findViewById(R.id.tv_delete_new_collection_bottom);

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

        appreciationAdapter.setOnItemClick(new AppreciationAdapter.OnItemClick() {
            @Override
            public void onClick(View view,AppreciationBean.DataBean.MyselfBean myselfBean) {

                int pos = recyclerView.getChildAdapterPosition(view);
                if (isEditor) {
                    itemSelected(pos);
                } else {
                    Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                    intent.putExtra("essayId", String.valueOf(myselfBean.getEssay_id()));
                    intent.putExtra("imgUrl","");
                    startActivity(intent);
                }
            }
        });


        ll_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectAll) {
                    iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
                    selectAllItem(false);
                } else {
                    iv_select_all.setImageResource(R.drawable.icon_edit_selected);
                    selectAllItem(true);
                }
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasSelected) {
                    deleteItems();
                }
            }
        });

//        iv_edit.setOnClickListener(this);
    }

    /**
     * 编辑状态下选中Item
     *
     * @param position
     */
    private void itemSelected(int position) {
        if (mData.get(position).isSelected()) {
            mData.get(position).setSelected(false);
        } else {
            mData.get(position).setSelected(true);
        }
        appreciationAdapter.notifyDataSetChanged();
        updateDeleteButtonBg();
    }


    /**
     * 删除Item
     */
    private void deleteItems() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isSelected()) {
                mSelectItemList.add(mData.get(i));
                array.put(mData.get(i).getId());
            }
        }
        if (array.length() != 0) {
            for (int i = 0; i < mSelectItemList.size(); i++) {
                mData.remove(mSelectItemList.get(i));
            }
            mSelectItemList.clear();
            appreciationAdapter.notifyDataSetChanged();

            deleteData(array.toString());


            cancelEditorMode();
        }
        if (mData.size() == 0) {
//            emptyData();
        }
    }

    private void deleteData(String nodeIds) {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",studentId);
            params.put("noteIds",nodeIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(deleteUrl,OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {

            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    /**
     * 编辑模式
     */
    private void editorMode() {
        rl_editor.setVisibility(View.VISIBLE);
        isEditor = true;

        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setEditor(true);
        }
        appreciationAdapter.notifyDataSetChanged();
    }

    /**
     * 取消编辑模式
     */
    private void cancelEditorMode() {
        rl_editor.setVisibility(View.GONE);
//        if (mListen != null) {
//            mListen.onEditor(false);
//        }
        isEditor = false;
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(false);
            mData.get(i).setEditor(false);
        }
        appreciationAdapter.notifyDataSetChanged();
    }


    /**
     * 全选
     */
    private void selectAllItem(boolean selectAll) {
        isSelectAll = selectAll;
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(selectAll);
        }
        appreciationAdapter.notifyDataSetChanged();
        updateDeleteButtonBg();
    }

    /**
     * 更新删除按钮的背景
     */
    private void updateDeleteButtonBg() {
        hasSelected = false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isSelected()) {
                hasSelected = true;
                break;
            }
        }
        if (hasSelected) {
            tv_delete.setBackgroundResource(R.drawable.shape_text_orange);
        } else {
            tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
            iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
            isSelectAll = false;
        }
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
                        if(isEditor){
                            for (int i=0;i<itemData.size();i++){
                                itemData.get(i).setEditor(true);
                            }
                        }
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

    public void editorOpenOrClose() {
            if (isEditor) {
                cancelEditorMode();
            } else {
                editorMode();
            }
    }

    public boolean getEditor(){
        return isEditor;
    }

}
