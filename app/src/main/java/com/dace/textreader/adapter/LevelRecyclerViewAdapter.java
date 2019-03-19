package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.LevelBean;

import java.util.List;

/**
 * 等级选择列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class LevelRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<LevelBean> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnLevelItemClickListener {
        void onItemClick(View view);
    }

    private OnLevelItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnLevelItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public LevelRecyclerViewAdapter(Context mContext, List<LevelBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_level_choose_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LevelBean levelBean = mList.get(position);
        if (levelBean.isSelected()) {
            ((ViewHolder) holder).tv_level.setTextColor(Color.parseColor("#ff9933"));
        } else {
            ((ViewHolder) holder).tv_level.setTextColor(Color.parseColor("#333333"));
        }
        ((ViewHolder) holder).tv_level.setText(levelBean.getGradeName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_level;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_level = itemView.findViewById(R.id.tv_level_choose_item);
        }
    }
}
