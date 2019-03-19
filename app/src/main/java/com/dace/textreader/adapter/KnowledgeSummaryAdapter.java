package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.KnowledgeBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/2/26 0026 下午 1:59.
 * Version   1.0;
 * Describe :  知识汇总列表适配器
 * History:
 * ==============================================================================
 */
public class KnowledgeSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<KnowledgeBean> mList;

    public KnowledgeSummaryAdapter(Context context, List<KnowledgeBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_knowledge_summary_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        KnowledgeBean knowledgeBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_title.setText(knowledgeBean.getTitle());
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        ((ViewHolder) viewHolder).recyclerView.setLayoutManager(layoutManager);
        KnowledgeChildAdapter adapter = new KnowledgeChildAdapter(mContext, knowledgeBean.getList());
        ((ViewHolder) viewHolder).recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListen(new KnowledgeChildAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = ((ViewHolder) viewHolder).recyclerView.getChildAdapterPosition(view);
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onClick(i, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListen {
        void onClick(int position, int childPosition);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_knowledge_summary_item);
            recyclerView = itemView.findViewById(R.id.recycler_view_knowledge_summary_item);
        }
    }

}
