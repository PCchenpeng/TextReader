package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.ConfirmPopWindow;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeCustomWebview;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeHandler;
import com.dace.textreader.view.weight.pullrecycler.mywebview.CallBackFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class WordDetailActivity extends BaseActivity implements View.OnClickListener {

    /** 视频全屏参数 */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private BridgeCustomWebview mWebview;
    private RelativeLayout rl_back;
    private ImageView iv_add;
    private ImageView iv_more;
    private String url;
    private String addWordUrl = HttpUrlPre.HTTP_URL_ + "/insert/raw/word";
    private String essayId;
    private String title;
    private String word;
    private String sourceType;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FullscreenHolder fullscreenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        initData();
        initView();
        initEvents();

        mWebview.loadUrl(url);

    }

    private void initData() {
        url = getIntent().getStringExtra("url");
//        url = "https://mp.weixin.qq.com/s/n1pfcfdYQlLX4xZsJYGCpQ";
        sourceType = getIntent().getStringExtra("sourceType");
        essayId = getIntent().getStringExtra("essayId");
        title = getIntent().getStringExtra("title");
        word = getIntent().getStringExtra("word");
    }

    private void initView() {
        mWebview = findViewById(R.id.webview);
        rl_back = findViewById(R.id.rl_back);
        iv_add = findViewById(R.id.iv_add);
        iv_more = findViewById(R.id.iv_more);

        initWebSettings();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_more.setOnClickListener(this);

        mWebview.registerHandler("linkToEssayDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("linkToEssayDetail", "指定Handler接收来自web的数据：" + data);
                if(data == null || data.equals(""))
                    return;
                Intent intent = new Intent(WordDetailActivity.this, ArticleDetailActivity.class);
                intent.putExtra("essayId", data);
                intent.putExtra("imgUrl","");
                startActivity(intent);

                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("collectWord", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("collectWord", "指定Handler接收来自web的数据：" + data);
                word = data;
                function.onCallBack("123");
            }
        });
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

        mWebview.setWebChromeClient(new WebChromeClient() {

            /*** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(WordDetailActivity.this);
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

//            @Override
//            public boolean onJsAlert(WebView view, String url, String message,
//                                     JsResult result) {
//                // TODO Auto-generated method stub
//                return super.onJsAlert(view, url, message, result);
//            }

        });

    }

    /** 视频播放全屏 **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        WordDetailActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(WordDetailActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /** 隐藏视频全屏 */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mWebview.setVisibility(View.VISIBLE);
    }

    /** 全屏容器界面 */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
                if (customView != null) {
                    hideCustomView();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation)
    {
        // TODO Auto-generated method stub

        /* 判断要更改的方向，以Toast提示 */
        switch (requestedOrientation)
        {
            /* 更改为LANDSCAPE */
            case (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE):
                break;
            /* 更改为PORTRAIT */
            case (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT):
                break;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public int getRequestedOrientation()
    {
        // TODO Auto-generated method stub

        /* 此重写getRequestedOrientation方法，可取得当下屏幕的方向 */
        return super.getRequestedOrientation();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_add:
                addWord(word);
                break;
            case R.id.iv_more:
                new ConfirmPopWindow(this,word).showAtBottom(iv_more);
                break;
        }

    }

    private void addWord(String word) {
        JSONObject params = new JSONObject();
        try {
            params.put("word",word);
            params.put("essayId",essayId);
            params.put("sourceType",sourceType);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("title",title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(addWordUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        MyToastUtil.showToast(WordDetailActivity.this,"添加成功");
                    }else  if (jsonObject.getString("status").equals("400")){
                        MyToastUtil.showToast(WordDetailActivity.this,"该词已在生词本中");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }
}
