package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.adapter.MicroLessonRecyclerViewAdapter;
import com.dace.textreader.audioUtils.OnPlayerEventListener;
import com.dace.textreader.bean.LessonBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideCircleTransform;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MIUI;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PermissionUtils;
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

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import me.biubiubiu.justifytext.library.JustifyTextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 微课详情
 */
public class MicroLessonActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/course/lesson/new";
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/course/";

    private ImageView iv_lesson_image;
    private TextView tv_lesson_name;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RelativeLayout rl_back;
    private ImageView iv_back;
    private RelativeLayout rl_share;
    private ImageView iv_share;
    private NestedScrollView scrollView;
    private JustifyTextView tv_lesson_description;
    private ImageView iv_teacher_image;
    private TextView tv_teacher_name;
    private JustifyTextView tv_teacher_description;
    private TextView tv_lesson_count;
    private RecyclerView recyclerView_lesson;
    private LinearLayout ll_status;
    private LinearLayout ll_lesson_explanation;
    private WebView webView_lesson_explanation;

    private RelativeLayout rl_to_top;

    //底部音频状态栏
    private ImageView iv_media_play;
    private ImageView iv_anim;
    private TextView tv_media_name;
    private LinearLayout ll_buy_lesson;
    private TextView tv_lesson_price;
    private TextView tv_lesson_original_price;

    private FrameLayout frameLayout;

    private MicroLessonActivity mContext;

    private boolean isBuy = false;
    private long lesson_id;
    private int lesson_status;  //课程状态，0为下架，1为持续更新上架，2为全上架
    private int lesson_count;  //课程课时;
    private String lesson_name;  //课程名称
    private double lesson_price;  //课程价格
    private double lesson_original_price;  //课程原价
    private String lesson_image;  //课程图片
    private String lesson_description;  //课程介绍
    private String lesson_explanation;  //课程说明
    private String teacher_image;  //课程老师的头像
    private String teacher_name;  //课程老师的名字
    private String teacher_description;  //课程老师的介绍
    private List<LessonBean> mList = new ArrayList<>();  //课程列表
    private MicroLessonRecyclerViewAdapter adapter;

    private int mediaPosition = -1;  //当前正在播放的课程的索引

    private boolean isTurnToLogin = false;  //是否登录

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;
    private int type_share = -1;  //分享类型
    private Bitmap lessonBitmap;
    private String urlForShare = "";

    private boolean isFromPlayService = false;

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
        setContentView(R.layout.activity_micro_lesson);

        mContext = this;

        initIntentData();
        initView();
        initData();
        initEvents();
        setImmerseLayout(toolbar);

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
                    lesson_id = object.optLong("productId", -1L);

                    isFromPlayService = false;

                    //上报点击事件
                    JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        } else {
            lesson_id = getIntent().getLongExtra("id", -1);
            isFromPlayService = getIntent().getBooleanExtra("isFromPlayService", false);
        }
    }

    // view为标题栏
    protected void setImmerseLayout(View view) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(getBaseContext());
        view.setPadding(0, statusBarHeight, 0, 0);
        rl_to_top.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayServiceListener();
        if (getPlayService() != null) {
            getPlayService().hideFloatView();
            if (NewMainActivity.lessonId == lesson_id) {
                int p = getPlayService().getPlayingPosition();
                if (p != -1 && mList.size() != 0) {
                    LessonBean lessonBean = mList.get(p);
                    tv_media_name.setText(lessonBean.getName());
                    updatePlayerList(p);
                }
                if (getPlayService().isPlaying()) {
                    iv_media_play.setImageResource(R.drawable.icon_media_player_pause);
                    GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
                } else {
                    iv_media_play.setImageResource(R.drawable.icon_media_player_start);
                    GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
                }

            }
        }
        if (isTurnToLogin) {
            if (NewMainActivity.STUDENT_ID != -1) {
                initData();
            }
            isTurnToLogin = false;
        }
    }

    private void initData() {
        showLoadingView();
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetLessonData(mContext)
                .execute(url, String.valueOf(lesson_id), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    private void initEvents() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -iv_lesson_image.getHeight() / 2) {
                    //修改状态栏为暗色主题
                    int flag = StatusBarUtil.StatusBarLightMode(mContext);
                    StatusBarUtil.StatusBarLightMode(mContext, flag);
                    iv_back.setImageResource(R.drawable.icon_back);
                    iv_share.setImageResource(R.drawable.bottom_share);
                    rl_to_top.setVisibility(View.VISIBLE);
                } else {
                    //修改状态栏为亮色主题
                    int flag = StatusBarUtil.StatusBarLightMode(mContext);
                    StatusBarUtil.StatusBarDarkMode(mContext, flag);
                    iv_back.setImageResource(R.drawable.icon_shadow_back);
                    iv_share.setImageResource(R.drawable.icon_share_shadow);
                    rl_to_top.setVisibility(View.GONE);
                }
            }
        });
        rl_back.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        iv_media_play.setOnClickListener(this);
        tv_media_name.setOnClickListener(this);
        ll_buy_lesson.setOnClickListener(this);
        rl_to_top.setOnClickListener(this);
        adapter.setOnMicroLessonItemListenClick(
                new MicroLessonRecyclerViewAdapter.OnMicroLessonItemListenClick() {
                    @Override
                    public void onItemClick(View view) {
                        int position = recyclerView_lesson.getChildAdapterPosition(view);
                        if (mediaPosition == position) {  //继续点同一课程不改变听课进度
                            turnToPlayer();
                        } else {
                            playLesson(position);
                        }
                    }
                });
    }

    /**
     * 查看课程是否可以听
     *
     * @param position
     */
    private void playLesson(int position) {
        if (isBuy) {  //已购买课程
            play(position);
        } else {  //未购买课程
            LessonBean lessonBean = mList.get(position);
            if (lessonBean.getFree() == 1) {  //可以试听
                play(position);
            } else {  //不能试听，只能购买后使用
                buyLesson();
            }
        }
    }

    /**
     * 播放课程
     *
     * @param position
     */
    private void play(int position) {
        if (getPlayService() != null) {
            if (mediaPosition == -1) {
                mediaPosition = position;
                getPlayService().play(mList, position);
            } else {
                mediaPosition = position;
                getPlayService().play(position);
            }
        }
        iv_media_play.setImageResource(R.drawable.icon_media_player_pause);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
        NewMainActivity.lessonId = lesson_id;
        NewMainActivity.lessonTitle = lesson_name;
        NewMainActivity.lessonContent = lesson_description;
        NewMainActivity.lessonPrice = lesson_price;
        NewMainActivity.lessonOriginalPrice = lesson_original_price;
        turnToPlayer();
    }

    private void initView() {
        appBarLayout = findViewById(R.id.app_bar_micro_lesson);
        toolbar = findViewById(R.id.toolbar_micro_lesson);
        rl_back = findViewById(R.id.rl_back_micro_lesson);
        iv_back = findViewById(R.id.iv_back_micro_lesson);
        rl_share = findViewById(R.id.rl_share_micro_lesson);
        iv_share = findViewById(R.id.iv_share_micro_lesson);
        frameLayout = findViewById(R.id.frame_micro_lesson);

        scrollView = findViewById(R.id.nested_scroll_view_micro_lesson);
        iv_lesson_image = findViewById(R.id.iv_lesson_image_micro_lesson);
        tv_lesson_name = findViewById(R.id.tv_lesson_name_micro_lesson);
        tv_lesson_description = findViewById(R.id.tv_lesson_description_micro_lesson);
        ll_lesson_explanation = findViewById(R.id.ll_lesson_explanation_micro_lesson);
        webView_lesson_explanation = findViewById(R.id.webView_lesson_explanation_micro_lesson);

        iv_teacher_image = findViewById(R.id.iv_teacher_image_micro_lesson);
        tv_teacher_name = findViewById(R.id.tv_teacher_name_micro_lesson);
        tv_teacher_description = findViewById(R.id.tv_teacher_description_micro_lesson);
        tv_lesson_count = findViewById(R.id.tv_lesson_hours_micro_lesson);
        ll_status = findViewById(R.id.ll_lesson_status_micro_lesson);
        recyclerView_lesson = findViewById(R.id.recycler_view_micro_lesson);
        recyclerView_lesson.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_lesson.setLayoutManager(layoutManager);
        adapter = new MicroLessonRecyclerViewAdapter(mContext, mList);
        recyclerView_lesson.setAdapter(adapter);

        rl_to_top = findViewById(R.id.rl_to_top_micro_lesson);

        iv_media_play = findViewById(R.id.iv_media_play_micro_bottom);
        iv_anim = findViewById(R.id.iv_voice_animator_micro_bottom);
        tv_media_name = findViewById(R.id.tv_media_name_micro_bottom);
        ll_buy_lesson = findViewById(R.id.ll_buy_lesson_micro_bottom);
        tv_lesson_price = findViewById(R.id.tv_lesson_price_micro_bottom);
        tv_lesson_original_price = findViewById(R.id.tv_lesson_normal_price_micro_bottom);
        tv_lesson_original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);

        initWebViewSetting();
    }

    //WebView设置
    private void initWebViewSetting() {

        WebSettings webSettings = webView_lesson_explanation.getSettings();
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
     * 显示加载等待视图
     */
    private void showLoadingView() {
        if (isDestroyed()) {
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
        frameLayout.removeAllViews();
        frameLayout.addView(view);
    }

    /**
     * 初始化服务播放音频播放进度监听器
     * 这个是要是通过监听即时更新主页面的底部控制器视图
     */
    public void initPlayServiceListener() {
        if (getPlayService() == null) {
            return;
        }
        getPlayService().setOnPlayEventListener(new OnPlayerEventListener() {
            /**
             * 切换歌曲
             * 主要是切换歌曲的时候需要及时刷新界面信息
             */
            @Override
            public void onChange(int position, LessonBean music) {
                mediaPosition = position;
                tv_media_name.setText(music.getName());
                updatePlayerList(position);
            }

            /**
             * 继续播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerStart() {
                iv_media_play.setImageResource(R.drawable.icon_media_player_pause);
                GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
            }

            /**
             * 暂停播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerPause() {
                iv_media_play.setImageResource(R.drawable.icon_media_player_start);
                GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
            }

            /**
             * 更新进度
             * 主要是播放音乐或者拖动进度条时，需要更新进度
             */
            @Override
            public void onUpdateProgress(int progress, int duration) {

            }

            @Override
            public void onBufferingUpdate(int percent) {

            }

        });
    }

    /**
     * 更新播放列表
     */
    private void updatePlayerList(int position) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setPlaying(false);
        }
        mList.get(position).setPlaying(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_micro_lesson:
                closeActivity();
                break;
            case R.id.rl_share_micro_lesson:
                showShareDialog();
                break;
            case R.id.iv_media_play_micro_bottom:
                if (mediaPosition == -1) {
                    if (mList.size() == 0) {
                        MyToastUtil.showToast(mContext, "课程列表为空");
                        return;
                    }
                    if (isBuy) {  //已购买课程
                        play(0);
                    } else {
                        LessonBean lessonBean = mList.get(0);
                        if (lessonBean.getFree() == 1) {  //可以试听
                            play(0);
                        } else {  //不能试听，只能购买后使用
                            buyLesson();
                        }
                    }
                } else {
                    if (getPlayService() != null) {
                        getPlayService().playPause();
                    }
                }
                break;
            case R.id.tv_media_name_micro_bottom:
                if (mediaPosition == -1) {
                    if (mList.size() == 0) {
                        MyToastUtil.showToast(mContext, "课程列表为空");
                        return;
                    }
                    if (isBuy) {  //已购买课程
                        play(0);
                    } else {
                        LessonBean lessonBean = mList.get(0);
                        if (lessonBean.getFree() == 1) {  //可以试听
                            play(0);
                        } else {  //不能试听，只能购买后使用
                            buyLesson();
                        }
                    }
                } else {
                    turnToPlayer();
                }
                break;
            case R.id.ll_buy_lesson_micro_bottom:
                buyLesson();
                break;
            case R.id.rl_to_top_micro_lesson:
                toTop();
                break;
        }
    }

    /**
     * 回到顶部
     */
    private void toTop() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                appBarLayout.setExpanded(true);
            }
        });
    }

    /**
     * 前往播放详情页
     */
    private void turnToPlayer() {
        Intent intent = new Intent(mContext, AudioPlayerActivity.class);
        intent.putExtra("fromMicro", true);
        startActivity(intent);
    }

    /**
     * 购买课程
     */
    private void buyLesson() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent = new Intent(mContext, SubmitReviewActivity.class);
            intent.putExtra("type", "lesson");
            intent.putExtra("lessonId", lesson_id);
            intent.putExtra("lessonPrice", lesson_price);
            intent.putExtra("lessonOriginalPrice", lesson_original_price);
            intent.putExtra("lessonTitle", lesson_name);
            intent.putExtra("lessonTeacher", teacher_name);
            intent.putExtra("lessonCount", mList.size());
            intent.putExtra("fromService", false);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        isTurnToLogin = true;
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                boolean isSubmit = data.getBooleanExtra("submit", false);
                if (isSubmit) {
                    buyLessonSuccess();
                }
            }
        }
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
                                    MyToastUtil.showToast(mContext, "请先安装微博");
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
     * 准备Bitmap
     */
    private void prepareBitmap() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = ImageUtils.GetNetworkBitmap(lesson_image);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.image_micro_lesson_default);
                }
                lessonBitmap = Bitmap.createScaledBitmap(bitmap,
                        108, 108, true);
                bitmap.recycle();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new GetShareHtml(mContext)
                    .execute(shareUrl, String.valueOf(lesson_id));
        }
    };

    /**
     * 获取分享链接
     *
     * @param type_share
     */
    private void getShareHtml(int type_share) {
        MyToastUtil.showToast(mContext, "正在准备分享内容...");
        this.type_share = type_share;
        if (urlForShare.equals("")) {  //还没获取过分享链接
            if (type_share == TYPE_SHARE_WX_FRIEND || type_share == TYPE_SHARE_WX_FRIENDS) {
                prepareBitmap();
            } else {
                new GetShareHtml(mContext)
                        .execute(shareUrl, String.valueOf(lesson_id));
            }
        } else {
            share(urlForShare);
        }
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(mContext, url, lesson_name, lesson_description, lesson_image);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(mContext, url, lesson_name, lesson_description, lesson_image);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {

        if (lessonBitmap == null) {
            lessonBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image_micro_lesson_default);
        }

        ShareUtil.shareToWx(mContext, url, lesson_name, lesson_description,
                ImageUtils.bmpToByteArray(lessonBitmap, true), friend);
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
                JSONObject data = jsonObject.getJSONObject("data");

                isBuy = data.getBoolean("bought");

                JSONObject object_teacher = data.getJSONObject("teacher");
                teacher_image = object_teacher.getString("userimg");
                teacher_name = object_teacher.getString("username");
                teacher_description = object_teacher.getString("description");
                NewMainActivity.lessonTeacher = teacher_name;

                JSONObject object_lesson = data.getJSONObject("course");
                lesson_name = object_lesson.getString("name");
                lesson_status = object_lesson.optInt("status", -1);
                lesson_count = object_lesson.optInt("hour", -1);
                lesson_description = object_lesson.getString("description");
                lesson_explanation = object_lesson.getString("specification");
                lesson_image = object_lesson.getString("img");
                lesson_price = object_lesson.getDouble("price");
                lesson_original_price = object_lesson.getDouble("costPrice");

                JSONArray array_lesson = data.getJSONArray("lessons");
                if (array_lesson.length() != 0) {
                    for (int i = 0; i < array_lesson.length(); i++) {
                        JSONObject lesson = array_lesson.getJSONObject(i);
                        LessonBean lessonBean = new LessonBean();
                        lessonBean.setId(lesson.getString("id"));
                        lessonBean.setName(lesson.getString("name"));
                        lessonBean.setDescription(lesson.getString("description"));
                        lessonBean.setMedia(lesson.getString("multiMedia"));
                        lessonBean.setDuration(lesson.getString("duration"));
                        String imagePath = lesson.getString("img");
                        lessonBean.setImage(imagePath);
                        if (isBuy) {
                            lessonBean.setFree(2);
                        } else {
                            lessonBean.setFree(lesson.optInt("free", 0));
                        }
                        lessonBean.setPlayNum(lesson.optInt("playBack", 0));
                        if (lesson.getString("status").equals("") ||
                                lesson.getString("status").equals("null")) {
                            lessonBean.setStatus(0);
                        } else {
                            lessonBean.setStatus(lesson.optInt("status", 0));
                        }
                        lessonBean.setPlaying(false);
                        mList.add(lessonBean);
                    }
                }
                updateUi();
            } else {
                errorConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 获取数据失败
     */
    private void errorConnect() {
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

    /**
     * 更新界面
     */
    private void updateUi() {
        if (isDestroyed()) {
            return;
        }
        if (isFromPlayService) {
            if (getPlayService() != null) {
                getPlayService().updatePlayList(mList);
            }
        }
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_placeholder_rectangle);
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(lesson_image)
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            iv_lesson_image.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            iv_lesson_image.setImageBitmap(resource);
                        }
                    });
        }
        tv_lesson_name.setText(lesson_name);
        String l_des = lesson_description + "\n";
        tv_lesson_description.setText(l_des);

        if (lesson_status == 0) {
            tv_lesson_count.setText("已下架");
        } else if (lesson_status == 1) {
            String text = "预计更新" + String.valueOf(lesson_count) + "节";
            tv_lesson_count.setText(text);
            ll_status.setVisibility(View.VISIBLE);
        } else if (lesson_status == 2) {
            String text = String.valueOf(lesson_count) + "节课";
            tv_lesson_count.setText(text);
        } else {
            tv_lesson_count.setVisibility(View.GONE);
        }

        if (!isDestroyed()) {
            RequestOptions options_teacher = new RequestOptions()
                    .placeholder(R.drawable.image_teacher)
                    .error(R.drawable.image_teacher)
                    .transform(new GlideCircleTransform(mContext));
            Glide.with(mContext)
                    .load(teacher_image)
                    .apply(options_teacher)
                    .into(iv_teacher_image);
        }
        tv_teacher_name.setText(teacher_name);
        String t_des = teacher_description + "\n";
        tv_teacher_description.setText(t_des);

        adapter.notifyDataSetChanged();

        if (lesson_explanation.equals("") || lesson_explanation.equals("null")) {
            ll_lesson_explanation.setVisibility(View.GONE);
        } else {
            if (webView_lesson_explanation != null) {
                webView_lesson_explanation.loadDataWithBaseURL(HttpUrlPre.COMPANY_URL,
                        getHtmlData(lesson_explanation),
                        "text/html", "utf-8", null);
            }
        }

        if (isBuy || lesson_price == 0) {
            ll_buy_lesson.setVisibility(View.GONE);
        } else {
            String price = DataUtil.double2String(lesson_price) + "派豆";
            tv_lesson_price.setText(price);
            if (lesson_original_price == 0 || lesson_original_price == -1) {
                tv_lesson_original_price.setVisibility(View.GONE);
            } else {
                String original_price = DataUtil.double2String(lesson_original_price) + "派豆";
                tv_lesson_original_price.setText(original_price);
                tv_lesson_original_price.setVisibility(View.VISIBLE);
            }
        }

        if (lesson_id == NewMainActivity.lessonId) {
            if (getPlayService() != null) {
                mediaPosition = getPlayService().getPlayingPosition();

                if (mediaPosition != -1 && mList.size() != 0) {
                    LessonBean lessonBean = mList.get(mediaPosition);
                    tv_media_name.setText(lessonBean.getName());
                    if (getPlayService().isPlaying()) {
                        iv_media_play.setImageResource(R.drawable.icon_media_player_pause);
                        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
                    } else if (getPlayService().isPausing()) {
                        iv_media_play.setImageResource(R.drawable.icon_media_player_start);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_anim, iv_anim);
                    }
                    updatePlayerList(mediaPosition);
                }
            }
        }

        frameLayout.setVisibility(View.GONE);
        frameLayout.removeAllViews();
    }

    /**
     * 购买成功
     */
    private void buyLessonSuccess() {
        isBuy = true;
        ll_buy_lesson.setVisibility(View.GONE);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setFree(2);
        }
        adapter.notifyDataSetChanged();
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

        if (lessonBitmap == null) {
            lessonBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image_micro_lesson_default);
        }

        ShareUtil.shareToWeibo(shareHandler, url, lesson_name, lesson_description, lessonBitmap);

    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(mContext, "分享失败，请稍后重试");
    }


    //获取完整的Html源码--正文
    private String getHtmlData(String bodyHtml) {
        String head = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>";
        String style = "<style> body{background:" + "#ffffff" + ";" +
                "line-height: " + "1.8" + ";" +
                "font-size:" + "0.92rem" + ";" +
                "padding:  0em 0.2em 0em 0.2em;  " +
                "text-align:justify;text-justify:distribute;" +
                "-webkit-text-size-adjust: none;} " +
                "p{line-height: " + "1.8" + ";" +
                "letter-spacing:0.7px;color:#333333;padding:8px 0;} " +
                "#content {" +
                "display:inline-block;" +
                "left:50%; " +
                "position:relative; " +
                "-webkit-transform: translateX(-50%);" +
                "-moz-transform: translateX(-50%);" +
                "-ms-transform: translateX(-50%);" +
                "-o-transform: translateX(-50%);" +
                "transform: translateX(-50%);" +
                "}" +
                "</style>";
        String body = "</head>\n" +
                " <body>";
        String div = "<div  style=\" " +
                "position:relative;\">";
        String over_1 = "</div>";
        String over_2 = "</body>" +
                "</html>";
        return head + style + body + div + bodyHtml + over_1 + over_2;
    }

    /**
     * 获取课程数据
     */
    private static class GetLessonData
            extends WeakAsyncTask<String, Void, String, MicroLessonActivity> {

        protected GetLessonData(MicroLessonActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MicroLessonActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("studentId", strings[2]);
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
        protected void onPostExecute(MicroLessonActivity activity, String s) {
            if (s == null) {
                activity.errorConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, MicroLessonActivity> {

        protected GetShareHtml(MicroLessonActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MicroLessonActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("name", "share_course");
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
        protected void onPostExecute(MicroLessonActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * 关闭界面
     */
    private void closeActivity() {
        if (getPlayService() != null) {
            if (getPlayService().isPreparing() || getPlayService().isPlaying()) {
                requestFloatPermission();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 判断悬浮窗权限
     */
    private void requestFloatPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                showReqOverlaysPermissionDialog();
            } else {
                finish();
            }
        } else {
            if (MIUI.rom()) {
                if (PermissionUtils.hasPermission(mContext)) {
                    finish();
                } else {
                    showReqOverlaysPermissionDialog();
                }
            } else {
                finish();
            }
        }
    }

    /**
     * 显示权限申请弹窗
     */
    private void showReqOverlaysPermissionDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_guide_settings_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView iv_guide = holder.getView(R.id.iv_guide_setting_dialog);
                        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_guide_setting, iv_guide);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_guide_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_guide_dialog);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    turnToSettings();
                                } else {
                                    MIUI.req(mContext);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(68)
                .show(getSupportFragmentManager());
    }

    /**
     * 跳转到权限设置
     */
    private void turnToSettings() {
        //启动Activity让用户授权
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (webView_lesson_explanation != null) {
            webView_lesson_explanation.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_lesson_explanation.clearHistory();

            ((ViewGroup) webView_lesson_explanation.getParent()).removeView(webView_lesson_explanation);
            webView_lesson_explanation.destroy();
            webView_lesson_explanation = null;
        }
        if (lessonBitmap != null) {
            lessonBitmap.recycle();
            lessonBitmap = null;
        }
        if (getPlayService() != null) {
            if (getPlayService().isPausing() || getPlayService().isDefault()) {
                getPlayService().hideFloatView();
            } else {
                getPlayService().showFloatView();
            }
            getPlayService().setOnPlayEventListener(null);
        }
        super.onDestroy();
    }

}
