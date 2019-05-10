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
import com.dace.textreader.activity.HomeAudioDetailActivity;
import com.dace.textreader.bean.ReaderLevelBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class HomeLevelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ReaderLevelBean.DataBean.ArticleListBean> mData;
    private Context mContext;
    public HomeLevelAdapter(List<ReaderLevelBean.DataBean.ArticleListBean> data, Context context){
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        final String imgUrl = mData.get(i).getImage();
        GlideApp.with(mContext)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
        GlideUtils.loadHomeImage(mContext, imgUrl,
                ((ListHolder) viewHolder).iv_big);
        GlideUtils.loadHomeUserImage(mContext, mData.get(i).getSourceImage(),
                ((ListHolder) viewHolder).iv_source);
        ((ListHolder) viewHolder).tv_title.setText(mData.get(i).getTitle());
        ((ListHolder) viewHolder).tv_sub.setText(mData.get(i).getSubContent());
        ((ListHolder) viewHolder).tv_source.setText(mData.get(i).getSource());
        ((ListHolder) viewHolder).tv_type.setText("#"+mData.get(i).getType()+"#");



        String audio = mData.get(i).getAudio();
        String video = mData.get(i).getVideo();
        final String id = mData.get(i).getId();
        final int flag = mData.get(i).getFlag();

        if(audio != null ){
            if(video != null){
                ((ListHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                ((ListHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_video);

                ((ListHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转带video的详情页
                        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        intent.putExtra("isVideo",true);
                        mContext.startActivity(intent);
                    }
                });

            }else {
                ((ListHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                ((ListHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_music);

                ((ListHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag == 0){
                            //跳转正常详情页
                            Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                            intent.putExtra("essayId", id);
                            intent.putExtra("imgUrl", imgUrl);
                            mContext.startActivity(intent);
                        }else if(flag == 1){
                            //跳转绘本
                            int py = mData.get(i).getScore();
                            Intent intent = new Intent(mContext, HomeAudioDetailActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("py",py);
                            mContext.startActivity(intent);
                        }

                    }
                });
            }


        }else {
            ((ListHolder) viewHolder).iv_type.setVisibility(View.GONE);
            ((ListHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag == 0){
                        //跳转正常详情页
                        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        mContext.startActivity(intent);
                    }else if(flag == 1){
                        //跳转绘本
                        int py = mData.get(i).getScore();
                        Intent intent = new Intent(mContext, HomeAudioDetailActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("py",py);
                        mContext.startActivity(intent);
                    }

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ListHolder  extends RecyclerView.ViewHolder{
        ImageView iv_big,iv_source,iv_type;
        TextView tv_title,tv_sub,tv_source,tv_type;
        public ListHolder(@NonNull View itemView) {
            super(itemView);
            iv_type = itemView.findViewById(R.id.iv_type);
            iv_big = itemView.findViewById(R.id.iv_big);
            iv_source = itemView.findViewById(R.id.iv_source);
            tv_title = itemView.findViewById(R.id.tv_big_title);
            tv_sub = itemView.findViewById(R.id.tv_sub);
            tv_source = itemView.findViewById(R.id.tv_source);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }
}
