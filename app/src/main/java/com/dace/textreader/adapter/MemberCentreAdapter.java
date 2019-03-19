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
import com.dace.textreader.bean.MemberCardBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/1/21 0021 下午 3:02.
 * Version   1.0;
 * Describe :  会员中心列表内容适配器
 * History:
 * ==============================================================================
 */
public class MemberCentreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<MemberCardBean> mList;
    private boolean activated = false;

    public MemberCentreAdapter(Context context, List<MemberCardBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_member_centre_layout,
                viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String key = (String) payloads.get(0);
            switch (key) {
                case "count":
                    MemberCardBean bean = mList.get(position);
                    if (activated) {
                        if (bean.getCount() == -1) {
                            ((ViewHolder) holder).tv_count.setVisibility(View.GONE);
                        } else {
                            String count = "剩余" + bean.getCount() + "次";
                            ((ViewHolder) holder).tv_count.setText(count);
                            ((ViewHolder) holder).tv_count.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MemberCardBean bean = mList.get(i);
        GlideUtils.loadImageWithNoRadius(mContext, bean.getCardImage(),
                ((ViewHolder) viewHolder).imageView);

        ((ViewHolder) viewHolder).tv_name.setText(bean.getCardName());
        ((ViewHolder) viewHolder).tv_detail.setText(bean.getCardDescription());
        if (activated) {
            if (bean.getCount() == -1) {
                ((ViewHolder) viewHolder).tv_count.setVisibility(View.GONE);
            } else {
                String count = "剩余" + bean.getCount() + "次";
                ((ViewHolder) viewHolder).tv_count.setText(count);
                ((ViewHolder) viewHolder).tv_count.setVisibility(View.VISIBLE);
            }
            if (bean.getCardType() == -1) {
                ((ViewHolder) viewHolder).iv_use.setVisibility(View.GONE);
            } else {
                ((ViewHolder) viewHolder).iv_use.setVisibility(View.VISIBLE);
            }
        } else {
            ((ViewHolder) viewHolder).tv_count.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).iv_use.setVisibility(View.GONE);
        }
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

        ImageView imageView;
        TextView tv_count;
        TextView tv_name;
        ImageView iv_use;
        TextView tv_detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_member_centre_item);
            tv_count = itemView.findViewById(R.id.tv_count_member_centre_item);
            tv_name = itemView.findViewById(R.id.tv_name_member_centre_item);
            iv_use = itemView.findViewById(R.id.iv_use_member_centre_item);
            tv_detail = itemView.findViewById(R.id.tv_detail_member_centre_item);
        }
    }

}
