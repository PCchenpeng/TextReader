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
import com.dace.textreader.adapter.ReaderTabAlbumDetailListAdapter;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailListFragment extends Fragment {

    private View view;
    private ReaderTabAlbumDetailListAdapter readerTabAlbumDetailListAdapter;
    private String url = HttpUrlPre.HTTP_URL_+"/select/album/detail";
    private boolean isRefresh = false;
    private List<ReadTabAlbumDetailBean.DataBean.BookBean.ArticleListBean> mData = new ArrayList<>();
    private RecyclerView recyclerView;

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

    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rcv_tab);
        readerTabAlbumDetailListAdapter = new ReaderTabAlbumDetailListAdapter(getContext(),mData);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager_recommend);
        recyclerView.setAdapter(readerTabAlbumDetailListAdapter);

    }

    private void loadTopData() {

    }

    private void loadItemData() {
        JSONObject params = new JSONObject();
        try {
            params.put("isShare","1");
            params.put("albumId","15");
            params.put("sign",123);
//            params.put("pageNum",pageNum);
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        ReadTabAlbumDetailBean readTabAlbumDetailBean = GsonUtil.GsonToBean(result.toString(),ReadTabAlbumDetailBean.class);
                        mData = readTabAlbumDetailBean.getData().getBook().get(0).getArticleList();
                        readerTabAlbumDetailListAdapter.refreshData(mData);

//                        if(isRefresh){
////                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
//                            if(itemata != null)
//                                readerTabSelectAdapter.refreshData(itemata);
////                            recyclerView.onPullComplete();
//                        } else{
//                            if(itemata != null){
//                                readerTabSelectAdapter.addData(itemata);
//                                refreshLayout.finishLoadMore();
//                            }
//
//                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }
}
