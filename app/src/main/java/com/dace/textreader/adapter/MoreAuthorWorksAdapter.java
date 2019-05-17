package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.AuthorWorksBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class MoreAuthorWorksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AuthorWorksBean.DataBean> mData;

    public final static int AUDIO_PIC = 10001;
    public final static int AUDIO_NOPIC = 10002;
    public final static int VIDEO = 10003;
    public final static int IMG = 10004;

    public MoreAuthorWorksAdapter(Context context, List<AuthorWorksBean.DataBean> data){
        this.context = context;
        this.mData = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_magnum_work, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        ((ItemHolder)viewHolder).tv_title.setText(mData.get(i).getTitle());
        GlideApp.with(context)
                .load(mData.get(i).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((ItemHolder) viewHolder).iv_recommend.getLayoutParams();
        layoutParams.width = DensityUtil.getScreenWidth(context);
        layoutParams.height = DensityUtil.getScreenWidth(context) * 2/3;
        ((ItemHolder) viewHolder).iv_recommend.setLayoutParams(layoutParams);
        GlideUtils.loadHomeImage(context, mData.get(i).getImage(),
                ((ItemHolder) viewHolder).iv_recommend);
        ((ItemHolder) viewHolder).tv_title.setText(mData.get(i).getTitle());
        ((ItemHolder) viewHolder).tv_subContent.setText(mData.get(i).getSubContent());
        String audio = mData.get(i).getAudio();
        String video = mData.get(i).getVideo();
        final String id = mData.get(i).getId();
        final int flag = mData.get(i).getFlag();
        if(audio != null ){
            if(video != null){
                ((ItemHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                ((ItemHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_video);
                ((ItemHolder) viewHolder).tv_py.setText(mData.get(i).getScore()+"PY");

                ((ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClick(VIDEO,id,mData.get(i).getImage(),flag,-1);
                    }
                });

            }else {
                ((ItemHolder) viewHolder).iv_type.setVisibility(View.VISIBLE);
                ((ItemHolder) viewHolder).iv_type.setImageResource(R.drawable.article_icon_music);
                ((ItemHolder) viewHolder).tv_py.setText(mData.get(i).getScore()+"PY");
//                        final String py = String.valueOf(mData.get(i).getScore());

                ((ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClick(AUDIO_PIC,id,mData.get(i).getImage(),flag,
                                mData.get(i).getScore());
                    }
                });
            }


        }else {
            ((ItemHolder) viewHolder).iv_type.setVisibility(View.GONE);
            ((ItemHolder) viewHolder).tv_py.setText(mData.get(i).getScore()+"PY");
            ((ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flag = mData.get(i).getFlag();
                    String id = mData.get(i).getId();
                    if(flag == 0){
                        onItemClickListener.onClick(IMG,id,mData.get(i).getImage(),flag,-1);
                    }else if(flag == 1){
                        onItemClickListener.onClick(IMG,id,mData.get(i).getImage(),flag,-1);
                    }

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 :mData.size() ;
    }


    class ItemHolder extends RecyclerView.ViewHolder{
        ImageView iv_recommend,iv_type;
        TextView tv_py,tv_title,tv_subContent;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_recommend = itemView.findViewById(R.id.iv_recommend);
            iv_type = itemView.findViewById(R.id.iv_type);
            tv_py = itemView.findViewById(R.id.tv_py);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
        }
    }

    public interface OnItemClickListener{
        void onClick(int type,String id,String imgUrl,int flag,int py);
    }
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
