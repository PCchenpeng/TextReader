package com.dace.textreader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.SearchHistoryAdapter;
import com.dace.textreader.bean.SearchHistoryBean;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/11/20 0020 下午 5:23.
 * Version   1.0;
 * Describe :  搜索历史
 * History:
 * ==============================================================================
 */
public class SearchHistoryFragment extends Fragment {

    private View view;
    private TextView tv_clear;
    private TextView tv_empty;
    private RecyclerView recyclerView;

    private Context mContext;

    private List<SearchHistoryBean> mList = new ArrayList<>();
    private SearchHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_history, container, false);

        initView();
        initData();
        initEvents();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initEvents() {
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteAll(SearchHistoryBean.class);
                mList.clear();
                adapter.notifyDataSetChanged();
                tv_clear.setVisibility(View.GONE);
                tv_empty.setVisibility(View.VISIBLE);
            }
        });
        adapter.setOnItemClickListen(new SearchHistoryAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (onWordsClickListen != null) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    String words = mList.get(pos).getWords();
                    onWordsClickListen.onClick(words);
                }
            }
        });
        adapter.setOnItemDeleteClickListen(new SearchHistoryAdapter.OnItemDeleteClickListen() {
            @Override
            public void onClick(int position) {
                long id = mList.get(position).getId();
                LitePal.delete(SearchHistoryBean.class, id);
                mList.remove(position);
                adapter.notifyDataSetChanged();
                if (mList.size() == 0) {
                    tv_empty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public interface OnWordsClickListen {
        void onClick(String words);
    }

    private OnWordsClickListen onWordsClickListen;

    public void setOnWordsClickListen(OnWordsClickListen onWordsClickListen) {
        this.onWordsClickListen = onWordsClickListen;
    }

    private void initData() {
        LitePal.findAllAsync(SearchHistoryBean.class).listen(new FindMultiCallback<SearchHistoryBean>() {
            @Override
            public void onFinish(List<SearchHistoryBean> list) {
                if (list.size() != 0) {
                    tv_empty.setVisibility(View.GONE);
                    tv_clear.setVisibility(View.VISIBLE);
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initView() {
        tv_clear = view.findViewById(R.id.tv_clear_search_history_fragment);
        tv_empty = view.findViewById(R.id.tv_empty_search_history_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_search_history_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchHistoryAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }
}
