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
import com.dace.textreader.util.ActivityUtils;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 活动详情
 * h5内容
 */
public class EventsActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/navigate/index/html?";

    private RelativeLayout rl_back;
    private RelativeLayout rl_share;
    private FrameLayout frameLayout;
    private WebView webView;

    private EventsActivity mContext;

    private String pageName = "";
    private String pageContent = "";
    private String pageUrl = "";
    private String shareUrl = "";

    private WbShareHandler shareHandler;

    /**
     * 极光推送相关
     **/
    //消息Id
    private static final String KEY_MSGID = "msg_id";
    //该通知的下发通道
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    //通知附加字段
    private static final String KEY_EXTRAS = "n_extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mContext = this;

        initView();
        initData();
        initEvent();

        setImmerseLayout();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            if (pageUrl.contains("studentId=-1")) {
                String string = "studentId=" + NewMainActivity.STUDENT_ID;
                pageUrl = pageUrl.replace("studentId=-1", string);
                loadUrl(pageUrl);
            } else {
                webView.reload();
            }

        }
    }

    // view为标题栏
    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext)
                + DensityUtil.dip2px(mContext, 10);
        rl_back.setPadding(0, statusBarHeight, 0, DensityUtil.dip2px(mContext, 10));
        rl_share.setPadding(0, statusBarHeight, 0, DensityUtil.dip2px(mContext, 10));
    }

    private void initEvent() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageUrl.equals("")) {
                    MyToastUtil.showToast(mContext, "无可分享内容");
                } else {
                    showShareDialog();
                }
            }
        });
    }

    private void initData() {
        showLoadingView(true);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            String data = intent.getData().toString();
            if (!TextUtils.isEmpty(data)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String msgId = jsonObject.optString(KEY_MSGID);
                    byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
                    String extras = jsonObject.optString(KEY_EXTRAS);

                    JSONObject extrasJson = new JSONObject(extras);
                    String myValue = extrasJson.getString("params");

                    JSONObject object = new JSONObject(myValue);
                    pageName = object.getString("productId");

                    //上报点击事件
                    JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadUrl("");
                }
            } else {
                loadUrl("");
            }
        } else {
            pageName = intent.getStringExtra("pageName");
        }
        if (Patterns.WEB_URL.matcher(pageName).matches() || URLUtil.isValidUrl(pageName)) {
            loadUrl(pageName);
        } else {
            new GetUrl(mContext).execute(url + "name=" + pageName + "&share=0&studentId="
                    + NewMainActivity.STUDENT_ID);
        }
    }

    /**
     * 显示正在加载
     */
    private void showLoadingView(boolean show) {
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
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

    private void initView() {
        rl_back = findViewById(R.id.rl_back_events);
        rl_share = findViewById(R.id.rl_share_events);

        frameLayout = findViewById(R.id.frame_events);
        webView = findViewById(R.id.web_view_events);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    if (url.contains("app://writing") && url.contains("param=")) {
                        if (NewMainActivity.STUDENT_ID == -1) {
                            turnToLogin();
                        } else {
                            String taskId = url.split("param=")[1];
                            dealWithWritingData(taskId);
                        }
                    } else if (url.contains("app://event") && url.contains("param=")) {
                        String name = url.split("")[1];
                        Intent intent = new Intent(mContext, EventsActivity.class);
                        intent.putExtra("pageName", name);
                        startActivity(intent);
                    } else if (url.contains("app://login")) {
                        turnToLogin();
                    }
                } else if (TextUtils.equals("pythe", scheme)) {
                    if (url.contains("pythe://voice") && url.contains("voice_entrance")) {
                        turnToVoiceEvaluationEntrance();
                    } else if (url.contains("pythe://voice") && url.contains("finishSignup")) {
                        turnToVoiceEvaluationEntrance();
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
     * 前往语音评测入口
     */
    private void turnToVoiceEvaluationEntrance() {
        if (ActivityUtils.isExsitMianActivity(
                mContext, VoiceEvaluationEntranceActivity.class)) {
            finish();
        } else {
            startActivity(new Intent(mContext, VoiceEvaluationEntranceActivity.class));
            finish();
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 处理写作活动数据
     */
    private void dealWithWritingData(String s) {
        String taskId;
        String type;
        String flag;
        try {
            JSONObject object = new JSONObject(s);
            taskId = object.getString("taskId");
            type = object.getString("type");
            flag = object.getString("flag");
        } catch (JSONException e) {
            e.printStackTrace();
            taskId = s;
            type = "5";
            flag = "-1";
        }
        if (type.equals("2") && flag.equals("0")) {
            showCompetitionDialog(taskId, type);
        } else {
            turnToWritingActivity(taskId, type, true);
        }
    }

    /**
     * 显示第一次进入限时比赛的提示框
     */
    private void showCompetitionDialog(final String taskId, final String type) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否立即开始写作");
                        tv_content.setText("比赛为限时作文，一旦开始，立即计时，中途退出，计时继续至限时结束。");
                        tv_left.setText("取消");
                        tv_right.setText("确定");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NewMainActivity.STUDENT_ID == -1) {
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                } else {
                                    turnToWritingActivity(taskId, type, false);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(60)
                .show(getSupportFragmentManager());
    }

    /**
     * 前往写作界面
     */
    private void turnToWritingActivity(String taskId, String type, boolean flag) {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", taskId);
        intent.putExtra("type", Integer.valueOf(type));
        intent.putExtra("isFromCompetitionH5", flag);
        startActivity(intent);
        finish();
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
     * 显示分享对话框
     */
    private void showShareDialog() {
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
                                shareArticleToWX(true, shareUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareArticleToWX(false, shareUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    shareToWeibo(shareUrl);
                                } else {
                                    MyToastUtil.showToast(mContext, "请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQQ(shareUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQZone(shareUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataUtil.copyContent(mContext, shareUrl);
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
     * 分享到微博
     *
     * @param shareUrl
     */
    private void shareToWeibo(String shareUrl) {

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWeibo(shareHandler, shareUrl, pageName, pageContent, thumb);
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
                JSONObject object = jsonObject.getJSONObject("data");
                pageUrl = object.getString("url");
                pageName = object.getString("title");
                pageContent = object.getString("message");
                loadUrl(pageUrl);
                String taskId = object.getString("taskId");
                if (!taskId.equals("") && !taskId.equals("null")) {
                    int status = object.optInt("taskStatus", 1);
                    if (status == 0) {
                        showEventsFinishedTipsView(taskId);
                    }
                }
            } else {
                loadUrl("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            loadUrl("");
        }
    }

    /**
     * 显示活动结束提示框
     */
    private void showEventsFinishedTipsView(final String taskId) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("活动已结束");
                        tv_content.setText("是否前往活动详情页查看活动信息");
                        tv_left.setText("算了");
                        tv_right.setText("查看");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                turnToEventDetail(taskId);
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(60)
                .show(getSupportFragmentManager());
    }

    /**
     * 前往活动详情
     */
    private void turnToEventDetail(String taskId) {
        Intent intent = new Intent(mContext, WritingEventDetailsActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
        finish();
    }

    /**
     * 加载界面
     *
     * @param url
     */
    private void loadUrl(String url) {
        if (url.equals("")) {
            pageUrl = "";
            rl_share.setVisibility(View.GONE);
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
            if (pageUrl.contains("share=0")) {
                shareUrl = pageUrl.replace("share=0", "share=1");
            } else {
                shareUrl = pageUrl + "&share=1";
            }
            if (webView != null) {
                webView.loadUrl(url);
            }
        }
        if (pageName.equals("") || pageName.equals("null") ||
                pageContent.equals("") || pageContent.equals("null")) {
            rl_share.setVisibility(View.GONE);
        }
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, pageName, pageContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, pageName, pageContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, pageName, pageContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 获取Url
     */
    private static class GetUrl
            extends WeakAsyncTask<String, Void, String, EventsActivity> {

        protected GetUrl(EventsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(EventsActivity activity, String[] strings) {
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
        protected void onPostExecute(EventsActivity activity, String s) {
            if (s == null) {
                activity.loadUrl("");
            } else {
                activity.analyzeData(s);
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
