package com.dace.textreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 语音评测入口
 */
public class VoiceEvaluationEntranceActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/is/not/activity/signup";

    private RelativeLayout rl_root;
    private RelativeLayout rl_back;
    private ImageView imageView;
    private TextView tv_region;
    private ImageView iv_logo;
    private TextView tv_introduction;
    private ImageView iv_cover;

    private VoiceEvaluationEntranceActivity mContext;

    private int status = -1;  //0为报名，1已报名，2已完成测试
    private boolean isClick = false;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_evaluation_entrance);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initView();
        initEvents();
        setImmerseLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isClick = false;
        status = -1;
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                } else {
                    if (status == 0) {
                        showPerfectInformation();
                    } else if (status == 1) {
                        turnToVoiceEvaluation();
                    } else if (status == 2) {
                        turnToResult();
                    } else {
                        isClick = true;
                        initData();
                    }
                }
            }
        });
        tv_introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToEvents();
            }
        });
    }

    /**
     * 前往查看结果
     */
    private void turnToResult() {
        Intent intent = new Intent(mContext, VoiceEvaluationResultActivity.class);
        startActivity(intent);
    }

    /**
     * 前往语音评测
     */
    private void turnToVoiceEvaluation() {
        Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
        intent.putExtra("materialId", "2");
        startActivity(intent);
    }

    /**
     * 显示完善信息
     */
    private void showPerfectInformation() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_voice_evaluation_perfect_info)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView textView = holder.getView(R.id.tv_voice_evaluation_perfect_info_dialog);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, EventsActivity.class);
                                intent.putExtra("pageName", "young_speak_activity");
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setMargin(30)
                .setHeight(300)
                .show(getSupportFragmentManager());
    }

    /**
     * 前往活动页
     */
    private void turnToEvents() {
        Intent intent = new Intent(mContext, EventsActivity.class);
        intent.putExtra("pageName", "545454");
        startActivity(intent);
    }

    private void initData() {
        if (!loading && NewMainActivity.STUDENT_ID != -1) {
            loading = true;
            new GetData(mContext).execute(url, String.valueOf(NewMainActivity.PHONENUMBER),
                    String.valueOf(NewMainActivity.STUDENT_ID));
        }
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_voice_evaluation_entrance);
        rl_back = findViewById(R.id.rl_back_voice_evaluation_entrance);
        imageView = findViewById(R.id.iv_voice_evaluation_entrance);
        tv_region = findViewById(R.id.tv_region_voice_evaluation_entrance);
        iv_logo = findViewById(R.id.iv_logo_voice_evaluation_entrance);
        tv_introduction = findViewById(R.id.tv_introduction_voice_evaluation_entrance);
        iv_cover = findViewById(R.id.iv_cover_voice_evaluation_entrance);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_region.setTypeface(mTypeface);
        tv_introduction.setTypeface(mTypeface);

        int w = 0;
        int width = DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 26);
        if (width < DensityUtil.dip2px(mContext, 350)) {
            w = width;
        }
        if (w != 0) {
            int h = w * DensityUtil.dip2px(mContext, 88) / DensityUtil.dip2px(mContext, 350);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = w;
            layoutParams.height = h;
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
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_evaluation_start, imageView);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_evaluation_logo, iv_logo);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_evaluation_cover, iv_cover);
        }

    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, VoiceEvaluationEntranceActivity> {

        protected GetData(VoiceEvaluationEntranceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationEntranceActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("phoneNum", strings[1]);
                object.put("studentId", strings[2]);
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
        protected void onPostExecute(VoiceEvaluationEntranceActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
            activity.loading = false;
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
                status = 0;
                if (isClick) {
                    showPerfectInformation();
                }
            } else if (300 == jsonObject.optInt("status", -1)) {
                status = 1;
                if (isClick) {
                    turnToVoiceEvaluation();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                status = 2;
                if (isClick) {
                    turnToResult();
                }
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (isClick) {
            MyToastUtil.showToast(mContext, "查询信息失败，请稍后再试~");
        }
    }

}
