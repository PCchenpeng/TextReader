package com.dace.textreader.view.weight.pullrecycler.mywebview;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dace.textreader.util.MyToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BridgeWebViewClient extends WebViewClient {
    private BridgeCustomWebview webView;

    public BridgeWebViewClient(BridgeCustomWebview webView) {
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            webView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
            webView.flushMessageQueue();
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if(onPageFinished != null)
            onPageFinished.onPageFinished();

        if (BridgeCustomWebview.toLoadJs != null) {
            BridgeUtil.webViewLoadLocalJs(view, BridgeCustomWebview.toLoadJs);
        }

//        view.loadUrl("javascript:"+BridgeCustomWebview.getContentJs);



        //
        if (webView.getStartupMessage() != null) {
            for (Message m : webView.getStartupMessage()) {
                webView.dispatchMessage(m);
            }
            webView.setStartupMessage(null);
        }

    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    public interface OnPageFinished{
        void onPageFinished();
    }

    private OnPageFinished onPageFinished;

    public void setOnPageFinished(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

}
