package com.dace.textreader.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dace.textreader.R;
import com.youth.banner.loader.ImageLoader;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/10/24 0024 上午 10:15.
 * Version   1.0;
 * Describe :  Banner的图片加载器
 * History:
 * ==============================================================================
 */
public class BannerGlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();
        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_placeholder_rectangle)
                .centerCrop();
        if (context != null) {
            Glide.with(context).load(path).apply(options)
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(imageView);
        }
    }
}
