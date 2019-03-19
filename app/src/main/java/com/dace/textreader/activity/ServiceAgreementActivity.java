package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 服务协议
 */
public class ServiceAgreementActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/navigate/page?";

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private FrameLayout frameLayout;
    private WebView webView;

    private ServiceAgreementActivity mContext;

    private String pageName = "";
    private boolean isRecharge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);

        mContext = this;

        isRecharge = getIntent().getBooleanExtra("isRecharge", false);
        if (isRecharge) {
            pageName = "charge_protocol";
        } else {
            pageName = "service_protocol";
        }

        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        new GetLessonUrl(mContext).execute(url + "name=" + pageName);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        if (isRecharge) {
            tv_page_title.setText("充值协议");
        } else {
            tv_page_title.setText("服务协议");
        }

        frameLayout = findViewById(R.id.frame_service_agreement);
        webView = findViewById(R.id.web_view_service_agreement);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
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
        if (isDestroyed()) {
            return;
        }
        if (url.equals("")) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("无内容");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            if (webView != null) {
                webView.loadUrl(url);
            }
        }
    }

    /**
     * 获取Url
     */
    private static class GetLessonUrl
            extends WeakAsyncTask<String, Void, String, ServiceAgreementActivity> {

        protected GetLessonUrl(ServiceAgreementActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(ServiceAgreementActivity activity, String[] strings) {
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
        protected void onPostExecute(ServiceAgreementActivity activity, String s) {
            if (s == null) {
                activity.loadUrl("");
            } else {
                activity.analyzeData(s);
            }
        }
    }

    @Override
    public void onDestroy() {
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
