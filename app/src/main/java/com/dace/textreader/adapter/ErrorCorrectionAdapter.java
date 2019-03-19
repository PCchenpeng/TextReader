package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.bean.ErrorClickBean;
import com.dace.textreader.util.GlideRoundImage;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 3:17.
 * Version   1.0;
 * Describe :  纠错内容列表
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ErrorClickBean> mList;

    public ErrorCorrectionAdapter(Context context, List<ErrorClickBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        if (i == 1) {
            view = inflater.inflate(R.layout.item_text_layout, viewGroup, false);
            TextViewHolder textViewHolder = new TextViewHolder(view);
            return textViewHolder;
        } else if (i == 0) {
            view = inflater.inflate(R.layout.item_image_layout, viewGroup, false);
            ImageViewHolder imageViewHolder = new ImageViewHolder(view);
            return imageViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        ErrorClickBean bean = mList.get(i);
        if (viewHolder instanceof TextViewHolder) {
            ((TextViewHolder) viewHolder).textView.setMovementMethod(LinkMovementMethod.getInstance());
            ((TextViewHolder) viewHolder).textView.setText(bean.getContent());
        } else if (viewHolder instanceof ImageViewHolder) {
            if (mContext != null) {
                RequestOptions options = new RequestOptions()
                        .transform(new GlideRoundImage(mContext, 8));
                Glide.with(mContext)
                        .asBitmap()
                        .load(bean.getImagePath())
                        .apply(options)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                ((ImageViewHolder) viewHolder).imageView.setImageResource(R.drawable.image_write_cover_bg);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                ((ImageViewHolder) viewHolder).imageView.setImageBitmap(resource);
                            }
                        });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    private class TextViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_text_item);
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image_item);
        }
    }
}
