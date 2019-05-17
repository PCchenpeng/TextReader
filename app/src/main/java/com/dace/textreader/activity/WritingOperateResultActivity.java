package com.dace.textreader.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.dace.textreader.bean.AutoSaveWritingBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.editor.RichEditor;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作文操作结果
 */
public class WritingOperateResultActivity extends BaseActivity implements View.OnClickListener {

    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/writing/";

    private TextView tv_sure;
    private ImageView iv_tips;
    private TextView tv_tips;
    private TextView tv_check;

    private LinearLayout ll_wx;
    private LinearLayout ll_wxs;
    private LinearLayout ll_pic;
    private LinearLayout ll_weibo;
    private LinearLayout ll_qq;
    private LinearLayout ll_qz;
    private LinearLayout ll_copy;

    private WritingOperateResultActivity mContext;

    private String writingId;
    private int writingFormat;
    private String writingTitle;
    private String writingContent;
    private String writingCover;
    private int writingNumber;
    private int writingArea;
    private int writingIndex;
    private String url = "";
    private String shareContent;

    private Bitmap bitmap;
    private String imageUrl = "";

    private int type_share = -1;  //分享类型
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_Copy = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;  //微博

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_operate_result);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initView();
        initData();
        initEvents();

        LitePal.deleteAll(AutoSaveWritingBean.class);

        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shareHandler.doResultIntent(data, null);
    }

    private void initEvents() {
        tv_sure.setOnClickListener(this);
        tv_check.setOnClickListener(this);
        ll_wx.setOnClickListener(this);
        ll_wxs.setOnClickListener(this);
        ll_pic.setOnClickListener(this);
        ll_weibo.setOnClickListener(this);
        ll_qq.setOnClickListener(this);
        ll_qz.setOnClickListener(this);
        ll_copy.setOnClickListener(this);
    }

    private void initData() {
        writingId = getIntent().getStringExtra("id");
        writingTitle = getIntent().getStringExtra("title");
        writingContent = getIntent().getStringExtra("content");
        writingCover = getIntent().getStringExtra("cover");
        writingNumber = getIntent().getIntExtra("count", 0);
        writingFormat = getIntent().getIntExtra("format", 1);
        writingArea = getIntent().getIntExtra("area", 5);
        writingIndex = getIntent().getIntExtra("index", 0);

        try {
            JSONArray array = new JSONArray(writingContent);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String type = object.getString("type");
                String s = object.getString("content");
                if (type.equals("text")) {
                    shareContent = shareContent + s;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            shareContent = writingContent;
        }
    }

    private void initView() {
        tv_sure = findViewById(R.id.tv_sure_writing_operate_result);
        iv_tips = findViewById(R.id.iv_tips_writing_operate_result);
        tv_tips = findViewById(R.id.tv_tips_writing_operate_result);
        tv_check = findViewById(R.id.tv_check_writing_operate_result);

        RequestOptions options = new RequestOptions()
                .override(DensityUtil.dip2px(mContext, 176),
                        DensityUtil.dip2px(mContext, 86));
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .load(R.drawable.image_write_complete)
                    .apply(options)
                    .into(iv_tips);
        }
        if (writingIndex == 4) {
            tv_tips.setText("棒棒哒～提交成功！\n注：活动作文可在截稿日期前再次编辑提交～");
        }

        ll_wx = findViewById(R.id.ll_wx_writing_operate_result);
        ll_wxs = findViewById(R.id.ll_wxs_writing_operate_result);
        ll_pic = findViewById(R.id.ll_picture_writing_operate_result);
        ll_weibo = findViewById(R.id.ll_weibo_writing_operate_result);
        ll_qq = findViewById(R.id.ll_qq_writing_operate_result);
        ll_qz = findViewById(R.id.ll_qzone_writing_operate_result);
        ll_copy = findViewById(R.id.ll_copy_writing_operate_result);
    }

    private void backActivity() {
        Intent intent = new Intent();
        intent.putExtra("submit", true);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure_writing_operate_result:
                backActivity();
                break;
            case R.id.tv_check_writing_operate_result:
                checkResult();
                break;
            case R.id.ll_wx_writing_operate_result:
                getShareHtml(TYPE_SHARE_WX_FRIEND);
                break;
            case R.id.ll_wxs_writing_operate_result:
                getShareHtml(TYPE_SHARE_WX_FRIENDS);
                break;
            case R.id.ll_weibo_writing_operate_result:
                if (WbSdk.isWbInstall(mContext)) {
                    getShareHtml(TYPE_SHARE_Weibo);
                } else {
                    showTips("请先安装微博");
                }
                break;
            case R.id.ll_qq_writing_operate_result:
                getShareHtml(TYPE_SHARE_QQ);
                break;
            case R.id.ll_qzone_writing_operate_result:
                getShareHtml(TYPE_SHARE_QZone);
                break;
            case R.id.ll_copy_writing_operate_result:
                getShareHtml(TYPE_SHARE_Copy);
                break;
            case R.id.ll_picture_writing_operate_result:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        showPictureDialog();
                    }
                } else {
                    showPictureDialog();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (verifyPermissions(grantResults)) {
                showPictureDialog();
            } else {
                showTips("没有存储权限，无法生成图片");
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示生成图片对话框
     */
    private void showPictureDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_writing_share)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final RelativeLayout rl_bottom = holder.getView(R.id.rl_bottom_writing_share_dialog);
                        final ScrollView scrollView = holder.getView(R.id.scroll_view_writing_share_dialog);
                        ImageView iv_user = holder.getView(R.id.iv_user_writing_share_dialog);
                        TextView tv_user = holder.getView(R.id.tv_user_writing_share_dialog);
                        TextView tv_grade = holder.getView(R.id.tv_grade_writing_share_dialog);
                        TextView tv_title = holder.getView(R.id.tv_title_writing_share_dialog);
                        TextView tv_date = holder.getView(R.id.tv_date_writing_share_dialog);
                        TextView tv_number = holder.getView(R.id.tv_number_writing_share_dialog);
                        final ImageView iv_cover = holder.getView(R.id.iv_cover_writing_share_dialog);
                        RichEditor editor = holder.getView(R.id.tv_content_writing_share_dialog);
                        LinearLayout ll_wx_dialog = holder.getView(R.id.ll_wx_writing_share_dialog);
                        LinearLayout ll_wxs_dialog = holder.getView(R.id.ll_wxs_writing_share_dialog);
                        LinearLayout ll_download_dialog = holder.getView(R.id.ll_download_writing_share_dialog);
                        LinearLayout ll_weibo_dialog = holder.getView(R.id.ll_weibo_writing_share_dialog);
                        LinearLayout ll_qq_dialog = holder.getView(R.id.ll_qq_writing_share_dialog);
                        LinearLayout ll_qz_dialog = holder.getView(R.id.ll_qzone_writing_share_dialog);
                        LinearLayout ll_copy_dialog = holder.getView(R.id.ll_copy_writing_share_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_writing_share_dialog);

                        GlideUtils.loadUserImage(mContext,
//                                HttpUrlPre.FILE_URL +
                                        NewMainActivity.USERIMG, iv_user);
                        tv_user.setText(NewMainActivity.USERNAME);
                        tv_grade.setText(DataUtil.gradeCode2Chinese(NewMainActivity.GRADE_ID));
                        tv_title.setText(writingTitle);
                        tv_date.setText(DateUtil.getTodayDate());
                        tv_number.setText(String.valueOf(writingNumber));
                        if (!isDestroyed()) {
                            RequestOptions options_cover = new RequestOptions()
                                    .transform(new GlideRoundImage(mContext, 4));
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(writingCover)
                                    .apply(options_cover)
                                    .listener(new RequestListener<Bitmap>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
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
                                            iv_cover.setImageBitmap(resource);
                                        }
                                    });
                        }

                        editor.setNoImageOperate();
                        editor.setContent(writingContent);
                        editor.setNoEditor();

                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_UP);
                                scrollView.fullScroll(View.FOCUS_UP);
                            }
                        });

                        ll_wx_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToWX(true, imageUrl);
                            }
                        });
                        ll_wxs_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bitmap == null) {
                                    bitmap = ImageUtils.createWaterMaskImage(mContext,
                                            ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                            ImageUtils.getViewGroupBitmap(rl_bottom));
                                }
                                shareImageToWX(false, imageUrl);
                            }
                        });
                        ll_download_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                if (imageUrl.equals("")) {
                                    showTips("保存图片失败");
                                } else {
                                    showTips("图片保存在" + imageUrl);
                                }
                            }
                        });
                        ll_weibo_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bitmap == null) {
                                    bitmap = ImageUtils.createWaterMaskImage(mContext,
                                            ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                            ImageUtils.getViewGroupBitmap(rl_bottom));
                                }
                                if (WbSdk.isWbInstall(mContext)) {
                                    ShareUtil.shareImageToWeibo(shareHandler, bitmap);
                                } else {
                                    showTips("请先安装微博");
                                }
                            }
                        });
                        ll_qq_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToQQ(imageUrl, false);
                            }
                        });
                        ll_qz_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getScrollViewBitmap(scrollView, mContext),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToQQ(imageUrl, true);
                            }
                        });
                        ll_copy_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_Copy);
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show(getSupportFragmentManager());
    }

    /**
     * @param imageUrl 本地图片路径
     */
    private void shareImageToQQ(String imageUrl, boolean isFriends) {
        ShareUtil.shareImageToQQ(this, imageUrl, !isFriends);
    }

    /**
     * 分享图片到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareImageToWX(boolean friend, String imageUrl) {
        ShareUtil.shareImageToWX(mContext, imageUrl, friend);
    }

    private void checkResult() {
        Intent intent_public = new Intent(mContext, MyCompositionActivity.class);
        intent_public.putExtra("index", writingIndex);
        startActivity(intent_public);
    }

    private void getShareHtml(int type) {
        type_share = type;
        if (url.equals("")) {
            MyToastUtil.showToast(mContext, "正在准备分享内容...");
            new GetShareHtml(this).execute(shareUrl, writingId,
                    String.valueOf(writingArea), String.valueOf(writingFormat));
        } else {
            share(url);
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, WritingOperateResultActivity> {

        protected GetShareHtml(WritingOperateResultActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingOperateResultActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("area", strings[2]);
                object.put("format", strings[3]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingOperateResultActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }

    /**
     * 分析分享链接数据
     *
     * @param s
     */
    private void analyzeShareData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                String url = jsonObject.getString("data");
                share(url);
            } else {
                errorShare();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorShare();
        }
    }

    /**
     * 分享
     *
     * @param url
     */
    private void share(String url) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url);
                break;
            case TYPE_SHARE_Weibo:
                shareToWeiBo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_Copy:
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeiBo(String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        ShareUtil.shareToWeibo(shareHandler, url, writingTitle, writingContent, thumb);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, writingTitle, writingContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, writingTitle, writingContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, writingTitle, writingContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(mContext, "分享失败，请稍后重试");
    }



    @Override
    protected void onDestroy() {

        if (bitmap != null) {
            bitmap.recycle();
        }

        super.onDestroy();
    }
}
