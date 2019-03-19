package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.TransactionBean;

import java.util.List;

/**
 * 交易记录子列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class TransactionChildRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<TransactionBean> mList;

    public TransactionChildRecyclerViewAdapter(Context context, List<TransactionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_transaction_child_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        TransactionBean transactionBean = mList.get(position);
        String time = "交易时间：" + transactionBean.getTime();
        ((ViewHolder) holder).tv_time.setText(time);
        ((ViewHolder) holder).tv_content.setText(transactionBean.getContent());
        int status = transactionBean.getStatus();
        if (status == 0) {
            //交易失败
            ((ViewHolder) holder).tv_status.setText("交易失败");
            ((ViewHolder) holder).tv_price.setTextColor(Color.parseColor("#999999"));
        } else {
            if (status == 3) {  //退款成功
                ((ViewHolder) holder).tv_status.setText("退款成功");
            } else {  //交易成功
                ((ViewHolder) holder).tv_status.setText("交易成功");
            }
            if (transactionBean.getCategory() == 0 || transactionBean.getCategory() == 3) {
                //往账户加钱
                ((ViewHolder) holder).tv_price.setTextColor(Color.parseColor("#46CA61"));
            } else {
                //扣除账户的钱
                ((ViewHolder) holder).tv_price.setTextColor(Color.parseColor("#FF9933"));
            }
        }
        String price = transactionBean.getCost();
        ((ViewHolder) holder).tv_price.setText(price);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnOrderChildClick != null) {
            mOnOrderChildClick.onClick(v);
        }
    }

    public interface OnOrderChildClick {
        void onClick(View view);
    }

    private OnOrderChildClick mOnOrderChildClick;

    public void setOnOrderChildClick(OnOrderChildClick onOrderChildClick) {
        this.mOnOrderChildClick = onOrderChildClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time;
        TextView tv_status;
        TextView tv_content;
        TextView tv_price;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time_transaction_child_item);
            tv_status = itemView.findViewById(R.id.tv_status_transaction_child_item);
            tv_content = itemView.findViewById(R.id.tv_content_transaction_child_item);
            tv_price = itemView.findViewById(R.id.tv_price_transaction_child_item);
        }
    }
}
