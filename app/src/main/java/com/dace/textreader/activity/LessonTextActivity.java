package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 微课文稿
 * h5实现
 */
public class LessonTextActivity extends BaseActivity {

    private final static String url = HttpUrlPre.HTTP_URL + "/course/draft/url";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private WebView webView;
    private FrameLayout frameLayout;

    private LessonTextActivity mContext;

    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_text);

        mContext = this;

        id = getIntent().getStringExtra("lessonId");

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData() {
        showLoadingView(true);
        new GetUrlData(mContext).execute(url, id);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("文稿");

        frameLayout = findViewById(R.id.frame_lesson_text);
        webView = findViewById(R.id.web_view_lesson_text);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showLoadingView(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    int errorCode = error.getErrorCode();
                    if (errorCode != 200) {
                        showErrorView();
                    }
                }
            }
        });
    }

    private void initWebSettings() {
        WebSettings webSettings = webView.getSettings();
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //允许js代码
        webSettings.setJavaScriptEnabled(true);
        //禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置不缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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
                String url = jsonObject.getString("data");
                loadUrl(url);
            } else {
                loadUrl("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            loadUrl("");
        }
    }

    /**
     * 加载界面
     *
     * @param url
     */
    private void loadUrl(String url) {
        if (url.equals("")) {
            showErrorView();
        } else {
            webView.loadUrl(url);
        }
    }

    /**
     * Loading状态
     *
     * @param showLading
     */
    private void showLoadingView(boolean showLading) {
        if (isDestroyed()) {
            return;
        }
        if (showLading) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
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
     * 显示错误界面
     */
    private void showErrorView() {
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
     * 获取文稿的地址
     */
    private static class GetUrlData
            extends WeakAsyncTask<String, Void, String, LessonTextActivity> {

        protected GetUrlData(LessonTextActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LessonTextActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lesson_id", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, jsonObject.toString());
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
        protected void onPostExecute(LessonTextActivity activity, String s) {
            if (s == null) {
                activity.loadUrl("");
            } else {
                activity.analyzeData(s);
            }
        }
    }
}
