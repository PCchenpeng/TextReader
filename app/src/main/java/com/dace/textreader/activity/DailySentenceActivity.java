package com.dace.textreader.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SentenceBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 收藏列表的每日一句详情
 */
public class DailySentenceActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/query/sentence?";
    private static final String collectUrl = HttpUrlPre.HTTP_URL + "/collect/sentenceEveryday?";
    private static final String cancelCollectUrl = HttpUrlPre.HTTP_URL + "/delete/collect/sentenceEveryday";
    //分享链接
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/sentenceEveryday/share";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_share;
    private RelativeLayout rl_collect;
    private ImageView iv_collect;
    private FrameLayout frameLayout;
    private TextView tv_date;
    private JustifyTextView tv_content;
    private TextView tv_author;
    private ImageView iv_practice;

    private DailySentenceActivity mContext;

    private GestureDetector mGestureDetector;

    private long sentenceId = -1;
    private String sentence = "";
    private List<SentenceBean> mList = new ArrayList<>();
    private boolean collectOrNot = false;

    private boolean isLoading = false;

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;
    private final int TYPE_SHARE_Weibo = 6;

    private int type_share = -1;  //分享类型
    private String shareTitle = "";  //分享标题
    private String shareContent = "";  //分享内容

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_sentence);

        mContext = this;

        sentenceId = getIntent().getLongExtra("sentenceId", -1L);

        initView();
        initData();
        initEvents();
        setImmerseLayout();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_collect.setPadding(0, statusBarHeight, 0, 0);
        rl_share.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        mGestureDetector = new GestureDetector(this, myGestureListener);
        rl_back.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_collect.setOnClickListener(this);
        tv_content.setOnClickListener(this);
        iv_practice.setOnClickListener(this);
    }

    private void initData() {
        isLoading = true;
        new GetData(mContext).execute(url + "id=" + sentenceId + "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("每日一句");
        rl_share = findViewById(R.id.rl_share_daily_sentence);
        rl_collect = findViewById(R.id.rl_collection_daily_sentence);
        iv_collect = findViewById(R.id.iv_collection_daily_sentence);
        frameLayout = findViewById(R.id.frame_daily_sentence);
        tv_date = findViewById(R.id.tv_date_daily_sentence);
        tv_content = findViewById(R.id.tv_content_daily_sentence);
        tv_author = findViewById(R.id.tv_author_daily_sentence);
        iv_practice = findViewById(R.id.iv_practice_daily_sentence);
    }

    GestureDetector.SimpleOnGestureListener myGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float x = e1.getX() - e2.getX();
            float x2 = e2.getX() - e1.getX();
            if (x > 50 && Math.abs(velocityX) > 0) {  //向左手势
                pre();
            } else if (x2 > 50 && Math.abs(velocityX) > 0) {  //向右手势
                next();
            }
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.frame_daily_sentence:
                break;
            case R.id.rl_share_daily_sentence:
                showShareDialog();
                break;
            case R.id.rl_collection_daily_sentence:
                if (!isLoading) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        if (collectOrNot) {
                            cancelCollect();
                        } else {
                            collect();
                        }
                    }
                }
                break;
            case R.id.tv_content_daily_sentence:
                copyText();
                break;
            case R.id.iv_practice_daily_sentence:
                turnToWritingPractice();
                break;
        }
    }

    /**
     * 刻意训练
     */
    private void turnToWritingPractice() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", "");
        intent.putExtra("area", 5);
        intent.putExtra("type", 5);
        intent.putExtra("practiceType", 1);
        intent.putExtra("practice", sentence);
        startActivity(intent);
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 复制每日一句
     */
    private void copyText() {
        if (!isLoading && mList.size() > 1) {
            String str = mList.get(1).getContent() + " -- " + mList.get(1).getAuthor();
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData cd = ClipData.newPlainText("Label", str);
            if (cm != null) {
                cm.setPrimaryClip(cd);
                showTips("复制成功");
            }
        }
    }

    /**
     * 取消收藏
     */
    private void cancelCollect() {
        collectOrNot = false;
        iv_collect.setImageResource(R.drawable.bottom_collection_unselected);
        JSONArray array = new JSONArray();
        array.put(sentenceId);
        new DeleteCollection(mContext).execute(cancelCollectUrl, array.toString());
    }

    /**
     * 收藏
     */
    private void collect() {
        collectOrNot = true;
        iv_collect.setImageResource(R.drawable.bottom_collection_selected);
        new CollectSentence(mContext)
                .execute(collectUrl + "id=" + sentenceId +
                        "&studentId=" + NewMainActivity.STUDENT_ID);
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
                                getShareHtml(TYPE_SHARE_WX_FRIEND);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    getShareHtml(TYPE_SHARE_Weibo);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK);
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
     * 获取分享链接
     *
     * @param type_share
     */
    private void getShareHtml(int type_share) {
        this.type_share = type_share;
        if (mList.size() < 1) {
            showTips("分享失败，请稍后重试~");
            return;
        }
        shareTitle = mList.get(1).getAuthor();
        shareContent = mList.get(1).getContent();
        showTips("正在准备分享内容...");
        new GetShareHtml(this).execute(shareUrl, String.valueOf(sentenceId));
    }

    /**
     * 上一句
     */
    private void pre() {
        long preId = mList.get(0).getId();
        if (preId == -1) {
            showTips("没有下一句了~");
        } else if (sentenceId == preId) {
            showTips("正在加载中，请稍后~");
        } else {
            SentenceBean sentenceBean = mList.get(0);
            showSentence(sentenceBean);
            initData();
        }
    }

    /**
     * 下一句
     */
    private void next() {
        long nextId = mList.get(2).getId();
        if (nextId == -1) {
            showTips("没有上一句了~");
        } else if (sentenceId == nextId) {
            showTips("正在加载中，请稍后~");
        } else {
            SentenceBean sentenceBean = mList.get(2);
            showSentence(sentenceBean);
            initData();
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                mList.clear();
                SentenceBean sentence_pre = new SentenceBean();
                if (null == object.getString("pre")
                        || object.getString("pre").equals("null")
                        || object.getString("pre").equals("")) {
                    sentence_pre.setId(-1);
                } else {
                    JSONObject object_pre = object.getJSONObject("pre");
                    sentence_pre.setId(object_pre.optLong("id", -1));
                    if (object_pre.getString("time").equals("")
                            || object_pre.getString("time").equals("null")) {
                        sentence_pre.setDate("2018-01-01 00:00");
                    } else {
                        sentence_pre.setDate(DateUtil.time2MD(object_pre.getString("time")));
                    }
                    sentence_pre.setAuthor(object_pre.getString("author"));
                    sentence_pre.setContent(object_pre.getString("content"));
                    if (1 == object_pre.optInt("status", 0)) {
                        sentence_pre.setCollectOrNot(true);
                    } else {
                        sentence_pre.setCollectOrNot(false);
                    }
                }
                mList.add(sentence_pre);

                SentenceBean sentence_current = new SentenceBean();
                if (null == object.getString("current")
                        || object.getString("current").equals("null")
                        || object.getString("current").equals("")) {
                    sentence_current.setId(-1);
                } else {
                    JSONObject object_current = object.getJSONObject("current");
                    sentence_current.setId(object_current.optLong("id", -1));
                    if (object_current.getString("time").equals("")
                            || object_current.getString("time").equals("null")) {
                        sentence_current.setDate("2018-01-01 00:00");
                    } else {
                        sentence_current.setDate(DateUtil.time2MD(object_current.getString("time")));
                    }
                    sentence_current.setAuthor(object_current.getString("author"));
                    sentence_current.setContent(object_current.getString("content"));
                    if (1 == object_current.optInt("status", 0)) {
                        sentence_current.setCollectOrNot(true);
                    } else {
                        sentence_current.setCollectOrNot(false);
                    }
                }
                mList.add(sentence_current);

                SentenceBean sentence_next = new SentenceBean();
                if (null == object.getString("next")
                        || object.getString("next").equals("null")
                        || object.getString("next").equals("null")) {
                    sentence_next.setId(-1);
                } else {
                    JSONObject object_next = object.getJSONObject("next");
                    sentence_next.setId(object_next.optLong("id", -1));
                    if (object_next.getString("time").equals("")
                            || object_next.getString("time").equals("null")) {
                        sentence_next.setDate("2018-01-01 00:00");
                    } else {
                        sentence_next.setDate(DateUtil.time2MD(object_next.getString("time")));
                    }
                    sentence_next.setAuthor(object_next.getString("author"));
                    sentence_next.setContent(object_next.getString("content"));
                    if (1 == object_next.optInt("status", 0)) {
                        sentence_next.setCollectOrNot(true);
                    } else {
                        sentence_next.setCollectOrNot(false);
                    }
                }
                mList.add(sentence_next);

                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新UI
     */
    private void updateUi() {
        if (mList.get(1).getId() == -1) {
            errorData();
        } else {
            if (tv_content.getText().toString().equals("")) {
                SentenceBean sentenceBean = mList.get(1);
                showSentence(sentenceBean);
            } else if (sentenceId != mList.get(1).getId()) {
                SentenceBean sentenceBean = mList.get(1);
                showSentence(sentenceBean);
            }
        }
    }

    /**
     * 显示句子详情
     */
    private void showSentence(SentenceBean sentenceBean) {
        sentenceId = sentenceBean.getId();
        sentence = sentenceBean.getContent();
        collectOrNot = sentenceBean.isCollectOrNot();
        if (collectOrNot) {
            iv_collect.setImageResource(R.drawable.bottom_collection_selected);
        } else {
            iv_collect.setImageResource(R.drawable.bottom_collection_unselected);
        }

        String content = sentenceBean.getContent() + "\n";
        tv_content.setText(content);

        tv_author.setText(sentenceBean.getAuthor());

        tv_date.setText(sentenceBean.getDate());
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        rl_share.setVisibility(View.GONE);
        rl_collect.setVisibility(View.GONE);
        if (mList.size() == 0) {
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
        }
    }

    /**
     * 分析分享链接数据
     *
     * @param s
     */
    private void analyzeShareData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                String url = jsonObject.getString("data");
                share(url);
            } else {
                errorShare();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorShare();
        }
    }

    /**
     * 分享
     *
     * @param url
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

        ShareUtil.shareToWeibo(shareHandler, url, shareTitle, shareContent, thumb);

    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, shareTitle, shareContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, shareTitle, shareContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, shareTitle, shareContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        showTips("分享失败，请稍后重试~");
    }

    /**
     * 显示吐丝
     *
     * @param s
     */
    private void showTips(String s) {
        MyToastUtil.showToast(mContext, s);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, DailySentenceActivity> {

        protected GetData(DailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(DailySentenceActivity activity, String[] strings) {
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
        protected void onPostExecute(DailySentenceActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
            activity.isLoading = false;
        }
    }

    /**
     * 收藏
     */
    private static class CollectSentence
            extends WeakAsyncTask<String, Void, String, DailySentenceActivity> {

        protected CollectSentence(DailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(DailySentenceActivity activity, String[] strings) {
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
        protected void onPostExecute(DailySentenceActivity activity, String s) {
            if (s == null) {
                activity.collectOrNot = false;
                activity.iv_collect.setImageResource(R.drawable.bottom_collection_unselected);
            } else {
                activity.showTips("收藏成功");
            }
        }
    }

    /**
     * 取消收藏
     */
    private static class DeleteCollection
            extends WeakAsyncTask<String, Integer, String, DailySentenceActivity> {

        protected DeleteCollection(DailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(DailySentenceActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("ids", params[1]);
                json.put("status", 0);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DailySentenceActivity activity, String s) {
            if (s == null) {
                activity.collectOrNot = true;
                activity.iv_collect.setImageResource(R.drawable.bottom_collection_selected);
            } else {
                activity.showTips("取消收藏");
            }
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, DailySentenceActivity> {

        protected GetShareHtml(DailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(DailySentenceActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("id", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DailySentenceActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }
}
