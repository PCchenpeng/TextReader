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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dace.textreader.bean.VoiceErrorWordBean;
import com.dace.textreader.bean.VoiceEvaluationSpeechBean;
import com.dace.textreader.fragment.VoiceEvaluationSpeechLeftFragment;
import com.dace.textreader.fragment.VoiceEvaluationSpeechRightFragment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 语音评测--朗诵结果
 */
public class VoiceEvaluationSpeechActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/speech/evaluate/result/select";

    private RelativeLayout rl_root;
    private RelativeLayout rl_back;
    private FrameLayout frameLayout;
    private TextView tv_title;
    private ImageView iv_restart;
    private ViewPager viewPager;
    private View view_left;
    private View view_right;

    private VoiceEvaluationSpeechActivity mContext;

    private String audioUrl = "";
    private String content = "";
    private List<VoiceErrorWordBean> errors = new ArrayList<>();
    private VoiceEvaluationSpeechBean bean;

    private FragmentManager fragmentManager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> mList = new ArrayList<>();
    private VoiceEvaluationSpeechLeftFragment leftFragment;
    private VoiceEvaluationSpeechRightFragment rightFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_evaluation_speech);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initView();
        initViewPager();
        initData();
        initEvents();
        setImmerseLayout();
    }

    private void initViewPager() {
        leftFragment = new VoiceEvaluationSpeechLeftFragment();
        mList.add(leftFragment);
        rightFragment = new VoiceEvaluationSpeechRightFragment();
        mList.add(rightFragment);

        fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        view_left.setSelected(true);
        view_right.setSelected(false);
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    view_left.setSelected(true);
                    view_right.setSelected(false);
                } else {
                    view_left.setSelected(false);
                    view_right.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 前往语音测评
     */
    private void turnToVoiceEvaluation() {
        Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
        intent.putExtra("materialId", "1");
        intent.putExtra("restart", true);
        startActivity(intent);
        finish();
    }

    private void initData() {

        showLoadingView(true);

        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID), "speech");

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
        rl_root = findViewById(R.id.rl_root_voice_evaluation_speech);
        rl_back = findViewById(R.id.rl_back_voice_evaluation_speech);
        frameLayout = findViewById(R.id.frame_voice_evaluation_speech);
        tv_title = findViewById(R.id.tv_title_voice_evaluation_speech);
        iv_restart = findViewById(R.id.iv_restart_voice_evaluation_speech);
        viewPager = findViewById(R.id.view_pager_voice_evaluation_speech);
        view_left = findViewById(R.id.view_left_voice_evaluation_speech);
        view_right = findViewById(R.id.view_right_voice_evaluation_speech);

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

                bean = new VoiceEvaluationSpeechBean();
                JSONObject score = object.getJSONObject("detail");
                bean.setVocal_score(score.optDouble("phoneScore", 0));
                bean.setSmooth_score(score.optDouble("fluencyScore", 0));
                bean.setModeration_score(score.optDouble("toneScore", 0));
                bean.setComplete_score(score.optDouble("integrityScore", 0));
                bean.setErrorLength(score.getJSONArray("errorWords").length());
                bean.setDuration(object.getString("duration"));

                double totalScore = object.optDouble("totalScore", 0);
                bean.setScore(totalScore);
                bean.setContent(object.getString("comment"));

                audioUrl = object.getString("audio");
                content = "天地苍苍,乾坤茫茫,中华少年,顶天立地当自强。\n" +
                        "少年中国者,则中国少年之责任也。\n" +
                        "故今日之责任,不在他人,而全在我少年。\n" +
                        "少年智则国智,少年富则国富;\n" +
                        "少年强则国强,少年独立则国独立;\n" +
                        "少年自由则国自由,少年进步则国进步。";

                JSONObject detail = object.getJSONObject("detail");
                JSONArray array = detail.getJSONArray("errorWords");
                for (int i = 0; i < array.length(); i++) {
                    VoiceErrorWordBean bean = new VoiceErrorWordBean();
                    JSONObject error = array.getJSONObject(i);
                    bean.setIndex(error.optInt("index", -1));
                    bean.setWord(error.getString("error"));
                    errors.add(bean);
                }

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
        leftFragment.setInfo(bean);
        rightFragment.setAudioUrl(audioUrl);
        rightFragment.setContent(content, errors);
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
            extends WeakAsyncTask<String, Void, String, VoiceEvaluationSpeechActivity> {

        protected GetData(VoiceEvaluationSpeechActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationSpeechActivity activity, String[] strings) {
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
        protected void onPostExecute(VoiceEvaluationSpeechActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 适配器
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }

}
