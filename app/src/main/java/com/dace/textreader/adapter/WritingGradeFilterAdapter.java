package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.LevelBean;
import com.dace.textreader.util.DensityUtil;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/7/26 0026 下午 2:23.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class WritingGradeFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private List<LevelBean> mList;
    private Context mContext;

    public WritingGradeFilterAdapter(List<LevelBean> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_writing_grade_filter_layout, null);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position % 3 == 0) {
            holder.itemView.setPadding(DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 15),
                    0);
        } else {
            holder.itemView.setPadding(0,
                    DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 15),
                    0);
        }
        ((ViewHolder) holder).tv_grade.setText(mList.get(position).getGradeName());
        ((ViewHolder) holder).rl_grade.setSelected(mList.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClick != null) {
            mItemClick.onClick(v);
        }
    }

    public interface OnWritingGradeFilterItemClick {
        void onClick(View view);
    }

    private OnWritingGradeFilterItemClick mItemClick;

    public void setOnWritingGradeFilterItemClickListen(OnWritingGradeFilterItemClick itemClick) {
        this.mItemClick = itemClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_grade;
        TextView tv_grade;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_grade = itemView.findViewById(R.id.rl_writing_grade_filter_item);
            tv_grade = itemView.findViewById(R.id.tv_writing_grade_filter_item);
        }
    }
}
