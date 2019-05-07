package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        ((ItemHolder)viewHolder).tv_title.setText(mData.get(i).getNameStr());
        String text = mData.get(i).getCont();
        String textTest = text.replaceAll("\n","\n\n");
        ((ItemHolder)viewHolder).tv_des.setText(textTest);
        ((ItemHolder)viewHolder).rl_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ItemHolder)viewHolder).tv_des.getVisibility() == View.VISIBLE){
                    ((ItemHolder)viewHolder).tv_des.setVisibility(View.GONE);
                    ((ItemHolder)viewHolder).iv_des.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }else {
                    ((ItemHolder)viewHolder).tv_des.setVisibility(View.VISIBLE);
                    ((ItemHolder)viewHolder).iv_des.setImageResource(R.drawable.ic_expand_less_black_36dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 :mData.size() ;
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tv_title,tv_des;
        private ImageView iv_des;
        private RelativeLayout rl_des;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_des = itemView.findViewById(R.id.tv_des);
            iv_des = itemView.findViewById(R.id.iv_des);
            rl_des = itemView.findViewById(R.id.rl_des);
        }
    }
}
