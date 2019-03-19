package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SearchHistoryBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/20 0020 下午 5:33.
 * Version   1.0;
 * Describe :  聚合搜索历史列表适配器
 * History:
 * ==============================================================================
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<SearchHistoryBean> mList;

    public SearchHistoryAdapter(Context context, List<SearchHistoryBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_history,
                viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        SearchHistoryBean bean = mList.get(i);
        ((ViewHolder) viewHolder).textView.setText(bean.getWords());
        ((ViewHolder) viewHolder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteClickListen != null) {
                    onItemDeleteClickListen.onClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListen != null) {
            onItemClickListen.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen onItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.onItemClickListen = onItemClickListen;
    }

    public interface OnItemDeleteClickListen {
        void onClick(int position);
    }

    private OnItemDeleteClickListen onItemDeleteClickListen;

    public void setOnItemDeleteClickListen(OnItemDeleteClickListen onItemDeleteClickListen) {
        this.onItemDeleteClickListen = onItemDeleteClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_word_history_item);
            imageView = itemView.findViewById(R.id.iv_clear_history_item);
        }

    }

}
