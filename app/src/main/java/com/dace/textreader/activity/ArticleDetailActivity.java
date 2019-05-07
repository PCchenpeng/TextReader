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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.AudioArticleBean;
import com.dace.textreader.bean.H5DataBean;
import com.dace.textreader.bean.WordDetailBean;
import com.dace.textreader.bean.WordListBean;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.CustomController;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.LineWrapLayout;
import com.dace.textreader.view.StatusBarHeightView;
import com.dace.textreader.view.VideoPlayer.BDVideoView;
import com.dace.textreader.view.VideoPlayer.bean.VideoDetailInfo;
import com.dace.textreader.view.VideoPlayer.listener.SimpleOnVideoControlListener;
import com.dace.textreader.view.VideoPlayer.utils.DisplayUtils;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.weight.pullrecycler.MyScrollView;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeCustomWebview;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeHandler;
import com.dace.textreader.view.weight.pullrecycler.mywebview.CallBackFunction;
import com.google.gson.JsonObject;
import com.shuyu.action.web.ActionSelectListener;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.suke.widget.SwitchButton;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

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
    private String collectUrl = HttpUrlPre.HTTP_URL_ + "/insert/essay/collect";
    private String deleteCollectUrl = HttpUrlPre.HTTP_URL_ + "/delete/essay/collect" ;
    private String essayId;
    private String title;

    private BridgeCustomWebview mWebview;
//    private AppBarLayout appBarLayout;
    private LinearLayout rl_bottom;
    private ScrollView scroll_view;
    private StatusBarHeightView statusView_top,statusView_top_copy;
    private RelativeLayout rl_back,rl_back_copy;
    private ImageView iv_collect,iv_collect_copy,iv_share,iv_share_copy,iv_day_night,iv_playvideo;
    private RelativeLayout rl_font,rl_night,rl_note,rl_appreciation,rl_day_night,rl_dialog_think,rl_top;
    private TextView tv_cancle,tv_keep;
    private EditText et_think;
    private String[] textSize = new String[]{"1.0rem", "1.1rem", "1.4rem", "1.6rem", "1.8rem"};  //字体大小
    private String[] textShowSize = new String[]{"15px", "16px", "18px", "20px", "22px"};
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

    private int type_share = -1;  //分享类型
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;

    private WbShareHandler shareHandler;

    private OnListDataOperateListen mListen;
    private String content = "";
    private NiceVideoPlayer videoPlayer;
    private CustomController controller;



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
//        view_video = findViewById(R.id.view_video);
        rl_top = findViewById(R.id.rl_top);
        videoPlayer = findViewById(R.id.videoplayer);

//        String videoUrl = "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4";
//        videoPlayer.setUp(videoUrl, null);
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
//        controller.setTitle("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？");
//        controller.setLenght(0);
        GlideApp.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.img_default)
                .into(controller.imageView());



        if(isVideo){
            videoPlayer.setVisibility(View.VISIBLE);
//            iv_playvideo.setVisibility(View.VISIBLE);
        }else {
            videoPlayer.setVisibility(View.GONE);
//            iv_playvideo.setVisibility(View.GONE);
        }

        GlideApp.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(iv_topimg);

        mWebview.setWebChromeClient(new WebChromeClient() {
            //            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
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
                NewMainActivity.STUDENT_ID+"&gradeId="+NewMainActivity.GRADE_ID+"&lineHeight=2.4&isShare=0&version=3.2.6&backgroundColor=FFFFFF&essayId="+essayId;
        mWebview.loadUrl(url);

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
    }

    private void initData() {
        imgUrl = getIntent().getStringExtra("imgUrl");
        essayId = getIntent().getStringExtra("essayId");
        isVideo = getIntent().getBooleanExtra("isVideo",false);
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
                Log.e("scroll_view",String.valueOf("scrollY = "+scrollY));
                Log.e("scroll_view",String.valueOf("oldScrollY = "+oldScrollY));
                if(scrollY > oldScrollY){
//                    Log.e("ScrollType","上划");
                    statusView_top.setVisibility(View.GONE);
                    rl_bottom.setVisibility(View.GONE);
                }else {
//                    Log.e("ScrollType","下滑");
                    if(scrollY>statusView_top_copy.getHeight()){
                        statusView_top.setVisibility(View.VISIBLE);
                        rl_bottom.setVisibility(View.VISIBLE);
                    }else {
                        statusView_top.setVisibility(View.GONE);
                        rl_bottom.setVisibility(View.GONE);
                    }

                }

//                if(Math.abs(scrollY - oldScrollY) > 170){
//                    mWebview.requestDisallowInterceptTouchEvent(true);
//                }else {
//                    mWebview.requestDisallowInterceptTouchEvent(false);
//                }

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
                Log.e("getMoreTranslateInfo", "指定Handler接收来自web的数据：" + data);
                String mData = data.replace("\\/","/");

                h5DataBean = GsonUtil.GsonToBean(mData,H5DataBean.class);
                if(h5DataBean != null){
                    isPageComplete = true;
                }else {
                    isPageComplete = false;
                }
                title = h5DataBean.getTitle();
                controller.setTitle(title);
                if(h5DataBean.getVideo() != null){
                    String videoUrl = h5DataBean.getVideo().toString();
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

        mWebview.registerHandler("h5IsPlayAudio", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("h5IsPlayAudio", "指定Handler接收来自web的数据：" + data);
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
                Log.e("transportPara", "指定Handler接收来自web的数据：" + data);
                function.onCallBack("123");
            }
        });

        mWebview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
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
                    Intent intent = new Intent(ArticleDetailActivity.this,NewSearchActivity.class);
                    intent.putExtra("word",s1);
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
                                addNote(s1);
                            }
                        });

                        ll_think.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                rl_dialog_think.setVisibility(View.VISIBLE);
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

//                WordListBean wordListBean = GsonUtil.GsonToBean(result.toString(),WordListBean.class);
//                if(wordListBean != null & wordListBean.getData()!= null){
//                    showWordDialog(wordListBean);
//                }else {
//                    MyToastUtil.showToast(ArticleDetailActivity.this,"没有数据");
//                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void keepNote() {
            JSONObject params = new JSONObject();
            try {
                params.put("text",selectText);
                params.put("note",thinkText);
                params.put("start",selectStart);
                params.put("end",selectEnd);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mWebview.callHandler("setTextWithLine", params.toString(), new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

//                Lo
                    Log.e("setTextWithLine",data);

                }
            });
        rl_dialog_think.setVisibility(View.GONE);
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
                            final String word = wordListBean.getData().getMix().get(i).getWord();
                            textView.setText(word);
                            lwy_word.addView(child);

                            final int finalI = i;
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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
                                        final String word = wordListBean.getData().getBase().get(i).getWord();
                                        textView.setText(word);
                                        lwy_word.addView(child);

                                        final int finalI = i;
                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(ArticleDetailActivity.this, word,Toast.LENGTH_SHORT).show();
                                                getWordDetail(word);

                                            }
                                        });
                                    }

                                }else {

                                    for (int i = 0;i < wordListBean.getData().getMix().size();i++){
                                        View child = View.inflate(ArticleDetailActivity.this,R.layout.item_dialog_word,null);
                                        TextView textView = child.findViewById(R.id.tv_num);
                                        final String word = wordListBean.getData().getMix().get(i).getWord();
                                        textView.setText(word);
                                        lwy_word.addView(child);

                                        final int finalI = i;
                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                Toast.makeText(ArticleDetailActivity.this, word,Toast.LENGTH_SHORT).show();
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
                                play(wordDetailBean.getData().getAudio().get(0).getUrl());
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
            params.put("fontSize",textShowSize[textSizePosition]);
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
                    shareNote("");
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
                break;
            case R.id.rl_appreciation:
                 intent = new Intent(ArticleDetailActivity.this,ArticleAppreciationActivity.class);
//                intent.putExtra("word",s1);
                startActivity(intent);
                break;
            case R.id.rl_note:
                 intent = new Intent(ArticleDetailActivity.this,ArticleNoteActivity.class);
//                intent.putExtra("word",s1);
                startActivity(intent);
                break;
            case R.id.tv_cancle:
                rl_dialog_think.setVisibility(View.GONE);
                break;
            case R.id.tv_keep:
                thinkText = et_think.getText().toString();
                keepNote();
                break;
            case R.id.iv_playvideo:
                rl_top.setVisibility(View.INVISIBLE);
//                if(videoInfo != null){
////                    view_video.startPlayVideo(videoInfo);
//                }else {
//                    MyToastUtil.showToast(this,"请等待页面加载完毕");
//                }
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
            params.put("essayId",essayids);
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
            mPlayer.setDataSource(DataEncryption.audioEncode(music));
            //准备
            mPlayer.prepareAsync();
            //监听
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.start();
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

//        view_video.onDestroy();

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
//        view_video.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        view_video.onStart();
    }



    @Override
    public void onBackPressed() {
//        if (!DisplayUtils.isPortrait(this)) {
////            if(!view_video.isLock()) {
////                DisplayUtils.toggleScreenOrientation(this);
////            }
//        } else {
//            super.onBackPressed();
//        }
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    /**
     * 分享笔记
     */
    private void shareNote(final String noteId) {
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
                                getShareHtml(TYPE_SHARE_WX_FRIEND, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(ArticleDetailActivity.this)) {
                                    getShareHtml(TYPE_SHARE_Weibo, noteId);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK, noteId);
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
    private void getShareHtml(int type_share, String noteId) {
        this.type_share = type_share;
        showTips("正在准备分享内容...");
//        new GetShareHtml(this).execute(shareUrl, noteId);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(this, url, title, content,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }


}
