package com.dace.textreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
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
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 素材详情
 * h5实现
 */
public class MaterialDetailActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView tv_title;

    private WebView webView;

    private FrameLayout frameLayout;

    private MaterialDetailActivity mContext;

    private String materialId = "";
    private String essayTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_detail);

        mContext = this;

        materialId = getIntent().getStringExtra("materialId");
        essayTitle = getIntent().getStringExtra("essayTitle");

        initView();
        initEvents();
        initLocalData();
    }

    private void initLocalData() {
        showLoadingView();
        webView.loadUrl("file:///android_asset/html/bookOpertation.html?" + "materialId=" + materialId);
    }

    /**
     * 显示加载等待视图
     */
    private void showLoadingView() {
        if (isDestroyed()) {
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
        frameLayout.removeAllViews();
        frameLayout.addView(view);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText(essayTitle);

        frameLayout = findViewById(R.id.frame_material_detail);

        webView = findViewById(R.id.web_view_material_detail);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    if (url.contains("app://material") && url.contains("param=")) {
                        dealWithData(url);
                    } else if (url.contains("app://material") && url.contains("title=")) {
                        try {
                            essayTitle = url.split("title=")[1];
                            essayTitle = URLDecoder.decode(essayTitle, "UTF-8");
                            tv_title.setText(essayTitle);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100 && frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                }
            }
        });
    }

    /**
     * 处理与js交互的数据
     *
     * @param url
     */
    private void dealWithData(String url) {
        String object = url.split("param=")[1];
        try {
            JSONObject jsonObject = new JSONObject(object);
            if (0 == jsonObject.optInt("status", -1)) {
                int type = jsonObject.optInt("type", -1);
                int essayId = jsonObject.optInt("essayId", -1);
                Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
                intent.putExtra("id", essayId);
                intent.putExtra("type", type);
                startActivity(intent);
            } else if (1 == jsonObject.optInt("status", -1)) {
                int id = jsonObject.optInt("essayId", -1);
                String s_title = jsonObject.getString("title");
                int type = jsonObject.optInt("type", -1);
                String s = jsonObject.getString("word");
                String word = URLDecoder.decode(s, "UTF-8");
                String title = URLDecoder.decode(s_title, "UTF-8");
                if (type != 2 && type != 4) {  //不需要古文词语解释
                    title = "";
                }
                Intent intent = new Intent(mContext, GlossaryWordExplainActivity.class);
                intent.putExtra("words", word);
                intent.putExtra("essayTitle", title);
                intent.putExtra("glossaryTitle", word);
                intent.putExtra("glossaryId", -1);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
        }
    }


    /**
     * 分析数据
     *
     * @param data
     */
    private void analyzeData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
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
            if (webView != null) {
                webView.loadUrl(url);
            }
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
                initLocalData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取首页的Url
     */
    private static class GetDetailUrl
            extends WeakAsyncTask<String, Void, String, MaterialDetailActivity> {

        protected GetDetailUrl(MaterialDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MaterialDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
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
        protected void onPostExecute(MaterialDetailActivity activity, String s) {
            if (s == null) {
                activity.loadUrl("");
            } else {
                activity.analyzeData(s);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("\"about:blank\"");
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
