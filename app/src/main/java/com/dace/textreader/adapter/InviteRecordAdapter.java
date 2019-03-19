package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.InviteRecord;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/8/29 0029 下午 3:41.
 * Version   1.0;
 * Describe :  邀请记录适配器
 * History:
 * ==============================================================================
 */

public class InviteRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<InviteRecord> mList;

    public InviteRecordAdapter(Context context, List<InviteRecord> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_invite_recard_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tv_number.setText(mList.get(position).getPhoneNumInvited());
        ((ViewHolder) holder).tv_time.setText(mList.get(position).getRegisterTime());
        ((ViewHolder) holder).tv_reward.setText(String.valueOf(mList.get(position).getIfcPrizeNum()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_number;
        TextView tv_time;
        TextView tv_reward;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_number = itemView.findViewById(R.id.tv_phone_number_invite_record_item);
            tv_time = itemView.findViewById(R.id.tv_time_invite_record_item);
            tv_reward = itemView.findViewById(R.id.tv_reward_invite_record_item);
        }
    }
}
