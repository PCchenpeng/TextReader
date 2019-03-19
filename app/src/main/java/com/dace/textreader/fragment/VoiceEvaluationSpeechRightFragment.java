package com.dace.textreader.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.VoiceErrorWordBean;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.Player;
import com.dace.textreader.view.voice.SLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/12/26 0026 上午 11:24.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class VoiceEvaluationSpeechRightFragment extends Fragment {

    private View view;
    private RelativeLayout rl_play;
    private ImageView iv_play_bg;
    private ImageView iv_play;
    private SLoadingIndicatorView loadingIndicatorView;
    private RelativeLayout rl_play_control;
    private SeekBar seekBar;
    private TextView tv_left;
    private TextView tv_right;
    private TextView tv_content;

    private Context mContext;

    private String audioUrl = "";
    private String content = "";
    private List<VoiceErrorWordBean> mList = new ArrayList<>();

    private Player mPlayer;
    private boolean isPlay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_voice_evaluation_speech_right, container, false);

        initView();

        mPlayer = new Player(seekBar, tv_left, tv_right);

        initEvents();

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setContent(String content, List<VoiceErrorWordBean> list) {
        this.content = content;
        mList.addAll(list);
        if (tv_content != null) {
            formatData();
        }
    }

    /**
     * 格式化数据
     */
    private void formatData() {
        tv_content.setText(content);
//        DataUtil.formatVoiceErrorWords(content, mList);
//        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
//        for (int i = 0; i < mList.size(); i++) {
//            int start = mList.get(i).getIndex();
//            if (start >= 0 && start < content.length() - 1) {
//                ForegroundColorSpan foregroundColorSpan =
//                        new ForegroundColorSpan(Color.parseColor("#A5120C"));
//                ssb.setSpan(foregroundColorSpan, start, start + 1,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//        tv_content.setText(ssb);
    }

    private void initEvents() {
        rl_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        mPlayer.setMediaPlayerFinish(new Player.MediaPlayerFinish() {
            @Override
            public void onFinish() {
                audioPause();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isPlay) {
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
                tv_left.setText(DateUtil.formatterTime(progress));
            }
        });
    }

    /**
     * 点击播放
     */
    private void play() {
        if (audioUrl.equals("")) {
            MyToastUtil.showToast(mContext, "音频错误");
            return;
        }
        if (isPlay) {
            if (mPlayer.mediaPlayer.isPlaying()) {
                mPlayer.pause();
                audioPause();
            } else {
                mPlayer.play();
                audioStart();
            }
        } else {
            mPlayer.play(audioUrl);
            audioStart();
            isPlay = true;
        }
    }

    /**
     * 音频开始
     */
    private void audioStart() {
        iv_play.setVisibility(View.GONE);
        loadingIndicatorView.setVisibility(View.VISIBLE);
        if (rl_play_control.getVisibility() == View.GONE) {
            rl_play_control.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 音频暂停
     */
    private void audioPause() {
        loadingIndicatorView.setVisibility(View.GONE);
        iv_play.setVisibility(View.VISIBLE);
    }

    private void initView() {
        rl_play = view.findViewById(R.id.rl_play_voice_evaluation_speech_right_fragment);
        iv_play_bg = view.findViewById(R.id.iv_play_bg_voice_evaluation_speech_right_fragment);
        iv_play = view.findViewById(R.id.iv_play_voice_evaluation_speech_right_fragment);
        loadingIndicatorView = view.findViewById(R.id.audio_column_view_voice_evaluation_speech_right_fragment);
        rl_play_control = view.findViewById(R.id.rl_voice_evaluation_play);
        seekBar = view.findViewById(R.id.seek_bar_voice_evaluation_play);
        tv_left = view.findViewById(R.id.tv_left_voice_evaluation_play);
        tv_right = view.findViewById(R.id.tv_right_voice_evaluation_play);
        tv_content = view.findViewById(R.id.tv_content_voice_evaluation_speech_right_fragment);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_content.setTypeface(mTypeface);

        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_voice_play_bg, iv_play_bg);

        formatData();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.pause();
            audioPause();
        }
    }

    @Override
    public void onDestroy() {

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }

        super.onDestroy();
    }

}
