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

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.adapter.BookAdapter;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.util.TurnToActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailBookFragment extends BaseFragment {

    private View view;
    private BookAdapter bookAdapter;
    private List<ReadTabAlbumDetailBean.DataBean.BookBean> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private FrameLayout framelayout;
    private String imgUrl = "";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_detail_list, container, false);

        initData();
        loadTopData();
        loadItemData();
        initView();
        return view;
    }

    private void initData() {

    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rcv_tab);
        framelayout = view.findViewById(R.id.framelayout);
        bookAdapter = new BookAdapter(getContext(),mData);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager_recommend);
        recyclerView.setAdapter(bookAdapter);
        bookAdapter.setOnItemClickListen(new BookAdapter.OnItemClickListen() {
            @Override
            public void onClick(int position, int childPosition) {
                turnToDetail(position, childPosition);
            }
        });

    }

    private void loadTopData() {

    }

    private void loadItemData() {
    }

    public void setmData(List<ReadTabAlbumDetailBean.DataBean.BookBean> mData) {
        this.mData = mData;
        if(mData !=null)
        bookAdapter.refreshData(mData);
        if (bookAdapter.getmList().size() == 0){
            showDefaultView(framelayout, R.drawable.image_state_empty, "暂无内容～", false, false, "", null);
        }
    }

    /**
     * 查看文章详细内容
     *
     * @param position
     * @param childPosition
     */
    private void turnToDetail(int position, int childPosition) {
        if (position == -1 || position > mData.size()) {
            return;
        }
        if (childPosition == -1 || childPosition > mData.get(position).getArticleList().size()) {
            return;
        }
        String id = mData.get(position).getArticleList().get(childPosition).getArticleId();
        int flag = mData.get(position).getArticleList().get(childPosition).getFlag();
        int py = mData.get(position).getArticleList().get(childPosition).getScore();
        TurnToActivityUtil.turnToDetail(getContext(),flag,id,py,imgUrl);


    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
