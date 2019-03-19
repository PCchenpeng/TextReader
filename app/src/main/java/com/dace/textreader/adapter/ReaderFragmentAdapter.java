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
import com.dace.textreader.bean.ReaderSortBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/9/25 0025 上午 11:12.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class ReaderFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<ReaderSortBean> mList;

    public ReaderFragmentAdapter(Context context, List<ReaderSortBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reader_sort_fragment,
                parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (position == 0) {
            layoutParams.width = DensityUtil.dip2px(mContext, 120);
            holder.itemView.setPadding(DensityUtil.dip2px(mContext, 15),
                    0, 0, 0);
        } else {
            layoutParams.width = DensityUtil.dip2px(mContext, 105);
            holder.itemView.setPadding(0, 0, 0, 0);
        }

        ReaderSortBean readerSortBean = mList.get(position);
        GlideUtils.loadSquareImage(mContext, readerSortBean.getImage(), ((ViewHolder) holder).imageView);
        ((ViewHolder) holder).tv_name.setText(readerSortBean.getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null) {
            onItemClick.OnItemClick(v);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_reader_sort_item);
            tv_name = itemView.findViewById(R.id.tv_name_reader_sort_item);
        }
    }

    public interface OnReaderFragmentSortItemClick {
        void OnItemClick(View view);
    }

    private OnReaderFragmentSortItemClick onItemClick;

    public void setOnItemClick(OnReaderFragmentSortItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
}
