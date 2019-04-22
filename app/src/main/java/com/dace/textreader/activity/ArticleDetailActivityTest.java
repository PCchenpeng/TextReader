package com.dace.textreader.activity;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeCustomWebview;
import com.dace.textreader.view.weight.pullrecycler.mywebview.CallBackFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleDetailActivityTest extends BaseActivity{

//    private BridgeCustomWebview webView;
    private ListView lv_test;
    private Adapter adapter;

    String url = "https://check.pythe.cn/1readingModule/pyReadDetail0.html?platForm=android&fontSize=18px&readModule=1&py=1&studentId=8429&gradeId=142&lineHeight=2.4&isShare=0&version=3.2.6&backgroundColor=FFFFFF&essayId=10032979";
//    String url = "https://www.baidu.com";
//    String url = "https://check.pythe.cn/1readingModule/pyReadDetail0.html?platForm=ios&fontSize=21px&readModule=1&py=1&studentId=8428&gradeId=151&lineHeight=2.6&isShare=0&version=3.2.6&backgroundColor=FFFBE9&essayId=10345373";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articledetail_test);

//        webView = findViewById(R.id.web_test);
        lv_test = findViewById(R.id.lv_test);
        adapter = new Adapter(this);
        lv_test.setAdapter(adapter);
    }
//        initWebSettings();

//        webView.setWebViewClient(new WebViewClient() {
//
//
//
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                    int errorCode = error.getErrorCode();
//                    if (errorCode != 200) {
//                    }
//                }
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
////                super.onReceivedSslError(view, handler, error);
//                handler.proceed();
//            }
//        });

//        JSONObject params = new JSONObject();
//        try {
//            params.put("screen_height",DensityUtil.px2dip(this,DensityUtil.getScreenHeight(this)));
//            params.put("screen_width",DensityUtil.px2dip(this,DensityUtil.getScreenWidth(this)));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        webView.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
//            @Override
//            public void onCallBack(String data) {
//            }
//        });
//
//
//        webView.loadUrl(url);
//    }



//    private void initWebSettings() {
//        WebSettings webSettings = webView.getSettings();
//        //5.0以上开启混合模式加载
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        //允许js代码
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        //禁用放缩
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setBuiltInZoomControls(false);
//        //禁用文字缩放
//        webSettings.setTextZoom(100);
//        //自动加载图片
//        webSettings.setLoadsImagesAutomatically(true);
//    }

    class Adapter extends BaseAdapter{

        private Context mContext;
        public Adapter(Context context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_article_detail_test, null);
                viewHolder = new ViewHolder();
                viewHolder.webView = convertView.findViewById(R.id.webview);

                WebSettings webSettings = viewHolder.webView.getSettings();
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

                JSONObject params = new JSONObject();
                try {
                    params.put("screen_height",DensityUtil.px2dip(mContext,DensityUtil.getScreenHeight(mContext)));
                    params.put("screen_width",DensityUtil.px2dip(mContext,DensityUtil.getScreenWidth(mContext)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewHolder.webView.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                    }
                });
                viewHolder.webView.loadUrl(url);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        class ViewHolder{
            BridgeCustomWebview webView;
        }
    }



}
