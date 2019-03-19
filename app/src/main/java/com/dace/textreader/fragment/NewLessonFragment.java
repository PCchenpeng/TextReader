package com.dace.textreader.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.MicroLessonActivity;
import com.dace.textreader.util.WeakAsyncTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewLessonFragment extends Fragment {

    private View view;

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private FrameLayout frameLayout;
    private SmartRefreshLayout refreshLayout;
    private WebView webView;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_lesson, container, false);

        initView();
        initData();
        initEvents();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
                refreshLayout.finishRefresh(1000);
            }
        });
    }

    private void initData() {
        webView.loadUrl("file:///android_asset/html/microClass.html");
    }

    private void initView() {
        rl_back = view.findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = view.findViewById(R.id.tv_page_title_top_layout);
        rl_back.setVisibility(View.GONE);
        tv_page_title.setText("微课");

        refreshLayout = view.findViewById(R.id.smart_refresh_layout_new_lesson_fragment);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setEnableLoadMore(false);

        frameLayout = view.findViewById(R.id.frame_new_lesson_fragment);
        webView = view.findViewById(R.id.web_view_new_lesson);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    if (url.contains("app://ooc") && url.contains("param=")) {
                        dealWithData(url);
                    }
                } else if (TextUtils.equals("pythe", scheme)) {
                    if (url.contains("pythe://microClass") && url.contains("requestParam=")) {
                        String param = url.split("requestParam=")[1];
                        if (param.equals("404")) {
                            showErrorView();
                        }
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
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
            Intent intent = new Intent(getContext(), MicroLessonActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } catch (JSONException e) {
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
            webView.loadUrl(url);
        }
    }

    /**
     * 显示错误界面
     */
    private void showErrorView() {
        View errorView = LayoutInflater.from(getContext())
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
     * 获取微课界面的Url
     */
    private static class GetLessonUrl extends WeakAsyncTask<String, Void, String, NewLessonFragment> {

        protected GetLessonUrl(NewLessonFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewLessonFragment fragment, String[] strings) {
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
        protected void onPostExecute(NewLessonFragment fragment, String s) {
            if (s == null) {
                fragment.loadUrl("");
            } else {
                fragment.analyzeData(s);
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
