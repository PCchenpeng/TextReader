package com.dace.textreader.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dace.textreader.R;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/12/18 0018 下午 2:58.
 * Version   1.0;
 * Describe :  Glide图片加载工具类
 * History:
 * ==============================================================================
 */
public class GlideUtils {

    /**
     * 加载矩形图片
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(1000)
                        .setCrossFadeEnabled(true).build();
//        DrawableCrossFadeFactory drawableCrossFadeFactory =
//                new DrawableCrossFadeFactory.Builder(1000)
//                        .setCrossFadeEnabled(true).build();

        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.image_placeholder_rectangle)
//                .error(R.drawable.image_placeholder_rectangle)
                .centerCrop()
                .transform(new GlideRoundImage(context, 8));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
//                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(imageView);
    }


//加载指定圆角图片

    public static void loadImage(Context context, String imageUrl, ImageView imageView,int radius) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(1000)
                        .setCrossFadeEnabled(true).build();
//        DrawableCrossFadeFactory drawableCrossFadeFactory =
//                new DrawableCrossFadeFactory.Builder(1000)
//                        .setCrossFadeEnabled(true).build();

        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.image_placeholder_rectangle)
//                .error(R.drawable.image_placeholder_rectangle)
                .centerCrop()
                .transform(new GlideRoundImage(context, radius));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
//                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(imageView);
    }



    /**
     * 加载矩形图片
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadHomeImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(imageView);
    }



    /**
     * 加载用户头像
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadHomeUserImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }


        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_student)
                .centerCrop()
                .transform(new GlideCircleTransform(context));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(imageView);
    }


    /**
     * 加载用户头像
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadUserImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();
        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_student)
                .centerCrop()
                .transform(new GlideCircleTransform(context));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(imageView);
    }

    /**
     * 加载小图
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadSmallImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();

        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_placeholder_rectangle)
                .centerCrop()
                .transform(new GlideRoundImage(context, 4));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(imageView);
    }

    /**
     * 加载方形图片
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadSquareImage(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();
        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_placeholder_square)
                .centerCrop()
                .transform(new GlideRoundImage(context, 4));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(imageView);
    }

    /**
     * 加载矩形图片
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadImageWithNoPlaceholder(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();
        RequestOptions options = new RequestOptions()
                .error(R.drawable.image_placeholder_rectangle)
                .centerCrop()
                .transform(new GlideRoundImage(context, 8));
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(imageView);
    }

    /**
     * 通过context加载drawable中的图片
     *
     * @param context   上下文对象
     * @param resId     图片ID
     * @param imageView 图片容器
     */
    public static void loadImageWithNoOptions(Context context, @DrawableRes int resId,
                                              ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        Glide.with(context).asBitmap().load(resId).into(imageView);
    }

    /**
     * 通过context加载drawable中的GIF图片
     *
     * @param context   上下文对象
     * @param resId     图片ID
     * @param imageView 图片容器
     */
    public static void loadGIFImageWithNoOptions(Context context, @DrawableRes int resId,
                                                 ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        Glide.with(context).asGif().load(resId).into(imageView);
    }

    /**
     * 加载矩形图片
     *
     * @param context   上下文对象
     * @param imageUrl  图片链接
     * @param imageView 图片容器
     */
    public static void loadImageWithNoRadius(Context context, String imageUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)){
            return;
        }

        DrawableCrossFadeFactory drawableCrossFadeFactory =
                new DrawableCrossFadeFactory.Builder(500)
                        .setCrossFadeEnabled(true).build();
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(imageView);
    }

    /**
     * context是否有效
     *
     * @param context
     * @return
     */
    private static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

}
