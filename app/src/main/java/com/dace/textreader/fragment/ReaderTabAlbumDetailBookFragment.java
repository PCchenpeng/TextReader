package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dace.textreader.R;
import com.dace.textreader.activity.KnowledgeDetailActivity;
import com.dace.textreader.adapter.BookAdapter;
import com.dace.textreader.adapter.KnowledgeSummaryAdapter;
import com.dace.textreader.adapter.ReaderTabAlbumDetailBookListAdapter;
import com.dace.textreader.adapter.ReaderTabAlbumDetailListAdapter;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailBookFragment extends Fragment {

    private View view;
    private BookAdapter bookAdapter;
    private List<ReadTabAlbumDetailBean.DataBean.BookBean> mData = new ArrayList<>();
    private RecyclerView recyclerView;


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
        Log.d("111",mData.toString());
        if(mData !=null)
        bookAdapter.refreshData(mData);
    }

    /**
     * 前往详情
     *
     * @param position
     * @param childPosition
     */
    private void turnToDetail(int position, int childPosition) {
        if (position == -1 || position > mData.size()) {
            return;
        }
        String title = mData.get(position).getLevel1();
        if (childPosition == -1 || childPosition > mData.get(position).getArticleList().size()) {
            return;
        }
        String id = mData.get(position).getArticleList().get(childPosition).getArticleId();
        Intent intent = new Intent(getContext(), KnowledgeDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
    }

}
