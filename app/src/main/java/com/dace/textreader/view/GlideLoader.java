package com.dace.textreader.view;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import cn.lightsky.infiniteindicator.ImageLoader;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view
 * Created by Administrator.
 * Created time 2018/4/4 0004 下午 2:48.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class GlideLoader implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {
        RequestOptions options = new RequestOptions()
                .centerCrop();
        if (res instanceof String) {
            Glide.with(context)
                    .load((String) res)
                    .apply(options)
                    .into(targetView);
        } else if (res instanceof Integer) {
            Glide.with(context)
                    .load((Integer) res)
                    .apply(options)
                    .into(targetView);
        }
    }
}
