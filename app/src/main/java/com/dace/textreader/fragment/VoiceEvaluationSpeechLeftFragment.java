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
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.VoiceEvaluationSpeechBean;
import com.dace.textreader.util.DataUtil;
import com.willy.ratingbar.ScaleRatingBar;

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
public class VoiceEvaluationSpeechLeftFragment extends Fragment {

    private View view;
    private TextView tv_score;
    private TextView tv_error;
    private TextView tv_duration;
    private TextView tv_vocal_score;
    private ScaleRatingBar ratingBar_vocal;
    private TextView tv_smooth_score;
    private ScaleRatingBar ratingBar_smooth;
    private TextView tv_moderation_score;
    private ScaleRatingBar ratingBar_moderation;
    private TextView tv_complete_score;
    private ScaleRatingBar ratingBar_complete;
    private TextView tv_issue;
    private TextView tv_content;

    private Context mContext;

    private VoiceEvaluationSpeechBean bean;
    private boolean isReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_voice_evaluation_speech_left, container, false);

        initView();

        isReady = true;

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView() {
        tv_score = view.findViewById(R.id.tv_score_voice_evaluation_speech_left_fragment);
        tv_error = view.findViewById(R.id.tv_error_voice_evaluation_speech_left_fragment);
        tv_duration = view.findViewById(R.id.tv_duration_voice_evaluation_speech_left_fragment);
        tv_vocal_score = view.findViewById(R.id.tv_vocal_score_voice_evaluation_speech_left_fragment);
        ratingBar_vocal = view.findViewById(R.id.ratingBar_vocal_score_voice_evaluation_speech_left_fragment);
        tv_smooth_score = view.findViewById(R.id.tv_smooth_score_voice_evaluation_speech_left_fragment);
        ratingBar_smooth = view.findViewById(R.id.ratingBar_smooth_score_voice_evaluation_speech_left_fragment);
        tv_moderation_score = view.findViewById(R.id.tv_moderation_score_voice_evaluation_speech_left_fragment);
        ratingBar_moderation = view.findViewById(R.id.ratingBar_moderation_score_voice_evaluation_speech_left_fragment);
        tv_complete_score = view.findViewById(R.id.tv_complete_score_voice_evaluation_speech_left_fragment);
        ratingBar_complete = view.findViewById(R.id.ratingBar_complete_score_voice_evaluation_speech_left_fragment);
        tv_issue = view.findViewById(R.id.tv_issue_voice_evaluation_speech_left_fragment);
        tv_content = view.findViewById(R.id.tv_content_voice_evaluation_speech_left_fragment);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");

        tv_error.setTypeface(mTypeface);
        tv_duration.setTypeface(mTypeface);
        tv_vocal_score.setTypeface(mTypeface);
        tv_smooth_score.setTypeface(mTypeface);
        tv_moderation_score.setTypeface(mTypeface);
        tv_complete_score.setTypeface(mTypeface);
        tv_issue.setTypeface(mTypeface);
        tv_content.setTypeface(mTypeface);

        if (bean != null) {
            updateUi();
        }
    }

    public void setInfo(VoiceEvaluationSpeechBean voiceEvaluationSpeechBean) {
        this.bean = voiceEvaluationSpeechBean;
        if (isReady) {
            updateUi();
        }
    }

    /**
     * 更细UI
     */
    private void updateUi() {
        tv_score.setText(DataUtil.double2IntString(bean.getScore()));
        String error = "错字：" + bean.getErrorLength() + "字";
        tv_error.setText(error);
        String duration = "用时：" + bean.getDuration();
        tv_duration.setText(duration);
        ratingBar_vocal.setRating(DataUtil.double2Float(bean.getVocal_score()));
        ratingBar_smooth.setRating(DataUtil.double2Float(bean.getSmooth_score()));
        ratingBar_moderation.setRating(DataUtil.double2Float(bean.getModeration_score()));
        ratingBar_complete.setRating(DataUtil.double2Float(bean.getComplete_score()));
        tv_content.setText(bean.getContent());
    }

}
