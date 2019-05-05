package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.AuthorDetailActivity;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class SearchAuthorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<SubListBean> mData;
    private Boolean isShowHeader;
    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 2;

    public SearchAuthorAdapter(List<SubListBean> data, Context context,Boolean isShowHeader){
        this.mContext = context;
        this.mData = data;
        this.isShowHeader = isShowHeader;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_search_author, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        GlideUtils.loadHomeUserImage(mContext,mData.get(i).getImage(),((ItemHolder)viewHolder).iv_author);
        ((ItemHolder)viewHolder).tv_author_name.setText(mData.get(i).getAuthor());
        final String authorId = mData.get(i).getIndex_id();
        ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AuthorDetailActivity.class);
                intent.putExtra("authorId",authorId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isShowHeader){
            return mData == null ? 0 : mData.size()+1;
        }else {
            return mData == null ? 0 : mData.size();
        }

    }

    @Override
    public int getItemViewType(int position){
        if(isShowHeader){
            if(position == 0){
                return TYPE_HEADER;
            }else {
                return TYPE_ITEM;
            }
        }else {
            return TYPE_ITEM;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_author_name;
        TextView tv_follow;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_author = itemView.findViewById(R.id.iv_author);
            tv_author_name = itemView.findViewById(R.id.tv_author_name);
            tv_follow = itemView.findViewById(R.id.tv_follow);
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        public HeadHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void addData(List<SubListBean> item){
        if(item != null)
        this.mData.addAll(item);
        notifyDataSetChanged();
    }

    public void refreshData(List<SubListBean> item){
        this.mData.clear();
        if(item != null)
        this.mData.addAll(item);
        notifyDataSetChanged();
    }

}
