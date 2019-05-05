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
import com.dace.textreader.bean.AuthorDetailBean;

import java.util.List;

public class AuthorDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AuthorDetailBean.DataBean.DescriptionListBean> mData;

    public AuthorDetailAdapter(Context context, List<AuthorDetailBean.DataBean.DescriptionListBean> data){
        this.context = context;
        this.mData = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_authordetail, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ItemHolder)viewHolder).tv_title.setText(mData.get(i).getNameStr());
        ((ItemHolder)viewHolder).tv_des.setText(mData.get(i).getNameStr());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 :mData.size() ;
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tv_title,tv_des;
        private ImageView iv_des;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_des = itemView.findViewById(R.id.tv_des);
            iv_des = itemView.findViewById(R.id.iv_des);
        }
    }
}
