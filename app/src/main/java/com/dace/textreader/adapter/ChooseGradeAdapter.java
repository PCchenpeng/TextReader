package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.GradeBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/9/30 0030 上午 10:03.
 * Version   1.0;
 * Describe :  年级选择适配器
 * History:
 * ==============================================================================
 */

public class ChooseGradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<GradeBean> mList;

    public ChooseGradeAdapter(Context context, List<GradeBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_choose_grade_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int h = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 166)) / 3;
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = h;


        GradeBean gradeBean = mList.get(position);
        ((ViewHolder) holder).tv_garde.setText(gradeBean.getGrade());
        int gradeId = DataUtil.gradeCode2Level(gradeBean.getGradeId());
        if (gradeId == 2) {
            holder.itemView.setBackgroundResource(R.drawable.selector_grade_junior_choose_bg);
        } else if (gradeId == 3) {
            holder.itemView.setBackgroundResource(R.drawable.selector_grade_high_choose_bg);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_grade_primary_choose_bg);
        }
        if (gradeBean.isSelected()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnGradeItemClick != null) {
            mOnGradeItemClick.onClick(v);
        }
    }

    public interface OnGradeItemClick {
        void onClick(View view);
    }

    private OnGradeItemClick mOnGradeItemClick;

    public void setOnGradeItemClick(OnGradeItemClick onGradeItemClick) {
        this.mOnGradeItemClick = onGradeItemClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_garde;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_garde = itemView.findViewById(R.id.tv_grade_choose_grade_item);
        }
    }

}
