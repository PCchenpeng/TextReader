package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.TypeBean;

import java.util.List;

/**
 * 文章类型选择适配器
 * Created by 70391 on 2018/4/2.
 */

public class ArticleTypeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<TypeBean> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnTypewItemClickListener {
        void onItemClick(View view);
    }

    private OnTypewItemClickListener mOnItemClickListener = null;

    public void setOnTypeItemClickListener(OnTypewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ArticleTypeRecyclerViewAdapter(Context mContext, List<TypeBean> mList) {
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
        TypeBean typeBean = mList.get(position);
        ((ViewHolder) holder).tv_item.setText(typeBean.getTypeName());
        if (typeBean.isSelected()) {
            ((ViewHolder) holder).tv_item.setTextColor(Color.parseColor("#ff9933"));
            ((ViewHolder) holder).under_line.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).tv_item.setTextColor(Color.parseColor("#999999"));
            ((ViewHolder) holder).under_line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;
        View under_line;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item = itemView.findViewById(R.id.tv_grade_choose_classes_item);
            under_line = itemView.findViewById(R.id.view_under_line_article_type_item);
        }
    }
}
