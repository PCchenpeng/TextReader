package com.dace.textreader.util;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * =============================================================================
 * Copyright (c) 2017 Administrator All rights reserved.
 * Packname com.fjg.open.opensourceproject.utils
 * Created by Administrator.
 * Created time 2017/12/25 0025 下午 5:05.
 * Version   1.0;
 * Describe :  音频播放控制器
 * History:
 * ==============================================================================
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    public MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView tv_cur;
    private TextView tv_total;
    private float mSpeed = 1f;  //播放速率

    private Timer mTimer = new Timer();

    public Player(SeekBar seekBar, TextView tv_cur, TextView tv_total) {
        this.seekBar = seekBar;
        this.tv_cur = tv_cur;
        this.tv_total = tv_total;
        init();
        mTimer.schedule(timerTask, 0, 1000);
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mMediaPlayerFinish != null) {
                    mMediaPlayerFinish.onFinish();
                }
                return false;
            }
        });
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying() && !seekBar.isPressed()) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mediaPlayer != null && seekBar != null && tv_cur != null) {
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (duration > 0) {
                    // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                    long pos = seekBar.getMax() * position / duration;
                    seekBar.setProgress((int) pos);
                    tv_cur.setText(DateUtil.formatterTime(position));
                }
            }
        }
    };

    public void prepare() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void playUrl(String url) {
        if (mediaPlayer == null) {
            init();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String url) {
        if (mediaPlayer == null) {
            init();
        }
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setPlayerSpeed(float speed) {
        this.mSpeed = speed;
    }

    public void changePlayerSpeed(float speed) {
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                mediaPlayer.pause();
            }
            this.mSpeed = speed;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        int duration = mediaPlayer.getDuration();
        tv_total.setText(DateUtil.formatterTime(duration));
        mp.start();
        changePlayerSpeed(mSpeed);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        tv_cur.setText(tv_total.getText().toString());
        seekBar.setProgress(seekBar.getMax());
        if (mMediaPlayerFinish != null) {
            mMediaPlayerFinish.onFinish();
        }
    }

    public interface MediaPlayerFinish {
        void onFinish();
    }

    private MediaPlayerFinish mMediaPlayerFinish;

    public void setMediaPlayerFinish(MediaPlayerFinish mediaPlayerFinish) {
        this.mMediaPlayerFinish = mediaPlayerFinish;
    }
}
