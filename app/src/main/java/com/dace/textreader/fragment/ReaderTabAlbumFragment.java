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

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.ReaderTabActivity;
import com.dace.textreader.adapter.ReaderTabAlbumAdapter;
import com.dace.textreader.bean.ReaderTabAlbumItemBean;
import com.dace.textreader.bean.ReaderTabAlbumTopBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumFragment extends Fragment {

    private View view;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ReaderTabAlbumAdapter readerTabAlbumAdapter;
    private String topUrl = HttpUrlPre.HTTP_URL_+"/select/album/newalbumlist";
    private String itemUrl = HttpUrlPre.HTTP_URL_+"/select/album/list";

    private String type;
    private String tab_type;
    public static String  TYPE = "type";
    public static String  TAB_TYPE = "tab_type";
    private boolean isRefresh = false;
    private int pageNum = 1;
    private List<ReaderTabAlbumTopBean.DataBean> topData = new ArrayList<>();
    private List<ReaderTabAlbumItemBean.DataBean> itemata = new ArrayList<>();
    private ReaderTabActivity readerTabActivity;

    public static ReaderTabAlbumFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(TYPE,type);
        ReaderTabAlbumFragment fragment = new ReaderTabAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reader_tab, container, false);

        initData();
        initView();
        loadTopData();
        loadItemData();;

        return view;
    }

    private void initData() {
        type = getArguments().getString(TYPE);
        readerTabActivity = (ReaderTabActivity)this.getActivity();
        readerTabActivity.setOnAlbumPullListener(new ReaderTabActivity.OnAlbumPullListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                isRefresh = true;
                loadTopData();
                loadItemData();
            }
        });

    }

    private void initView() {
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.rcv_tab);

        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNum ++;
                isRefresh = false;
                loadItemData();
            }
        });
//        recyclerView.setNestedScrollingEnabled(true);



        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager_recommend);
        readerTabAlbumAdapter = new ReaderTabAlbumAdapter(getContext(),topData,itemata);
        recyclerView.setAdapter(readerTabAlbumAdapter);

    }

    private void loadTopData() {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("py","100");
            params.put("type",type);
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(topUrl, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReaderTabAlbumTopBean readerTabAlbumTopBean = GsonUtil.GsonToBean(result.toString(),ReaderTabAlbumTopBean.class);
                        topData = readerTabAlbumTopBean.getData();
                        readerTabAlbumAdapter.setTopData(topData);
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });

    }

    private void loadItemData() {

        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("py","100");
            params.put("type",type);
            params.put("pageNum",pageNum);
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(itemUrl, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReaderTabAlbumItemBean readerTabAlbumItemBean = GsonUtil.GsonToBean(result.toString(),ReaderTabAlbumItemBean.class);
                        itemata = readerTabAlbumItemBean.getData();

                        if(isRefresh){
//                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
                            if(itemata != null)
                                readerTabAlbumAdapter.refreshData(itemata);
//                            recyclerView.onPullComplete();
                        } else{
                            if(itemata != null){
                                readerTabAlbumAdapter.addData(itemata);
                                refreshLayout.finishLoadMore();
                            }

                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }
}

