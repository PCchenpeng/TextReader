package com.dace.textreader.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.AfterReadingActivity;
import com.dace.textreader.activity.AfterReadingDetailActivity;
import com.dace.textreader.activity.AuthorActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.MoreClassesArticleActivity;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.WordExplainActivity;
import com.dace.textreader.activity.WriteAfterReadingActivity;
import com.dace.textreader.adapter.AfterReadingRecyclerViewAdapter;
import com.dace.textreader.adapter.ArticleReaderRecyclerViewAdapter;
import com.dace.textreader.bean.AfterReading;
import com.dace.textreader.bean.Article;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.Player;
import com.dace.textreader.util.ScreenListener;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.Utils;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.shuyu.action.web.ActionSelectListener;
import com.shuyu.action.web.CustomActionWebView;
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
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/4/12 0012 上午 10:39.
 * Version   1.0;
 * Describe :  文章详情的正文部分
 * History:
 * ==============================================================================
 */

public class ArticleTextFragment extends Fragment implements View.OnClickListener {

    //获取课外文章内容
    private static final String readerUrl = HttpUrlPre.HTTP_URL + "/essays/query?";
    //收藏文章
    private static final String collectionUrl = HttpUrlPre.HTTP_URL + "/essays/collect?";
    //取消收藏
    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/essays/collect/delete";
    //更新浏览次数
    private final String viewsUrl = HttpUrlPre.HTTP_URL + "/statistics/article/update/pv?";
    //获取读后感数量
    private final String afterReadingUrl = HttpUrlPre.HTTP_URL + "/perusal/essayFeeling?";
    //加密的分享链接
    private final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/essay/";
    //添加摘抄
    private final String addExcerptUrl = HttpUrlPre.HTTP_URL + "/personal/summary/insert";
    //课外阅读
    private final String moreReaderListUrl = HttpUrlPre.HTTP_URL + "/recommendation?";
    //开始阅读
    private final String startReadUrl = HttpUrlPre.HTTP_URL + "/statistics/start/update?";
    //结束时间
    private final String endReadUrl = HttpUrlPre.HTTP_URL + "/statistics/end/update?";
    //读后感
    private static final String afterReadingListUrl = HttpUrlPre.HTTP_URL + "/essay/feeling?";
    //写读后感
    private final String addAfterReadingUrl = HttpUrlPre.HTTP_URL + "/personal/feeling/insert?";
    //点赞读后感
    private static final String updateLikeUrl = HttpUrlPre.HTTP_URL + "/personal/feeling_num/update?";

    private View view;

    private CoordinatorLayout rl_view;
    private AppBarLayout appBarLayout;
    //顶部操作栏
    private LinearLayout ll_page_top;
    private RelativeLayout rl_back;
    private View view_top;
    private LinearLayout ll_top;
    private RelativeLayout rl_setting;
    private ImageView iv_collection;
    private RelativeLayout rl_collection;
    private RelativeLayout rl_share;
    private TextView tv_next;

    //状态视图
    private FrameLayout frameLayout;

    private NestedScrollView scrollView;

    //重要内容
    private TextView tv_title;
    private CustomActionWebView webView_content;

    private TextView tv_write_after_reading;
    private RecyclerView recyclerView_after_reading;
    private TextView tv_after_reading_number;

    //可选内容
    private TextView tv_author;

    private RelativeLayout rl_guide;
    private JustifyTextView tv_guide;

    private RelativeLayout rl_media;
    private ImageView iv_play_media;
    private SeekBar seekBar_media;
    private TextView tv_cur_media;
    private TextView tv_max_media;

    private LinearLayout ll_more_reader;
    private TextView tv_more_reader;
    private RecyclerView recyclerView_more_reader;

    private LinearLayout ll_appreciation;
    private ImageView iv_appreciation;
    private CustomActionWebView webView_appreciation;

    private LinearLayout ll_background;
    private ImageView iv_background;
    private CustomActionWebView webView_background;

    //文章相关
    private boolean isClasses = false;  //是否是课内文章
    private long essayId = -1;  //文章ID
    private int essayType = -1;  //文章类型
    private String essayTitle = "";  //文章标题
    private String essayAuthor = ""; //文章作者
    private String essayGuide = "";  //文章导读
    private String essayContent = "";  //文章内容
    private String essayContentText = "";  //文章的纯文本内容
    private String essayAppreciation = "";  //文章鉴赏信息
    private String essayBackground = "";  //文章背景信息
    private String essayShareContent = "";  //文章分享内容
    private String essayMediaUrl = "";  //文章音频地址
    private String essayChildMediaUrl = "";  //童声音频
    private int isMachineMedia = 0;  //是否是机器音频
    private int nextEssayId = -1;//下一篇的文章ID
    private String afterReadingId = "";  //读后感ID
    private int commentCount = -1;  //评论数量
    //用户操作相关
    private boolean collectOrNot = false;  //收藏与否
    private boolean isMp3 = false;  //是否是mp3文件
    private Player mPlayer;  //音频播放控制
    private String mediaUrl = "";
    private boolean hasExist = false;  //控制器是否存在音频资源
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //语音状态，-1为未播放，0为暂停，1为播放中，2为播放完成
    private int synthesizer_status = -1;

    //读后感列表相关
    private List<AfterReading> mList_after_reading = new ArrayList<>();
    private AfterReadingRecyclerViewAdapter adapter_after_reading;
    private int mSelectedAfterReaderPos = -1;

    //推荐文章列表相关
    private List<Article> mList_reader = new ArrayList<>();
    private ArticleReaderRecyclerViewAdapter adapter_reader;

    //自定义ActionItem
    private List<String> actionItemList = new ArrayList<>();

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;  //微博
    private int type_share = -1;  //分享类型

    private WbShareHandler shareHandler;

    private Float[] audioSpeed = new Float[]{0.8f, 0.9f, 1f, 1.15f, 1.3f, 1.5f};  //音频速率
    private int audioSpeedPosition = 2;
    private SharedPreferences sharedPreferences;
    private String[] textSize = new String[]{"1.0rem", "1.1rem", "1.4rem", "1.6rem", "1.8rem"};  //字体大小
    private String[] textShowSize = new String[]{"15", "16", "18", "20", "22"};
    private int textSizePosition = 1;
    private String[] textLineSpace = new String[]{"2.4", "2.2", "2.0"};  //行间距
    private int textLineSpacePosition = 1;
    private int screenLight = 100;  //屏幕亮度，0~255
    private String[] background = new String[]{"#FFFBE9", "#FFFFFF", "#EDEDF8", "#DCEBCE", "#EEDFC6"};  //背景色
    private int backgroundPosition = 1;

    private String articleShareUrl = "";

    private ScreenListener screenListener;
    private boolean isPlaying = false; //是否在播放，用来判断锁屏后是否继续播放

    private boolean isFirstStart = true;
    private boolean isFirstText = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_article_text, container, false);

        essayId = NewArticleDetailActivity.essayId;
        essayType = NewArticleDetailActivity.essayType;
        if (essayType == 10) {
            isClasses = true;
        }

        init();
        initView();
        initData();
        initCustomActionData();
        initEvents();
        initWebActionEvents();

        shareHandler = new WbShareHandler(getActivity());
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));

        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("text_setting", Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            audioSpeedPosition = sharedPreferences.getInt("audioSpeed", 2);
            textSizePosition = sharedPreferences.getInt("textSize", 1);
            textLineSpacePosition = sharedPreferences.getInt("textLineSpace", 1);
            screenLight = sharedPreferences.getInt("screenLight", 100);
            backgroundPosition = sharedPreferences.getInt("background", 1);

            Utils.saveScreenBrightness(getActivity(), screenLight);
        } else {
            screenLight = Utils.getSystemBrightness(getContext());
        }

        SharedPreferences firstStart = getContext().getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        isFirstStart = firstStart.getBoolean("article", true);
        isFirstText = firstStart.getBoolean("text", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取读后感数据
        new GetAfterReadingNumber(this)
                .execute(afterReadingUrl + "essayId=" + essayId);
    }

    /**
     * 显示第一次进入时的提示
     */
    public void showFirstStartTips() {
        TipsUtil tipsUtil = new TipsUtil(getContext());
        tipsUtil.showTipBelowView(rl_setting, "设置\n个性化体验");
        tipsUtil.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnTipsDismiss != null) {
                    mOnTipsDismiss.onDismiss();
                }
            }
        });
        SharedPreferences firstSP = getContext().getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstSP.edit();
        editor.putBoolean("article", false);
        editor.apply();
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 机器合成音
     */
    private void robotSpeak() {
        synthesizer_status = 1;
        iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
        setParam();
    }

    /**
     * 显示提示信息
     *
     * @param s
     */
    private void showTip(String s) {
        MyToastUtil.showToast(getContext(), s);
    }

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        if (mTts == null) {
            showTip("初始化失败，无法播放音频");
            return;
        }

        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "20");
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        int code = mTts.startSpeaking(essayContentText, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        } else if (mOnArticleMediaPlay != null) {
            mOnArticleMediaPlay.onPlay();
        }
    }

    /**
     * 继续播放
     */
    private void resumeSpeak() {
        synthesizer_status = 1;
        iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
        mTts.resumeSpeaking();
        if (mOnArticleMediaPlay != null) {
            mOnArticleMediaPlay.onPlay();
        }
    }

    /**
     * 暂停播放
     */
    private void stopSpeak() {
        synthesizer_status = 0;
        iv_play_media.setImageResource(R.drawable.icon_media_player_start);
        mTts.pauseSpeaking();
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            synthesizer_status = 1;
            iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
        }

        @Override
        public void onSpeakPaused() {
            synthesizer_status = 0;
            iv_play_media.setImageResource(R.drawable.icon_media_player_start);
        }

        @Override
        public void onSpeakResumed() {
            synthesizer_status = 1;
            iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            seekBar_media.setSecondaryProgress(percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            seekBar_media.setProgress(percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //播放完成
                synthesizer_status = 2;
            } else if (error != null) {
                //播放失败
            }
            iv_play_media.setImageResource(R.drawable.icon_media_player_start);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     * 初始化WebView菜单事件
     */
    private void initWebActionEvents() {
        webView_content.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        addExcerpt(s1);
                    }
                } else if (s.equals("复制")) {
                    copyText(s1);
                } else {
                    Intent intent = new Intent(getContext(), WordExplainActivity.class);

                    intent.putExtra("readId", NewArticleDetailActivity.readID);
                    intent.putExtra("word", s1);
                    intent.putExtra("bomb", 1);
                    if (essayType == 2 || essayType == 4) {
                        intent.putExtra("type", 1);
                    } else {
                        intent.putExtra("type", 0);
                    }

                    intent.putExtra("glossarySourceType", 1);
                    intent.putExtra("glossaryId", essayId);
                    intent.putExtra("glossaryType", essayType);
                    intent.putExtra("glossaryTitle", essayTitle);

                    startActivity(intent);
                }
            }
        });
        webView_appreciation.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        addExcerpt(s1);
                    }
                } else if (s.equals("复制")) {
                    copyText(s1);
                } else {
                    turnToWordExplain(s1);
                }
            }
        });
        webView_background.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        addExcerpt(s1);
                    }
                } else if (s.equals("复制")) {
                    copyText(s1);
                } else {
                    turnToWordExplain(s1);
                }
            }
        });
    }

    /**
     * 前往词语解释
     *
     * @param word
     */
    private void turnToWordExplain(String word) {
        Intent intent = new Intent(getContext(), WordExplainActivity.class);
        intent.putExtra("readId", NewArticleDetailActivity.readID);
        intent.putExtra("word", word);
        intent.putExtra("type", 0);
        intent.putExtra("bomb", 1);

        intent.putExtra("glossarySourceType", 1);
        intent.putExtra("glossaryId", essayId);
        intent.putExtra("glossaryType", essayType);
        intent.putExtra("glossaryTitle", essayTitle);
        startActivity(intent);
    }

    /**
     * 复制
     */
    private void copyText(String s) {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", s);
        if (cm != null) {
            cm.setPrimaryClip(cd);
            showTip("复制成功");
        }
    }

    /**
     * 初始化菜单选项
     */
    private void initCustomActionData() {
        actionItemList.add("炸词");
        actionItemList.add("摘抄");
        actionItemList.add("复制");
        webView_content.setActionList(actionItemList);
        webView_appreciation.setActionList(actionItemList);
        webView_background.setActionList(actionItemList);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        showLoadingView(true);
        if (essayId == -1) {
            errorLoading();
        } else {
            new GetData(this)
                    .execute(readerUrl + "type=" + essayType +
                            "&essayId=" + essayId + "&studentId=" + NewMainActivity.STUDENT_ID);
        }
    }

    /**
     * 初始化事件监听
     */
    private void initEvents() {
        rl_back.setOnClickListener(this);
        view_top.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_collection.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        iv_play_media.setOnClickListener(this);
        tv_write_after_reading.setOnClickListener(this);
        tv_after_reading_number.setOnClickListener(this);
        tv_more_reader.setOnClickListener(this);
        ll_appreciation.setOnClickListener(this);
        ll_background.setOnClickListener(this);

        seekBar_media.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isMp3) {
                    // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
                    if (hasExist) {
                        this.progress = progress * mPlayer.mediaPlayer.getDuration()
                                / seekBar.getMax();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isMp3) {
                    // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
                    mPlayer.mediaPlayer.seekTo(progress);
                    tv_cur_media.setText(DateUtil.formatterTime(progress));
                }
            }
        });
        mPlayer.setMediaPlayerFinish(new Player.MediaPlayerFinish() {
            @Override
            public void onFinish() {
                iv_play_media.setImageResource(R.drawable.icon_media_player_start);
            }
        });
        adapter_after_reading.setOnItemLikeClickListener(new AfterReadingRecyclerViewAdapter.OnAfterReadingLikeItemClick() {
            @Override
            public void onItemClick(int position) {
                like(position);
            }
        });
        screenListener = new ScreenListener(getContext());
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {
                if (isPlaying) {
                    play();
                }
            }

            @Override
            public void onUserPresent() {
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == DensityUtil.dip2px(getContext(), 0.1f)) {
                    showOperateView();
                } else {
                    hideOperateView();
                }
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (isFirstText && i1 > 240) {
                    isFirstText = false;
                    showFirstTips();
                }
            }
        });
    }

    /**
     * 隐藏操作栏
     */
    public void hideOperateView() {
        NewArticleDetailActivity activity = (NewArticleDetailActivity) getActivity();
        activity.hideOperateBar();

    }

    /**
     * 显示操作栏
     */
    public void showOperateView() {
        NewArticleDetailActivity activity = (NewArticleDetailActivity) getActivity();
        activity.showOperateBar();
    }

    /**
     * 显示第一次进入的提示
     */
    private void showFirstTips() {
        SharedPreferences firstSP = getContext().getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstSP.edit();
        editor.putBoolean("text", false);
        editor.apply();
        TipsUtil tipsUtil = new TipsUtil(getContext());
        tipsUtil.showTipWebView(view, "长按选择正文\n有惊喜");
        tipsUtil.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                TipsUtil tipsUtil = new TipsUtil(getContext());
                tipsUtil.showTipWebView(view, "正文段落\n双击试试");
            }
        });
    }

    /**
     * 点赞读后感
     *
     * @param position
     */
    private void like(int position) {
        if (mList_after_reading.get(position).isLiked() != 1) {
            new UpdateLikeData(this)
                    .execute(updateLikeUrl +
                            "id=" + mList_after_reading.get(position).getId() +
                            "&studentId=" + NewMainActivity.STUDENT_ID +
                            "&essayId=" + essayId + "&type=" + essayType +
                            "&title=" + essayTitle);
            int likerNum = mList_after_reading.get(position).getLikeNum() + 1;
            mList_after_reading.get(position).setLikeNum(likerNum);
            mList_after_reading.get(position).setLiked(1);

            Bundle bundle = new Bundle();
            bundle.putInt("isLiked", 1);
            bundle.putInt("likeNum", likerNum);
            adapter_after_reading.notifyItemChanged(position, bundle);

        } else {
            MyToastUtil.showToast(getContext(), "已点赞，不能再点");
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        rl_view = view.findViewById(R.id.rl_fragment_article_text);
        rl_view.setBackgroundColor(Color.parseColor(background[backgroundPosition]));
        appBarLayout = view.findViewById(R.id.app_bar_article_text);
        rl_back = view.findViewById(R.id.rl_back_article_text_fragment);
        view_top = view.findViewById(R.id.view_top_article_text_fragment);
        ll_top = view.findViewById(R.id.ll_top_article_text_fragment);
        rl_setting = view.findViewById(R.id.rl_setting_article_text_fragment);
        rl_collection = view.findViewById(R.id.rl_collection_article_text_fragment);
        iv_collection = view.findViewById(R.id.iv_collection_article_text_fragment);
        rl_share = view.findViewById(R.id.rl_share_article_text_fragment);
        tv_next = view.findViewById(R.id.tv_next_article_text_fragment);

        frameLayout = view.findViewById(R.id.frame_article_text_fragment);

        scrollView = view.findViewById(R.id.nested_scroll_view_article_text_fragment);

        tv_title = view.findViewById(R.id.tv_essay_title_article_text_fragment);
        webView_content = view.findViewById(R.id.webView_content_article_text_fragment);

        tv_write_after_reading = view.findViewById(R.id.tv_write_after_reading_article_text_fragment);
        recyclerView_after_reading = view.findViewById(R.id.recycler_view_after_reading_article_text_fragment);
        recyclerView_after_reading.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView_after_reading.setLayoutManager(layoutManager);
        adapter_after_reading = new AfterReadingRecyclerViewAdapter(getContext(), mList_after_reading);
        adapter_after_reading.setOnItemClickListener(new AfterReadingRecyclerViewAdapter.OnAfterReadingItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView_after_reading.getChildAdapterPosition(view);
                turnToAfterReadingDetail(pos);
            }
        });
        recyclerView_after_reading.setAdapter(adapter_after_reading);
        tv_after_reading_number = view.findViewById(R.id.tv_after_reading_number_article_text_fragment);

        tv_author = view.findViewById(R.id.tv_essay_author_article_text_fragment);
        tv_author.setMovementMethod(LinkMovementMethod.getInstance());

        rl_guide = view.findViewById(R.id.rl_guide_article_text_fragment);
        tv_guide = view.findViewById(R.id.tv_guide_article_text_fragment);

        rl_media = view.findViewById(R.id.rl_media_article_text_fragment);
        iv_play_media = view.findViewById(R.id.iv_player_classes_article);
        seekBar_media = view.findViewById(R.id.seek_bar_classes_article);
        tv_cur_media = view.findViewById(R.id.tv_cur_media_time);
        tv_max_media = view.findViewById(R.id.tv_total_media_time);
        //初始化音频控制器
        mPlayer = new Player(seekBar_media, tv_cur_media, tv_max_media);
        mPlayer.setPlayerSpeed(audioSpeed[audioSpeedPosition]);

        ll_more_reader = view.findViewById(R.id.ll_reader_article_text_fragment);
        tv_more_reader = view.findViewById(R.id.tv_more_reader_article_text_fragment);
        recyclerView_more_reader = view.findViewById(R.id.recycler_view_reader_article_text_fragment);
        recyclerView_more_reader.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView_more_reader.setLayoutManager(mLayoutManager);
        adapter_reader = new ArticleReaderRecyclerViewAdapter(getContext(), mList_reader);
        adapter_reader.setOnItemClickListener(
                new ArticleReaderRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        int position = recyclerView_more_reader.getChildAdapterPosition(view);
                        turnToReaderArticle(position);
                    }
                });
        recyclerView_more_reader.setAdapter(adapter_reader);

        ll_appreciation = view.findViewById(R.id.ll_appreciation_article_text_fragment);
        iv_appreciation = view.findViewById(R.id.iv_appreciation_article_text_fragment);
        webView_appreciation = view.findViewById(R.id.webView_appreciation_article_text_fragment);

        ll_background = view.findViewById(R.id.ll_background_article_text_fragment);
        iv_background = view.findViewById(R.id.iv_background_article_text_fragment);
        webView_background = view.findViewById(R.id.webView_background_article_text_fragment);

        initWebView();
    }

    /**
     * 初始化WebView的设置
     */
    private void initWebView() {
        webView_content.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.putExtra("readId", NewArticleDetailActivity.readID);
                    intent.putExtra("bomb", 0);
                    if (essayType == 2 || essayType == 4) {
                        intent.putExtra("type", 1);
                    } else {
                        intent.putExtra("type", 0);
                    }

                    intent.putExtra("glossarySourceType", 1);
                    intent.putExtra("glossaryId", essayId);
                    intent.putExtra("glossaryType", essayType);
                    intent.putExtra("glossaryTitle", essayTitle);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView_content.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    postRemoveLoading();
                }
            }
        });
        webView_content.linkJSInterface();
        webView_content.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        webView_content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView_content.getSettings().setLoadWithOverviewMode(true);
        webView_content.setHorizontalScrollBarEnabled(false);//禁止水平滚动
        webView_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView_content.getSettings().setDefaultTextEncodingName("utf-8");

        webView_appreciation.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.putExtra("readId", NewArticleDetailActivity.readID);
                    intent.putExtra("type", 0);
                    intent.putExtra("bomb", 0);

                    intent.putExtra("glossarySourceType", 1);
                    intent.putExtra("glossaryId", essayId);
                    intent.putExtra("glossaryType", essayType);
                    intent.putExtra("glossaryTitle", essayTitle);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView_appreciation.linkJSInterface();
        webView_appreciation.getSettings().setJavaScriptEnabled(true);
        webView_appreciation.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView_appreciation.getSettings().setDefaultTextEncodingName("utf-8");

        webView_background.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.putExtra("readId", NewArticleDetailActivity.readID);
                    intent.putExtra("type", 0);
                    intent.putExtra("bomb", 0);

                    intent.putExtra("glossarySourceType", 1);
                    intent.putExtra("glossaryId", essayId);
                    intent.putExtra("glossaryType", essayType);
                    intent.putExtra("glossaryTitle", essayTitle);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView_background.linkJSInterface();
        webView_background.getSettings().setJavaScriptEnabled(true);
        webView_background.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView_background.getSettings().setDefaultTextEncodingName("utf-8");
    }

    /**
     * 延迟移除loading
     */
    private void postRemoveLoading() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLoadingView(false);
        }
    };

    /**
     * 进入读后感详情页
     *
     * @param pos
     */
    private void turnToAfterReadingDetail(int pos) {
        mSelectedAfterReaderPos = pos;
        String id = mList_after_reading.get(pos).getId();
        Intent intent = new Intent(getContext(), AfterReadingDetailActivity.class);
        intent.putExtra("afterReadingId", id);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 || requestCode == 1) {
            if (data != null) {
                boolean isClickLike = data.getBooleanExtra("clickLikeOrNot", false);
                if (isClickLike && mSelectedAfterReaderPos != -1) {
                    int likeNum = mList_after_reading.get(mSelectedAfterReaderPos).getLikeNum() + 1;
                    mList_after_reading.get(mSelectedAfterReaderPos).setLikeNum(likeNum);
                    mList_after_reading.get(mSelectedAfterReaderPos).setLiked(1);
                    Bundle bundle = new Bundle();
                    bundle.putInt("isLiked", 1);
                    bundle.putInt("likeNum", likeNum);
                    adapter_after_reading.notifyItemChanged(mSelectedAfterReaderPos, bundle);
                    mSelectedAfterReaderPos = -1;
                }
                //获取读后感数据
                new GetAfterReadingNumber(this)
                        .execute(afterReadingUrl + "essayId=" + essayId);
            }
        } else if (requestCode == 2) {
            if (data != null) {
                String s = data.getStringExtra("content");
                int type = data.getIntExtra("type", 1);
                addAfterReading(s, type);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_article_text_fragment:
                closeActivity();
                break;
            case R.id.view_top_article_text_fragment:
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                break;
            case R.id.rl_setting_article_text_fragment:
                showSettingsDialog();
                break;
            case R.id.rl_collection_article_text_fragment:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    if (collectOrNot) {
                        unCollectArticle();
                    } else {
                        collectThisArticle();
                    }
                }
                break;
            case R.id.rl_share_article_text_fragment:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    showShareDialog();
                }
                break;
            case R.id.tv_next_article_text_fragment:
                overRead();
                if (nextEssayId == -1) {
                    MyToastUtil.showToast(getContext(), "没有下一篇了");
                } else {
                    nextArticle(nextEssayId);
                }
                break;
            case R.id.iv_player_classes_article:
                play();
                break;
            case R.id.tv_more_reader_article_text_fragment:
                Intent intent = new Intent(new Intent(getContext(), MoreClassesArticleActivity.class));
                intent.putExtra("essayId", essayId);
                intent.putExtra("essayType", essayType);
                startActivity(intent);
                break;
            case R.id.tv_write_after_reading_article_text_fragment:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    if (afterReadingId.equals("") || afterReadingId.equals("null")) {
                        writeAfterReading();
                    } else {
                        Intent intent_after_reading = new Intent(getContext(), AfterReadingDetailActivity.class);
                        intent_after_reading.putExtra("afterReadingId", afterReadingId);
                        intent_after_reading.putExtra("essayId", essayId);
                        intent_after_reading.putExtra("essayType", essayType);
                        intent_after_reading.putExtra("essayTitle", essayTitle);
                        startActivityForResult(intent_after_reading, 1);
                    }
                }
                break;
            case R.id.tv_after_reading_number_article_text_fragment:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    Intent intent_after_reading = new Intent(getContext(), AfterReadingActivity.class);
                    intent_after_reading.putExtra("essayId", essayId);
                    intent_after_reading.putExtra("essayType", essayType);
                    intent_after_reading.putExtra("essayTitle", essayTitle);
                    startActivity(intent_after_reading);
                }
                break;
            case R.id.ll_appreciation_article_text_fragment:
                if (webView_appreciation.getVisibility() == View.GONE) {
                    webView_appreciation.setVisibility(View.VISIBLE);
                    iv_appreciation.setImageResource(R.drawable.ic_expand_less_black_36dp);
                } else {
                    webView_appreciation.setVisibility(View.GONE);
                    iv_appreciation.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
                break;
            case R.id.ll_background_article_text_fragment:
                if (webView_background.getVisibility() == View.GONE) {
                    webView_background.setVisibility(View.VISIBLE);
                    iv_background.setImageResource(R.drawable.ic_expand_less_black_36dp);
                } else {
                    webView_background.setVisibility(View.GONE);
                    iv_background.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
                break;
        }
    }

    /**
     * 点击播放按钮
     */
    private void play() {
        if (isMp3) {
            if (hasExist) {
                if (mPlayer.mediaPlayer.isPlaying()) {
                    mPlayer.pause();
                    iv_play_media.setImageResource(R.drawable.icon_media_player_start);
                } else {
                    mPlayer.play();
                    iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
                    if (mOnArticleMediaPlay != null) {
                        mOnArticleMediaPlay.onPlay();
                    }
                }
            } else {
                iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
                mPlayer.playUrl(mediaUrl);
                hasExist = true;
                if (mOnArticleMediaPlay != null) {
                    mOnArticleMediaPlay.onPlay();
                }
            }
        } else {
            if (synthesizer_status == -1) {
                robotSpeak();
            } else if (synthesizer_status == 0) {
                resumeSpeak();
            } else if (synthesizer_status == 1) {
                stopSpeak();
            } else {
                robotSpeak();
            }
        }
    }

    /**
     * 显示设置对话框
     */
    private void showSettingsDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_text_setting)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        LinearLayout ll_audio_source = holder.getView(R.id.ll_audio_source_text_setting_dialog);
                        final TextView tv_audio_normal = holder.getView(R.id.tv_audio_normal_text_setting_dialog);
                        final TextView tv_audio_child = holder.getView(R.id.tv_audio_child_text_setting_dialog);
                        if (!isClasses && isMp3 && isMachineMedia != 0 && isMachineMedia != 2) {
                            ll_audio_source.setVisibility(View.VISIBLE);
                            if (mediaUrl.equals(essayMediaUrl)) {
                                tv_audio_normal.setSelected(true);
                                tv_audio_child.setSelected(false);
                            } else if (mediaUrl.equals(essayChildMediaUrl)) {
                                tv_audio_child.setSelected(true);
                                tv_audio_normal.setSelected(false);
                            }
                        } else {
                            ll_audio_source.setVisibility(View.GONE);
                        }
                        LinearLayout ll_audio_speed = holder.getView(R.id.ll_audio_speed_text_setting_dialog);
                        final TextView tv_audio_slow = holder.getView(R.id.tv_audio_speed_slow_text_setting_dialog);
                        final TextView tv_audio = holder.getView(R.id.tv_audio_speed_text_setting_dialog);
                        final TextView tv_audio_fast = holder.getView(R.id.tv_audio_speed_fast_text_setting_dialog);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            ll_audio_speed.setVisibility(View.GONE);
                        }
                        final RelativeLayout rl_size_min = holder.getView(R.id.rl_font_minify);
                        final ImageView iv_size_min = holder.getView(R.id.iv_font_minify);
                        final TextView tv_size = holder.getView(R.id.tv_font_size);
                        final RelativeLayout rl_size_max = holder.getView(R.id.rl_font_enlarge);
                        final ImageView iv_size_max = holder.getView(R.id.iv_font_enlarge);
                        final RelativeLayout rl_row_space_max = holder.getView(R.id.rl_row_spacing_max);
                        final RelativeLayout rl_row_space_normal = holder.getView(R.id.rl_row_spacing_normal);
                        final RelativeLayout rl_row_space_min = holder.getView(R.id.rl_row_spacing_min);
                        SeekBar seekBar = holder.getView(R.id.seek_bar_light_text);
                        final RelativeLayout rl_bg_one = holder.getView(R.id.rl_bg_one_text);
                        final RelativeLayout rl_bg_two = holder.getView(R.id.rl_bg_two_text);
                        final RelativeLayout rl_bg_three = holder.getView(R.id.rl_bg_three_text);
                        final RelativeLayout rl_bg_four = holder.getView(R.id.rl_bg_four_text);
                        final RelativeLayout rl_bg_five = holder.getView(R.id.rl_bg_five_text);

                        if (audioSpeedPosition == 0) {
                            tv_audio_slow.setTextColor(Color.parseColor("#999999"));
                        } else if (audioSpeedPosition == audioSpeed.length - 1) {
                            tv_audio_fast.setTextColor(Color.parseColor("#999999"));
                        }
                        tv_audio.setText(String.valueOf(audioSpeed[audioSpeedPosition]));
                        if (textSizePosition == 0) {
                            iv_size_min.setImageAlpha(80);
                        } else {
                            iv_size_min.setImageAlpha(255);
                        }
                        if (textSizePosition == 4) {
                            iv_size_max.setImageAlpha(80);
                        } else {
                            iv_size_max.setImageAlpha(255);
                        }
                        tv_size.setText(textShowSize[textSizePosition]);
                        rl_row_space_max.setSelected(false);
                        rl_row_space_normal.setSelected(false);
                        rl_row_space_min.setSelected(false);
                        if (textLineSpacePosition == 0) {
                            rl_row_space_max.setSelected(true);
                        } else if (textLineSpacePosition == 1) {
                            rl_row_space_normal.setSelected(true);
                        } else {
                            rl_row_space_min.setSelected(true);
                        }
                        seekBar.setProgress(screenLight);
                        rl_bg_one.setSelected(false);
                        rl_bg_two.setSelected(false);
                        rl_bg_three.setSelected(false);
                        rl_bg_four.setSelected(false);
                        rl_bg_five.setSelected(false);
                        if (backgroundPosition == 0) {
                            rl_bg_one.setSelected(true);
                        } else if (backgroundPosition == 1) {
                            rl_bg_two.setSelected(true);
                        } else if (backgroundPosition == 2) {
                            rl_bg_three.setSelected(true);
                        } else if (backgroundPosition == 3) {
                            rl_bg_four.setSelected(true);
                        } else {
                            rl_bg_five.setSelected(true);
                        }

                        tv_audio_normal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mediaUrl.equals(essayMediaUrl)) {
                                    mediaUrl = essayMediaUrl;
                                    iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
                                    mPlayer.playUrl(mediaUrl);
                                    hasExist = true;
                                    if (mOnArticleMediaPlay != null) {
                                        mOnArticleMediaPlay.onPlay();
                                    }
                                    tv_audio_normal.setSelected(true);
                                    tv_audio_child.setSelected(false);
                                }
                            }
                        });
                        tv_audio_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mediaUrl.equals(essayChildMediaUrl)) {
                                    mediaUrl = essayChildMediaUrl;
                                    iv_play_media.setImageResource(R.drawable.icon_media_player_pause);
                                    mPlayer.playUrl(mediaUrl);
                                    hasExist = true;
                                    if (mOnArticleMediaPlay != null) {
                                        mOnArticleMediaPlay.onPlay();
                                    }
                                    tv_audio_child.setSelected(true);
                                    tv_audio_normal.setSelected(false);
                                }
                            }
                        });
                        tv_audio_slow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (audioSpeedPosition != 0) {
                                    audioSpeedPosition = audioSpeedPosition - 1;
                                    tv_audio.setText(String.valueOf(audioSpeed[audioSpeedPosition]));
                                    if (mPlayer.mediaPlayer.isPlaying()) {
                                        mPlayer.changePlayerSpeed(audioSpeed[audioSpeedPosition]);
                                    } else {
                                        mPlayer.setPlayerSpeed(audioSpeed[audioSpeedPosition]);
                                    }
                                    if (audioSpeedPosition == 0) {
                                        tv_audio_slow.setTextColor(Color.parseColor("#999999"));
                                    } else {
                                        tv_audio_slow.setTextColor(Color.parseColor("#333333"));
                                    }
                                    if (audioSpeedPosition == audioSpeed.length - 1) {
                                        tv_audio_fast.setTextColor(Color.parseColor("#999999"));
                                    } else {
                                        tv_audio_fast.setTextColor(Color.parseColor("#333333"));
                                    }
                                }
                            }
                        });
                        tv_audio_fast.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (audioSpeedPosition != audioSpeed.length - 1) {
                                    audioSpeedPosition = audioSpeedPosition + 1;
                                    tv_audio.setText(String.valueOf(audioSpeed[audioSpeedPosition]));
                                    if (mPlayer.mediaPlayer.isPlaying()) {
                                        mPlayer.changePlayerSpeed(audioSpeed[audioSpeedPosition]);
                                    } else {
                                        mPlayer.setPlayerSpeed(audioSpeed[audioSpeedPosition]);
                                    }
                                    if (audioSpeedPosition == 0) {
                                        tv_audio_slow.setTextColor(Color.parseColor("#999999"));
                                    } else {
                                        tv_audio_slow.setTextColor(Color.parseColor("#333333"));
                                    }
                                    if (audioSpeedPosition == audioSpeed.length - 1) {
                                        tv_audio_fast.setTextColor(Color.parseColor("#999999"));
                                    } else {
                                        tv_audio_fast.setTextColor(Color.parseColor("#333333"));
                                    }
                                }
                            }
                        });
                        rl_size_min.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 0) {
                                    textSizePosition = textSizePosition - 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    loadH5View();
                                }
                                if (textSizePosition == 0) {
                                    iv_size_min.setImageAlpha(80);
                                } else {
                                    iv_size_min.setImageAlpha(255);
                                }
                                if (textSizePosition == 4) {
                                    iv_size_max.setImageAlpha(80);
                                } else {
                                    iv_size_max.setImageAlpha(255);
                                }
                            }
                        });
                        rl_size_max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 4) {
                                    textSizePosition = textSizePosition + 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    loadH5View();
                                }
                                if (textSizePosition == 0) {
                                    iv_size_min.setImageAlpha(80);
                                } else {
                                    iv_size_min.setImageAlpha(255);
                                }
                                if (textSizePosition == 4) {
                                    iv_size_max.setImageAlpha(80);
                                } else {
                                    iv_size_max.setImageAlpha(255);
                                }
                            }
                        });
                        rl_row_space_max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_max.isSelected()) {
                                    textLineSpacePosition = 0;
                                    rl_row_space_max.setSelected(true);
                                    rl_row_space_normal.setSelected(false);
                                    rl_row_space_min.setSelected(false);
                                    loadH5View();
                                }
                            }
                        });
                        rl_row_space_normal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_normal.isSelected()) {
                                    textLineSpacePosition = 1;
                                    rl_row_space_normal.setSelected(true);
                                    rl_row_space_max.setSelected(false);
                                    rl_row_space_min.setSelected(false);
                                    loadH5View();
                                }
                            }
                        });
                        rl_row_space_min.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_min.isSelected()) {
                                    textLineSpacePosition = 2;
                                    rl_row_space_max.setSelected(false);
                                    rl_row_space_normal.setSelected(false);
                                    rl_row_space_min.setSelected(true);
                                    loadH5View();
                                }
                            }
                        });
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                Utils.saveScreenBrightness(getActivity(), progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                screenLight = seekBar.getProgress();
                            }
                        });
                        rl_bg_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_one.isSelected()) {
                                    backgroundPosition = 0;
                                    rl_bg_one.setSelected(true);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeBg();
                                    loadH5View();
                                }
                            }
                        });
                        rl_bg_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_two.isSelected()) {
                                    backgroundPosition = 1;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(true);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeBg();
                                    loadH5View();
                                }
                            }
                        });
                        rl_bg_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_three.isSelected()) {
                                    backgroundPosition = 2;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(true);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeBg();
                                    loadH5View();
                                }
                            }
                        });
                        rl_bg_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_four.isSelected()) {
                                    backgroundPosition = 3;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(true);
                                    rl_bg_five.setSelected(false);
                                    changeBg();
                                    loadH5View();
                                }
                            }
                        });
                        rl_bg_five.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_five.isSelected()) {
                                    backgroundPosition = 4;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(true);
                                    changeBg();
                                    loadH5View();
                                }
                            }
                        });
                    }
                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    /**
     * 改变背景
     */
    private void changeBg() {
        rl_view.setBackgroundColor(Color.parseColor(background[backgroundPosition]));
    }


    /**
     * 写读后感
     */
    private void writeAfterReading() {
        Intent intent = new Intent(getContext(), WriteAfterReadingActivity.class);
        intent.putExtra("content", "");
        intent.putExtra("isPriviate", 1);
        startActivityForResult(intent, 2);
    }

    /**
     * 插入读后感数据
     *
     * @param s
     * @param isPrivate //是否公开，1表示公开，0表示私有
     */
    private void addAfterReading(String s, int isPrivate) {
        new InsertAfterReading(ArticleTextFragment.this)
                .execute(addAfterReadingUrl +
                        "studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId +
                        "&feeling=" + s +
                        "&isPrivate=" + isPrivate);
    }

    /**
     * 取消收藏这篇文章
     */
    private void unCollectArticle() {
        collectOrNot = false;
        iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
        JSONArray array = new JSONArray();
        array.put(essayId);
        new DeleteData(this).execute(deleteUrl, array.toString());
    }

    /**
     * 收藏这篇文章
     */
    private void collectThisArticle() {
        collectOrNot = true;
        iv_collection.setImageResource(R.drawable.bottom_collection_selected);
        new CollectArticle(this)
                .execute(collectionUrl + "essayId=" + essayId +
                        "&studentId=" + NewMainActivity.STUDENT_ID +
                        "&type=" + essayType);
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
                                if (WbSdk.isWbInstall(getContext())) {
                                    getShareHtml(TYPE_SHARE_Weibo);
                                } else {
                                    showTip("请先安装微博");
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
                .show(getActivity().getSupportFragmentManager());
    }

    /**
     * 获取分享网页
     *
     * @param type_share
     */
    private void getShareHtml(int type_share) {
        this.type_share = type_share;
        if (articleShareUrl.equals("")) {
            MyToastUtil.showToast(getContext(), "正在准备分享内容...");
            new GetShareHtml(this)
                    .execute(shareUrl, String.valueOf(essayType), String.valueOf(essayId));
        } else {
            share();
        }
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ() {
        ShareUtil.shareToQQ(getActivity(), articleShareUrl, essayTitle, essayShareContent,
                HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone() {
        ShareUtil.shareToQZone(getActivity(), articleShareUrl, essayTitle, essayShareContent,
                HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(getContext(), articleShareUrl, essayTitle, essayShareContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);

    }

    /**
     * 下一篇文章
     *
     * @param page 文章ID
     */
    private void nextArticle(long page) {
        essayId = page;
        if (mOnArticleNextClick != null) {
            mOnArticleNextClick.onNext(page);
        }
        seekBar_media.setSecondaryProgress(0);
        seekBar_media.setProgress(0);
        if (isMp3) {
            resetMediaPlayer();
        } else {
            if (mTts != null) {
                if (mTts.isSpeaking()) {
                    stopSpeak();
                }
            }
            synthesizer_status = -1;
        }

        resetData();

        initData();
    }

    /**
     * 重置数据
     */
    private void resetData() {
        essayTitle = "";  //文章标题
        essayAuthor = ""; //文章作者
        essayGuide = "";  //文章导读
        essayContent = "";  //文章内容
        essayContentText = "";  //文章的纯文本内容
        essayAppreciation = "";  //文章鉴赏信息
        essayBackground = "";  //文章背景信息
        essayShareContent = "";  //文章分享内容
        essayMediaUrl = "";  //文章音频地址
        essayChildMediaUrl = "";
        nextEssayId = -1;//下一篇的文章ID
        afterReadingId = "";  //读后感ID
        //用户操作相关
        collectOrNot = false;  //收藏与否
        isMp3 = false;  //是否是mp3文件
        hasExist = false;  //控制器是否存在音频资源

        mList_after_reading.clear();
        adapter_after_reading.notifyDataSetChanged();
        mList_reader.clear();
        adapter_reader.notifyDataSetChanged();
    }

    /**
     * 重置播放器
     */
    private void resetMediaPlayer() {
        iv_play_media.setImageResource(R.drawable.icon_media_player_start);
        hasExist = false;
        mPlayer.mediaPlayer.pause();
        seekBar_media.setProgress(0);
        seekBar_media.setSecondaryProgress(0);
    }

    /**
     * 添加摘抄
     *
     * @param excerpt 摘抄的内容
     */
    private void addExcerpt(String excerpt) {
        new AddExcerpt(this).execute(addExcerptUrl, excerpt, String.valueOf(essayId),
                String.valueOf(essayType), essayTitle);
    }

    /**
     * 跳转到课外阅读
     *
     * @param position
     */
    private void turnToReaderArticle(int position) {
        long essayId = mList_reader.get(position).getId();
        int type = mList_reader.get(position).getType();
        Intent intent = new Intent(getContext(), NewArticleDetailActivity.class);
        intent.putExtra("id", essayId);
        intent.putExtra("type", type);
        startActivity(intent);
        int num = mList_reader.get(position).getViews();
        mList_reader.get(position).setViews(num + 1);
        adapter_reader.notifyItemChanged(position);
    }

    /**
     * 前往作者界面
     *
     * @param str
     */
    private void showAuthorInfo(String str) {
        Intent intent_author = new Intent(getContext(), AuthorActivity.class);
        intent_author.putExtra("author", str);
        intent_author.putExtra("readId", NewArticleDetailActivity.readID);
        startActivity(intent_author);
    }

    /**
     * 统计开始阅读和更新浏览次数
     *
     * @param id   文章ID
     * @param type 文章类型
     */
    private void startRead(long id, int type) {
        if (NewMainActivity.STUDENT_ID != -1) {
            //统计开始阅读
            new StartRead(this)
                    .execute(startReadUrl + "studentId=" + NewMainActivity.STUDENT_ID +
                            "&articleId=" + id +
                            "&articleType=" + type);
            //更新浏览次数
            new UpdatePV(this)
                    .execute(viewsUrl + "articleId=" + id);
        }
    }

    /**
     * 用户阅读结束
     */
    private void overRead() {
        if (NewMainActivity.STUDENT_ID != -1 && NewArticleDetailActivity.readID != -1) {
            new OverRead().execute(endReadUrl + "id=" + NewArticleDetailActivity.readID);
        }
    }

    /**
     * UI更新
     */
    private void updateUI() {

        if (collectOrNot) {
            iv_collection.setImageResource(R.drawable.bottom_collection_selected);
        } else {
            iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
        }
        tv_title.setText(essayTitle);
        if (!essayAuthor.equals("") && !essayAuthor.equals("null")) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(essayAuthor);
            int start = 0;
            int end = 0;
            String[] strings = essayAuthor.split(" ");
            for (int i = 0; i < strings.length; i++) {
                final String str = strings[i];
                if (i > 0) {
                    start = end + 1;
                } else {
                    start = end;
                }
                end = start + str.length();
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        showAuthorInfo(str);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#3aa4f1"));
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv_author.setText(ssb);
            tv_author.setVisibility(View.VISIBLE);
        }
        if (!essayGuide.equals("") && !essayGuide.equals("null")) {
            essayGuide = essayGuide + "\n";
            tv_guide.setText(essayGuide);
            rl_guide.setVisibility(View.VISIBLE);
        }
        rl_media.setVisibility(View.VISIBLE);
        if (essayMediaUrl.contains(".mp3") || essayChildMediaUrl.contains(".mp3")) {
            isMp3 = true;
            if (isMachineMedia == 0 || isClasses) {
                mediaUrl = essayMediaUrl;
            } else {
                if (essayChildMediaUrl.contains("null") || essayMediaUrl.equals("")) {
                    mediaUrl = essayMediaUrl;
                } else {
                    mediaUrl = essayChildMediaUrl;
                }
            }
            tv_cur_media.setVisibility(View.VISIBLE);
            tv_max_media.setVisibility(View.VISIBLE);
            seekBar_media.setEnabled(true);
        } else {
            isMp3 = false;
            // 初始化合成对象
            mTts = SpeechSynthesizer.createSynthesizer(getContext(), mTtsInitListener);
            tv_cur_media.setVisibility(View.INVISIBLE);
            tv_max_media.setVisibility(View.INVISIBLE);
            seekBar_media.setEnabled(false);
        }

        loadH5View();

        startRead(essayId, essayType);

        //获取读后感数据
        new GetAfterReadingNumber(this)
                .execute(afterReadingUrl + "essayId=" + essayId);

        //获取文章推荐列表
        new ReaderList(this).execute(moreReaderListUrl + "essayId=" + essayId +
                "&type=" + essayType +
                "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    /**
     * 加载H5内容
     */
    private void loadH5View() {
        if (webView_content != null) {
            if (essayContent.equals("") || essayContent.equals("null")) {
                errorLoading();
                return;
            } else {
                webView_content.loadDataWithBaseURL(HttpUrlPre.COMPANY_URL, getHtmlData(essayContent),
                        "text/html", "utf-8", null);
            }
        }

        if (webView_appreciation != null) {
            if (!essayAppreciation.equals("") && !essayAppreciation.equals("null")) {
                webView_appreciation.loadDataWithBaseURL(HttpUrlPre.COMPANY_URL,
                        toHtmlData(essayAppreciation),
                        "text/html", "utf-8", null);
                ll_appreciation.setVisibility(View.VISIBLE);
            }
        }
        if (webView_background != null) {
            if (!essayBackground.equals("") && !essayBackground.equals("null")) {
                webView_background.loadDataWithBaseURL(HttpUrlPre.COMPANY_URL,
                        toHtmlData(essayBackground),
                        "text/html", "utf-8", null);
                ll_background.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Loading状态
     *
     * @param showLading
     */
    private void showLoadingView(boolean showLading) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (showLading) {
            ll_top.setVisibility(View.INVISIBLE);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_h5_layout, null);
            ImageView iv_loading = view.findViewById(R.id.iv_h5_loading);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_placeholder_h5, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            ll_top.setVisibility(View.VISIBLE);
            rl_setting.post(new Runnable() {
                @Override
                public void run() {
                    if (isFirstStart) {
                        isFirstStart = false;
                        showFirstStartTips();
                    }
                }
            });
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONObject data = json.getJSONObject("data");
                collectOrNot = data.getBoolean("collectOrNot");
                nextEssayId = data.optInt("next", -1);
                essayShareContent = data.getString("subContent");

                JSONObject essay = data.getJSONObject("essay");
                essayId = essay.optInt("id", -1);
                essayType = essay.optInt("type", -1);
                essayTitle = essay.getString("title");
                NewArticleDetailActivity.essayTitle = essayTitle;
                essayContent = essay.getString("content");
                essayContentText = essay.getString("contentText");
                essayMediaUrl = essay.getString("audio");
                isMachineMedia = essay.optInt("isMachine", 0);
                essayAuthor = essay.getString("author");
                commentCount = essay.optInt("commentNum", 0);
                if (mOnReceivedCommentData != null) {
                    mOnReceivedCommentData.onReceived(commentCount);
                }
                if (isClasses) {
                    essayGuide = essay.getString("guidance");
                } else {
                    essayChildMediaUrl = essay.getString("audioChild");
                    essayAppreciation = essay.getString("appreciation");
                    essayBackground = essay.getString("background");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (essayContent.equals("") || essayContent.equals("null")) {
            errorLoading();
        } else {
            updateUI();
        }

    }

    /**
     * 获取数据失败
     */
    private void errorLoading() {
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
     * 分析推荐阅读列表的数据
     *
     * @param s
     */
    private void analyzeReaderListData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Article article = new Article();
                    JSONObject object = jsonArray.getJSONObject(i);
                    article.setId(object.optLong("id", -1));
                    article.setType(object.optInt("type", -1));
                    article.setTitle(object.getString("title"));
                    article.setViews(object.optInt("pv", 0));
                    mList_reader.add(article);
                }
                adapter_reader.notifyDataSetChanged();
                ll_more_reader.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析获取读后感数量的数据
     *
     * @param s
     */
    private void analyzeAfterReadingNumber(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                int afterReadingNumber = object.optInt("feelingQuantity", 0);
                if (afterReadingNumber != 0) {
                    String info = "全部读后感" + afterReadingNumber + "条";
                    tv_after_reading_number.setText(info);
                } else {
                    tv_after_reading_number.setText("暂无读后感");
                    mList_after_reading.clear();
                    adapter_after_reading.notifyDataSetChanged();
                }
            } else {
                tv_after_reading_number.setText("暂无读后感");
                mList_after_reading.clear();
                adapter_after_reading.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            tv_after_reading_number.setText("暂无读后感");
        }
        new GetAfterReadingData(this)
                .execute(afterReadingListUrl +
                        "studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId +
                        "&pageNum=1&pageSize=3");
    }

    /**
     * 分析读后感列表数据
     *
     * @param s
     */
    private void analyzeAfterReadingData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject json = jsonObject.getJSONObject("data");
                afterReadingId = json.getString("self_feeling_id");
                JSONArray data = json.getJSONArray("feelings");
                if (data.length() != 0) {
                    mList_after_reading.clear();
                    for (int i = 0; i < data.length(); i++) {
                        AfterReading afterReading = new AfterReading();
                        JSONObject feeling = data.getJSONObject(i);
                        afterReading.setId(feeling.getString("id"));
                        afterReading.setUserImg(feeling.getString("userimg"));
                        afterReading.setUsername(feeling.getString("username"));
                        afterReading.setLikeNum(feeling.optInt("feelingLikeNum", 0));
                        afterReading.setFeeling(feeling.getString("feeling"));
                        afterReading.setLiked(feeling.optInt("likeOrNot", 0));
                        if (feeling.getString("feelingTime").equals("")
                                || feeling.getString("feelingTime").equals("null")) {
                            afterReading.setDate("2018-01-01 00:00");
                        } else {
                            afterReading.setDate(DateUtil.time2MD(feeling.getString("feelingTime")));
                        }
                        mList_after_reading.add(afterReading);
                    }
                    adapter_after_reading.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (afterReadingId.equals("") || afterReadingId.equals("null")) {
            tv_write_after_reading.setText("写读后感");
        } else {
            tv_write_after_reading.setText("我的读后感");
        }
    }

    /**
     * 分析写读后感的数据
     *
     * @param s
     */
    private void analyzeAddAfterReadingData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONObject data = json.getJSONObject("data");

                afterReadingId = data.getString("id");
                tv_write_after_reading.setText("我的读后感");

                if (mList_after_reading.size() < 3) {
                    AfterReading afterReading = new AfterReading();
                    afterReading.setId(afterReadingId);
                    afterReading.setUsername(NewMainActivity.USERNAME);
                    afterReading.setUserImg(NewMainActivity.USERIMG);
                    if (data.getString("feelingTime").equals("")
                            || data.getString("feelingTime").equals("null")) {
                        afterReading.setDate("2018-01-01 00:00");
                    } else {
                        afterReading.setDate(DateUtil.time2MD(data.getString("feelingTime")));
                    }
                    afterReading.setLiked(0);
                    afterReading.setLikeNum(data.optInt("feelingLikeNum", 0));
                    afterReading.setFeeling(data.getString("feeling"));
                    mList_after_reading.add(afterReading);
                    adapter_after_reading.notifyDataSetChanged();

                    String info = "全部读后感" + mList_after_reading.size() + "条";
                    tv_after_reading_number.setText(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析开始阅读数据
     *
     * @param s
     */
    private void analyzeStartReadData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                NewArticleDetailActivity.readID = jsonObject.optInt("data", -1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                articleShareUrl = jsonObject.getString("data");
                share();
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
     */
    private void share() {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false);
                break;
            case TYPE_SHARE_Weibo:
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                ShareUtil.shareToWeibo(shareHandler, articleShareUrl, essayTitle, essayShareContent, thumb);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ();
                break;
            case TYPE_SHARE_QZone:
                shareToQZone();
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(getContext(), articleShareUrl);
                break;
        }
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(getContext(), "分享失败，请稍后重试");
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
        String style = "<style> body{background:" + background[backgroundPosition] + ";" +
                "line-height: " + textLineSpace[textLineSpacePosition] + ";" +
                "font-family:Helvetica,华文细黑;" +
                "font-size:" + textSize[textSizePosition] + ";" +
                "padding:  0em 0.2em 0em 0.2em;  " +
                "text-align:justify;text-justify:distribute;" +
                "-webkit-text-size-adjust: none;} " +
                "p{line-height: " + textLineSpace[textLineSpacePosition] + ";" +
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
        String script_1 = "<script src=\"file:///android_asset/js/hammer.min.js\"></script>\n";
        String script_2 = "<script src=\"file:///android_asset/js/index.js\"></script>\n";
        String over_1 = "</div>";
        String over_2 = "</body>" +
                "</html>";
        return head + style + body + div + bodyHtml + script_1 + script_2 + over_1 + over_2;
    }

    //获取完整的Html源码--鉴赏、背景
    private String toHtmlData(String bodyHtml) {
        String head = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>";
        String style = "<style> body{background:" + background[backgroundPosition] + ";" +
                "line-height: " + textLineSpace[textLineSpacePosition] + ";" +
                "font-family:Helvetica,华文细黑;" +
                "font-size:" + textSize[textSizePosition] + ";" +
                "padding: 0.5em 0.8em 0.5em 0.8em;  " +
                "text-align:justify;text-justify:distribute;" +
                "margin: 0 auto;" +
                "-webkit-text-size-adjust: none;" +
                "min-height: 100%;} " +
                "p{line-height: 1.8;letter-spacing:0.7px;color:#333333;} " +
                "</style>";
        String body = "</head>\n" +
                " <body>";
        String script_1 = "<script src=\"file:///android_asset/js/hammer.min.js\"></script>\n";
        String script_2 = "<script src=\"file:///android_asset/js/index.js\"></script>\n";
        String over_2 = "</body>\n" +
                "</html>";
        return head + style + body + script_1 + bodyHtml + script_2 + over_2;
    }

    private static class GetData
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected GetData(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s == null) {
                fragment.errorLoading();
            } else {
                fragment.analyzeData(s);
            }
        }
    }

    /**
     * 更新浏览次数
     */
    private static class UpdatePV
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected UpdatePV(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {

        }
    }

    /**
     * 获取读后感的数量
     */
    private static class GetAfterReadingNumber
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected GetAfterReadingNumber(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeAfterReadingNumber(s);
            }
        }
    }

    /**
     * 文章推荐列表
     */
    private static class ReaderList
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected ReaderList(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeReaderListData(s);
            }
        }
    }

    /**
     * 收藏文章
     */
    private static class CollectArticle
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected CollectArticle(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s == null) {
                fragment.collectOrNot = false;
                fragment.iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
                MyToastUtil.showToast(fragment.getContext(), "收藏失败");
            } else {
                MyToastUtil.showToast(fragment.getContext(), "收藏成功");
            }
        }
    }

    /**
     * 取消收藏文章
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Integer, String, ArticleTextFragment> {

        protected DeleteData(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("essayIds", params[1]);
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s == null) {
                fragment.collectOrNot = true;
                fragment.iv_collection.setImageResource(R.drawable.bottom_collection_selected);
            } else {
                MyToastUtil.showToast(fragment.getContext(), "取消收藏");
            }
        }
    }

    /**
     * 添加摘抄内容
     */
    private static class AddExcerpt
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected AddExcerpt(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("summary", strings[1]);
                json.put("essayId", strings[2]);
                json.put("type", strings[3]);
                json.put("title", strings[4]);
                json.put("sourceType", 1);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s != null && s.contains("摘抄成功")) {
                MyToastUtil.showToast(fragment.getContext(), "摘抄成功");
            }
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected GetShareHtml(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("type", strings[1]);
                object.put("essayId", strings[2]);
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s == null) {
                fragment.errorShare();
            } else {
                fragment.analyzeShareData(s);
            }
        }
    }

    /**
     * 获取读后感数据
     */
    private static class GetAfterReadingData
            extends WeakAsyncTask<String, Integer, String, ArticleTextFragment> {

        protected GetAfterReadingData(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeAfterReadingData(s);
            }
        }
    }

    /**
     * 添加读后感
     */
    private static class InsertAfterReading
            extends WeakAsyncTask<String, Integer, String, ArticleTextFragment> {

        protected InsertAfterReading(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            //获取数据之后
            if (s == null) {
                fragment.showTip("添加读后感失败，请稍后再试");
            } else {
                fragment.analyzeAddAfterReadingData(s);
            }
        }
    }

    /**
     * 点赞读后感
     */
    private static class UpdateLikeData
            extends WeakAsyncTask<String, Integer, String, ArticleTextFragment> {

        protected UpdateLikeData(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArticleTextFragment fragment, String s) {

        }
    }

    /**
     * 开始阅读统计
     */
    private static class StartRead
            extends WeakAsyncTask<String, Void, String, ArticleTextFragment> {

        protected StartRead(ArticleTextFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ArticleTextFragment fragment, String[] strings) {
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
        protected void onPostExecute(ArticleTextFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeStartReadData(s);
            }
        }
    }

    /**
     * 结束阅读统计
     */
    private class OverRead extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /**
     * 关闭页面
     */
    private void closeActivity() {
        if (mOnArticleTextBackClick != null) {
            mOnArticleTextBackClick.onClick();
        }
    }

    public interface OnArticleTextBackClick {
        void onClick();
    }

    private OnArticleTextBackClick mOnArticleTextBackClick;

    public void setOnArticleTextBackClick(OnArticleTextBackClick onArticleTextBackClick) {
        this.mOnArticleTextBackClick = onArticleTextBackClick;
    }

    public interface OnArticleNextClick {
        void onNext(long nextPage);
    }

    private OnArticleNextClick mOnArticleNextClick;

    public void setOnArticleNextClick(OnArticleNextClick onArticleNextClick) {
        this.mOnArticleNextClick = onArticleNextClick;
    }

    public interface OnArticleMediaPlay {
        void onPlay();
    }

    private OnArticleMediaPlay mOnArticleMediaPlay;

    public void setOnArticleMediaPlay(OnArticleMediaPlay onArticleMediaPlay) {
        this.mOnArticleMediaPlay = onArticleMediaPlay;
    }

    public interface OnTipsDismiss {
        void onDismiss();
    }

    private OnTipsDismiss mOnTipsDismiss;

    public void setOnTipsDismiss(OnTipsDismiss onTipsDismiss) {
        this.mOnTipsDismiss = onTipsDismiss;
    }

    public interface OnReceivedCommentData {
        void onReceived(int count);
    }

    private OnReceivedCommentData mOnReceivedCommentData;

    public void setOnReceivedCommentData(OnReceivedCommentData onReceivedCommentData) {
        this.mOnReceivedCommentData = onReceivedCommentData;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayer != null) {
            isPlaying = mPlayer.mediaPlayer.isPlaying();
            mPlayer.pause();
            iv_play_media.setImageResource(R.drawable.icon_media_player_start);
        }
        if (mTts != null) {
            if (mTts.isSpeaking()) {
                stopSpeak();
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("audioSpeed", audioSpeedPosition);
        editor.putInt("textSize", textSizePosition);
        editor.putInt("textLineSpace", textLineSpacePosition);
        editor.putInt("screenLight", screenLight);
        editor.putInt("background", backgroundPosition);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        if (webView_content != null) {
            webView_content.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_content.clearHistory();

            ((ViewGroup) webView_content.getParent()).removeView(webView_content);
            webView_content.destroy();
            webView_content = null;
        }
        if (webView_appreciation != null) {
            webView_appreciation.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_appreciation.clearHistory();

            ((ViewGroup) webView_appreciation.getParent()).removeView(webView_appreciation);
            webView_appreciation.destroy();
            webView_appreciation = null;
        }
        if (webView_background != null) {
            webView_background.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_background.clearHistory();

            ((ViewGroup) webView_background.getParent()).removeView(webView_background);
            webView_background.destroy();
            webView_background = null;
        }
        if (screenListener != null) {
            screenListener.unregisterListener();
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        super.onDestroy();
    }
}
