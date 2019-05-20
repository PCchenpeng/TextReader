package com.dace.textreader.audioUtils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.activity.AudioPlayerActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.SubmitReviewActivity;
import com.dace.textreader.bean.LessonBean;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideCircleTransform;
import com.dace.textreader.util.MIUI;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.audioUtils
 * Created by Administrator.
 * Created time 2018/4/18 0018 上午 9:11.
 * Version   1.0;
 * Describe :Service就是用来在后台完成一些不需要和用户交互的动作
 * History:
 * ==============================================================================
 */

public class PlayService extends Service {

    /**
     * 正在播放的歌曲的序号
     */
    private int mPlayingPosition = -1;

    private int mOldPlayPosition = -1;

    private boolean isPlayError = false;

    /**
     * 正在播放的歌曲[本地|网络]
     */
    private LessonBean mPlayingMusic;
    /**
     * 在线音频的集合
     */
    private List<LessonBean> audioMusics;
    /**
     * 播放状态
     */
    private int mPlayState = MusicPlayAction.STATE_IDLE;
    /**
     * 播放器
     */
    private MediaPlayer mPlayer;
    /**
     * 播放进度监听器
     */
    private OnPlayerEventListener mListener;
    /**
     * 更新播放进度的显示，时间的显示
     */
    private static final int UPDATE_PLAY_PROGRESS_SHOW = 0;
    /**
     * 捕获/丢弃音乐焦点处理
     */
    private AudioFocusManager mAudioFocusManager;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private RelativeLayout mFloatView;
    private ImageView mMusicView;
    private ObjectAnimator mAnimator;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PLAY_PROGRESS_SHOW:
                    updatePlayProgressShow();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     *
     * @param intent intent
     * @return IBinder对象
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    /**
     * 比如，广播，耳机声控，通知栏广播，来电或者拔下耳机广播开启服务
     *
     * @param context 上下文
     * @param type    类型
     */
    public static void startCommand(Context context, String type) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(type);
        context.startService(intent);
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        audioMusics = new ArrayList<>();
        createMediaPlayer();
        initAudioFocusManager();
        createOrNotImageButton();
    }

    /**
     * 判断是否有权限创建悬浮窗
     */
    private void createOrNotImageButton() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(getApplicationContext())) {  //有悬浮窗权限才创建悬浮按钮
                createImageButton();
            }
        } else {
            if (MIUI.rom()) {
                if (PermissionUtils.hasPermission(getApplicationContext())) {
                    createImageButton();
                }
            } else {
                createImageButton();
            }
        }
    }

    /**
     * 创建悬浮按钮
     */
    private void createImageButton() {
        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mLayoutParams.format = PixelFormat.RGBA_8888;
        // 设置Window flag
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mLayoutParams.x = DensityUtil.dip2px(getApplicationContext(), 16);
        mLayoutParams.y = DensityUtil.dip2px(getApplicationContext(), 77);
        mLayoutParams.width = DensityUtil.dip2px(getApplicationContext(), 56);
        mLayoutParams.height = DensityUtil.dip2px(getApplicationContext(), 56);

        mFloatView = new RelativeLayout(getApplicationContext());
        ImageView iv_bg = new ImageView(getApplicationContext());
        iv_bg.setLayoutParams(new RelativeLayout.LayoutParams(
                DensityUtil.dip2px(getApplicationContext(), 56),
                DensityUtil.dip2px(getApplicationContext(), 56)));
        iv_bg.setImageResource(R.drawable.image_float_shadow);
        mFloatView.addView(iv_bg);
        mMusicView = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                DensityUtil.dip2px(getApplicationContext(), 44),
                DensityUtil.dip2px(getApplicationContext(), 44));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mMusicView.setLayoutParams(layoutParams);
        mMusicView.setImageResource(R.drawable.icon_audio_default);
        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(5000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        //让旋转动画一直转，不停顿的重点
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);

        mMusicView.setAnimation(rotateAnimation);
        mFloatView.addView(mMusicView);
        mFloatView.setVisibility(View.GONE);
        mWindowManager.addView(mFloatView, mLayoutParams);

        mAnimator = ObjectAnimator.ofFloat(mFloatView, "rotation", 0f, 359f);
        mAnimator.setDuration(3000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(-1);
        mAnimator.start();

        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioMusics.size() == 0) {
                    mFloatView.setVisibility(View.GONE);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        mAnimator.pause();
    }

    /**
     * 隐藏悬浮按钮
     */
    public void hideFloatView() {
        if (mFloatView != null) {
            mFloatView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示悬浮按钮
     */
    public void showFloatView() {
        if (mFloatView != null) {
            mFloatView.setVisibility(View.VISIBLE);
        } else {
            createOrNotImageButton();
        }
    }

    /**
     * 创建MediaPlayer对象
     */
    private void createMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
    }

    /**
     * 捕获/丢弃音乐焦点处理
     */
    private void initAudioFocusManager() {
        mAudioFocusManager = new AudioFocusManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁handler
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (mAnimator != null) {
            mAnimator.pause();
            mAnimator = null;
        }
        if (mFloatView != null && mWindowManager != null) {
            mWindowManager.removeView(mFloatView);
        }
        //销毁MediaPlayer
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        //放弃音频焦点
        mAudioFocusManager.abandonAudioFocus();
    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     *
     * @param intent  intent
     * @param flags   flags
     * @param startId startId
     * @return onStartCommand方法返回值作用：
     * START_STICKY：粘性，service进程被异常杀掉，系统重新创建进程与服务，会重新执行onCreate()、onStartCommand(Intent)
     * START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
     * START_NOT_STICKY：非粘性，Service进程被异常杀掉，系统不会自动重启该Service。
     * START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                //上一首
                case MusicPlayAction.TYPE_PRE:
                    prev();
                    break;
                //下一首
                case MusicPlayAction.TYPE_NEXT:
                    next();
                    break;
                //播放或暂停
                case MusicPlayAction.TYPE_START_PAUSE:
                    playPause();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**---------------------播放或暂停，上一首，下一首-----------------------------------------*/

    /**
     * 播放或暂停
     * 逻辑：
     * 1.如果正在准备，点击则是停止播放
     * 2.如果是正在播放，点击则是暂停
     * 3.如果是暂停状态，点击则是开始播放
     * 4.其他情况是直接播放
     */
    public void playPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(getPlayingPosition());
        }
    }

    /**
     * 上一首
     * 记住有播放类型，单曲循环，顺序循环，随机播放
     * 逻辑：如果不是第一首，则还有上一首；如果没有上一首，则切换到最后一首
     */
    public void prev() {
        //建议都添加这个判断
        if (audioMusics.isEmpty()) {
            MyToastUtil.showToast(getApplicationContext(), "课程列表为空");
            return;
        }
        if (mPlayingPosition == 0) {
            // 如果是第一首，则播放最后一首
            mOldPlayPosition = mPlayingPosition;
            mPlayingPosition = audioMusics.size() - 1;
        } else {
            // 如果不是第一首，则还有上一首
            mOldPlayPosition = mPlayingPosition;
            mPlayingPosition--;
        }
        play(mPlayingPosition);
    }

    /**
     * 下一首
     * 记住有播放类型，单曲循环，顺序循环，随机播放
     * 逻辑：如果不是最后一首，则还有下一首；如果是最后一首，则切换回第一首
     */
    public void next() {
        //建议都添加这个判断
        if (audioMusics.isEmpty()) {
            MyToastUtil.showToast(getApplicationContext(), "课程列表为空");
            return;
        }
        if (mPlayingPosition == audioMusics.size() - 1) {
            // 如果不是最后一首，则从头开始继续播放
            mOldPlayPosition = mPlayingPosition;
            mPlayingPosition = 0;
        } else {
            mOldPlayPosition = mPlayingPosition;
            // 如果不是最后一首，则还有下一首
            mPlayingPosition++;
        }
        play(mPlayingPosition);
    }

    /**---------------------开始播放，暂停播放，停止播放等-----------------------------------------*/

    /**
     * 开始播放
     */
    private void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }
        if (mPlayingMusic == null) {
            return;
        }
        if (mAudioFocusManager.requestAudioFocus()) {
            if (mPlayer != null) {
                mPlayer.start();
                mPlayState = MusicPlayAction.STATE_PLAYING;
                //开始发送消息，执行进度条进度更新
                handler.sendEmptyMessage(UPDATE_PLAY_PROGRESS_SHOW);
                if (mListener != null) {
                    mListener.onPlayerStart();
                }
                if (mAnimator != null) {
                    mAnimator.resume();
                }
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mPlayingMusic == null) {
            return;
        }
        if (mPlayer != null) {
            //暂停
            mPlayer.pause();
            //切换状态
            mPlayState = MusicPlayAction.STATE_PAUSE;
            //移除，注意一定要移除，否则一直走更新方法
            handler.removeMessages(UPDATE_PLAY_PROGRESS_SHOW);
            //监听
            if (mListener != null) {
                mListener.onPlayerPause();
            }
            if (mAnimator != null) {
                mAnimator.pause();
            }
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (isDefault()) {
            return;
        }
        pause();
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayState = MusicPlayAction.STATE_IDLE;
        }
    }

    /**
     * 播放索引为position的音乐
     *
     * @param position 索引
     */
    public void play(int position) {
        if (audioMusics.isEmpty()) {
            return;
        }
        if (isPlayError) {
            isPlayError = false;
            return;
        }
        if (position == -1 || position >= audioMusics.size()) {
            return;
        }
        LessonBean music = audioMusics.get(position);
        if (music.getFree() != 0) {
            mPlayingPosition = position;
            play(music);
        } else {
            mPlayingPosition = mOldPlayPosition;
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(getApplicationContext())) {  //有权限才创建全局对话框
                    showNeedBuyLessonDialog();
                } else {
                    MyToastUtil.showToast(getApplicationContext(), "此节课程需要购买后才能学习");
                }
            } else {
                MyToastUtil.showToast(getApplicationContext(), "此节课程需要购买后才能学习");
            }
        }
    }

    /**
     * 拖动seekBar时，调节进度
     *
     * @param progress 进度
     */
    public void seekTo(int progress) {
        //只有当播放或者暂停的时候才允许拖动bar
        if (isPlaying() || isPausing()) {
            mPlayer.seekTo(progress);
            if (mListener != null) {
                mListener.onUpdateProgress(progress, mPlayer.getDuration());
            }
        }
    }

    /**
     * 播放，这种是直接传音频实体类
     * 有两种，一种是播放本地播放，另一种是在线播放
     *
     * @param music music
     */
    public void play(LessonBean music) {
        if (isPlayError) {
            isPlayError = false;
            return;
        }
        mPlayingMusic = music;
        createMediaPlayer();
        try {
            mPlayer.reset();
            //把音频路径传给播放器
            mPlayer.setDataSource(DataEncryption.audioEncode(mPlayingMusic.getMedia()));
            //准备
            mPlayer.prepareAsync();
            //设置状态为准备中
            mPlayState = MusicPlayAction.STATE_PREPARING;
            //监听
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mPlayer.setOnCompletionListener(mOnCompletionListener);
            mPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            mPlayer.setOnErrorListener(mOnErrorListener);
            mPlayer.setOnInfoListener(mOnInfoListener);
            //当播放的时候，需要刷新界面信息
            if (mListener != null) {
                mListener.onChange(mPlayingPosition, mPlayingMusic);
            }
            if (mOnPlayNumNeedUpdate != null) {
                mOnPlayNumNeedUpdate.update(mPlayingMusic);
            }
            if (mFloatView != null) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.icon_audio_default)
                        .error(R.drawable.icon_audio_default)
                        .transform(new GlideCircleTransform(getApplicationContext()));
                Glide.with(getApplicationContext())
                        .load(mPlayingMusic.getImage())
                        .apply(options)
                        .into(mMusicView);
            }
            if (mAnimator != null) {
                mAnimator.resume();
            }
            updatePlayerList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示需要购买课程的对话框
     */
    private void showNeedBuyLessonDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(PlayService.this).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        View view = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.dialog_buy_lesson_layout, null);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel_buy_lesson_dialog);
        TextView tv_sure = view.findViewById(R.id.tv_sure_buy_lesson_dialog);
        if (NewMainActivity.STUDENT_ID == -1) {
            tv_sure.setText("先登录");
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    pause();
                    hideFloatView();
                } else {
                    Intent intent = new Intent(getApplicationContext(), SubmitReviewActivity.class);
                    intent.putExtra("type", "lesson");
                    intent.putExtra("lessonId", NewMainActivity.lessonId);
                    intent.putExtra("lessonPrice", NewMainActivity.lessonPrice);
                    intent.putExtra("lessonOriginalPrice", NewMainActivity.lessonOriginalPrice);
                    intent.putExtra("lessonTitle", NewMainActivity.lessonTitle);
                    intent.putExtra("lessonTeacher", NewMainActivity.lessonTeacher);
                    intent.putExtra("lessonCount", audioMusics.size());
                    intent.putExtra("fromService", true);
                    startActivity(intent);
                    pause();
                    hideFloatView();
                }
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 更新播放列表
     */
    private void updatePlayerList() {
        for (int i = 0; i < audioMusics.size(); i++) {
            audioMusics.get(i).setPlaying(false);
        }
        audioMusics.get(mPlayingPosition).setPlaying(true);
    }

    /**
     * 播放，这种是传音频实体类集合
     * 有两种，一种是播放本地播放，另一种是在线播放
     *
     * @param music music
     */
    public void play(List<LessonBean> music, int position) {
        if (music == null || music.size() == 0 || position < 0) {
            return;
        }
        if (audioMusics == null) {
            audioMusics = new ArrayList<>();
        }
        if (!audioMusics.isEmpty()) {
            audioMusics.clear();
        }
        audioMusics.addAll(music);
        //赋值
        mPlayingMusic = music.get(position);
        if (mPlayingMusic.getFree() != 0) {
            mPlayingPosition = position;
            createMediaPlayer();
            try {
                mPlayer.reset();
                //把音频路径传给播放器
                mPlayer.setDataSource(DataEncryption.audioEncode(mPlayingMusic.getMedia()));
                //准备
                mPlayer.prepareAsync();
                //设置状态为准备中
                mPlayState = MusicPlayAction.STATE_PREPARING;
                //监听
                mPlayer.setOnPreparedListener(mOnPreparedListener);
                mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
                mPlayer.setOnCompletionListener(mOnCompletionListener);
                mPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
                mPlayer.setOnErrorListener(mOnErrorListener);
                mPlayer.setOnInfoListener(mOnInfoListener);
                //当播放的时候，需要刷新界面信息
                if (mListener != null) {
                    mListener.onChange(mPlayingPosition, mPlayingMusic);
                }
                if (mOnPlayNumNeedUpdate != null) {
                    mOnPlayNumNeedUpdate.update(mPlayingMusic);
                }
                if (mFloatView != null) {
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.icon_audio_default)
                            .error(R.drawable.icon_audio_default)
                            .transform(new GlideCircleTransform(getApplicationContext()));
                    Glide.with(getApplicationContext())
                            .load(mPlayingMusic.getImage())
                            .apply(options)
                            .into(mMusicView);
                }
                if (mAnimator != null) {
                    mAnimator.resume();
                }
                updatePlayerList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(getApplicationContext())) {  //有权限才创建全局对话框
                    showNeedBuyLessonDialog();
                }
            } else {
                MyToastUtil.showToast(getApplicationContext(), "此节课程需要购买后才能学习");
            }
        }
    }

    /**
     * 更新播放列表
     *
     * @param music
     */
    public void updatePlayList(List<LessonBean> music) {
        if (music == null || music.size() == 0) {
            return;
        }
        if (audioMusics == null) {
            audioMusics = new ArrayList<>();
        }
        if (!audioMusics.isEmpty()) {
            audioMusics.clear();
        }
        audioMusics.addAll(music);
    }

    /**
     * 更新播放进度的显示，时间的显示
     */
    private void updatePlayProgressShow() {
        if (isPlaying() && mListener != null) {
            int currentPosition = mPlayer.getCurrentPosition();
            int duration = mPlayer.getDuration();
            mListener.onUpdateProgress(currentPosition, duration);
        }
        // 每30毫秒更新一下显示的内容，注意这里时间不要太短，因为这个是一个循环
        // 经过测试，60毫秒更新一次有点卡，30毫秒最为顺畅
        handler.sendEmptyMessageDelayed(UPDATE_PLAY_PROGRESS_SHOW, 300);
    }

    /**
     * 音频准备好的监听器
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        /** 当音频准备好可以播放了，则这个方法会被调用  */
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };

    /**
     * 当音频播放结束的时候的监听器
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        /** 当音频播放结果的时候这个方法会被调用 */
        @Override
        public void onCompletion(MediaPlayer mp) {
            pause();
            next();
        }
    };

    /**
     * 当音频缓冲的监听器
     */
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mListener != null) {
                // 缓冲百分比
                mListener.onBufferingUpdate(percent);
            }
        }
    };

    /**
     * 跳转完成时的监听
     */
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }
    };

    /**
     * 播放错误的监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            pause();
            stop();
            return false;
        }
    };

    /**
     * 设置音频信息监听器
     */
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    /**
     * 是否正在播放
     *
     * @return true表示正在播放
     */
    public boolean isPlaying() {
        return mPlayState == MusicPlayAction.STATE_PLAYING;
    }


    /**
     * 是否暂停
     *
     * @return true表示暂停
     */
    public boolean isPausing() {
        return mPlayState == MusicPlayAction.STATE_PAUSE;
    }


    /**
     * 是否正在准备中
     *
     * @return true表示正在准备中
     */
    public boolean isPreparing() {
        return mPlayState == MusicPlayAction.STATE_PREPARING;
    }

    /**
     * 是否正在准备中
     *
     * @return true表示正在准备中
     */
    public boolean isDefault() {
        return mPlayState == MusicPlayAction.STATE_IDLE;
    }

    /**------------------------------------------------------------------------------------------*/

    /**
     * 退出时候调用
     */
    public void quit() {
        // 先停止播放
        stop();
        // 当另一个组件（如 Activity）通过调用 startService() 请求启动服务时，系统将调用onStartCommand。
        // 一旦执行此方法，服务即会启动并可在后台无限期运行。 如果自己实现此方法，则需要在服务工作完成后，
        // 通过调用 stopSelf() 或 stopService() 来停止服务。
        stopSelf();
    }

    /**
     * 获取正在播放的本地歌曲的序号
     */
    public int getPlayingPosition() {
        return mPlayingPosition;
    }


    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public LessonBean getPlayingMusic() {
        return mPlayingMusic;
    }

    /**
     * 获取正在播放的歌曲列表[本地|网络]
     */
    public List<LessonBean> getMusicList() {
        return audioMusics;
    }

    /**
     * 获取播放的进度
     */
    public int[] getCurrentPosition() {
        if (isPlaying() || isPausing()) {
            return new int[]{mPlayer.getCurrentPosition(), mPlayer.getDuration()};
        } else {
            return new int[]{0, 0};
        }
    }

    /**
     * 获取播放进度监听器对象
     *
     * @return OnPlayerEventListener对象
     */
    public OnPlayerEventListener getOnPlayEventListener() {
        return mListener;
    }

    /**
     * 设置播放进度监听器
     *
     * @param listener listener
     */
    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    public interface OnPlayNumNeedUpdate {
        void update(LessonBean lessonBean);
    }

    private OnPlayNumNeedUpdate mOnPlayNumNeedUpdate;

    public void setOnPlayNumNeedUpdateListener(OnPlayNumNeedUpdate onPlayNumNeedUpdate) {
        this.mOnPlayNumNeedUpdate = onPlayNumNeedUpdate;
    }
}
