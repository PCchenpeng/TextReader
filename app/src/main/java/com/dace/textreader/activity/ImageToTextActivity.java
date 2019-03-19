package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 图片转文字
 */
public class ImageToTextActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/composition/image/ocr";

    private RelativeLayout rl_root;
    private ScrollView scrollView;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private ImageView iv_line;
    private TextView tv_restart;
    private TextView tv_tips;
    private LinearLayout rl_result;
    private LinearLayout ll_move;
    private ImageView iv_move;
    private TextView tv_commit;
    private EditText et_result;
    private RelativeLayout rl_back;

    private ImageToTextActivity mContext;

    private String imagePath = "";

    private String text = "";

    private int mScreenHeight = 0;
    private int dp_20 = 0;
    private int dp_100 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        imagePath = getIntent().getStringExtra("imagePath");

        initValue();
        initView();
        initData();
        initEvents();

    }

    private void initValue() {
        mScreenHeight = DensityUtil.getScreenHeight(mContext);
        dp_20 = DensityUtil.dip2px(mContext, 20);
        dp_100 = DensityUtil.dip2px(mContext, 100);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = et_result.getText().toString();
                backActivity();
            }
        });
        ll_move.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hideInputMethod();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        changeViewHeight((int) (event.getRawY() - downY));
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) et_result.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_result.getApplicationWindowToken(), 0);
        }
    }

    private float downY = 0;

    private void initView() {
        rl_root = findViewById(R.id.rl_root_image_to_text);
        rl_back = findViewById(R.id.rl_back_image_to_text);
        scrollView = findViewById(R.id.scroll_view_image_to_text);
        imageView = findViewById(R.id.iv_image_to_text);
        frameLayout = findViewById(R.id.frame_image_to_text);
        iv_line = findViewById(R.id.iv_line_image_to_text);
        tv_restart = findViewById(R.id.tv_restart_image_to_text);
        tv_tips = findViewById(R.id.tv_tips_image_to_text);
        rl_result = findViewById(R.id.rl_result_image_to_text);
        ll_move = findViewById(R.id.ll_move_image_to_text);
        iv_move = findViewById(R.id.iv_move_image_to_text);
        tv_commit = findViewById(R.id.tv_commit_image_to_text);
        et_result = findViewById(R.id.et_result_image_to_text);

        if (!isDestroyed()) {
            Glide.with(mContext)
                    .load(imagePath)
                    .into(imageView);
        }
    }

    private void initData() {
        showLoadingView();
        new UploadImage(mContext).execute(url, imagePath);
    }

    private void changeViewHeight(int offset) {
        if (offset < dp_100) {
            iv_move.setImageResource(R.drawable.icon_move_to_down);
            return;
        }

        int resultHeight = mScreenHeight - offset;

        if (resultHeight < dp_100) {
            iv_move.setImageResource(R.drawable.icon_move_to_top);
            return;
        }

        ViewGroup.LayoutParams layoutParams = rl_result.getLayoutParams();
        layoutParams.height = resultHeight + dp_20;
        rl_result.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams_scroll = scrollView.getLayoutParams();
        layoutParams_scroll.height = offset;
        scrollView.setLayoutParams(layoutParams_scroll);
    }

    /**
     * 显示loading视图
     */
    private void showLoadingView() {
        tv_tips.setText("文字提取识别中...");
        tv_restart.setVisibility(View.GONE);
        iv_line.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.8f);
        mAnimation.setDuration(5000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(new LinearInterpolator());
        iv_line.setAnimation(mAnimation);
    }

    /**
     * 返回
     */
    private void backActivity() {
        hideSoftInput();
        Intent intent = new Intent();
        intent.putExtra("text", text);
        setResult(0, intent);
        finish();
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(et_result.getWindowToken(), 0);
        }
    }


    /**
     * 上传图片
     */
    private static class UploadImage
            extends WeakAsyncTask<String, Integer, String, ImageToTextActivity> {

        protected UploadImage(ImageToTextActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(ImageToTextActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", strings[1],
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", strings[1])
                        .addFormDataPart("fileType", "image")
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImageToTextActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeImageData(s);
            }
        }
    }

    /**
     * 分析上传图片数据
     *
     * @param s
     */
    private void analyzeImageData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    text = text + array.getString(i);
                }
                showResult();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 显示识别结果
     */
    private void showResult() {
        iv_line.clearAnimation();
        frameLayout.setVisibility(View.GONE);
        rl_result.setVisibility(View.VISIBLE);
        et_result.setText(text);
        et_result.setSelection(text.length());

        if (imageView.getHeight() > mScreenHeight / 2) {
            changeViewHeight(mScreenHeight / 2);
        } else {
            changeViewHeight(imageView.getHeight());
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        iv_line.clearAnimation();
        iv_line.setVisibility(View.GONE);
        tv_restart.setVisibility(View.VISIBLE);
        tv_tips.setText("识别文字失败，请稍后重试~");
    }

}
