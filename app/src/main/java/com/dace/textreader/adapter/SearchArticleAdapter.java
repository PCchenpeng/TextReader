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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class SearchArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<SubListBean> mData;

    public SearchArticleAdapter(List<SubListBean> data, Context context){
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_search_article, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int itemPosition) {
//        String imgUrl = mData.get(i).getImage();
//        if (imgUrl != null && !imgUrl.equals(""))
//        GlideUtils.loadHomeUserImage(mContext,imgUrl,((ItemHolder)viewHolder).iv_author);
//        ((ItemHolder)viewHolder).tv_author_name.setText(mData.get(i).getAuthor());

        final String imgUrl = mData.get(itemPosition).getImage();
        if (imgUrl != null && !imgUrl.equals("") && !imgUrl.equals("null")){
            GlideApp.with(mContext)
                    .load(mData.get(itemPosition).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
            ((ItemHolder) viewHolder).iv_cover.setVisibility(View.VISIBLE);
            GlideUtils.loadImage(mContext, mData.get(itemPosition).getImage(),
                    ((ItemHolder) viewHolder).iv_cover,4);
        }else {
            ((ItemHolder) viewHolder).iv_cover.setVisibility(View.GONE);
        }
        ((ItemHolder) viewHolder).tv_content.setText(mData.get(itemPosition).getTitle());
        ((ItemHolder) viewHolder).tv_subContent.setText(mData.get(itemPosition).getContent());
        ((ItemHolder) viewHolder).tv_fenlei.setText("#"+mData.get(itemPosition).getCategory()+"#");
        ((ItemHolder) viewHolder).tv_user.setText(mData.get(itemPosition).getSource());
        GlideUtils.loadHomeUserImage(mContext, mData.get(itemPosition).getSource_image(),
                ((ItemHolder) viewHolder).iv_user);

        final String category = mData.get(itemPosition).getCategory();
        int flag = mData.get(itemPosition).getFlag();
        final String id = mData.get(itemPosition).getIndex_id();

        ((ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(category .equals("创作")){
                    intent = new Intent(mContext, ArticleDetailActivity.class);
                    intent.putExtra("essayId", id);
                    intent.putExtra("imgUrl", imgUrl);
                    intent.putExtra("isVideo",true);
                }else {
                    intent = new Intent(mContext, ArticleDetailActivity.class);
                    intent.putExtra("essayId", id);
                    intent.putExtra("imgUrl", imgUrl);
                    intent.putExtra("isVideo",true);
                }

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_cover,iv_user;
        TextView tv_content,tv_subContent,tv_user,tv_fenlei;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            iv_user = itemView.findViewById(R.id.iv_user);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_fenlei = itemView.findViewById(R.id.tv_fenlei);
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
