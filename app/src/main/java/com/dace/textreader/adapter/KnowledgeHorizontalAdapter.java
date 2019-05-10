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
import com.dace.textreader.util.DensityUtil;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/2/27 0027 下午 3:22.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class KnowledgeHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<KnowledgeChildBean> mList;

    public KnowledgeHorizontalAdapter(Context context, List<KnowledgeChildBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_knowledge_horizontal_layout, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        if (i == 0) {
//            viewHolder.itemView.setPadding(DensityUtil.dip2px(mContext, 15),
//                    0, 0, 0);
//        } else {
//            viewHolder.itemView.setPadding(0, 0, 0, 0);
//        }
        ((ViewHolder) viewHolder).textView.setText(mList.get(i).getTitle());
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

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title_knowledge_horizontal_item);
        }
    }

}
