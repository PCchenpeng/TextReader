package com.dace.textreader.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CardBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/8/31 0031 下午 2:57.
 * Version   1.0;
 * Describe :  我的卡包列表适配器
 * History:
 * ==============================================================================
 */

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<CardBean> mList;
    private boolean available;

    public CardAdapter(Context context, List<CardBean> list, boolean available) {
        this.mContext = context;
        this.mList = list;
        this.available = available;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            CardBean cardBean = mList.get(position);
            for (String key : bundle.keySet()) {
                switch (key) {
                    case "key_selected":
                        if (available) {
                            ((ViewHolder) holder).tv_use.setVisibility(View.GONE);
                            if (cardBean.isSelected()) {
                                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_select);
                            } else {
                                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_default);
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        RecyclerView.LayoutParams layoutParams =
                (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == mList.size() - 1) {
            layoutParams.setMargins(DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20),
                    DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20));
        } else {
            layoutParams.setMargins(DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20),
                    DensityUtil.dip2px(mContext, 15),
                    0);
        }
        holder.itemView.setLayoutParams(layoutParams);

        CardBean cardBean = mList.get(position);
        String imageUrl;
        if (cardBean.getStatus() == 1) {
            imageUrl = cardBean.getValidImage();
            ((ViewHolder) holder).tv_use.setText("去使用");
        } else {
            imageUrl = cardBean.getInvalidImage();
            ((ViewHolder) holder).tv_use.setText("去购买");
        }
        GlideUtils.loadImageWithNoPlaceholder(mContext, imageUrl, ((ViewHolder) holder).iv_bg);
        ((ViewHolder) holder).tv_title.setText(cardBean.getTitle());
        if (cardBean.getFrequency() == -1) {
            ((ViewHolder) holder).tv_content.setVisibility(View.GONE);
        } else {
            String content = "剩余" + cardBean.getFrequency() + "次";
            ((ViewHolder) holder).tv_content.setText(content);
            ((ViewHolder) holder).tv_content.setVisibility(View.VISIBLE);
        }
        String time = "有效期至" + cardBean.getStopTime();
        ((ViewHolder) holder).tv_time.setText(time);

        if (available) {
            ((ViewHolder) holder).tv_use.setVisibility(View.GONE);
            if (cardBean.isSelected()) {
                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_select);
            } else {
                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_default);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (onCardItemClick != null) {
            onCardItemClick.onItemClick(v);
        }
    }

    public interface OnCardItemClick {
        void onItemClick(View view);
    }

    private OnCardItemClick onCardItemClick;

    public void setOnCardItemClick(OnCardItemClick onCardItemClick) {
        this.onCardItemClick = onCardItemClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_bg;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        LinearLayout ll_use;
        TextView tv_use;
        ImageView iv_use;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_bg = itemView.findViewById(R.id.iv_bg_card_item);
            tv_title = itemView.findViewById(R.id.tv_title_card_item);
            tv_content = itemView.findViewById(R.id.tv_content_card_item);
            tv_time = itemView.findViewById(R.id.tv_time_card_item);
            ll_use = itemView.findViewById(R.id.ll_use_card_item);
            tv_use = itemView.findViewById(R.id.tv_use_card_item);
            iv_use = itemView.findViewById(R.id.iv_use_card_item);
        }
    }
}
