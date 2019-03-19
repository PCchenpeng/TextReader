package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CardBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/8/31 0031 下午 2:57.
 * Version   1.0;
 * Describe :  推荐卡包列表适配器
 * History:
 * ==============================================================================
 */

public class CardRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<CardBean> mList;

    public CardRecommendAdapter(Context context, List<CardBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_recommend_layout,
                parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        CardBean cardBean = mList.get(position);
        GlideUtils.loadImageWithNoPlaceholder(mContext, cardBean.getValidImage(),
                ((ViewHolder) holder).iv_bg);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClick != null) {
            itemClick.onItemClick(v);
        }
    }

    public interface OnCardRecommendItemClick {
        void onItemClick(View view);
    }

    private OnCardRecommendItemClick itemClick;

    public void setOnCardRecommendItemClickListen(OnCardRecommendItemClick itemClickListen) {
        this.itemClick = itemClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_bg = itemView.findViewById(R.id.iv_bg_card_recommend_item);
        }
    }
}
