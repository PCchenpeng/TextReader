package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.H5DataBean;
import com.dace.textreader.bean.WordDetailBean;
import com.dace.textreader.bean.WordListBean;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.CustomController;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.KeyboardUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.LineWrapLayout;
import com.dace.textreader.view.StatusBarHeightView;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ReadSpeedDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeCustomWebview;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeHandler;
import com.dace.textreader.view.weight.pullrecycler.mywebview.CallBackFunction;
import com.shuyu.action.web.ActionSelectListener;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.suke.widget.SwitchButton;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener{

    private String wordListUrl = HttpUrlPre.HTTP_URL_ + "/select/fire/word/list";
    private String wordDetailUrl = HttpUrlPre.HTTP_URL_ + "/select/fire/word/annotation";
    private String addWordUrl = HttpUrlPre.HTTP_URL_ + "/insert/raw/word";
    private String addNoteUrl = HttpUrlPre.HTTP_URL_ + "/insert/article/note";
    private String updateUrl = HttpUrlPre.HTTP_URL_ + "/update/article/note";
    private String collectUrl = HttpUrlPre.HTTP_URL_ + "/insert/essay/collect";
    private String deleteCollectUrl = HttpUrlPre.HTTP_URL_ + "/delete/essay/collect" ;
    private String essayId;
    private String title;
    private String shareLink;
    private Bitmap shareBitmap;
    private String shareImgUrl;
    private BridgeCustomWebview mWebview;
//    private AppBarLayout appBarLayout;
    private LinearLayout rl_bottom;
    private ScrollView scroll_view;
    private StatusBarHeightView statusView_top,statusView_top_copy;
    private RelativeLayout rl_back,rl_back_copy;
    private ImageView iv_collect,iv_collect_copy,iv_share,iv_share_copy,iv_day_night,iv_playvideo;
    private RelativeLayout rl_font,rl_night,rl_note,rl_appreciation,rl_day_night,rl_top;
    private LinearLayout rl_dialog_think;
    private TextView tv_cancle,tv_keep;
    private EditText et_think;
    private String[] textSize = new String[]{"1.0rem", "1.1rem", "1.4rem", "1.6rem", "1.8rem"};  //字体大小
    private String[] textShowSize = new String[]{"15", "16", "18", "20", "22"};
    private int textSizePosition = 1;
    private String[] textLineSpace = new String[]{"2.4", "2.2", "2.0"};  //行间距
    private int textLineSpacePosition = 1;
    private String[] background = new String[]{"#FFFFFF", "#FFFBE9", "#EDEDF8", "#DCEBCE"};  //背景色
    private int backgroundPosition = 1;
    private String readModule = "1";
    private String imgUrl;
    private ImageView iv_topimg;
    private H5DataBean h5DataBean;

    private List<String> actionItemList = new ArrayList<>();

    private String H5Content;
    private String selectText;
    private String thinkText;
    private int selectStart,selectEnd;
    private boolean isVideo;
//    private BDVideoView view_video;
    private boolean isCollected;
    private boolean isPageComplete;

    /**
     * 播放器
     */
    private MediaPlayer mPlayer;
    private Thread musicThread;

    private String url;

    private WbShareHandler shareHandler;

    private OnListDataOperateListen mListen;
    private String shareContent = "";
    private NiceVideoPlayer videoPlayer;
    private CustomController controller;

    private String audioUrl;
    private boolean hasPlay;
    private FrameLayout fm_exception;
    private String albumId;
    private String sentenceNum;
    private int format;
    private int keepNoteType = 1;

    private String note;
    private String noteId;

    private String authorId;
//    private ImageView juhua_loading;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articledetail);
        mPlayer = new MediaPlayer();
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
        initData();
        initView();
        initEvents();
        showLoading(fm_exception);

    }

    @SuppressLint("CheckResult")
    private void initView() {
        mWebview = findViewById(R.id.webview);
        scroll_view = findViewById(R.id.scroll_view);
        statusView_top = findViewById(R.id.statusView_top);
        statusView_top_copy = findViewById(R.id.statusView_top_copy);
        rl_back = findViewById(R.id.rl_back);
        rl_back_copy = findViewById(R.id.rl_back_copy);
        iv_collect = findViewById(R.id.iv_collect);
        iv_collect_copy = findViewById(R.id.iv_collect_copy);
        iv_share = findViewById(R.id.iv_share);
        iv_share_copy = findViewById(R.id.iv_share_copy);
        rl_font = findViewById(R.id.rl_font);
        rl_appreciation = findViewById(R.id.rl_appreciation);
        rl_night = findViewById(R.id.rl_night);
        rl_note = findViewById(R.id.rl_note);
        iv_day_night = findViewById(R.id.iv_day_night);
        rl_day_night = findViewById(R.id.rl_day_night);
        iv_topimg = findViewById(R.id.iv_topimg);
        rl_dialog_think = findViewById(R.id.rl_dialog_think);
        tv_cancle = findViewById(R.id.tv_cancle);
        tv_keep = findViewById(R.id.tv_keep);
        et_think = findViewById(R.id.et_think);
        iv_playvideo = findViewById(R.id.iv_playvideo);
        rl_top = findViewById(R.id.rl_top);
        videoPlayer = findViewById(R.id.videoplayer);
        fm_exception = findViewById(R.id.fm_exception);
//        juhua_loading = findViewById(R.id.juhua_loading);







        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        //让旋转动画一直转，不停顿的重点
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
//        juhua_loading.setAnimation(rotateAnimation);

        controller = new CustomController(this);
        controller.setOnScreenChangeListener(new CustomController.OnScreenChangeListener() {
            @Override
            public void onNormal() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        scroll_view.scrollTo(0,0);
                    }
                }, 500);

            }

            @Override
            public void onFullScreen() {

            }
        });
        GlideApp.with(this)
                .load(imgUrl)
                .into(controller.imageView());

        GlideApp.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
                .into(iv_topimg);

//        mWebview.setWebChromeClient(new WebChromeClient() {
//            //            @Override
//            public boolean onJsAlert(WebView view, String url, String message,
//                                     JsResult result) {
//                // TODO Auto-generated method stub
//                return super.onJsAlert(view, url, message, result);
//            }
//
//        });

        mWebview.setOnPageFinished(new BridgeCustomWebview.OnPageFinished() {
            @Override
            public void onPageFinished() {
                Log.e("onPageFinished","setOnPageFinished");
                refreshH5View();
                fm_exception.setVisibility(View.GONE);
            }
        });
        initWebSettings();


        mWebview.linkJSInterface();
        actionItemList.add("词云");
        actionItemList.add("摘抄");
        actionItemList.add("搜索");
        actionItemList.add("复制");
        mWebview.setActionList(actionItemList);

        rl_bottom = findViewById(R.id.rl_bottom);
//        essayId = "10032979";
        url = "https://check.pythe.cn/1readingModule/pyReadDetail0.html?platForm=android&fontSize=18px&readModule=1&py=1&studentId="+
                NewMainActivity.STUDENT_ID+"&gradeId="+NewMainActivity.GRADE_ID+"&lineHeight=2.4&isShare=0&version=3.2.6&backgroundColor=FFFFFF&essayId="+DataEncryption.encode(essayId);
        mWebview.loadUrl(url);

    }



    @SuppressLint("SetJavaScriptEnabled")
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

    private void initData() {
        imgUrl = getIntent().getStringExtra("imgUrl");
        essayId = getIntent().getStringExtra("essayId");
//        isVideo = getIntent().getBooleanExtra("isVideo",false);

        textLineSpacePosition = (int) PreferencesUtil.getData(this,"textLineSpacePosition",0);
        textSizePosition = (int) PreferencesUtil.getData(this,"textSizePosition",0);
        backgroundPosition = (int) PreferencesUtil.getData(this,"backgroundPosition",0);
        readModule =  PreferencesUtil.getData(this,"readModule","1").toString();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvents() {

        rl_back.setOnClickListener(this);
        rl_back_copy.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_collect_copy.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_share_copy.setOnClickListener(this);
        rl_font.setOnClickListener(this);
        rl_night.setOnClickListener(this);
        rl_appreciation.setOnClickListener(this);
        rl_note.setOnClickListener(this);
        tv_keep.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
//        iv_playvideo.setOnClickListener(this);

        scroll_view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY > oldScrollY && ((scrollY - oldScrollY) > 30)){
//                    Log.e("ScrollType","上划");
                    statusView_top.setVisibility(View.GONE);
                    rl_bottom.setVisibility(View.GONE);
                }else if(oldScrollY > scrollY && (oldScrollY - scrollY) > 30){
//                    Log.e("ScrollType","下滑");
                    if(scrollY>statusView_top_copy.getHeight()){
                        statusView_top.setVisibility(View.VISIBLE);
                        rl_bottom.setVisibility(View.VISIBLE);
                    }else {
                        statusView_top.setVisibility(View.GONE);
                        rl_bottom.setVisibility(View.GONE);
                    }

                }
            }
        });

        et_think.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                ArticleDetailActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  ArticleDetailActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                screenHeight -= DensityUtil.getNavigationBarHeight(ArticleDetailActivity.this);
                int heightDifference = screenHeight - r.bottom;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rl_dialog_think.getLayoutParams();
                layoutParams.bottomMargin = heightDifference;
                rl_dialog_think.setLayoutParams(layoutParams);

            }

        });






        JSONObject params = new JSONObject();
        try {
            params.put("screen_height",DensityUtil.px2dip(this,DensityUtil.getScreenHeight(this)));
            params.put("screen_width",DensityUtil.px2dip(this,DensityUtil.getScreenWidth(this)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebview.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
            }
        });


        mWebview.registerHandler("getMoreTranslateInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("onPageFinished","registerHandler");
                Log.e("getMoreTranslateInfo", "指定Handler接收来自web的数据：" + data);
                String mData = data.replace("\\/","/");
                fm_exception.setVisibility(View.GONE);
                h5DataBean = GsonUtil.GsonToBean(mData,H5DataBean.class);
                if(h5DataBean != null){
                    isPageComplete = true;
                }

                authorId = h5DataBean.getAuthorId();
                if(h5DataBean.getVideo() != null){
                    videoPlayer.setVisibility(View.VISIBLE);
                }else {
                    videoPlayer.setVisibility(View.GONE);
                }

                if(imgUrl == null || imgUrl.equals("")){
                    imgUrl = h5DataBean.getImage();
                    GlideApp.with(ArticleDetailActivity.this)
                            .load(imgUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(iv_topimg);

                }

                title = h5DataBean.getTitle();
                if(h5DataBean.getMachineAudioList() != null && h5DataBean.getMachineAudioList().get(0) != null){
                    audioUrl = h5DataBean.getMachineAudioList().get(0).getAudio();
                }

                if(h5DataBean.getAlbum() != null){
                    albumId = h5DataBean.getAlbum().getId();
                    sentenceNum = h5DataBean.getAlbum().getSentenceNum();
                    format = h5DataBean.getAlbum().getFormat();
                }

                shareImgUrl = h5DataBean.getShareList().getWx().getImage();
                prepareBitmap(shareImgUrl);
                shareContent = h5DataBean.getSubContent();

                controller.setTitle(title);
                if(h5DataBean.getVideo() != null){
                    String videoUrl = h5DataBean.getVideo();
                    videoUrl = videoUrl.replaceAll("https","http");
                    videoPlayer.setUp(videoUrl, null);
                    videoPlayer.setController(controller);
                }
                isCollected = h5DataBean.getCollectOrNot() == 1;
                if(isCollected){
                    iv_collect.setImageResource(R.drawable.nav_icon_collect_select);
                    iv_collect_copy.setImageResource(R.drawable.icon_bg_collect_select);
                }else {
                    iv_collect.setImageResource(R.drawable.nav_icon_collect_default);
                    iv_collect_copy.setImageResource(R.drawable.icon_bg_collect_default);
                }
                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("authorDetails", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("h5IsPlayAudio", "指定Handler接收来自web的数据：" + data);

                if(authorId != null){
                    Intent intent = new Intent(ArticleDetailActivity.this,AuthorDetailActivity.class);
                    intent.putExtra("authorId",authorId);
                    startActivity(intent);
                }else {
                    MyToastUtil.showToast(ArticleDetailActivity.this,"亲,该作者暂无介绍哦");
                }


                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("h5IsPlayAudio", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("h5IsPlayAudio", "指定Handler接收来自web的数据：" + data);
                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("transportPara", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {

                try {
                    JSONObject params = new JSONObject(data);
                    Intent intent = new Intent(ArticleDetailActivity.this, ArticleDetailActivity.class);
                    intent.putExtra("essayId", params.getString("id"));
                    String imageUrl =  params.getString("imageUrl");
                    imageUrl = imageUrl.replace("\\/","/");
                    intent.putExtra("imgUrl",imageUrl);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("transportPara", "指定Handler接收来自web的数据：" + data);


                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("h5AlbumEvent", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Intent intent = new Intent(ArticleDetailActivity.this,ReaderTabAlbumDetailActivity.class);
                intent.putExtra("format",format);
                intent.putExtra("sentenceNum",sentenceNum);
                intent.putExtra("albumId",albumId);
                startActivity(intent);
            }
        });

        mWebview.registerHandler("evaluateReadSpeedWithResult", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if(!isLogin()){
                    toLogin();
                    return;
                }

                ReadSpeedDialog readSpeedDialog = new ReadSpeedDialog(ArticleDetailActivity.this,data);
                readSpeedDialog.show();
                Log.e("ReadSpeed", "指定Handler接收来自web的数据：" + data);
                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("toTranslatePage", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("toTranslatePage", "指定Handler接收来自web的数据：" + data);
                Intent intent = new Intent(ArticleDetailActivity.this,TranslatePageActivity.class);
                intent.putExtra("url",data);
                intent.putExtra("title",title);
                startActivity(intent);
                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("translateNoteAndIdToAPP", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject dataBean = new JSONObject(data);
                    note = dataBean.getString("note");
                    noteId = dataBean.getString("id");
                    showThinkDialog(note,noteId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("toTranslatePage", "指定Handler接收来自web的数据：" + data);
            }
        });

        mWebview.registerHandler("h5IsPlayAudio", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (data!=null && !data.equals("")){
                    if(audioUrl != null && !audioUrl.equals("")){
                        if(data.equals("needPlay")){
                            if(hasPlay){
                                mPlayer.start();
                            }else {
                                play(audioUrl);
                            }
                            hasPlay = true;
                        }else {
                            mPlayer.pause();
                        }
                    }
                }
                function.onCallBack("123");
            }
        });

        mWebview.registerHandler("shareOutFun", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("shareOutFun", "指定Handler接收来自web的数据：" + data);
                function.onCallBack("123");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String action = jsonObject.getString("txt");
                    if(action != null && !action.equals("")){
                        switch (action){
                            case "wx":
                                shareArticleToWX(true,h5DataBean.getShareList().getWx().getLink());
                                break;
                            case "wxquan":
                                shareArticleToWX(false,h5DataBean.getShareList().getWx().getLink());
                                break;
                            case "qq":
                                shareToQQ(h5DataBean.getShareList().getQq().getLink());
                                break;
                            case "more":
                                shareNote();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        mWebview.setContentListener(new BridgeCustomWebview.ContentListener() {
            @Override
            public void getContent(String content) {
                H5Content = replace(content);
            }
        });

        mWebview.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                Log.e("ActionSelectListener","s = "+s+"---"+"s1="+s1);
                selectText = s1;
                if(s.equals("词云")){
                    getWordList(s1);
                }else if(s.equals("摘抄")){
                    selectStart = H5Content.indexOf(s1);
                    selectEnd = selectStart + s1.length();
                    showNoteDialog(s1,selectStart,selectStart);

                }else if (s.equals("搜索")){
                    Intent intent = new Intent(ArticleDetailActivity.this,SearchResultActivity.class);
                    intent.putExtra("searchWord",s1);
                    startActivity(intent);
                }else if(s.equals("复制")){
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cd = ClipData.newPlainText("Label", s1);
                    if (cm != null) {
                        cm.setPrimaryClip(cd);
                        MyToastUtil.showToast(ArticleDetailActivity.this,"复制成功");
                    }
                }
            }
        });

    }

    private void showThinkDialog(final String note, final String id) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_article_think)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_delete = holder.getView(R.id.ll_delete);
                        LinearLayout ll_edit = holder.getView(R.id.ll_edit);
                        TextView tv_think = holder.getView(R.id.tv_think);

                        final JSONObject params = new JSONObject();
                        try {
                            params.put("note",note);
                            params.put("noteId",id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ll_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mWebview.callHandler("deleteReadNote", params.toString(), new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        dialog.dismiss();
                                        Log.e("deleteReadNote",data);

                                    }
                                });
                            }
                        });

                        ll_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isLogin()){
                                    toLogin();
                                    return;
                                }
                                dialog.dismiss();
                                keepNoteType = 2;
                                rl_dialog_think.setVisibility(View.VISIBLE);
                                KeyboardUtils.showKeyboard(rl_dialog_think);
                                et_think.setText(note);
                            }
                        });

                        tv_think.setText(note);

                    }


                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void showNoteDialog(final String s1,int start,int end) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_article_word_note)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_note = holder.getView(R.id.ll_note);
                        LinearLayout ll_think = holder.getView(R.id.ll_think);
                        TextView tv_text = holder.getView(R.id.tv_text);

                        ll_note.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isLogin()){
                                    toLogin();
                                    return;
                                }

                                addNote(s1);
                                dialog.dismiss();
                            }
                        });

                        ll_think.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isLogin()){
                                    toLogin();
                                    return;
                                }
                                dialog.dismiss();
                                keepNoteType = 1;
                                rl_dialog_think.setVisibility(View.VISIBLE);
                                KeyboardUtils.showKeyboard(rl_dialog_think);
                                et_think.setText("");
                            }
                        });

                        tv_text.setText(s1);

                    }


                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void addNote(String s1) {
        JSONObject params = new JSONObject();
        try {
            params.put("content",s1);
            params.put("essayId",essayId);
            params.put("category","1");
            params.put("essayTitle",title);
            params.put("note","");
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(addNoteUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        MyToastUtil.showToast(ArticleDetailActivity.this,"添加成功");
                    }else  if (jsonObject.getString("status").equals("400")){
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
//type 1 插入 2 更新

    private void keepNote() {
        if(keepNoteType == 1){
            try {
                JSONObject params = new JSONObject();
                params.put("text",selectText);
                params.put("note",thinkText);
                params.put("start",selectStart);
                params.put("end",selectEnd);
                mWebview.callHandler("setTextWithLine", params.toString(), new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        Log.e("setTextWithLine",data);
                    }
                });
                rl_dialog_think.setVisibility(View.GONE);
                KeyboardUtils.hideKeyboard(et_think);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(keepNoteType == 2){
            JSONObject params = new JSONObject();
            try {
                params.put("studentId",PreferencesUtil.getData(this,"studentId","-1").toString());
                params.put("noteId",noteId);
                params.put("note",thinkText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpManager.getInstance(this).requestAsyn(updateUrl, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
                @Override
                public void onReqSuccess(Object result) {
                    try {
                        JSONObject response = new JSONObject(result.toString());
                        if(response.getInt("status") == 200){
                            mWebview.callHandler("reloadNoteData","", new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.e("reloadNoteData",data);
                                }
                            });
                            rl_dialog_think.setVisibility(View.GONE);
                            KeyboardUtils.hideKeyboard(et_think);
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

    private void getWordList(String s1) {
        JSONObject params = new JSONObject();
        try {
            params.put("word",s1);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(wordListUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {
                WordListBean wordListBean = GsonUtil.GsonToBean(result.toString(),WordListBean.class);
                if(wordListBean != null & wordListBean.getData()!= null){
                    showWordDialog(wordListBean);
                }else {
                    MyToastUtil.showToast(ArticleDetailActivity.this,"没有数据");
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void showWordDialog(final WordListBean wordListBean) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_article_word)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        SwitchButton bt_switch = holder.getView(R.id.bt_swtich);
                        final LineWrapLayout lwy_word = holder.getView(R.id.lwy_word);

                        for (int i = 0;i < wordListBean.getData().getMix().size();i++){
                            View child = View.inflate(ArticleDetailActivity.this,R.layout.item_dialog_word,null);
                            TextView textView = child.findViewById(R.id.tv_num);
                            TextView textView1 = child.findViewById(R.id.tv_copy);
                            final String word = wordListBean.getData().getMix().get(i).getWord();
                            textView.setText(word);
                            textView1.setText(word);
                            lwy_word.addView(child);

                            final int finalI = i;
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    juhua_loading.setVisibility(View.VISIBLE);
                                    getWordDetail(word);
                                }
                            });
                        }

                        bt_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                                Log.e("OnCheckedChangeListener",String.valueOf(isChecked));
                                lwy_word.removeAllViews();
                                if(isChecked){
                                    for (int i = 0;i < wordListBean.getData().getBase().size();i++){
                                        View child = View.inflate(ArticleDetailActivity.this,R.layout.item_dialog_word,null);
                                        TextView textView = child.findViewById(R.id.tv_num);
                                        TextView textView1 = child.findViewById(R.id.tv_copy);
                                        final String word = wordListBean.getData().getBase().get(i).getWord();
                                        textView.setText(word);
                                        textView1.setText(word);
                                        lwy_word.addView(child);

                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                juhua_loading.setVisibility(View.VISIBLE);
                                                getWordDetail(word);
                                            }
                                        });
                                    }

                                }else {

                                    for (int i = 0;i < wordListBean.getData().getMix().size();i++){
                                        View child = View.inflate(ArticleDetailActivity.this,R.layout.item_dialog_word,null);
                                        TextView textView = child.findViewById(R.id.tv_num);
                                        TextView textView1 = child.findViewById(R.id.tv_copy);
                                        final String word = wordListBean.getData().getMix().get(i).getWord();
                                        textView.setText(word);
                                        textView1.setText(word);
                                        lwy_word.addView(child);

                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                juhua_loading.setVisibility(View.VISIBLE);
                                                getWordDetail(word);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void getWordDetail(String word) {
        JSONObject params = new JSONObject();
        try {
            params.put("word",word);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        }catch (Exception e){
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(wordDetailUrl, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                WordDetailBean wordDetailBean = GsonUtil.GsonToBean(result.toString(),WordDetailBean.class);
                if(wordDetailBean != null && wordDetailBean.getData() != null){
//                    juhua_loading.setVisibility(View.GONE);
                    showWordDetailDialog(wordDetailBean);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void showWordDetailDialog(final WordDetailBean wordDetailBean) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_article_word_detail)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView iv_back = holder.getView(R.id.iv_dialog_back);
                        TextView tv_word = holder.getView(R.id.tv_word);
                        LinearLayout ll_add = holder.getView(R.id.ll_add);
                        TextView tv_pinyin = holder.getView(R.id.tv_pinyin);
                        ImageView iv_audio = holder.getView(R.id.iv_audio);
                        TextView tv_checkdetail = holder.getView(R.id.tv_check_detail);
                        RecyclerView rcl_worddetail = holder.getView(R.id.rcl_worddetail);
                        WordDetailAdapter wordDetailAdapter = new WordDetailAdapter(ArticleDetailActivity.this,wordDetailBean.getData().getExplain(),wordDetailBean.getData().getWord());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ArticleDetailActivity.this,
                                LinearLayoutManager.VERTICAL, false);
                        rcl_worddetail.setLayoutManager(layoutManager);
                        rcl_worddetail.setAdapter(wordDetailAdapter);
                        tv_word.setText(wordDetailBean.getData().getWord());
                        tv_pinyin.setText(wordDetailBean.getData().getAudio().get(0).getPinyin());

                        iv_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        iv_audio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mPlayer.isPlaying()) {
                                    play(wordDetailBean.getData().getAudio().get(0).getUrl());
                                } else {
                                    mPlayer.pause();
                                }

                            }
                        });

                        tv_checkdetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ArticleDetailActivity.this,WordDetailActivity.class);
                                intent.putExtra("url",wordDetailBean.getData().getUrl());
                                intent.putExtra("essayId",essayId);
                                intent.putExtra("sourceType","1");
                                intent.putExtra("title",title);
                                intent.putExtra("word",wordDetailBean.getData().getWord());
                                startActivity(intent);
                            }
                        });

                        ll_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addWord(wordDetailBean.getData().getWord());
                            }
                        });


                    }
                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }


    //添加生词
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
                        MyToastUtil.showToast(ArticleDetailActivity.this,"添加成功");
                    }else  if (jsonObject.getString("status").equals("400")){
                        MyToastUtil.showToast(ArticleDetailActivity.this,"该词已在生词本中");
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

    class WordDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private Context context;
        private List<WordDetailBean.DataBean.ExplainBean> mData;
        private String word;

        public WordDetailAdapter(Context context , List<WordDetailBean.DataBean.ExplainBean> mData,String word){
            this.context = context;
            this.mData = mData;
            this.word = word;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_word_detail_explan, viewGroup, false);

            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            String exam = mData.get(i).getExam();
            if (exam != null){
                exam = exam.replace(word,"<font color=\"#FFF09C\">"+word+"</font>");
                ((ItemHolder)viewHolder).tv_exam.setText(Html.fromHtml(exam));
            }
            ((ItemHolder)viewHolder).tv_desc.setText(String.valueOf(i+1)+"、"+mData.get(i).getDesc());

        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        class ItemHolder extends RecyclerView.ViewHolder{

            TextView tv_exam;
            TextView tv_desc;
            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                tv_exam = itemView.findViewById(R.id.tv_exam);
                tv_desc = itemView.findViewById(R.id.tv_desc);
            }
        }
    }


    /**
     * 显示设置对话框
     */
    private void showSettingsDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_articledetail_font)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        final RelativeLayout rl_size_min = holder.getView(R.id.rl_font_minify);
                        final ImageView iv_size_min = holder.getView(R.id.iv_font_minify);
                        final TextView tv_size = holder.getView(R.id.tv_font_size);
                        final RelativeLayout rl_size_max = holder.getView(R.id.rl_font_enlarge);
                        final ImageView iv_size_max = holder.getView(R.id.iv_font_enlarge);
                        final RelativeLayout rl_row_space_max = holder.getView(R.id.rl_row_spacing_max);
                        final RelativeLayout rl_row_space_normal = holder.getView(R.id.rl_row_spacing_normal);
                        final RelativeLayout rl_row_space_min = holder.getView(R.id.rl_row_spacing_min);
                        final RelativeLayout rl_bg_one = holder.getView(R.id.rl_bg_one_text);
                        final RelativeLayout rl_bg_two = holder.getView(R.id.rl_bg_two_text);
                        final RelativeLayout rl_bg_three = holder.getView(R.id.rl_bg_three_text);
                        final RelativeLayout rl_bg_four = holder.getView(R.id.rl_bg_four_text);

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
                        rl_bg_one.setSelected(false);
                        rl_bg_two.setSelected(false);
                        rl_bg_three.setSelected(false);
                        rl_bg_four.setSelected(false);
                        if (backgroundPosition == 0) {
                            rl_bg_one.setSelected(true);
                        } else if (backgroundPosition == 1) {
                            rl_bg_two.setSelected(true);
                        } else if (backgroundPosition == 2) {
                            rl_bg_three.setSelected(true);
                        } else if (backgroundPosition == 3) {
                            rl_bg_four.setSelected(true);
                        }

                        rl_size_min.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 0) {
                                    textSizePosition = textSizePosition - 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    refreshH5View();
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

                                PreferencesUtil.saveData(ArticleDetailActivity.this,"textSizePosition",textSizePosition);
                            }
                        });
                        rl_size_max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 4) {
                                    textSizePosition = textSizePosition + 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    refreshH5View();
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
                                PreferencesUtil.saveData(ArticleDetailActivity.this,"textSizePosition",textSizePosition);
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
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"textLineSpacePosition",textLineSpacePosition);
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
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"textLineSpacePosition",textLineSpacePosition);
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
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"textLineSpacePosition",textLineSpacePosition);
                                }
                            }
                        });

                        rl_bg_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_one.isSelected()) {
                                    rl_bg_one.setSelected(true);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    backgroundPosition = 0;
                                    readModule = "1";
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"readModule",readModule);
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"backgroundPosition",backgroundPosition);
                                }
                            }
                        });
                        rl_bg_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_two.isSelected()) {
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(true);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    backgroundPosition = 1;
                                    readModule = "1";
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"readModule",readModule);
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"backgroundPosition",backgroundPosition);
                                }
                            }
                        });
                        rl_bg_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_three.isSelected()) {
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(true);
                                    rl_bg_four.setSelected(false);
                                    backgroundPosition = 2;
                                    readModule = "1";
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"readModule",readModule);
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"backgroundPosition",backgroundPosition);
                                }
                            }
                        });
                        rl_bg_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_four.isSelected()) {
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(true);
                                    backgroundPosition = 3;
                                    readModule = "1";
                                    refreshH5View();
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"readModule",readModule);
                                    PreferencesUtil.saveData(ArticleDetailActivity.this,"backgroundPosition",backgroundPosition);
                                }
                            }
                        });
                    }
                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void refreshH5View() {
        JSONObject params = new JSONObject();
        try {
            params.put("fontSize",textShowSize[textSizePosition]+"px");
            params.put("lineHeight",textLineSpace[textLineSpacePosition]);
            params.put("backgroundColor",background[backgroundPosition]);
            params.put("readModule",readModule);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebview.callHandler("changeEssayDetail", params.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
//                Lo
                Log.e("ArticleDetailActivity",data);

            }
        });

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.rl_back:
            case R.id.rl_back_copy:
                finish();
                break;
            case R.id.iv_collect:
            case R.id.iv_collect_copy:
                if(isPageComplete){
                    if(!isLogin()){
                        toLogin();
                        return;
                    }
                    if(isCollected)
                        deleteCollect();
                    else
                        collectedArticle();
                }else {
                    showTips("请等待页面加载完成");
                }

                break;
            case R.id.iv_share:
            case R.id.iv_share_copy:
                if (isPageComplete){
//                    if(!isLogin()){
//                        toLogin();
//                        return;
//                    }
                    shareNote();
                }else {
                    showTips("请等待页面加载完成");
                }
                 break;
            case R.id.rl_font:
                showSettingsDialog();
                break;
            case R.id.rl_night:
                rl_day_night.setVisibility(View.VISIBLE);
                if(readModule.equals("1")){
                    readModule = "0";
                    Glide.with(this).asGif().load(R.drawable.day_night).listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            try {
                                Field gifStateField = GifDrawable.class.getDeclaredField("state");
                                gifStateField.setAccessible(true);
                                Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                                Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                                gifFrameLoaderField.setAccessible(true);
                                Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                                Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                                gifDecoderField.setAccessible(true);
                                Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                                Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
                                Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                                getDelayMethod.setAccessible(true);
                                //设置只播放一次
                                resource.setLoopCount(1);
                                //获得总帧数
                                int count = resource.getFrameCount();
                                int delay = 200;
                                for (int i = 0; i < count; i++) {
                                    //计算每一帧所需要的时间进行累加
                                    delay += (int) getDelayMethod.invoke(gifDecoder, i);
                                }
                                iv_day_night.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        rl_day_night.setVisibility(View.GONE);
                                    }
                                }, delay);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }).into(iv_day_night);
                }else {
                    Glide.with(this).asGif().load(R.drawable.night_day).listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {


                            try {
                                Field gifStateField = GifDrawable.class.getDeclaredField("state");
                                gifStateField.setAccessible(true);
                                Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                                Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                                gifFrameLoaderField.setAccessible(true);
                                Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                                Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                                gifDecoderField.setAccessible(true);
                                Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                                Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
                                Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                                getDelayMethod.setAccessible(true);
                                //设置只播放一次
                                resource.setLoopCount(1);
                                //获得总帧数
                                int count = resource.getFrameCount();
                                int delay = 200;
                                for (int i = 0; i < count; i++) {
                                    //计算每一帧所需要的时间进行累加
                                    delay += (int) getDelayMethod.invoke(gifDecoder, i);
                                }
                                iv_day_night.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        rl_day_night.setVisibility(View.GONE);
                                    }
                                }, delay);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }).into(iv_day_night);
                    readModule = "1";
                }
                refreshH5View();
                PreferencesUtil.saveData(ArticleDetailActivity.this,"readModule",readModule);
                String ss = PreferencesUtil.getData(this,"readModule","1").toString();
                PreferencesUtil.saveData(ArticleDetailActivity.this,"backgroundPosition",backgroundPosition);
                break;
            case R.id.rl_appreciation:
                intent = new Intent(ArticleDetailActivity.this,ArticleAppreciationActivity.class);
                intent.putExtra("essayId",essayId);
                intent.putExtra("title",title);
                startActivity(intent);
                break;
            case R.id.rl_note:
                if(!isLogin()){
                    toLogin();
                    return;
                }
                 intent = new Intent(ArticleDetailActivity.this,ArticleNoteActivity.class);
                intent.putExtra("essayId",essayId);
                startActivity(intent);
                break;
            case R.id.tv_cancle:
                rl_dialog_think.setVisibility(View.GONE);
                KeyboardUtils.hideKeyboard(et_think);
                break;
            case R.id.tv_keep:
                thinkText = et_think.getText().toString();
                if(thinkText.equals("")){
                    MyToastUtil.showToast(this,"请输入想法");
                    return;
                }
                keepNote();
                break;
            case R.id.iv_playvideo:
                rl_top.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void collectedArticle() {
        JSONObject params = new JSONObject();
        try {
            params.put("essayId",essayId);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(collectUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        showTips("收藏成功");
                        isCollected = true;
                        iv_collect.setImageResource(R.drawable.nav_icon_collect_select);
                        iv_collect_copy.setImageResource(R.drawable.icon_bg_collect_select);
                    }else  if (jsonObject.getString("status").equals("400")){
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

    private void deleteCollect() {
        JSONObject params = new JSONObject();
        String essayids  = "["+essayId+"]";
        try {
            params.put("essayIds",essayids);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(deleteCollectUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        showTips("删除收藏成功");
                        isCollected = false;
                        iv_collect.setImageResource(R.drawable.nav_icon_collect_default);
                        iv_collect_copy.setImageResource(R.drawable.icon_bg_collect_default);
                    }else  if (jsonObject.getString("status").equals("400")){
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


    /**
     * 播放，这种是直接传音频实体类
     * 有两种，一种是播放本地播放，另一种是在线播放
     *
     * @param music music
     */
    public void play(String music) {

        try {
            mPlayer.reset();
            //把音频路径传给播放器
            mPlayer.setDataSource(music);
            //准备
            mPlayer.prepareAsync();
            //监听
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mPlayer != null) {
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 音频准备好的监听器
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        /** 当音频准备好可以播放了，则这个方法会被调用  */
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();

        //销毁MediaPlayer
        if(mPlayer != null){
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private String replace(String str) {
        String destination = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    /**
     * 分享笔记
     */
    private void shareNote() {
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
                                shareArticleToWX(true,h5DataBean.getShareList().getWx().getLink());
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareArticleToWX(false,h5DataBean.getShareList().getWx().getLink());
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(ArticleDetailActivity.this)) {
                                    shareToWeibo(h5DataBean.getShareList().getWx().getLink());
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQQ(h5DataBean.getShareList().getWx().getLink());
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQZone(h5DataBean.getShareList().getWx().getLink());
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                MyToastUtil.showToast(ArticleDetailActivity.this,"该词已在生词本中");
//                                showTips("复制成功");
                                DataUtil.copyContent(ArticleDetailActivity.this, h5DataBean.getShareList().getWx().getLink());
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
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {

        Bitmap thumb = shareBitmap;
        if(thumb == null)
            thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(this, url, title, shareContent,
                ImageUtils.bmpToByteArrayCopy(thumb, false), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        if (shareBitmap == null) {
            shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
        }

        ShareUtil.shareToWeibo(shareHandler, url, title,
                shareContent, shareBitmap);

    }

    /**
     * 准备Bitmap
     */
    private void prepareBitmap(final String shareImgUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                shareBitmap = ImageUtils.GetNetworkBitmap(shareImgUrl);
                if (shareBitmap == null) {
                    shareBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }
            }
        }.start();
    }


}
