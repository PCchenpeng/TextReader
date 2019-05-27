package com.dace.textreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 已购课程
 * 内嵌h5实现
 */
public class BoughtLessonActivity extends BaseActivity {

    private FrameLayout frameLayout;
    private RelativeLayout rl_back;
    private TextView tv_title;
    private WebView webView;

    private BoughtLessonActivity mContext;

    private boolean isMemberContent = false;
    private long cardId = -1;
    private boolean activated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_lesson);

        mContext = this;

        isMemberContent = getIntent().getBooleanExtra("isMemberContent", false);
        cardId = getIntent().getLongExtra("cardId", -1);
        activated = getIntent().getBooleanExtra("activated", activated);

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

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        if (isMemberContent) {
            tv_title.setText("微课");
        } else {
            tv_title.setText("已购课程");
        }

        frameLayout = findViewById(R.id.frame_bought_lesson);
        webView = findViewById(R.id.webView_bought_lesson);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    if (url.contains("app://ooc") && url.contains("param=")) {
                        dealWithData(url);
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
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

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
    }

    private void initData() {
//        if (isMemberContent) {
//            if (activated){
//                webView.loadUrl("file:///android_asset/html/studyCardClass.html" +
//                        "?studentId=" + NewMainActivity.STUDENT_ID +
//                        "&cardId=" + cardId);
//            } else {
//                webView.loadUrl("file:///android_asset/html/studyCardClass1.html" +
//                        "?studentId=" + NewMainActivity.STUDENT_ID +
//                        "&cardId=" + cardId);
//            }
//        } else {
//            webView.loadUrl("file:///android_asset/html/boughtPytheMicroClass.html" +
//                    "?studentId=" + NewMainActivity.STUDENT_ID);
//        }
        webView.loadUrl(PreferencesUtil.getData(BoughtLessonActivity.this,"course_bought_url","https://web.pythe.cn/microClasshtml5/boughtPytheMicroClass.html") + "?studentId=" + NewMainActivity.STUDENT_ID);
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
        webSettings.setAllowFileAccessFromFileURLs(true);
        //禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
    }

    /**
     * 处理数据
     *
     * @param url
     */
    private void dealWithData(String url) {
        String param = url.split("param=")[1];
        try {
            JSONObject jsonObject = new JSONObject(param);
            long id = jsonObject.optLong("oocId", -1);
            Intent intent = new Intent(mContext, MicroLessonActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
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

}
