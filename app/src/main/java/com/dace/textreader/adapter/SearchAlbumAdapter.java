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
import com.dace.textreader.activity.ReaderTabAlbumDetailActivity;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class SearchAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<SubListBean> mData;

    public SearchAlbumAdapter(List<SubListBean> data, Context context){
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_search_album, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        GlideUtils.loadImage(mContext,mData.get(i).getImage(),((ItemHolder)viewHolder).iv_img);
        ((ItemHolder)viewHolder).tv_album_name.setText(mData.get(i).getTitle());
        ((ItemHolder)viewHolder).tv_title.setText("#" + mData.get(i).getCategory()+"#");

        final int format = mData.get(i).getFormat();
        final String sentenceNum = mData.get(i).getSentenceNum();
        final String albumId = mData.get(i).getIndex_id();

        ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ReaderTabAlbumDetailActivity.class);
                intent.putExtra("format",format);
                intent.putExtra("sentenceNum",sentenceNum);
                intent.putExtra("albumId",albumId);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_img;
        TextView tv_album_name;
        TextView tv_title;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_img);
            tv_album_name = itemView.findViewById(R.id.tv_album_name);
            tv_title = itemView.findViewById(R.id.tv_title);
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
