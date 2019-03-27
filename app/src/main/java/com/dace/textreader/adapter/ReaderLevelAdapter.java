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
import com.dace.textreader.bean.ReaderLevelBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class ReaderLevelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ReaderLevelBean.DataBean.ArticleListBean> mData;
    private Context mContext;
    public ReaderLevelAdapter(List<ReaderLevelBean.DataBean.ArticleListBean> data, Context context){
        this.mData = data;
        this.mContext = context;
    }

    public void setData(List<ReaderLevelBean.DataBean.ArticleListBean> item){
        this.mData = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_reader_level, viewGroup, false);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        GlideUtils.loadHomeImage(mContext, mData.get(i).getImage(),
                ((ListHolder) viewHolder).iv_big);
        GlideUtils.loadHomeUserImage(mContext, mData.get(i).getSourceImage(),
                ((ListHolder) viewHolder).iv_source);
        ((ListHolder) viewHolder).tv_title.setText(mData.get(i).getTitle());
        ((ListHolder) viewHolder).tv_sub.setText(mData.get(i).getSubContent());
        ((ListHolder) viewHolder).tv_source.setText(mData.get(i).getSource());
        ((ListHolder) viewHolder).tv_type.setText("#"+mData.get(i).getType()+"#");
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ListHolder  extends RecyclerView.ViewHolder{
        ImageView iv_big,iv_source;
        TextView tv_title,tv_sub,tv_source,tv_type;
        public ListHolder(@NonNull View itemView) {
            super(itemView);
            iv_big = itemView.findViewById(R.id.iv_big);
            iv_source = itemView.findViewById(R.id.iv_source);
            tv_title = itemView.findViewById(R.id.tv_big_title);
            tv_sub = itemView.findViewById(R.id.tv_sub);
            tv_source = itemView.findViewById(R.id.tv_source);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }
}
