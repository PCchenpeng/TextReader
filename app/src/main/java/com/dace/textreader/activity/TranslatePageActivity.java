package com.dace.textreader.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;

public class TranslatePageActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView tv_title;
    private WebView mWebview;
    private String url;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        initData();
        initView();
        initEvents();

        mWebview.loadUrl(url);
    }

    private void initData() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
    }

    private void initView() {
        mWebview = findViewById(R.id.webview);
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        initWebSettings();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    private void initWebSettings() {
        WebSettings webSettings = mWebview.getSettings();
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

        webSettings.setDatabaseEnabled(true);
        //取得缓存路径
        String path = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        //设置路径
        webSettings.setDatabasePath(path);
        //设置支持DomStorage
        webSettings.setDomStorageEnabled(true);
        //设置存储模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setAppCacheEnabled(true);
        mWebview.requestFocus();
    }
}
