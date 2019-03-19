package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.RechargeBean;

import java.util.List;

/**
 * 充值列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class RechargeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<RechargeBean> mList;

    public RechargeRecyclerViewAdapter(Context context, List<RechargeBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_recharge_layout, parent, false);
        //给布局设置点击监听
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        RechargeBean rechargeBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(rechargeBean.getTitle());
        ((ViewHolder) holder).tv_content.setText(rechargeBean.getContent());
        holder.itemView.setSelected(rechargeBean.isSelected());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    public interface OnRechargeItemClick {
        void onItemClick(View view);
    }

    private OnRechargeItemClick mOnItemClickListener;

    public void setOnItemClickListener(OnRechargeItemClick listener) {
        mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_recharge_item);
            tv_content = itemView.findViewById(R.id.tv_content_recharge_item);
        }
    }
}
