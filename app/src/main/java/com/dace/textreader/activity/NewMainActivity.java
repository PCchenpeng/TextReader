package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.audioUtils.AppHelper;
import com.dace.textreader.audioUtils.PlayService;
import com.dace.textreader.bean.AutoSaveWritingBean;
import com.dace.textreader.bean.HtmlLinkBean;
import com.dace.textreader.bean.LessonBean;
import com.dace.textreader.bean.ReaderTabBean;
import com.dace.textreader.fragment.HomeFragment;
import com.dace.textreader.fragment.NewHomeFragment;
import com.dace.textreader.fragment.NewLessonFragment;
import com.dace.textreader.fragment.NewMineFragment;
import com.dace.textreader.fragment.NewReaderFragment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.SpeechRecognizerUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 主界面
 */
public class NewMainActivity extends BaseActivity implements View.OnClickListener {

    //TOKEN登录接口
    public static String TOKEN_URL = HttpUrlPre.HTTP_URL + "/tokenLogin";
    //统计用户流量
    private final String statisticsUrl = HttpUrlPre.HTTP_URL + "/statistics/flow";
    //所有的HTML链接
    private final String htmlLinkUrl = HttpUrlPre.HTTP_URL_ + "/select/all/html/link";
    //更新音频的播放次数
    private static final String updatePlayNumUrl = HttpUrlPre.HTTP_URL + "/lesson/update/playback";
    //TOKEN检测接口
    private static final String TOKEN_ACCOUNT_DETECTION_URL = HttpUrlPre.HTTP_URL + "/select/is/not/token";
    //获取消息数量
    private static final String messageUrl = HttpUrlPre.HTTP_URL + "/system/message/notify";
    //极光注册
    private static final String jiguangUrl = HttpUrlPre.HTTP_URL + "/info/record/jiguang";
    //阅读四个tab
    private String readerTabUrl = HttpUrlPre.HTTP_URL_+"/select/category/detail";

    private static final String HOME_FRAGMENT_TAG = "home";
    private static final String READER_FRAGMENT_TAG = "reader";
    private static final String LESSON_FRAGMENT_TAG = "lesson";
    private static final String MINE_FRAGMENT_TAG = "mine";

    private static final int LOGIN_HANDLER_WHAT = 1;  //登录的handler标识
    private static final int STATISTICS_HANDLER_WHAT = 2;  //统计用户的handler标识
    private static final int TOKEN_HANDLER_WHAT = 3;  //token检测的handler标识
    private static final int BACK_HANDLER_WHAT = 4;  //点击返回的handler表示

    public static String TOKEN = "";  //TOKEN
    public static long STUDENT_ID = -1;  //学生ID
    public static String USERNAME = "";  //学生名字
    public static String USERIMG = "";  //学生头像
    public static int GRADE = 0;  //学生等级
    public static int GRADE_ID = -1; //学生等级ID
    public static String PY_SCORE = ""; //学生等级ID
    public static int LEVEL = 0;  //当前等级
    public static int NEWS_COUNT = 0;  //未读消息数量
    public static String PHONENUMBER = "";  //用户手机号码
    public static String DESCRIPTION = "";  //用户简介

    private RelativeLayout rl_root;
    public View view_cover;
    private LinearLayout ll_home;
    private ImageView iv_home;
    private TextView tv_home;
    private LinearLayout ll_reader;
    private ImageView iv_reader;
    private TextView tv_reader;
    private LinearLayout ll_lesson;
    private ImageView iv_lesson;
    private TextView tv_lesson;
    private RelativeLayout ll_mine;
    private TextView tv_news_count;
    private ImageView iv_mine;
    private TextView tv_mine;
    private ImageView iv_writing;

    private FragmentManager fm;  //Fragment管理对象
    private Fragment mFragment;
    private HomeFragment homeFragment;
    private NewHomeFragment newHomeFragment;
    private NewReaderFragment readerFragment;
    private NewLessonFragment lessonFragment;
    private NewMineFragment mineFragment;

    //文字未选中颜色
    private int color_unselected = Color.parseColor("#666666");
    //文字选中颜色
    private int color_selected = Color.parseColor("#4d72ff");

    //音频播放服务连接
    private PlayServiceConnection mPlayServiceConnection;
    public static long lessonId = -1;  //当前播放的课程ID
    public static String lessonTeacher = "";//当前播放课程的老师
    public static String lessonTitle = "";//当前播放课程的标题
    public static double lessonPrice = 0;//当前播放课程的价格
    public static double lessonOriginalPrice = -1;//当前播放课程的原价
    public static String lessonContent = "";//当前播放课程的内容

    private NewMainActivity mContext;

    private View view = null; //第一次进入演示视图
    private TextView tv_text;
    private ImageView iv_text;
    private ImageView iv_speak;
    private TextView tv_tips;
    private TextView tv_skip;
    private RelativeLayout rl_tab;

    // 语音听写对象
    private SpeechRecognizerUtil speechRecognizerUtil;
    //视频索引，0表示第一个“快乐学语文”视频，1表示语音识别通过视频，2表示语音识别不通过视频
    private int videoIndex = 0;
    private String text = "";
    private AssetFileDescriptor fileDescriptor;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;

    private boolean isReadySpeak = false;

    public static boolean isInitiativeClose = false;  //是否是主动关闭更新

    private boolean isFirstStart = true;  //是否是第一次进入，用于显示提示信息

    private AssetManager assetManager = null;
    private MediaPlayer mediaPlayer = null;

    public static boolean isLoginHideBack = false;

    private boolean isClickBack = false;  //是否点击了返回，用来双击退出

    private String phoneModel = "";  //手机型号

    private List<HtmlLinkBean.DataBean> htmlLinkBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        //修改状态栏的文字颜色为黑色
//        int flag = StatusBarUtil.StatusBarLightMode(this);
//        StatusBarUtil.StatusBarLightMode(this, flag);

        mContext = this;

        fm = getSupportFragmentManager();

        initView();
        initEvents();

        SharedPreferences firstStart = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        isFirstStart = firstStart.getBoolean("home", true);

        //获取存储在SharedPreferences中的TOKEN
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        TOKEN = sharedPreferences.getString("token", "");

//        if (isFirstStart) {  //第一次进入APP，显示演示视频
//            setNeedCheckCode(false);
//            speechRecognizerUtil = SpeechRecognizerUtil.getInstance();
//            speechRecognizerUtil.init(mContext, mInitListener);
//            showVideoView();
//        } else {  //获取app的最新版本
        hideLogin();
        broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_SYSTEM_UPGRADE);
        checkAutoSaveWriting();
//        }

        phoneModel = Build.BRAND;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
        intentFilter.addAction(HttpUrlPre.ACTION_BROADCAST_BUY_LESSON);
        intentFilter.addAction(HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, intentFilter);


        getReaderTabData();

        startStatistics();

        startAccountDetection();

        removeMainActivity(this);

        //绑定音频播放服务
        bindService();

        loadhtmlLinkData();

    }

    private void loadhtmlLinkData() {
        JSONObject params = new JSONObject();
        OkHttpManager.getInstance(this).requestAsyn(htmlLinkUrl, OkHttpManager.TYPE_GET, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        HtmlLinkBean htmlLinkBean = GsonUtil.GsonToBean(result.toString(),HtmlLinkBean.class);
                        List<HtmlLinkBean.DataBean> data = htmlLinkBean.getData();
                        if(htmlLinkBeanList != null) {
                            htmlLinkBeanList.addAll(data);
                            if (htmlLinkBeanList.size() > 0) {
                                for (int i = 0;i < htmlLinkBeanList.size();i++) {
                                    Log.d("111", "getName " + htmlLinkBeanList.get(i).getName() + "getName " + htmlLinkBeanList.get(i).getUrl());
                                }
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }


    private void getReaderTabData() {
        JSONObject params = new JSONObject();
        OkHttpManager.getInstance(this).requestAsyn(readerTabUrl, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        PreferencesUtil.saveData(NewMainActivity.this,"readerTab",result);
                        ReaderTabBean readerTabBean = GsonUtil.GsonToBean(result.toString(),ReaderTabBean.class);
                        if(readerTabBean != null){
                            for (int i=0;i<readerTabBean.getData().size();i++){
                                GlideApp.with(NewMainActivity.this)
                                        .load(readerTabBean.getData().get(i).getImage())
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload();
                            }
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    /**
     * 检查自动保存的作文内容
     */
    private void checkAutoSaveWriting() {
        LitePal.findAllAsync(AutoSaveWritingBean.class).listen(
                new FindMultiCallback<AutoSaveWritingBean>() {
                    @Override
                    public void onFinish(List<AutoSaveWritingBean> list) {
                        if (list.size() != 0) {
                            showTurnToWritingDialog();
                        }
                    }
                });
    }

    /**
     * 显示前往写作的对话框
     */
    private void showTurnToWritingDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_single_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_single_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_single_choose_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_single_choose_dialog);

                        tv_title.setText("作文自动存稿");
                        tv_content.setText("你上次中途退出作文编辑，小派已尽力为你保存。");
                        tv_sure.setText("马上查看");

                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, WritingActivity.class);
                                intent.putExtra("id", "");
                                intent.putExtra("taskId", "");
                                intent.putExtra("area", 5);
                                intent.putExtra("type", 5);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setMargin(64)
                .setOutCancel(false)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                MyToastUtil.showToast(mContext, "初始化讯飞语音失败");
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//            if (error.getErrorCode() == 14002) {
//                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
//            } else
            if (error.getErrorCode() == 10118) {
                MyToastUtil.showToast(mContext, "您好像没有说话哦~");
                checkUserPermission();
            } else if (error.getErrorCode() == 20001) {
                MyToastUtil.showToast(mContext, "请检查网络是否连接~");
            }
            stopSpeech();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            text = text + JsonParser.parseIatResult(results.getResultString());
            tv_text.setText(text);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

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

    private boolean isStartSpeech = false;

    /**
     * 开始语音输入
     */
    private void startSpeech() {
        isStartSpeech = true;
        iv_speak.setImageResource(R.drawable.icon_speak_pause);
        tv_tips.setText("语音识别转化中");
        if (tv_text.getVisibility() == View.INVISIBLE) {
            tv_text.setVisibility(View.VISIBLE);
            iv_text.setVisibility(View.VISIBLE);
        }

        int ret = speechRecognizerUtil.startVoice(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            MyToastUtil.showToast(mContext, "听写失败");
        } else {
            MyToastUtil.showToast(mContext, "正在听写");
        }
    }

    /**
     * 停止语音输入
     */
    private void stopSpeech() {
        speechRecognizerUtil.stopVoice();
        iv_speak.setImageResource(R.drawable.icon_speak_start);
        tv_tips.setText("按住开始说话");
    }

    /**
     * 显示听写结果
     */
    private void showResult() {
        if (!isStartSpeech) {
            return;
        }
        if (text.equals("快乐学语文")) {
            tv_tips.setText("匹对成功！");
            iv_speak.setImageResource(R.drawable.icon_speak_right);
            videoIndex = 1;
        } else {
            text = "";
            tv_tips.setText("按住说话再试一遍");
            tv_text.setText("");
            iv_speak.setImageResource(R.drawable.icon_speak_start);
            videoIndex = 2;
        }
        createVideoView();
    }

    /**
     * 检查用户权限
     */
    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (!verifyPermissions(grantResults)) {
                MyToastUtil.showToast(mContext, "录音权限被拒绝");
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行账号检测
     */
    private void startAccountDetection() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String data = "";
                if (!TOKEN.equals("")) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json = new JSONObject();
                        json.put("token", TOKEN);
                        Log.d("111","android.os.Build.BRAND " + android.os.Build.BRAND);
                        json.put("phoneModel", android.os.Build.BRAND);
                        json.put("studentId", NewMainActivity.STUDENT_ID);
                        json.put("platform", "android");
                        RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                        Request request = new Request.Builder()
                                .url(TOKEN_ACCOUNT_DETECTION_URL)
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        data = response.body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Message msg = Message.obtain();
                msg.what = TOKEN_HANDLER_WHAT;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 第一次进入显示演示视频
     */
    private void showVideoView() {
        isFirstStart = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        mediaPlayer = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.dialog_app_demonstration_layout, null);
        surfaceView = view.findViewById(R.id.surface_view_app_demonstration_dialog);
        tv_text = view.findViewById(R.id.tv_text_app_demonstration);
        iv_text = view.findViewById(R.id.iv_text_app_demonstration);
        iv_speak = view.findViewById(R.id.iv_speak_app_demonstration);
        tv_tips = view.findViewById(R.id.tv_tips_app_demonstration);
        tv_skip = view.findViewById(R.id.tv_skip_app_demonstration);
        tv_text.setVisibility(View.INVISIBLE);
        iv_speak.setVisibility(View.INVISIBLE);
        tv_tips.setVisibility(View.INVISIBLE);
        rl_root.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlayerView();
            }
        });
        iv_speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isReadySpeak) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(mContext,
                                        Manifest.permission.RECORD_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    isStartSpeech = false;
                                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                            0);
                                } else {
                                    startSpeech();
                                }
                            } else {
                                startSpeech();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopSpeech();
                        showResult();
                        break;
                }
                return true;
            }
        });

        assetManager = mContext.getAssets();
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(mCallback);
        initMediaPlayer();
        createVideoView();

        SharedPreferences firstSP = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstSP.edit();
        editor.putBoolean("home", false);
        editor.apply();
    }

    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isReadySpeak = false;
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (videoIndex == 0) {
                    iv_speak.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.VISIBLE);
                } else if (videoIndex == 1) {
                    removePlayerView();
                }
                isReadySpeak = true;
            }
        });
    }

    /**
     * 创建视频
     */
    private void createVideoView() {
        try {
            String fileName;
            if (videoIndex == 0) {
                fileName = "demonstration0.mp4";
            } else if (videoIndex == 1) {
                fileName = "demonstration1.mp4";
            } else {
                fileName = "demonstration2.mp4";
            }
            fileDescriptor = assetManager.openFd(fileName);

            if (mediaPlayer == null) {
                initMediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            removePlayerView();
        }
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private boolean isVideoPause = false;

    @Override
    protected void onResume() {
        super.onResume();
        hideDialog();
        if (STUDENT_ID != -1) {
            new GetData(this).execute(messageUrl);
        }
        if (view != null && mediaPlayer != null) {
            if (isVideoPause) {
                mediaPlayer.start();
            }
        }
    }

    /**
     * 注册极光
     */
    private void registeredJIGUANG() {
        String registrationID = JPushInterface.getRegistrationID(mContext);
        new RegistrationJiGuang(mContext).execute(jiguangUrl, String.valueOf(STUDENT_ID),
                registrationID);
    }

    /**
     * 绑定服务
     * 注意对于绑定服务一定要解绑
     */
    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 音频播放服务连接
     */
    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            AppHelper.get().setPlayService(playService);
            initPlayServiceListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * 初始化服务播放音频播放进度监听器
     * 这个是要是通过监听即时更新主页面的底部控制器视图
     */
    public void initPlayServiceListener() {
        if (null == getPlayService()) {
            return;
        }
        getPlayService().setOnPlayNumNeedUpdateListener(new PlayService.OnPlayNumNeedUpdate() {
            @Override
            public void update(LessonBean lessonBean) {  //更新播放次数
                updatePlayNum(lessonBean.getId());
            }
        });
    }

    /**
     * 更新播放次数
     *
     * @param id
     */
    private void updatePlayNum(String id) {
        new UpdatePlayNum(mContext).execute(updatePlayNumUrl, id);
    }

    /**
     * 广播接收器，退出登录和微课优惠券的广播
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case HttpUrlPre.ACTION_BROADCAST_USER_EXIT:
                    if (newHomeFragment == null) {
                        newHomeFragment = new NewHomeFragment();
                    }
                    showFragment(newHomeFragment, HOME_FRAGMENT_TAG);
                    break;
                case HttpUrlPre.ACTION_BROADCAST_BUY_LESSON:
                    if (lessonFragment == null) {
                        lessonFragment = new NewLessonFragment();
                    }
                    showFragment(lessonFragment, LESSON_FRAGMENT_TAG);
                    break;
                case HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN:
                    registeredJIGUANG();
                    break;
            }
        }
    };

    /**
     * 后台登录
     */
    private void hideLogin() {
        if (TOKEN.equals("") || STUDENT_ID != -1) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                String data = "";
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("token", TOKEN);
                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                    Request request = new Request.Builder()
                            .url(TOKEN_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    data = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    TOKEN = "";
                    STUDENT_ID = -1;
                }
                Message msg = Message.obtain();
                msg.what = LOGIN_HANDLER_WHAT;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 统计用户流量
     */
    private void startStatistics() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(300000);
                    if (STUDENT_ID != -1) {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json = new JSONObject();
                        json.put("studentId", STUDENT_ID);
                        json.put("annotation", "android");
                        json.put("phoneModel", phoneModel);
                        RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                        Request request = new Request.Builder()
                                .url(statisticsUrl)
                                .post(requestBody)
                                .build();
                        client.newCall(request).execute();
                    }
                    mHandler.sendEmptyMessage(STATISTICS_HANDLER_WHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_HANDLER_WHAT:
                    String data = (String) msg.obj;
                    analyzeData(data);
                    break;
                case STATISTICS_HANDLER_WHAT:
                    startStatistics();
                    break;
                case TOKEN_HANDLER_WHAT:
                    String s = (String) msg.obj;
                    analyzeAccountData(s);
                    break;
                case BACK_HANDLER_WHAT:
                    isClickBack = false;
                    break;
            }
        }
    };

    /**
     * 账号检测数据
     *
     * @param s
     */
    private void analyzeAccountData(String s) {
        if (!s.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (400 == jsonObject.optInt("status", -1)) {
                    broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_OTHER_DEVICE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startAccountDetection();
    }

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * 退出登录，清除用户信息
     */
    private void clearUser() {
        NewMainActivity.TOKEN = "";
        NewMainActivity.STUDENT_ID = -1;
        NewMainActivity.USERNAME = "";
        NewMainActivity.GRADE = -1;
        NewMainActivity.LEVEL = -1;
        NewMainActivity.PY_SCORE = "-1";
        NewMainActivity.USERIMG = "";
        NewMainActivity.NEWS_COUNT = 0;
        NewMainActivity.PHONENUMBER = "";
        NewMainActivity.DESCRIPTION = "";

        tv_news_count.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("token", "");
        editor.apply();
    }

    /**
     * 分析数据
     *
     * @param data 数据
     */
    public void analyzeData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            int status = json.optInt("status", -1);
            if (status == 200) {
                JSONObject student = json.getJSONObject("data");
                STUDENT_ID = student.optLong("studentid", -1);
                TOKEN = student.getString("token");
                USERNAME = student.getString("username");
                USERIMG = student.getString("userimg");
                GRADE = student.optInt("level", -1);
                GRADE_ID = student.optInt("gradeid", 110);
                PY_SCORE = student.getString("score");
                LEVEL = student.optInt("level", -1);
                PHONENUMBER = student.getString("phonenum");
                DESCRIPTION = student.getString("description");

                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("token", TOKEN);
                editor.apply();

                new GetData(this).execute(messageUrl);
                registeredJIGUANG();

            } else {
                clearUser();
                registeredJIGUANG();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            clearUser();
            registeredJIGUANG();
        }
    }

    private void initEvents() {
        ll_home.setOnClickListener(this);
        ll_reader.setOnClickListener(this);
        ll_lesson.setOnClickListener(this);
        ll_mine.setOnClickListener(this);
        iv_writing.setOnClickListener(this);
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_new_main);
        rl_tab = findViewById(R.id.rl_tab);
        view_cover = findViewById(R.id.view_cover);
        ll_home = findViewById(R.id.ll_new_home_bottom_main);
        iv_home = findViewById(R.id.iv_new_home_bottom_main);
        tv_home = findViewById(R.id.tv_new_home_bottom_main);
        ll_reader = findViewById(R.id.ll_new_reader_bottom_main);
        iv_reader = findViewById(R.id.iv_new_reader_bottom_main);
        tv_reader = findViewById(R.id.tv_new_reader_bottom_main);
        ll_lesson = findViewById(R.id.ll_new_lesson_bottom_main);
        iv_lesson = findViewById(R.id.iv_new_lesson_bottom_main);
        tv_lesson = findViewById(R.id.tv_new_lesson_bottom_main);
        ll_mine = findViewById(R.id.ll_new_mine_bottom_main);
        iv_mine = findViewById(R.id.iv_new_mine_bottom_main);
        tv_news_count = findViewById(R.id.tv_news_count_bottom_main);
        tv_mine = findViewById(R.id.tv_new_mine_bottom_main);
        iv_writing = findViewById(R.id.iv_writing_bottom_new_main);

        //Fragment的初始化
        newHomeFragment = new NewHomeFragment();
        fm.beginTransaction().add(R.id.frame_new_fragment, newHomeFragment, HOME_FRAGMENT_TAG).commit();
        mFragment = newHomeFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_home_bottom_main:
                showFragment(newHomeFragment, HOME_FRAGMENT_TAG);
                break;
            case R.id.ll_new_reader_bottom_main:
                if (readerFragment == null) {
                    readerFragment = new NewReaderFragment();
                }
                showFragment(readerFragment, READER_FRAGMENT_TAG);
                break;
            case R.id.ll_new_lesson_bottom_main:
                if (lessonFragment == null) {
                    lessonFragment = new NewLessonFragment();
                }
                showFragment(lessonFragment, LESSON_FRAGMENT_TAG);
                break;
            case R.id.ll_new_mine_bottom_main:
                if (STUDENT_ID == -1) {
                    if (getPlayService() != null) {
                        getPlayService().pause();
                        getPlayService().hideFloatView();
                    }
                    startActivity(new Intent(mContext, LoginActivity.class));
                } else {
                    if (mineFragment == null) {
                        mineFragment = new NewMineFragment();
                    }
                    showFragment(mineFragment, MINE_FRAGMENT_TAG);
                    mineFragment.getMessageCount();
                }
                break;
            case R.id.iv_writing_bottom_new_main:
                Intent intent = new Intent(mContext, WritingActivity.class);
                intent.putExtra("id", "");
                intent.putExtra("taskId", "");
                intent.putExtra("area", 5);
                intent.putExtra("type", 5);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示视图
     *
     * @param fragment
     * @param tag
     */
    private void showFragment(Fragment fragment, String tag) {
        if (mFragment != fragment) {
            //开启Fragment事务
            FragmentTransaction transaction = fm.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.hide(mFragment).add(R.id.frame_new_fragment, fragment, tag);
            } else {
                transaction.hide(mFragment).show(fragment);
            }
            transaction.commitAllowingStateLoss();
            mFragment = fragment;
            initBottomLayout();
            switch (tag) {
                case HOME_FRAGMENT_TAG:
                    iv_home.setImageResource(R.drawable.icon_tab_new_home_sel);
                    tv_home.setTextColor(color_selected);
                    break;
                case READER_FRAGMENT_TAG:
                    iv_reader.setImageResource(R.drawable.icon_tab_new_reader_sel);
                    tv_reader.setTextColor(color_selected);
                    break;
                case LESSON_FRAGMENT_TAG:
                    iv_lesson.setImageResource(R.drawable.icon_tab_new_classes_sel);
                    tv_lesson.setTextColor(color_selected);
                    break;
                case MINE_FRAGMENT_TAG:
                    iv_mine.setImageResource(R.drawable.icon_tab_new_myself_sel);
                    tv_mine.setTextColor(color_selected);
                    break;
            }
        }
        if (STUDENT_ID != -1) {
            new GetData(this).execute(messageUrl);
        }
    }

    /**
     * 初始化底部布局
     */
    private void initBottomLayout() {
        iv_home.setImageResource(R.drawable.icon_tab_new_home_nor);
        tv_home.setTextColor(color_unselected);
        iv_reader.setImageResource(R.drawable.icon_tab_new_reader_nor);
        tv_reader.setTextColor(color_unselected);
        iv_lesson.setImageResource(R.drawable.icon_tab_new_classes_nor);
        tv_lesson.setTextColor(color_unselected);
        iv_mine.setImageResource(R.drawable.icon_tab_new_myself_nor);
        tv_mine.setTextColor(color_unselected);
    }


    /**
     * 更新音频的播放次数
     */
    private static class UpdatePlayNum
            extends WeakAsyncTask<String, Void, String, NewMainActivity> {

        protected UpdatePlayNum(NewMainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewMainActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("lesson_id", strings[1]);
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
        protected void onPostExecute(NewMainActivity activity, String s) {

        }
    }

    /**
     * 获取未读消息数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, NewMainActivity> {

        protected GetData(NewMainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewMainActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(NewMainActivity activity, String s) {
            if (s != null) {
                activity.analyzeMessageData(s);
            }
        }
    }

    /**
     * 分析获取消息数量的数据
     *
     * @param s
     */
    private void analyzeMessageData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                NewMainActivity.NEWS_COUNT = object.optInt("total", 0);
                if (NEWS_COUNT == 0) {
                    tv_news_count.setVisibility(View.INVISIBLE);
                } else {
                    tv_news_count.setText(String.valueOf(NEWS_COUNT));
                    tv_news_count.setVisibility(View.VISIBLE);
                }
            } else {
                tv_news_count.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            tv_news_count.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 关联极光
     */
    private static class RegistrationJiGuang
            extends WeakAsyncTask<String, Void, String, NewMainActivity> {

        protected RegistrationJiGuang(NewMainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewMainActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("jiguangId", strings[2]);
                object.put("type", "android");
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
        protected void onPostExecute(NewMainActivity activity, String s) {

        }
    }


    @Override
    public void onBackPressed() {
        if (view == null) {
            if (isClickBack) {
                LitePal.deleteAll(AutoSaveWritingBean.class);
                super.onBackPressed();
            } else {
                isClickBack = true;
                showTips("再按一次退出派知语文");
                startDoubleClickListen();
            }
        }
    }

    /**
     * 开始监听返回按钮的双击，1500ms内再次点击就退出应用
     */
    private void startDoubleClickListen() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);
                    mHandler.sendEmptyMessage(BACK_HANDLER_WHAT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (view != null && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isVideoPause = true;
            }
        }
    }

    /**
     * 移除播放视图
     */
    private void removePlayerView() {
        if (view != null) {
            rl_root.removeView(view);
            view = null;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            if (mediaPlayer != null) {
                boolean isPlaying = false;
                try {
                    isPlaying = mediaPlayer.isPlaying();
                } catch (IllegalStateException e) {
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                }
                if (isPlaying) {
                    mediaPlayer.pause();
                    mediaPlayer.stop();
                    mediaPlayer = null;
                }
            }
        }
        getClipContent();
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizerUtil != null) {
            speechRecognizerUtil.release();
        }
        if (mPlayServiceConnection != null) {
            if (getPlayService() != null) {
                getPlayService().quit();
            }
            unbindService(mPlayServiceConnection);
        }
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if( hasFocus ){
            if (newHomeFragment != null)
            newHomeFragment.setViewCoverTopMargin();
        }
    }

    public RelativeLayout getRl_tab() {
        return rl_tab;
    }

}
