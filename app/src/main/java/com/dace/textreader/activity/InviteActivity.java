package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.URLUtil;
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
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.QRCodeUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 新人邀请活动
 */
public class InviteActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/get/invite/friend/activity/url";
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/invite/new/friend/url";

    private RelativeLayout rl_back;
    private FrameLayout frameLayout;
    private WebView webView;

    private InviteActivity mContext;

    private String id;
    private String title;
    private String content;
    private String loadUrl = "";

    private int operateType = -1;  // 操作类型，0为分享，1为扫码

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;
    private int type_share = -1;  //分享类型

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        loadUrl = getIntent().getStringExtra("url");

        initView();
        initData();
        initEvents();

        setImmerseLayout();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    private void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_back.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        showLoadingView(true);
        if (loadUrl.equals("")) {
            new GetUrl(mContext).execute(url, id);
        } else {
            loadUrl(loadUrl);
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_invite);
        frameLayout = findViewById(R.id.frame_invite);
        webView = findViewById(R.id.web_view_invite);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    if (url.contains("app://event") && url.contains("param=")) {
                        String operate = url.split("param=")[1];
                        if (operate.equals("shareAndInvite")) {
                            //分享
                            operateType = 0;
                            showShareDialog();
                        } else if (operate.equals("scanCode")) {
                            //扫码邀请
                            operateType = 1;
                            getHtml();
                        } else if (operate.equals("searchInviteRecord")) {
                            turnToInviteRecord();
                        } else {
                            if (Patterns.WEB_URL.matcher(operate).matches() ||
                                    URLUtil.isValidUrl(operate)) {
                                //如果接收到的字符串是URL地址，跳转访问
                                Intent intent = new Intent(mContext, InviteActivity.class);
                                intent.putExtra("id", "");
                                intent.putExtra("title", "");
                                intent.putExtra("content", "");
                                intent.putExtra("url", operate);
                                startActivity(intent);
                            }
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
                        loadUrl("");
                    }
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 80) {
                    if (frameLayout.getVisibility() == View.VISIBLE) {
                        showLoadingView(false);
                    }
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return true;
            }
        });
    }

    /**
     * 前往查看邀请记录
     */
    private void turnToInviteRecord() {
        startActivity(new Intent(mContext, InviteRecordActivity.class));
    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog() {
        type_share = -1;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.share_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_wechat, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type_share = TYPE_SHARE_WX_FRIEND;
                                getHtml();
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type_share = TYPE_SHARE_WX_FRIENDS;
                                getHtml();
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    type_share = TYPE_SHARE_Weibo;
                                    getHtml();
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type_share = TYPE_SHARE_QQ;
                                getHtml();
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type_share = TYPE_SHARE_QZone;
                                getHtml();
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type_share = TYPE_SHARE_LINK;
                                getHtml();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setDimAmount(0.3f)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取链接
     */
    private void getHtml() {
        if (operateType == 0) {
            showTips("正在获取分享链接，请稍等...");
        } else {
            showTips("正在生成二维码，请稍等...");
        }
        new GetHtmlUrl(mContext).execute(shareUrl, id);
    }

    /**
     * 分享
     */
    private void share(String url) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url);
                break;
            case TYPE_SHARE_Weibo:
                shareToWeibo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWeibo(shareHandler, url, title, content, thumb);

    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, title, content,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 生成二维码
     */
    private void generateQRCode(final String url) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_qr_code_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(url,
                                DensityUtil.dip2px(mContext, 150),
                                DensityUtil.dip2px(mContext, 150));
                        if (bitmap == null) {
                            dialog.dismiss();
                            showTips("生成二维码失败，请稍后再试");
                        } else {
                            ImageView iv_user = holder.getView(R.id.iv_user_qr_code_dialog);
                            TextView tv_user = holder.getView(R.id.tv_user_qr_code_dialog);
                            ImageView iv_close = holder.getView(R.id.iv_close_qr_code_dialog);
                            ImageView iv_qr_code = holder.getView(R.id.iv_qr_code_dialog);
                            GlideUtils.loadUserImage(mContext,
                                    HttpUrlPre.FILE_URL + NewMainActivity.USERIMG, iv_user);
                            tv_user.setText(NewMainActivity.USERNAME);
                            iv_qr_code.setImageBitmap(bitmap);
                            iv_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                })
                .setShowBottom(false)
                .setMargin(40)
                .show(getSupportFragmentManager());

    }

    /**
     * 显示正在加载
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
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
                String pageUrl = jsonObject.getString("data");
                loadUrl(pageUrl);
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
        } else {
            if (webView != null) {
                webView.loadUrl(url);
            }
        }
    }

    private void analyzeGetHtmlData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                String url = jsonObject.getString("data");
                if (operateType == 0) {
                    share(url);
                } else if (operateType == 1) {
                    generateQRCode(url);
                }
            } else {
                errorGetHtmlUrl();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorGetHtmlUrl();
        }
        operateType = -1;
    }

    private void errorGetHtmlUrl() {
        if (operateType == 0) {
            showTips("获取分享链接失败,请稍后再试");
        } else if (operateType == 1) {
            showTips("生成二维码失败,请稍后再试");
        } else {
            showTips("获取数据失败,请稍后再试");
        }
    }



    /**
     * 获取Url
     */
    private static class GetUrl
            extends WeakAsyncTask<String, Void, String, InviteActivity> {

        protected GetUrl(InviteActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("activityId", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(InviteActivity activity, String s) {
            if (s == null) {
                activity.loadUrl("");
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取分享链接
     */
    private static class GetHtmlUrl
            extends WeakAsyncTask<String, Void, String, InviteActivity> {

        protected GetHtmlUrl(InviteActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("activityId", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(InviteActivity activity, String s) {
            if (s == null) {
                activity.errorGetHtmlUrl();
            } else {
                activity.analyzeGetHtmlData(s);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
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
