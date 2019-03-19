package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.SentenceDetailAdapter;
import com.dace.textreader.bean.SentenceBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
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

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 改版后的每日一句
 */
public class NewDailySentenceActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/sentence/list";
    private static final String collectUrl = HttpUrlPre.HTTP_URL + "/collect/sentenceEveryday?";
    private static final String cancelCollectUrl = HttpUrlPre.HTTP_URL + "/delete/collect/sentenceEveryday";
    //分享链接
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/sentenceEveryday/share";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;

    private NewDailySentenceActivity mContext;

    private List<SentenceBean> mList = new ArrayList<>();
    private SentenceDetailAdapter adapter;
    private LinearLayoutManager mLayoutManager;


    private long sentenceId = -1;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private int mPosition = -1;

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;
    private int type_share = -1;  //分享类型
    private String shareTitle = "";
    private String shareContent = "";

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
        setContentView(R.layout.activity_new_daily_sentence);

        mContext = this;

        initView();
        initIntentData();
        initData();
        initEvents();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    private void initIntentData() {
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
                    sentenceId = object.optLong("productId", -1L);

                    //上报点击事件
                    JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        } else {
            sentenceId = getIntent().getLongExtra("sentenceId", -1);
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!refreshing && !isEnd) {
                    getMoreData(newState);
                }
            }
        });
        adapter.setOnItemCollectionClick(new SentenceDetailAdapter.OnItemCollectionClick() {
            @Override
            public void onClick(int position) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else if (position != -1 && position < mList.size() && mPosition == -1) {
                    collectOrCancel(position);
                }
            }
        });
        adapter.setOnItemShareClick(new SentenceDetailAdapter.OnItemShareClick() {
            @Override
            public void onClick(int position) {
                if (position != -1 && position < mList.size() && mPosition == -1) {
                    showShareDialog(position);
                }
            }
        });
        adapter.setOnItemEditorClick(new SentenceDetailAdapter.OnItemEditorClick() {
            @Override
            public void onClick(int position) {
                if (position != -1 && position < mList.size() && mPosition == -1) {
                    turnToWritingPractice(position);
                }
            }
        });
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 前往写作练习
     *
     * @param position
     */
    private void turnToWritingPractice(int position) {
        String practice = mList.get(position).getContent();

        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", "");
        intent.putExtra("area", 5);
        intent.putExtra("type", 5);
        intent.putExtra("practiceType", 1);
        intent.putExtra("practice", practice);
        startActivity(intent);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            showLoadingView(true);
            refreshing = true;
            pageNum = pageNum + 1;
            new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(pageNum));
        }
    }

    private void initData() {
        showLoadingView(true);
        refreshing = true;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(pageNum));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("每日一句");

        frameLayout = findViewById(R.id.frame_new_daily_sentence);
        recyclerView = findViewById(R.id.recycler_view_new_daily_sentence);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new SentenceDetailAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
        //每次recyclerView滑动一个item
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * 显示加载视图
     *
     * @param show
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        refreshing = show;
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

    /**
     * 收藏或者取消收藏
     *
     * @param position
     */
    private void collectOrCancel(int position) {
        mPosition = position;
        boolean isCollect = mList.get(position).isCollectOrNot();
        if (isCollect) {
            cancelCollect();
        } else {
            collect();
        }
    }

    /**
     * 取消收藏
     */
    private void cancelCollect() {
        long sentenceId = mList.get(mPosition).getId();
        JSONArray array = new JSONArray();
        array.put(sentenceId);
        new DeleteCollection(mContext).execute(cancelCollectUrl, array.toString());
    }

    /**
     * 收藏
     */
    private void collect() {
        long sentenceId = mList.get(mPosition).getId();
        new CollectSentence(mContext)
                .execute(collectUrl + "id=" + sentenceId +
                        "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog(final int position) {
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
                                getShareHtml(TYPE_SHARE_WX_FRIEND, position);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS, position);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    getShareHtml(TYPE_SHARE_Weibo, position);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ, position);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone, position);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK, position);
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
     */
    private void getShareHtml(int type_share, int position) {
        this.type_share = type_share;

        if (position != -1 && position < mList.size()) {
            mPosition = position;
            showTips("正在准备分享内容...");
            long sentenceId = mList.get(position).getId();
            shareTitle = mList.get(mPosition).getAuthor();
            shareContent = mList.get(mPosition).getContent();
            new GetShareHtml(this).execute(shareUrl, String.valueOf(sentenceId));
            mPosition = -1;
        } else {
            showTips("分享失败，请稍后重试~");
            return;
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        SentenceBean sentence = new SentenceBean();
                        sentence.setId(object.optLong("id", -1));
                        if (object.getString("time").equals("")
                                || object.getString("time").equals("null")) {
                            sentence.setDate("2018/01/01");
                        } else {
                            sentence.setDate(DateUtil.date2YMD(object.getString("time")));
                        }
                        sentence.setAuthor(object.getString("author"));

                        sentence.setContent(object.getString("content"));
                        if (1 == object.optInt("status", 0)) {
                            sentence.setCollectOrNot(true);
                        } else {
                            sentence.setCollectOrNot(false);
                        }
                        mList.add(sentence);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("暂无每日一句内容");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("获取数据失败，请稍后重试~");
            tv_reload.setText("获取每日一句");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    initData();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后重试~");
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("无网络连接，请连接网络后重试~");
            tv_reload.setText("获取每日一句");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    initData();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("无网络连接，请稍后重试~");
        }
    }

    /**
     * 分析收藏数据
     *
     * @param s
     */
    private void analyzeCollectData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (mPosition != -1 && mPosition < mList.size()) {
                    mList.get(mPosition).setCollectOrNot(true);
                    adapter.notifyItemChanged(mPosition);
                    mPosition = -1;
                }
            } else {
                errorCollect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorCollect();
        }
    }

    /**
     * 收藏失败
     */
    private void errorCollect() {
        showTips("收藏失败，请稍后再试~");
        mPosition = -1;
    }

    /**
     * 分析取消收藏数据
     *
     * @param s
     */
    private void analyzeCancelCollectData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (mPosition != -1 && mPosition < mList.size()) {
                    mList.get(mPosition).setCollectOrNot(false);
                    adapter.notifyItemChanged(mPosition);
                    mPosition = -1;
                }
            } else {
                errorCancelCollect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorCancelCollect();
        }
    }

    /**
     * 取消收藏失败
     */
    private void errorCancelCollect() {
        showTips("取消收藏失败，请稍后再试~");
        mPosition = -1;
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
        showTips("分享失败，请稍后重试");
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    private static class GetData
            extends WeakAsyncTask<String, Void, String, NewDailySentenceActivity> {

        protected GetData(NewDailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewDailySentenceActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                if (activity.sentenceId != -1) {
                    object.put("sentenceId", activity.sentenceId);
                }
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
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
        protected void onPostExecute(NewDailySentenceActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 收藏
     */
    private static class CollectSentence
            extends WeakAsyncTask<String, Void, String, NewDailySentenceActivity> {

        protected CollectSentence(NewDailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewDailySentenceActivity activity, String[] strings) {
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
        protected void onPostExecute(NewDailySentenceActivity activity, String s) {
            if (s == null) {
                activity.errorCollect();
            } else {
                activity.analyzeCollectData(s);
            }
        }
    }

    /**
     * 取消收藏
     */
    private static class DeleteCollection
            extends WeakAsyncTask<String, Integer, String, NewDailySentenceActivity> {

        protected DeleteCollection(NewDailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewDailySentenceActivity activity, String[] params) {
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
        protected void onPostExecute(NewDailySentenceActivity activity, String s) {
            if (s == null) {
                activity.errorCancelCollect();
            } else {
                activity.analyzeCancelCollectData(s);
            }
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, NewDailySentenceActivity> {

        protected GetShareHtml(NewDailySentenceActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewDailySentenceActivity activity, String[] strings) {
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
        protected void onPostExecute(NewDailySentenceActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }
}
