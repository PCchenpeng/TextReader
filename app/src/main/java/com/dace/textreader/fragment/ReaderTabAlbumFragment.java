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
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumFragment extends Fragment implements PullListener {

    private View view;
    private PullRecyclerView recyclerView;
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
        loadItemData();

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
        recyclerView = view.findViewById(R.id.rcv_tab);

//        recyclerView.setNestedScrollingEnabled(true);



        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        readerTabAlbumAdapter = new ReaderTabAlbumAdapter(getContext(),topData,itemata);
        recyclerView
//                .setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
//                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager_recommend)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(readerTabAlbumAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        pageNum ++;
                        isRefresh = false;
                        loadItemData();
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
    }

    private void loadTopData() {
        JSONObject params = new JSONObject();
        try {
            params.put("category",type);
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
            params.put("category",type);
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
                                recyclerView.onPullComplete();
                            }

                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        isRefresh = true;
        loadItemData();
    }

    @Override
    public void onLoadMore() {

    }
}

