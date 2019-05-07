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
import com.dace.textreader.bean.AuthorWorksBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class AuthorWorksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AuthorWorksBean.DataBean> mData;

    public AuthorWorksAdapter(Context context, List<AuthorWorksBean.DataBean> data){
        this.context = context;
        this.mData = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_authorworks, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        ((ItemHolder)viewHolder).tv_title.setText(mData.get(i).getTitle());
      GlideUtils.loadImage(context,mData.get(i).getImage(),((ItemHolder)viewHolder).iv_img);
        ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 :mData.size() ;
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tv_title;
        private ImageView iv_img;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_img = itemView.findViewById(R.id.iv_img);
        }
    }
}
