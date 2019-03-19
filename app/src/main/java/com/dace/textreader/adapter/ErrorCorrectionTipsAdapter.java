package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.ErrorCorrectionTipsBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 5:36.
 * Version   1.0;
 * Describe :  纠错提示选择列表适配器
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionTipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ErrorCorrectionTipsBean> mList;

    public ErrorCorrectionTipsAdapter(Context context, List<ErrorCorrectionTipsBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_error_correction_tips_item
                , viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ErrorCorrectionTipsBean bean = mList.get(i);
        String tips = bean.getTips() + "：";
        ((ViewHolder) viewHolder).tv_tip.setText(tips);
        ((ViewHolder) viewHolder).tv_error.setText(bean.getError());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_tip;
        TextView tv_error;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tip = itemView.findViewById(R.id.tv_tips_error_correction_tip);
            tv_error = itemView.findViewById(R.id.tv_error_correction_tip);
        }
    }

}
