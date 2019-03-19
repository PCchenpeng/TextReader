package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class KnowledgeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<KnowledgeChildBean> mList;

    public KnowledgeDetailAdapter(Context context, List<KnowledgeChildBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_knowledge_detail_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        KnowledgeChildBean knowledgeChildBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_title.setText(knowledgeChildBean.getTitle());
        ((ViewHolder) viewHolder).tv_content.setText(knowledgeChildBean.getContent());
        ((ViewHolder) viewHolder).ll_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ViewHolder) viewHolder).tv_content.getMaxLines() == 3) {
                    ((ViewHolder) viewHolder).tv_content.setMaxLines(Integer.MAX_VALUE);
                    ((ViewHolder) viewHolder).tv_content.setLineSpacing(0, 2);
                    ((ViewHolder) viewHolder).tv_expand.setText("收起");
                    ((ViewHolder) viewHolder).iv_expand.setImageResource(R.drawable.ic_expand_less_black_36dp);
                } else {
                    ((ViewHolder) viewHolder).tv_content.setMaxLines(3);
                    ((ViewHolder) viewHolder).tv_content.setLineSpacing(0, 2);
                    ((ViewHolder) viewHolder).tv_expand.setText("展开");
                    ((ViewHolder) viewHolder).iv_expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_content;
        LinearLayout ll_expand;
        TextView tv_expand;
        ImageView iv_expand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_knowledge_detail_item);
            tv_content = itemView.findViewById(R.id.tv_content_knowledge_detail_item);
            ll_expand = itemView.findViewById(R.id.ll_expand_knowledge_detail_item);
            tv_expand = itemView.findViewById(R.id.tv_expand_knowledge_detail_item);
            iv_expand = itemView.findViewById(R.id.iv_expand_knowledge_detail_item);
        }
    }
}
