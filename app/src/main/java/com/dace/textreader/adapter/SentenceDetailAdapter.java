package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SentenceBean;
import com.dace.textreader.util.DensityUtil;

import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/26 0026 上午 10:50.
 * Version   1.0;
 * Describe :  每日一句详情列表适配器
 * History:
 * ==============================================================================
 */
public class SentenceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SentenceBean> mList;

    public SentenceDetailAdapter(Context context, List<SentenceBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sentence_deatil_layout,
                viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();

        if (i == 0) {
            layoutParams.width = DensityUtil.getScreenWidth(mContext) -
                    DensityUtil.dip2px(mContext, 23);
            viewHolder.itemView.setPadding(DensityUtil.dip2px(mContext, 33),
                    0, DensityUtil.dip2px(mContext, 10), 0);
        } else if (i == mList.size() - 1) {
            layoutParams.width = DensityUtil.getScreenWidth(mContext) -
                    DensityUtil.dip2px(mContext, 23);
            viewHolder.itemView.setPadding(DensityUtil.dip2px(mContext, 10), 0,
                    DensityUtil.dip2px(mContext, 33), 0);
        } else {
            layoutParams.width = DensityUtil.getScreenWidth(mContext) -
                    DensityUtil.dip2px(mContext, 46);
            viewHolder.itemView.setPadding(DensityUtil.dip2px(mContext, 10), 0,
                    DensityUtil.dip2px(mContext, 10), 0);
        }

        SentenceBean sentenceBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_date.setText(sentenceBean.getDate());
        String content = sentenceBean.getContent() + "\n";
        ((ViewHolder) viewHolder).tv_content.setText(content);
        ((ViewHolder) viewHolder).tv_author.setText(sentenceBean.getAuthor());
        if (sentenceBean.isCollectOrNot()) {
            ((ViewHolder) viewHolder).iv_collection.setImageResource(R.drawable.bottom_collection_selected);
        } else {
            ((ViewHolder) viewHolder).iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
        }
        ((ViewHolder) viewHolder).rl_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCollectionClick != null) {
                    onItemCollectionClick.onClick(i);
                }
            }
        });
        ((ViewHolder) viewHolder).rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemShareClick != null) {
                    onItemShareClick.onClick(i);
                }
            }
        });
        ((ViewHolder) viewHolder).iv_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEditorClick != null) {
                    onItemEditorClick.onClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemCollectionClick {
        void onClick(int position);
    }

    private OnItemCollectionClick onItemCollectionClick;

    public void setOnItemCollectionClick(OnItemCollectionClick onItemCollectionClick) {
        this.onItemCollectionClick = onItemCollectionClick;
    }

    public interface OnItemShareClick {
        void onClick(int position);
    }

    private OnItemShareClick onItemShareClick;

    public void setOnItemShareClick(OnItemShareClick onItemShareClick) {
        this.onItemShareClick = onItemShareClick;
    }

    public interface OnItemEditorClick {
        void onClick(int position);
    }

    private OnItemEditorClick onItemEditorClick;

    public void setOnItemEditorClick(OnItemEditorClick onItemEditorClick) {
        this.onItemEditorClick = onItemEditorClick;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_collection;
        ImageView iv_collection;
        RelativeLayout rl_share;
        TextView tv_date;
        JustifyTextView tv_content;
        TextView tv_author;
        ImageView iv_practice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_collection = itemView.findViewById(R.id.rl_collection_sentence_detail_item);
            iv_collection = itemView.findViewById(R.id.iv_collection_sentence_detail_item);
            rl_share = itemView.findViewById(R.id.rl_share_sentence_detail_item);
            tv_date = itemView.findViewById(R.id.tv_date_sentence_detail_item);
            tv_content = itemView.findViewById(R.id.tv_content_sentence_detail_item);
            tv_author = itemView.findViewById(R.id.tv_author_sentence_detail_item);
            iv_practice = itemView.findViewById(R.id.iv_practice_sentence_detail_item);
        }
    }

}
