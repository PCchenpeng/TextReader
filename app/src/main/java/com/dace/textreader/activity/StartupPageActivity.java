package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.ReaderTabBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 启动页
 */
public class StartupPageActivity extends AppCompatActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/ad";

    private ImageView iv_start;
    private RelativeLayout rl_skip;
    private TextView tv_skip;

    private StartupPageActivity mContext;

    private boolean isFirstStart = true;

    private boolean isClick = false;

    private boolean isThreadReady = false;
    private boolean isAdsReady = false;
    private int type = -1;
    private String id = "";
    private int essayType = -1;
    private int status = -1;
    private String image = "";
    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        SharedPreferences sharedPreferences = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        isFirstStart = sharedPreferences.getBoolean("main", true);

        iv_start = findViewById(R.id.iv_guide_start_up);
        rl_skip = findViewById(R.id.rl_skip_start_up);
        tv_skip = findViewById(R.id.tv_skip_start_up);
        tv_skip.setText("跳过 3");

        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_startup, iv_start);
        setImmerseLayout();

        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdsReady) {
                    isClick = true;
                    if (type == 0) {
                        turnToComposition();
                    } else if (type == 1) {
                        turnToArticle();
                    } else if (type == 2) {
                        turnToMicro();
                    } else if (type == 3) {
                        turnToEvents();
                    } else if (type == 4) {
                        turnToSentence();
                    } else if (type == 5) {
                        turnToVoiceEvaluation();
                    } else if (type == 6) {
                        turnToEvents();
                    } else {
                        isClick = false;
                    }
                }
            }
        });
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toWhere();
            }
        });

        if (!isFirstStart) {
            new GetData(mContext).execute(url);
        }

        mThread.start();


    }



    // view为标题栏
    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_skip.setPadding(0, statusBarHeight, 0, 0);
    }

    /**
     * 前往语音测评
     */
    private void turnToVoiceEvaluation() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
        intents[1] = new Intent(mContext, VoiceEvaluationEntranceActivity.class);
        startActivities(intents);
        finish();
    }

    /**
     * 前往每日一句
     */
    private void turnToSentence() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
        intents[1] = new Intent(mContext, NewDailySentenceActivity.class);
        intents[1].putExtra("sentenceId", Long.valueOf(id));
        startActivities(intents);
        finish();
    }

    /**
     * 前往活动
     */
    private void turnToEvents() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
        intents[1] = new Intent(mContext, EventsActivity.class);
        intents[1].putExtra("pageName", id);
        startActivities(intents);
        finish();
    }

    /**
     * 前往微课
     */
    private void turnToMicro() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
        intents[1] = new Intent(mContext, MicroLessonActivity.class);
        intents[1].putExtra("id", Long.valueOf(id));
        startActivities(intents);
        finish();
    }

    /**
     * 前往文章
     */
    private void turnToArticle() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
//        intents[1] = new Intent(mContext, NewArticleDetailActivity.class);
//        intents[1].putExtra("id", Long.valueOf(id));
//        intents[1].putExtra("area", essayType);
//        startActivities(intents);
//        finish();
    }

    /**
     * 前往作文
     */
    private void turnToComposition() {
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(mContext, NewMainActivity.class);
        intents[1] = new Intent(mContext, CompositionDetailActivity.class);
        intents[1].putExtra("writingId", id);
        intents[1].putExtra("area", 0);
        startActivities(intents);
        finish();
    }

    private Thread mThread = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                sleep(1500);
                handler.sendEmptyMessage(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isThreadReady = true;
            if (isAdsReady) {
                iv_start.setImageBitmap(bitmap);
                tv_skip.setVisibility(View.VISIBLE);
                if (timer != null) {
                    timer.start();
                }
            } else if (!isClick) {
                toWhere();
            }
        }
    };

    /**
     * 前往哪个界面（第一次去往引导页）
     */
    private void toWhere() {
        if (isFirstStart) {
            startActivity(new Intent(mContext, GuideActivity.class));
        } else {
            startActivity(new Intent(mContext, NewMainActivity.class));
        }
        finish();
    }

    private int seconds = 4;

    /**
     * 计时器
     */
    private CountDownTimer timer = new CountDownTimer(4000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            seconds = seconds - 1;
            String skip = "跳过 " + seconds;
            tv_skip.setText(skip);
        }

        @Override
        public void onFinish() {
            if (!isClick) {
                toWhere();
            }
        }
    };

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, StartupPageActivity> {

        protected GetData(StartupPageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(StartupPageActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(StartupPageActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
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
                id = object.getString("productId");
                type = object.optInt("sourceType", -1);
                status = object.optInt("status", -1);
                image = object.getString("url");
                essayType = object.optInt("productType", -1);
                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    private void updateUi() {
        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .centerCrop();
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            isAdsReady = false;
                            tv_skip.setVisibility(View.GONE);
                            if (isThreadReady) {
                                toWhere();
                            }
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
                            if (isThreadReady) {
                                iv_start.setImageBitmap(resource);
                                tv_skip.setVisibility(View.VISIBLE);
                                if (timer != null) {
                                    timer.start();
                                }
                            } else {
                                bitmap = resource;
                                isAdsReady = true;
                            }
                        }
                    });
        } else {
            isAdsReady = false;
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        isAdsReady = false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {

        if (mThread != null && !mThread.isInterrupted()) {
            mThread.interrupt();
            mThread = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        super.onDestroy();
    }
}
