package com.dace.textreader.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.ConfirmPopWindow;

import org.json.JSONException;
import org.json.JSONObject;

public class WordDetailActivity extends BaseActivity implements View.OnClickListener {

    private WebView mWebview;
    private RelativeLayout rl_back;
    private ImageView iv_add;
    private ImageView iv_more;
    private String url;
    private String addWordUrl = HttpUrlPre.HTTP_URL_ + "/insert/raw/word";
    private String essayId;
    private String title;
    private String word;
    private String sourceType;

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
                new ConfirmPopWindow(this).showAtBottom(iv_more);

                break;
        }

    }

    private void addWord(String word) {
        JSONObject params = new JSONObject();
        try {
            params.put("word",word);
            params.put("essayId",essayId);
            params.put("sourceType","1");
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
