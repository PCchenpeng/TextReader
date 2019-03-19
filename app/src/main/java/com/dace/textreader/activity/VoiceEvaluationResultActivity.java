package com.dace.textreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
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
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 语音评测结果
 */
public class VoiceEvaluationResultActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/speak/result/election/general/select";

    private RelativeLayout rl_root;
    private RelativeLayout rl_back;
    private FrameLayout frameLayout;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_score;
    private RelativeLayout rl_self;
    private ImageView iv_self_status;
    private TextView tv_self_text;
    private TextView tv_self_score;
    private RelativeLayout rl_speech;
    private ImageView iv_speech_status;
    private TextView tv_speech_text;
    private TextView tv_speech_score;
    private ImageView iv_eligibility;

    private VoiceEvaluationResultActivity mContext;

    private String code = "";
    private String content = "";
    private double score;
    private double score_self;
    private boolean pass_self;
    private double score_speech;
    private boolean pass_speech;
    private boolean pass;

    private Bitmap bitmap;
    private String imageUrl = "";
    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_evaluation_result);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initView();
        initEvents();
        setImmerseLayout();

        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_root.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToVoiceEvaluationSelf();
            }
        });
        rl_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToVoiceEvaluationSpeech();
            }
        });
        iv_eligibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass) {
                    showGloryCertificate();
                } else {
                    MyToastUtil.showToast(mContext, "暂无参赛资格");
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 显示荣耀证书
     */
    private void showGloryCertificate() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_voice_evaluation_glory_certificate)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {

                        float scale = 1.0f;
                        float width = DensityUtil.getScreenWidth(mContext);
                        if ((width / DensityUtil.dip2px(mContext, 288)) < 1.1f) {
                            scale = 0.8f;
                        } else if ((width / DensityUtil.dip2px(mContext, 288)) < 1.3f) {
                            scale = 0.9f;
                        }

                        final RelativeLayout rl_content = holder.getView(R.id.rl_content_glory_certificate_dialog);
                        final RelativeLayout rl_bottom = holder.getView(R.id.rl_bottom_glory_certificate_dialog);
                        final RelativeLayout rl_image = holder.getView(R.id.rl_image_glory_certificate_dialog);
                        ImageView iv_code = holder.getView(R.id.iv_code_glory_certificate_dialog);
                        TextView tv_code = holder.getView(R.id.tv_code_glory_certificate_dialog);
                        ImageView iv_top = holder.getView(R.id.iv_top_glory_certificate_dialog);
                        ImageView iv_bottom = holder.getView(R.id.iv_bottom_glory_certificate_dialog);
                        ImageView imageView = holder.getView(R.id.iv_glory_certificate_dialog);
                        TextView textView = holder.getView(R.id.tv_glory_certificate_dialog);
                        ImageView iv_logo = holder.getView(R.id.iv_logo_glory_certificate_dialog);
                        RelativeLayout rl_user = holder.getView(R.id.rl_head_glory_certificate_dialog);
                        ImageView iv_user = holder.getView(R.id.iv_head_glory_certificate_dialog);
                        TextView tv_user = holder.getView(R.id.tv_user_glory_certificate_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_glory_certificate_dialog);
                        View view_one = holder.getView(R.id.view_one_glory_certificate_dialog);
                        View view_two = holder.getView(R.id.view_two_glory_certificate_dialog);
                        View view_three = holder.getView(R.id.view_three_glory_certificate_dialog);
                        View view_four = holder.getView(R.id.view_four_glory_certificate_dialog);

                        LinearLayout ll_wx = holder.getView(R.id.ll_wx_glory_certificate_dialog);
                        LinearLayout ll_wxs = holder.getView(R.id.ll_wxs_glory_certificate_dialog);
                        LinearLayout ll_pic = holder.getView(R.id.ll_download_glory_certificate_dialog);
                        LinearLayout ll_weibo = holder.getView(R.id.ll_weibo_glory_certificate_dialog);
                        LinearLayout ll_qq = holder.getView(R.id.ll_qq_glory_certificate_dialog);
                        LinearLayout ll_qzone = holder.getView(R.id.ll_qzone_glory_certificate_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_glory_certificate_dialog);

                        if (scale != 1.0f) {
                            ViewGroup.LayoutParams layoutParams_one = view_one.getLayoutParams();
                            int oneWidth = (int) (layoutParams_one.width * scale);
                            layoutParams_one.width = oneWidth;
                            int oneHeight = (int) (layoutParams_one.height * scale);
                            layoutParams_one.height = oneHeight;

                            ViewGroup.LayoutParams layoutParams_imageView = imageView.getLayoutParams();
                            int imageViewWidth = (int) (layoutParams_imageView.width * scale);
                            layoutParams_imageView.width = imageViewWidth;
                            int imageViewHeight = (int) (layoutParams_imageView.height * scale);
                            layoutParams_imageView.height = imageViewHeight;

                            ViewGroup.LayoutParams layoutParams_two = view_two.getLayoutParams();
                            int twoWidth = (int) (layoutParams_two.width * scale);
                            layoutParams_two.width = twoWidth;
                            int twoHeight = (int) (layoutParams_two.height * scale);
                            layoutParams_two.height = twoHeight;

                            ViewGroup.LayoutParams layoutParams_three = view_three.getLayoutParams();
                            int threeWidth = (int) (layoutParams_three.width * scale);
                            layoutParams_three.width = threeWidth;
                            int threeHeight = (int) (layoutParams_three.height * scale);
                            layoutParams_three.height = threeHeight;

                            ViewGroup.LayoutParams layoutParams_logo = iv_logo.getLayoutParams();
                            int logoWidth = (int) (layoutParams_logo.width * scale);
                            layoutParams_logo.width = logoWidth;
                            int logoHeight = (int) (layoutParams_logo.height * scale);
                            layoutParams_logo.height = logoHeight;

                            ViewGroup.LayoutParams layoutParams_four = view_four.getLayoutParams();
                            int fourWidth = (int) (layoutParams_four.width * scale);
                            layoutParams_four.width = fourWidth;
                            int fourHeight = (int) (layoutParams_four.height * scale);
                            layoutParams_four.height = fourHeight;

                            ViewGroup.LayoutParams layoutParams_user_rl = rl_user.getLayoutParams();
                            int userRlWidth = (int) (layoutParams_user_rl.width * scale);
                            layoutParams_user_rl.width = userRlWidth;
                            int userRlHeight = (int) (layoutParams_user_rl.height * scale);
                            layoutParams_user_rl.height = userRlHeight;

                            ViewGroup.LayoutParams layoutParams_user = iv_user.getLayoutParams();
                            int userWidth = (int) (layoutParams_user.width * scale);
                            layoutParams_user.width = userWidth;
                            int userHeight = (int) (layoutParams_user.height * scale);
                            layoutParams_user.height = userHeight;
                        }

                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_glory_certificate_top, iv_top);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_glory_certificate_bottom, iv_bottom);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_glory_certificate, imageView);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_code, iv_code);
                        String tips = "长按识别二维码\n" + "了解赛事详情";
                        tv_code.setText(tips);
                        String certificate = "证书编码：" + code;
                        textView.setText(certificate);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_glory_certificate_logo, iv_logo);
                        GlideUtils.loadUserImage(mContext,
                                HttpUrlPre.FILE_URL + NewMainActivity.USERIMG, iv_user);
                        tv_user.setText(NewMainActivity.USERNAME);
                        String content = "在第三届“少年中国说”广东赛区海选选拔赛中勇创佳绩，通过选拔！";
                        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
                        ForegroundColorSpan foregroundColorSpan =
                                new ForegroundColorSpan(Color.parseColor("#8F221B"));
                        ssb.setSpan(foregroundColorSpan, 4, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_content.setText(ssb);

                        if (scale != 1.0f) {
                            //设置缩放动画
                            // 从相对于自身0.5倍的位置开始缩放，也就是从控件的位置缩放
                            final ScaleAnimation animation = new ScaleAnimation(1f, scale,
                                    1f, scale,
                                    Animation.RELATIVE_TO_SELF, 0.5f,
                                    Animation.RELATIVE_TO_SELF, 1f);
                            animation.setDuration(100);//设置动画持续时间
                            animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                            rl_content.setAnimation(animation);
                            animation.startNow();
                        }

                        ll_wx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                ShareUtil.shareImageToWX(mContext, imageUrl, true);
                            }
                        });
                        ll_wxs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                ShareUtil.shareImageToWX(mContext, imageUrl, false);
                            }
                        });
                        ll_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                if (imageUrl.equals("")) {
                                    MyToastUtil.showToast(mContext, "保存图片失败");
                                } else {
                                    MyToastUtil.showToast(mContext, "图片保存在" + imageUrl);
                                }
                            }
                        });
                        ll_weibo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bitmap == null) {
                                    bitmap = ImageUtils.createWaterMaskImage(mContext,
                                            ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                            ImageUtils.getViewGroupBitmap(rl_bottom));
                                }
                                if (WbSdk.isWbInstall(mContext)) {
                                    ShareUtil.shareImageToWeibo(shareHandler, bitmap);
                                } else {
                                    MyToastUtil.showToast(mContext, "请先安装微博");
                                }
                            }
                        });
                        ll_qq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                ShareUtil.shareImageToQQ(mContext, imageUrl, true);
                            }
                        });
                        ll_qzone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getViewGroupBitmap(rl_image, rl_bottom),
                                                ImageUtils.getViewGroupBitmap(rl_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                ShareUtil.shareImageToQQ(mContext, imageUrl, false);
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
     * 前往自我介绍结果页
     */
    private void turnToVoiceEvaluationSelf() {
        if (score_self == 0) {
            Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
            intent.putExtra("materialId", "2");
            intent.putExtra("restart", true);
            startActivity(intent);
        } else {
            startActivity(new Intent(mContext, VoiceEvaluationSelfActivity.class));
        }
    }

    /**
     * 前往语音测评结果页
     */
    private void turnToVoiceEvaluationSpeech() {
        if (score_speech == 0) {
            Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
            intent.putExtra("materialId", "1");
            intent.putExtra("restart", true);
            startActivity(intent);
        } else {
            startActivity(new Intent(mContext, VoiceEvaluationSpeechActivity.class));
        }
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID));
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_voice_evaluation_result);
        rl_back = findViewById(R.id.rl_back_voice_evaluation_result);
        frameLayout = findViewById(R.id.frame_voice_evaluation_result);
        tv_title = findViewById(R.id.tv_title_voice_evaluation_result);
        tv_content = findViewById(R.id.tv_content_voice_evaluation_result);
        tv_score = findViewById(R.id.tv_score_voice_evaluation_result);
        rl_self = findViewById(R.id.rl_self_voice_evaluation_result);
        iv_self_status = findViewById(R.id.iv_self_status_voice_evaluation_result);
        tv_self_text = findViewById(R.id.tv_self_text_voice_evaluation_result);
        tv_self_score = findViewById(R.id.tv_self_score_voice_evaluation_result);
        rl_speech = findViewById(R.id.rl_speech_voice_evaluation_result);
        iv_speech_status = findViewById(R.id.iv_speech_status_voice_evaluation_result);
        tv_speech_text = findViewById(R.id.tv_speech_text_voice_evaluation_result);
        tv_speech_score = findViewById(R.id.tv_speech_score_voice_evaluation_result);
        iv_eligibility = findViewById(R.id.iv_eligibility_voice_evaluation_result);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_title.setTypeface(mTypeface);
        tv_content.setTypeface(mTypeface);
        tv_self_text.setTypeface(mTypeface);
        tv_self_score.setTypeface(mTypeface);
        tv_speech_score.setTypeface(mTypeface);
        tv_speech_text.setTypeface(mTypeface);

        int screenWidth = DensityUtil.getScreenWidth(mContext);
        //如果屏幕宽度小于350dp，则宽高乘以0.8
        if (screenWidth < DensityUtil.dip2px(mContext, 350)) {
            ViewGroup.LayoutParams layoutParams_self = rl_self.getLayoutParams();
            layoutParams_self.width = DensityUtil.dip2px(mContext, 280);
            layoutParams_self.height = DensityUtil.dip2px(mContext, 104);

            ViewGroup.LayoutParams layoutParams_speech = rl_speech.getLayoutParams();
            layoutParams_speech.width = DensityUtil.dip2px(mContext, 280);
            layoutParams_speech.height = DensityUtil.dip2px(mContext, 104);

            ViewGroup.LayoutParams layoutParams_self_status = iv_self_status.getLayoutParams();
            layoutParams_self_status.width = DensityUtil.dip2px(mContext, 72);
            layoutParams_self_status.height = DensityUtil.dip2px(mContext, 72);

            ViewGroup.LayoutParams layoutParams_speech_status = iv_speech_status.getLayoutParams();
            layoutParams_speech_status.width = DensityUtil.dip2px(mContext, 72);
            layoutParams_speech_status.height = DensityUtil.dip2px(mContext, 72);

        }

        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .centerCrop();
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.image_voice_evaluation_bg)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            rl_root.setBackground(drawable);
                        }
                    });
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.image_voice_evaluation_self_bg)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            rl_self.setBackground(drawable);
                        }
                    });
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.image_voice_evaluation_speech_bg)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            rl_speech.setBackground(drawable);
                        }
                    });
        }
    }

    private void showLoadingView(boolean show) {
        if (mContext == null) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                code = object.getString("code");
                content = object.getString("comment");
                pass = object.optBoolean("pass", false);
                score = object.optDouble("total", 0);
                JSONObject selfObj = object.getJSONObject("self");
                score_self = selfObj.optDouble("score", 0);
                pass_self = selfObj.optBoolean("pass", false);
                JSONObject speechObj = object.getJSONObject("speech");
                score_speech = speechObj.optDouble("score", 0);
                pass_speech = speechObj.optBoolean("pass", false);
                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新UI
     */
    private void updateUi() {
        tv_content.setText(content);
        tv_score.setText(DataUtil.double2IntString(score));
        String self = "评测结果：" + DataUtil.double2IntString(score_self) + "分";
        tv_self_score.setText(self);
        if (pass_self) {
            GlideUtils.loadImageWithNoOptions(mContext,
                    R.drawable.image_voice_evaluation_self_correct, iv_self_status);
        } else {
            GlideUtils.loadImageWithNoOptions(mContext,
                    R.drawable.image_voice_evaluation_self_error, iv_self_status);
        }
        String speech = "评测结果：" + DataUtil.double2IntString(score_speech) + "分";
        tv_speech_score.setText(speech);
        if (pass_speech) {
            GlideUtils.loadImageWithNoOptions(mContext,
                    R.drawable.image_voice_evaluation_speech_correct, iv_speech_status);
        } else {
            GlideUtils.loadImageWithNoOptions(mContext,
                    R.drawable.image_voice_evaluation_speech_error, iv_speech_status);
        }
        if (pass) {
            GlideUtils.loadImageWithNoOptions(mContext,
                    R.drawable.image_voice_evaluation_eligibility, iv_eligibility);
            iv_eligibility.setVisibility(View.VISIBLE);
        } else {
            iv_eligibility.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (mContext == null) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                initData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, VoiceEvaluationResultActivity> {

        protected GetData(VoiceEvaluationResultActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationResultActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
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
        protected void onPostExecute(VoiceEvaluationResultActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

}
