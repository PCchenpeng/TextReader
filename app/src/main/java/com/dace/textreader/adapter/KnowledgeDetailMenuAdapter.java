package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.KnowledgeChildBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/2/27 0027 上午 10:16.
 * Version   1.0;
 * Describe :  知识点详情列表适配器
 * History:
 * ==============================================================================
 */
public class KnowledgeDetailMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<KnowledgeChildBean> mList;

    public KnowledgeDetailMenuAdapter(Context context, List<KnowledgeChildBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_knowledge_detail_menu_layout, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        KnowledgeChildBean knowledgeChildBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_title.setText(knowledgeChildBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListen != null) {
            mOnItemClickListen.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_knowledge_detail_menu_item);
        }
    }
}
