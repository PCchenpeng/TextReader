package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dace.textreader.R;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

/**
 * 透明界面
 * 注册之后显示新人礼包
 */
public class TransitActivity extends BaseActivity {

    private String imagePath = "";
    private TransitActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        imagePath = getIntent().getStringExtra("imagePath");

        setNeedCheckCode(false);

        showPrizeImage();
    }

    /**
     * 显示奖励大礼包
     */
    private void showPrizeImage() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_prize_image_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        ImageView imageView = holder.getView(R.id.iv_prize_image_dialog);
                        final ImageView iv_close = holder.getView(R.id.iv_close_prize_image_dialog);
                        RequestOptions options = new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE);
                        if (!isDestroyed()) {
                            Glide.with(mContext)
                                    .load(imagePath)
                                    .apply(options)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            iv_close.setVisibility(View.VISIBLE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            iv_close.setVisibility(View.VISIBLE);
                                            return false;
                                        }
                                    })
                                    .into(imageView);
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(mContext, MyselfNewsActivity.class));
                                finish();
                            }
                        });
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(0)
                .show(getSupportFragmentManager());
    }

}
