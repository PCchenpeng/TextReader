package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.RecommendBean.DataBean.ArticleListBean;
import com.dace.textreader.util.GlideUtils;


import java.util.ArrayList;
import java.util.List;

public class HomeRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int TYPE_BIG = 1;
    private final int TYPE_IMG = 2;
    private final int TYPE_NOIMG = 3;
    private final int TYPE_TOP = 4;
    public final static int AUDIO_PIC = 10001;
    public final static int AUDIO_NOPIC = 10002;
    public final static int VIDEO = 10003;
    public final static int IMG = 10004;
    public final static int ARTICLE_PIC = 10005;
    public final static int ARTICLE_NOPIC = 10006;
    public final static int TOP = 10007;
    private List<ArticleListBean> itemList = new ArrayList<>();
    private Context mContext;

    public HomeRecommendAdapter(List<ArticleListBean> data, Context context) {
        this.mContext = context;
        this.itemList = data;
    }

    public void addData(List<ArticleListBean> item){
        this.itemList.addAll(item);
        notifyDataSetChanged();
    }

    public void refreshData(List<ArticleListBean> item){
        this.itemList.clear();
        this.itemList.addAll(item);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case TYPE_BIG:
                View view1 = LayoutInflater.from(mContext).inflate(
                        R.layout.item_home_recommend_1, viewGroup, false);
//                view1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int flag = itemList.get(itemPosition).getArticle().getFlag();
//                        String id = itemList.get(itemPosition).getArticle().getId();
//                        if(flag == 0){
//                            onItemClickListener.onClick(AUDIO_NOPIC,id,"");
//                        }else if(flag == 1){
//                            onItemClickListener.onClick(AUDIO_PIC,"","");
////                        }
//
//                    }
//                });

                return new BigHolder(view1);
            case TYPE_IMG:
                View view2 = LayoutInflater.from(mContext).inflate(
                        R.layout.item_home_recommend_2, viewGroup, false);
                return new ImgHolder(view2);
            case TYPE_NOIMG:
                View view3 = LayoutInflater.from(mContext).inflate(
                        R.layout.item_home_recommend_3, viewGroup, false);
                return new NoImgHolder(view3);
            case TYPE_TOP:
                View view4 = LayoutInflater.from(mContext).inflate(
                        R.layout.item_home_recommend_4, viewGroup, false);
                return new TopHolder(view4);
            default:
               return null;
        }
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return TYPE_TOP;
        }

        if(itemList.get(position-1).getCategory() == 0){
            Log.e("haha","TYPE_BIG");
            return TYPE_BIG;
        }else if(itemList.get(position-1).getCategory() == 1){
            String cover = itemList.get(position-1).getArticle().getCover();
            if(cover != null){
                Log.e("haha","TYPE_IMG");
                return TYPE_IMG;
            }else {
                Log.e("haha","TYPE_NOIMG");
                return TYPE_NOIMG;
            }
        }else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);
        final int itemPosition = i-1;
        switch (viewType){
            case TYPE_TOP:
                ((TopHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        int flag = itemList.get(itemPosition).getArticle().getFlag();
//                        String id = itemList.get(itemPosition).getArticle().getId();
//                        if(flag == 0){
//                            onItemClickListener.onClick(AUDIO_NOPIC,id,"");
//                        }else if(flag == 1){
//                            onItemClickListener.onClick(AUDIO_PIC,id,"");
//                        }
                        onItemClickListener.onClick(TOP,"","",-1);

                    }
                });
                break;

            case TYPE_BIG:

                GlideApp.with(mContext)
                        .load(itemList.get(itemPosition).getArticle().getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload();


                GlideUtils.loadHomeImage(mContext, itemList.get(itemPosition).getArticle().getImage(),
                        ((BigHolder) viewHolder).iv_recommend);
                ((BigHolder) viewHolder).tv_title.setText(itemList.get(itemPosition).getArticle().getTitle());
                ((BigHolder) viewHolder).tv_source.setText(itemList.get(itemPosition).getArticle().getSource());
                ((BigHolder) viewHolder).tv_subContent.setText(itemList.get(itemPosition).getArticle().getSubContent());
                ((BigHolder) viewHolder).tv_fenlei.setText("#"+itemList.get(itemPosition).getFenlei()+"#");
                GlideUtils.loadHomeUserImage(mContext, itemList.get(itemPosition).getArticle().getSourceImage(),
                        ((BigHolder) viewHolder).iv_source);

                String audio = itemList.get(itemPosition).getArticle().getAudio();
                String video = itemList.get(itemPosition).getArticle().getVideo();
                final String id = itemList.get(itemPosition).getArticle().getId();
                final int flag = itemList.get(itemPosition).getArticle().getFlag();
                if(audio != null ){
                    if(video != null){
                        ((BigHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                        ((BigHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_video);
                        ((BigHolder) viewHolder).tv_py.setText(itemList.get(itemPosition).getArticle().getVideoTime());

                        ((BigHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickListener.onClick(VIDEO,id,itemList.get(itemPosition).getArticle().getImage(),flag);
                            }
                        });

                    }else {
                        ((BigHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                        ((BigHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_music);
                        ((BigHolder) viewHolder).tv_py.setText(itemList.get(itemPosition).getArticle().getPv()+"py");

                        ((BigHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickListener.onClick(AUDIO_PIC,id,itemList.get(itemPosition).getArticle().getImage(),flag);
                            }
                        });
                    }


//                    ((BigHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        int flag = itemList.get(itemPosition).getArticle().getFlag();
//                        String id = itemList.get(itemPosition).getArticle().getId();
//                        if(flag == 0){
//                            onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getImage(),flag);
//                        }else if(flag == 1){
//                            onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getImage(),flag);
//                        }
//
//                        }
//                    });

                }else {
                    ((BigHolder) viewHolder).iv_type.setVisibility(View.GONE);
                    ((BigHolder) viewHolder).tv_py.setText(itemList.get(i).getArticle().getPv()+"py");
                    ((BigHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int flag = itemList.get(itemPosition).getArticle().getFlag();
                            String id = itemList.get(itemPosition).getArticle().getId();
                            if(flag == 0){
                                onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getImage(),flag);
                            }else if(flag == 1){
                                onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getImage(),flag);
                            }

                        }
                    });
                }

                break;
            case TYPE_IMG:
                GlideApp.with(mContext)
                        .load(itemList.get(itemPosition).getArticle().getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload();
                GlideUtils.loadHomeImage(mContext, itemList.get(itemPosition).getArticle().getCover(),
                        ((ImgHolder) viewHolder).iv_cover);
                ((ImgHolder) viewHolder).tv_content.setText(itemList.get(itemPosition).getArticle().getContent());
                ((ImgHolder) viewHolder).tv_subContent.setText(itemList.get(itemPosition).getArticle().getSubContent());
                ((ImgHolder) viewHolder).tv_fenlei.setText("#"+itemList.get(itemPosition).getFenlei()+"#");
                ((ImgHolder) viewHolder).tv_user.setText(itemList.get(itemPosition).getArticle().getUsername());
                GlideUtils.loadHomeUserImage(mContext, itemList.get(itemPosition).getArticle().getUserImage(),
                        ((ImgHolder) viewHolder).iv_user);

                ((ImgHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int flag = itemList.get(itemPosition).getArticle().getFlag();
                        String id = itemList.get(itemPosition).getArticle().getId();
                        if(flag == 0){
                            onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getCover(),flag);
                        }else if(flag == 1){
                            onItemClickListener.onClick(IMG,id,itemList.get(itemPosition).getArticle().getCover(),flag);
                        }

                    }
                });

                break;
            case TYPE_NOIMG:
                ((NoImgHolder) viewHolder).tv_content.setText(itemList.get(itemPosition).getArticle().getContent());
                ((NoImgHolder) viewHolder).tv_subContent.setText(itemList.get(itemPosition).getArticle().getSubContent());
                ((NoImgHolder) viewHolder).tv_fenlei.setText("#"+itemList.get(itemPosition).getFenlei()+"#");
                ((NoImgHolder) viewHolder).tv_user.setText(itemList.get(itemPosition).getArticle().getUsername());
                GlideUtils.loadHomeUserImage(mContext, itemList.get(itemPosition).getArticle().getUserImage(),
                        ((NoImgHolder) viewHolder).iv_user);

                ((NoImgHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int flag = itemList.get(itemPosition).getArticle().getFlag();
                        String id = itemList.get(itemPosition).getArticle().getId();
                        if(flag == 0){
                            onItemClickListener.onClick(IMG,id,"",flag);
                        }else if(flag == 1){
                            onItemClickListener.onClick(IMG,id,"",flag);
                        }

                    }
                });

                break;

            default:
                break;
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }




    class BigHolder extends RecyclerView.ViewHolder{
        ImageView iv_recommend,iv_type,iv_source;
        TextView tv_py,tv_title,tv_subContent,tv_source,tv_fenlei;

        public BigHolder(@NonNull View itemView) {
            super(itemView);
            iv_recommend = itemView.findViewById(R.id.iv_recommend);
            iv_type = itemView.findViewById(R.id.iv_type);
            iv_source = itemView.findViewById(R.id.iv_source);
            tv_py = itemView.findViewById(R.id.tv_py);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
            tv_source = itemView.findViewById(R.id.tv_source);
            tv_fenlei = itemView.findViewById(R.id.tv_fenlei);
        }
    }

    class ImgHolder extends RecyclerView.ViewHolder{
        ImageView iv_cover,iv_user;
        TextView tv_content,tv_subContent,tv_user,tv_fenlei;
        public ImgHolder(@NonNull View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            iv_user = itemView.findViewById(R.id.iv_user);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_fenlei = itemView.findViewById(R.id.tv_fenlei);
        }
    }

    class NoImgHolder extends RecyclerView.ViewHolder{
        ImageView iv_user;
        TextView tv_content,tv_subContent,tv_user,tv_fenlei;
        public NoImgHolder(@NonNull View itemView) {
            super(itemView);
            iv_user = itemView.findViewById(R.id.iv_user);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_fenlei = itemView.findViewById(R.id.tv_fenlei);
        }
    }

    class TopHolder extends RecyclerView.ViewHolder{
        ImageView iv_gif;
        public TopHolder(@NonNull View itemView) {
            super(itemView);
            iv_gif =  itemView.findViewById(R.id.iv_gif);
            iv_gif.setImageResource(R.drawable.anim_home_gif);
            AnimationDrawable animationDrawable = (AnimationDrawable) iv_gif.getDrawable();
            animationDrawable.start();
        }
    }

    public interface OnItemClickListener{
        void onClick(int type,String id,String imgUrl,int flag);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
