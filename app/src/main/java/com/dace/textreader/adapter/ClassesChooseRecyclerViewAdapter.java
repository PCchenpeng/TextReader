package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;

import java.util.List;

/**
 * 课内文章列表条件筛选适配器
 * Created by 70391 on 2017/7/31.
 */

public class ClassesChooseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private int selectedItem = 0;
    private Context mContext;
    private List<String> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ClassesChooseRecyclerViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_classes_grade_choose_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String item = mList.get(position);
        ((ViewHolder) holder).tv_item.setText(item);
        if (position == selectedItem) {
            ((ViewHolder) holder).tv_item.setTextColor(Color.parseColor("#4D72FF"));
        } else {
            ((ViewHolder) holder).tv_item.setTextColor(Color.parseColor("#999999"));
        }
    }

    /**
     * 设置item被选中
     *
     * @param position //item位置
     */
    public void setItemSelected(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item = itemView.findViewById(R.id.tv_grade_choose_classes_item);
        }
    }
}
