package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.Player;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.audio.AudioRecorder;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.WaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 语音评测
 */
public class VoiceEvaluationActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/speak/prepare/material";
    private static final String uploadSelfUrl = HttpUrlPre.HTTP_URL + "/introduction/self/upload";
    private static final String uploadUrl = HttpUrlPre.HTTP_URL + "/speech/evaluate/test";

    private RelativeLayout rl_root;
    private FrameLayout frameLayout;
    private RelativeLayout rl_back;
    private RelativeLayout rl_play_example;
    private TextView tv_title;
    private TextView tv_tips;
    private TextView tv_subtitle;
    private TextView tv_content;
    private WaveView waveView;
    private RelativeLayout rl_input;
    private ImageView iv_input;
    private TextView tv_input;
    private LinearLayout ll_play;
    private ImageView iv_play;
    private SeekBar seekBar;
    private View view_white;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_top;
    private TextView tv_restart;
    private TextView tv_commit;

    private RelativeLayout rl_example;
    private ImageView iv_play_example;
    private SeekBar seekBar_example;
    private TextView tv_start_example;
    private TextView tv_end_example;
    private ImageView iv_close_example;

    private VoiceEvaluationActivity mContext;

    private int status = 0;  //录音状态,0初始状态，1正在录音，2暂停录音

    private String filename = "";
    private String filePath = "";

    private String materialId = "";  //1为朗诵，2为自我介绍，3为获取朗诵内容
    private String title = "";
    private String tips = "";
    private String subtitle = "";
    private String content = "";
    private String audio = "";

    private AudioRecorder audioRecorder;

    private int seconds = 0;
    private int millisecond = 0;

    private boolean isRestart = false;

    private Player mPlayer;
    private boolean isExist = false;

    //申请两个权限，录音和文件读写
    //1、首先声明一个数组permissions，将需要的权限都放在里面
    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPermissionList中
    private List<String> mPermissionList = new ArrayList<>();

    private boolean isMp3 = false;

    private BaseNiceDialog uploadDialog = null;

    /**
     * 播放录音文件
     */
    public MediaPlayer mediaPlayer;  //音频播放
    private Timer mTimer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                handler.sendEmptyMessage(0);
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mediaPlayer != null) {
                int position = mediaPlayer.getCurrentPosition();
                tv_start.setText(DateUtil.formatterTime(position));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_evaluation);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        materialId = getIntent().getStringExtra("materialId");
        isRestart = getIntent().getBooleanExtra("restart", false);

        initView();
        initData();
        initEvents();
        setImmerseLayout();

        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            initPermission();
        }

        //播放录制音频时的时间显示
        mTimer.schedule(timerTask, 0, 1000);
    }

    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
            initAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (1 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                showTip("没有权限，无法操作");
                finish();
            } else {
                //全部权限通过，可以进行下一步操作。。。
                initAudio();
            }
        }
    }

    /**
     * 初始化录音
     */
    private void initAudio() {
        audioRecorder = AudioRecorder.getInstance();
        audioRecorder.setContext(mContext);
    }

    // view为标题栏
    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_root.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initData() {
        showLoadingView(true);
        String id;
        if (materialId.equals("1")) {
            id = "3";
        } else {
            id = materialId;
        }
        new GetData(mContext).execute(url, id);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rl_play_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExampleAudio();
            }
        });
        rl_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 0 || status == 2) {
                    startRecord();
                } else if (status == 1) {
                    pauseRecord();
                }
            }
        });
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    iv_play.setImageResource(R.drawable.icon_play_dark_red);
                } else {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        iv_play.setImageResource(R.drawable.icon_pause_dark_red);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.pause();
                iv_play.setImageResource(R.drawable.icon_play_dark_red);
                tv_start.setText("00:00");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTopTextPosition();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tv_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
                reset();
                startRecord();
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 1) {
                    stopRecord();
                }
                uploadFile();
            }
        });
        rl_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iv_play_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playExampleAudio();
            }
        });
        iv_close_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeExampleAudio();
            }
        });
        seekBar_example.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
                if (isExist) {
                    this.progress = progress * mPlayer.mediaPlayer.getDuration()
                            / seekBar.getMax();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
                mPlayer.mediaPlayer.seekTo(progress);
                tv_start_example.setText(DateUtil.formatterTime(progress));
            }
        });
        mPlayer.setMediaPlayerFinish(new Player.MediaPlayerFinish() {
            @Override
            public void onFinish() {
                iv_play_example.setImageResource(R.drawable.icon_play_dark_red);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 更新进度条上面时间文本的位置
     */
    private void updateTopTextPosition() {
        //获取文本宽度
        float textWidth = tv_top.getWidth();

        //获取seekbar最左端的x位置
        float left = seekBar.getLeft();

        //进度条的刻度值
        float max = Math.abs(seekBar.getMax());

        //这不叫thumb的宽度,叫seekbar距左边宽度,
        //seekbar 不是顶格的，两头都存在一定空间，
        //所以xml 需要用paddingStart 和 paddingEnd 来确定具体空了多少值
        float thumb = DensityUtil.dip2px(mContext, 16);

        //每移动1个单位，text应该变化的距离 = (seekBar的宽度 - 两头空的空间) / 总的progress长度
        float average = (((float) seekBar.getWidth()) - 2 * thumb) / max;

        //int to float
        float currentProgress = seekBar.getProgress();

        //textview 应该所处的位置 =
        // seekbar最左端 + seekbar左端空的空间 + 当前progress应该加的长度
        // - textview宽度的一半(保持居中作用)
        float pox = left - textWidth / 2 + thumb + average * currentProgress;

        if (iv_play.getVisibility() == View.VISIBLE) {
            pox = pox - (iv_play.getWidth() + DensityUtil.dip2px(mContext, 16)) * currentProgress / max;
        }
        //超出右边界
        if (pox + textWidth >= seekBar.getRight()) {
            pox = seekBar.getRight();
        }

        tv_top.setX(pox);
    }

    /**
     * 显示示范音频
     */
    private void showExampleAudio() {
        if (rl_example.getVisibility() == View.VISIBLE) {
            return;
        }
        if (status == 1) {
            pauseRecord();
        }
        rl_example.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭示范音频
     */
    private void closeExampleAudio() {
        if (mPlayer.mediaPlayer.isPlaying()) {
            mPlayer.pause();
            iv_play_example.setImageResource(R.drawable.icon_play_dark_red);
        }
        if (rl_example.getVisibility() != View.GONE) {
            rl_example.setVisibility(View.GONE);
        }
    }

    /**
     * 播放示范音频
     */
    private void playExampleAudio() {
        if (isExist) {
            if (mPlayer.mediaPlayer.isPlaying()) {
                mPlayer.pause();
                iv_play_example.setImageResource(R.drawable.icon_play_dark_red);
            } else {
                mPlayer.play();
                iv_play_example.setImageResource(R.drawable.icon_pause_dark_red);
            }
        } else {
            iv_play_example.setImageResource(R.drawable.icon_pause_dark_red);
            mPlayer.playUrl(audio);
            isExist = true;
        }
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_voice_evaluation);
        frameLayout = findViewById(R.id.frame_voice_evaluation);
        rl_back = findViewById(R.id.rl_back_voice_evaluation);
        rl_play_example = findViewById(R.id.rl_play_example_voice_evaluation);
        tv_title = findViewById(R.id.tv_title_voice_evaluation);
        tv_tips = findViewById(R.id.tv_tips_voice_evaluation);
        tv_subtitle = findViewById(R.id.tv_subtitle_voice_evaluation);
        tv_content = findViewById(R.id.tv_content_voice_evaluation);
        waveView = findViewById(R.id.wave_view_voice_evaluation);
        rl_input = findViewById(R.id.rl_input_voice_evaluation);
        iv_input = findViewById(R.id.iv_input_voice_evaluation);
        tv_input = findViewById(R.id.tv_input_voice_evaluation);
        ll_play = findViewById(R.id.ll_play_voice_evaluation);
        iv_play = findViewById(R.id.iv_play_voice_evaluation);
        seekBar = findViewById(R.id.seek_bar_voice_evaluation);
        view_white = findViewById(R.id.view_voice_evaluation);
        tv_start = findViewById(R.id.tv_start_time_voice_evaluation);
        tv_end = findViewById(R.id.tv_end_time_voice_evaluation);
        tv_top = findViewById(R.id.tv_top_time_voice_evaluation);
        tv_restart = findViewById(R.id.tv_restart_voice_evaluation);
        tv_commit = findViewById(R.id.tv_commit_voice_evaluation);

        rl_example = findViewById(R.id.rl_example_voice_evaluation);
        iv_play_example = findViewById(R.id.iv_play_example_voice_evaluation);
        seekBar_example = findViewById(R.id.seek_bar_example_voice_evaluation);
        tv_start_example = findViewById(R.id.tv_start_time_example_voice_evaluation);
        tv_end_example = findViewById(R.id.tv_end_time_example_voice_evaluation);
        iv_close_example = findViewById(R.id.iv_close_example_voice_evaluation);

        waveView.setStyle(Paint.Style.FILL);
        waveView.setColor(Color.parseColor("#A04737"));
        seekBar.setMax(100);
        seekBar.setEnabled(false);

        mPlayer = new Player(seekBar_example, tv_start_example, tv_end_example);
        mediaPlayer = new MediaPlayer();

        reset();

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_title.setTypeface(mTypeface);
        tv_tips.setTypeface(mTypeface);
        tv_subtitle.setTypeface(mTypeface);
        tv_content.setTypeface(mTypeface);
        tv_input.setTypeface(mTypeface);
        tv_restart.setTypeface(mTypeface);
        tv_commit.setTypeface(mTypeface);

        if (materialId.equals("2")) {
            view_white.setVisibility(View.VISIBLE);
        } else {
            view_white.setVisibility(View.INVISIBLE);
        }

        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .centerCrop();
            Glide.with(mContext).asBitmap()
                    .load(R.drawable.image_voice_evaluation_bg)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            rl_root.setBackground(drawable);
                        }
                    });
        }
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                iv_play.setImageResource(R.drawable.icon_play_dark_red);
            }
        }

        if (audioRecorder == null) {
            return;
        }

        if (millisecond >= 60 * 1000) {
            return;
        }

        ll_play.setVisibility(View.VISIBLE);
        tv_top.setVisibility(View.VISIBLE);
        tv_start.setText("录制中");
        tv_start.setTextColor(Color.parseColor("#A04737"));
        waveView.start();

        tv_input.setVisibility(View.GONE);
        iv_input.setImageResource(R.drawable.icon_pause_dark_red_large);
        iv_input.setVisibility(View.VISIBLE);
        iv_play.setVisibility(View.GONE);
        tv_restart.setVisibility(View.GONE);
        tv_commit.setVisibility(View.GONE);

        updateTopTextPosition();

        filename = "pythe";
        filePath = getWavFilePath(filename);

        audioRecorder.createDefaultAudio(filename);
        audioRecorder.startRecord();

        timer.start();

        status = 1;

    }

    private String getWavFilePath(String filename) {
//        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename + ".wav";
        return mContext.getFilesDir() + "/" + filename + ".wav";
    }

    private String getMp3FilePath(String filename) {
//        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename + ".mp3";
        return mContext.getFilesDir() + "/" + filename + ".mp3";
    }

    /**
     * 暂停录制
     */
    private void pauseRecord() {
        if (audioRecorder == null) {
            return;
        }

        if (status == 2) {
            return;
        }

        tv_input.setVisibility(View.VISIBLE);
        if (millisecond >= 60 * 1000) {
            tv_input.setText("结束");
        } else {
            tv_input.setText("继续");
        }
        iv_input.setVisibility(View.GONE);
        iv_play.setVisibility(View.VISIBLE);
        waveView.stopImmediately();

        tv_start.setText("00:00");
        tv_start.setTextColor(Color.parseColor("#333333"));

        tv_restart.setVisibility(View.VISIBLE);
        if (millisecond >= 5 * 1000) {
            tv_commit.setVisibility(View.VISIBLE);
        } else {
            tv_commit.setVisibility(View.GONE);
        }

        audioRecorder.stopRecord();
        timer.cancel();

        updateTopTextPosition();

        status = 2;
    }

    /**
     * 停止录音
     */
    private void stopRecord() {

        if (status == 0) {
            return;
        }

        pauseRecord();

        if (audioRecorder != null) {
            audioRecorder.reset();
        }

        status = 0;

    }

    /**
     * 重新录音
     */
    private void reset() {
        ll_play.setVisibility(View.GONE);
        tv_top.setVisibility(View.INVISIBLE);
        tv_start.setText("00:00");
        tv_start.setTextColor(Color.parseColor("#333333"));

        rl_input.setVisibility(View.VISIBLE);
        tv_input.setVisibility(View.VISIBLE);
        tv_input.setText("录制");
        iv_input.setVisibility(View.GONE);
        iv_play.setVisibility(View.GONE);
        tv_restart.setVisibility(View.GONE);
        tv_commit.setVisibility(View.GONE);
        waveView.stop();

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        millisecond = 0;
    }

    /**
     * 上传音频文件
     */
    private void uploadFile() {
        showUploadLoading(true);

        //上传文件时，暂停其他音频播放
        closeExampleAudio();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                iv_play.setImageResource(R.drawable.icon_play_dark_red);
            }
        }

        if (DataUtil.isMp3Ok) {
            File file = new File(getWavFilePath(filename));
            AndroidAudioConverter.with(this)
                    // Your current audio file
                    .setFile(file)

                    // Your desired audio format
                    .setFormat(AudioFormat.MP3)

                    // An callback to know when conversion is finished
                    .setCallback(callback)

                    // Start conversion
                    .convert();
        } else {
            String name = filename + ".wav";
            upload(name, filePath);
        }
    }

    /**
     * 显示上传动画
     *
     * @param show
     */
    private void showUploadLoading(boolean show) {
        if (show) {
            NiceDialog.init()
                    .setLayoutId(R.layout.loading_upload_file_layout)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                            uploadDialog = dialog;
                            ImageView imageView = holder.getView(R.id.iv_loading_upload_file);
                            if (mContext != null && !isDestroyed()) {
                                GlideUtils.loadGIFImageWithNoOptions(mContext,
                                        R.drawable.image_upload_file, imageView);
                            }
                        }
                    })
                    .setMargin(30)
                    .setWidth(300)
                    .setHeight(250)
                    .setOutCancel(false)
                    .show(getSupportFragmentManager());
        } else {
            if (uploadDialog != null) {
                uploadDialog.dismiss();
            }
        }
    }

    /**
     * 上传
     *
     * @param name
     * @param path
     */
    private void upload(String name, String path) {
        if (materialId.equals("2")) {
            new UploadSelfFile(mContext).execute(uploadSelfUrl, name, path,
                    String.valueOf(NewMainActivity.STUDENT_ID));
        } else {
            new UploadFile(mContext).execute(uploadUrl, name, path,
                    String.valueOf(NewMainActivity.STUDENT_ID), materialId);
        }
    }

    private IConvertCallback callback = new IConvertCallback() {
        @Override
        public void onSuccess(File convertedFile) {
            // So fast? Love it!
            String name = convertedFile.getName();
            String path = convertedFile.getAbsolutePath();
            isMp3 = true;
            upload(name, path);
        }

        @Override
        public void onFailure(Exception error) {
            // Oops! Something went wrong
            String name = filename + ".wav";
            upload(name, filePath);
        }
    };

    /**
     * 显示加载数据视图
     *
     * @param show
     */
    private void showLoadingView(boolean show) {
        if (mContext == null) {
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

    /**
     * 录音计时器
     */
    private CountDownTimer timer = new CountDownTimer(62000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            millisecond = millisecond + 1000;
            tv_top.setText(getTimeFormat(millisecond / 1000));
            long pos = seekBar.getMax() * millisecond / (60 * 1000);
            seekBar.setProgress((int) pos);
            if (materialId.equals("2")) {
                if (millisecond >= 4 * 1000) {
                    view_white.setVisibility(View.INVISIBLE);
                } else {
                    view_white.setVisibility(View.VISIBLE);
                }
            }
            if (millisecond >= 60 * 1000) {
                pauseRecord();
            }
        }

        @Override
        public void onFinish() {

        }
    };

    /**
     * 获取标准时间
     *
     * @param seconds
     * @return
     */
    private String getTimeFormat(int seconds) {
        int a = seconds / 60;
        int b = seconds % 60;
        String m = "";
        if (a < 10) {
            m = "0";
        }
        m = m + String.valueOf(a);
        String s = "";
        if (b < 10) {
            s = "0";
        }
        s = s + String.valueOf(b);
        return m + ":" + s;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.pause();
            iv_play_example.setImageResource(R.drawable.icon_play_dark_red);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                iv_play.setImageResource(R.drawable.icon_play_dark_red);
            }
        }
        pauseRecord();
    }

    @Override
    protected void onDestroy() {

        if (audioRecorder != null) {
            audioRecorder.reset();
            audioRecorder.cancel();
            audioRecorder = null;
        }
        if (isMp3) {
            File file = new File(getMp3FilePath(filename));
            if (file.exists()) {
                file.delete();
            }
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }

        super.onDestroy();
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status")) {
                JSONObject object = jsonObject.getJSONObject("data");
                title = object.getString("title");
                audio = object.getString("audio");
                JSONArray array = object.getJSONArray("cot");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    String text = json.getString("content");
                    if (materialId.equals("2")) {
                        if (i == array.length() - 1) {
                            content = content + text;
                        } else {
                            content = content + text + "\n";
                        }
                    } else {
                        if (i == 0) {
                            tips = text;
                        } else if (i == 1) {
                            subtitle = text;
                        } else {
                            if (i == array.length() - 1) {
                                content = content + text;
                            } else {
                                content = content + text + "\n";
                            }
                        }
                    }
                }
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
        tv_title.setText(title);
        tv_content.setText(content);
        if (!materialId.equals("2") && !tips.equals("") && !tips.equals("null")) {
            tv_tips.setText(tips);
            tv_tips.setVisibility(View.VISIBLE);
        } else {
            tv_tips.setVisibility(View.GONE);
        }
        if (!materialId.equals("2") && !subtitle.equals("") && !subtitle.equals("null")) {
            tv_subtitle.setText(subtitle);
            tv_subtitle.setVisibility(View.VISIBLE);
        } else {
            tv_subtitle.setVisibility(View.GONE);
        }
        if (!materialId.equals("2") && !audio.equals("") && !audio.equals("null")) {
            rl_play_example.setVisibility(View.VISIBLE);
        } else {
            rl_play_example.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (mContext == null) {
            return;
        }
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

    private void showTip(String s) {
        MyToastUtil.showToast(this, s);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, VoiceEvaluationActivity> {

        protected GetData(VoiceEvaluationActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("materialId", strings[1]);
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
        protected void onPostExecute(VoiceEvaluationActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 上传自我介绍音频文件
     */
    private static class UploadSelfFile
            extends WeakAsyncTask<String, Integer, String, VoiceEvaluationActivity> {

        protected UploadSelfFile(VoiceEvaluationActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationActivity activity, String[] strings) {
            File file = new File(strings[2]);
            if (file.exists()) {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("formData", strings[1],
                                    RequestBody.create(
                                            MediaType.parse("multipart/form-data"), file))
                            .addFormDataPart("studentId", strings[3])
                            .build();
                    Request request = new Request.Builder()
                            .url(strings[0])
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(VoiceEvaluationActivity activity, String s) {
            activity.showUploadLoading(false);
            if (s == null) {
                activity.errorUpload();
            } else {
                activity.analyzeUploadData(s);
            }
        }
    }

    /**
     * 分析上传数据接口
     *
     * @param s
     */
    private void analyzeUploadData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status")) {
                showTip("上传成功");
                if (isRestart) {
                    finish();
                } else {
                    if (materialId.equals("2")) {
                        turnToVoiceEvaluation();
                    } else {
                        turnToResult();
                    }
                }
            } else if (300 == jsonObject.optInt("status")) {
                errorUpload();
            } else {
                String msg = jsonObject.getString("msg");
                showTip(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorUpload();
        }
    }

    /**
     * 前往朗诵评测
     */
    private void turnToVoiceEvaluation() {
        Intent intent = new Intent(mContext, VoiceEvaluationActivity.class);
        intent.putExtra("materialId", "1");
        startActivity(intent);
        finish();
    }

    /**
     * 前往结果页
     */
    private void turnToResult() {
        startActivity(new Intent(mContext, VoiceEvaluationResultActivity.class));
        finish();
    }

    /**
     * 上传失败
     */
    private void errorUpload() {
        NiceDialog.init()
                .setLayoutId(R.layout.loading_upload_file_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout linearLayout = holder.getView(R.id.ll_content_upload_file);
                        ImageView imageView = holder.getView(R.id.iv_loading_upload_file);
                        TextView textView = holder.getView(R.id.tv_loading_upload_file);
                        textView.setText("上传音频文件失败，请重试~");
                        if (mContext != null && !isDestroyed()) {
                            GlideUtils.loadImageWithNoOptions(mContext,
                                    R.drawable.image_error_correction_failed, imageView);
                        }
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setMargin(30)
                .setWidth(300)
                .setHeight(250)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
        showTip("上传失败");
    }

    /**
     * 上传音频文件
     */
    private static class UploadFile
            extends WeakAsyncTask<String, Integer, String, VoiceEvaluationActivity> {

        protected UploadFile(VoiceEvaluationActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(VoiceEvaluationActivity activity, String[] strings) {
            File file = new File(strings[2]);
            if (file.exists()) {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("formData", strings[1],
                                    RequestBody.create(
                                            MediaType.parse("multipart/form-data"), file))
                            .addFormDataPart("studentId", strings[3])
                            .addFormDataPart("materialId", strings[4])
                            .build();
                    Request request = new Request.Builder()
                            .url(strings[0])
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(VoiceEvaluationActivity activity, String s) {
            activity.showUploadLoading(false);
            if (s == null) {
                activity.errorUpload();
            } else {
                activity.analyzeUploadData(s);
            }
        }
    }

}
