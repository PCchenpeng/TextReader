package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.adapter.BookAdapter;
import com.dace.textreader.adapter.ReaderTabAlbumDetailListAdapter;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.huawei.updatesdk.sdk.service.storekit.bean.JsonBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailListFragment extends BaseFragment {

    private View view;
    private ReaderTabAlbumDetailListAdapter readerTabAlbumDetailListAdapter;
    private String url = HttpUrlPre.HTTP_URL_+"/select/album/detail";
    private List<ReadTabAlbumDetailBean.DataBean.BookBean.ArticleListBean> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private FrameLayout framelayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_detail_list, container, false);

        initData();
        initView();
//        loadTopData();
//        loadItemData();

        return view;
    }

    private void initData() {

    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rcv_tab);
        framelayout = view.findViewById(R.id.framelayout);
        readerTabAlbumDetailListAdapter = new ReaderTabAlbumDetailListAdapter(getContext(),mData);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager_recommend);
        recyclerView.setAdapter(readerTabAlbumDetailListAdapter);
        readerTabAlbumDetailListAdapter.setOnItemClickListen(new ReaderTabAlbumDetailListAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                turnToDetail(position);
            }
        });
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

    public void setmData(List<ReadTabAlbumDetailBean.DataBean.BookBean.ArticleListBean> mData) {
        this.mData = mData;
        if(mData !=null)
        readerTabAlbumDetailListAdapter.refreshData(mData);
        if (readerTabAlbumDetailListAdapter.getItemData().size() == 0){
            showDefaultView(framelayout, R.drawable.image_state_empty, "暂无内容～", false, false, "", null);
        }
    }

    /**
     * 查看文章详细内容
     *
     * @param position
     */
    private void turnToDetail(int position) {
        if (position == -1 || position > mData.size()) {
            return;
        }
        String id = mData.get(position).getArticleId();
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("essayId", id);
        intent.putExtra("imgUrl", mData.get(position).getImage());
        startActivity(intent);
    }

}
