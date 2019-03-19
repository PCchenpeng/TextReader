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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.fragment.VoiceEvaluationSelfFragment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 语音评测--自我介绍结果
 */
public class VoiceEvaluationSelfActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/speech/evaluate/result/select";

    private RelativeLayout rl_root;
    private RelativeLayout rl_back;
    private FrameLayout frameLayout;
    private TextView tv_title;
    private ImageView iv_restart;

    private VoiceEvaluationSelfActivity mContext;

    private FragmentManager fm;  //Fragment管理对象
    private VoiceEvaluationSelfFragment fragment;

    private String audioUrl = "";
    private String score = "";
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_evaluation_self);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        fm = getSupportFragmentManager();

        initView();
        initData();
        initEvents();
        setImmerseLayout();
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
        iv_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToVoiceEvaluation();
            }
        });
    }

    /**
     * 前往自我介绍语音测评
     */
    private void turnToVoiceEvaluation() {
        Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
        intent.putExtra("materialId", "2");
        intent.putExtra("restart", true);
        startActivity(intent);
        finish();
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID), "self");
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

    private void initView() {
        rl_root = findViewById(R.id.rl_root_voice_evaluation_self);
        rl_back = findViewById(R.id.rl_back_voice_evaluation_self);
        frameLayout = findViewById(R.id.frame_voice_evaluation_self);
        tv_title = findViewById(R.id.tv_title_voice_evaluation_self);
        iv_restart = findViewById(R.id.iv_restart_voice_evaluation_self);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_title.setTypeface(mTypeface);

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
                    .load(R.drawable.image_voice_evaluation_restart)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            iv_restart.setImageBitmap(resource);
                        }
                    });
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
                audioUrl = object.getString("audio");
                double totalScore = object.optDouble("totalScore", 0);
                score = DataUtil.double2IntString(totalScore);
                content = object.getString("comment");
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
     * 更新Ui
     */
    private void updateUi() {
        if (mContext == null) {
            return;
        }
        fragment = new VoiceEvaluationSelfFragment();
        fragment.setAudioUrl(audioUrl);
        fragment.setScore(score);
        fragment.setContent(content);
        fm.beginTransaction()
                .add(R.id.frame_fragment_voice_evaluation_self, fragment, "self")
                .commit();
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
            extends WeakAsyncTask<String, Void, String, VoiceEvaluationSelfActivity> {

        protected GetData(VoiceEvaluationSelfActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationSelfActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("type", strings[2]);
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
        protected void onPostExecute(VoiceEvaluationSelfActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

}
