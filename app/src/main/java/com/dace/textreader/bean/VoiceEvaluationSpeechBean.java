package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/12/26 0026 下午 1:59.
 * Version   1.0;
 * Describe :  语音评测结果
 * History:
 * ==============================================================================
 */
public class VoiceEvaluationSpeechBean {

    private double score;  //总分
    private int errorLength;  //错误字符数
    private String duration;  //用时时长
    private double vocal_score;  //声韵分
    private double smooth_score;  //流畅分
    private double moderation_score;  //调型分
    private double complete_score;  //完整分
    private String content;  //评估及建议

    public VoiceEvaluationSpeechBean() {
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getErrorLength() {
        return errorLength;
    }

    public void setErrorLength(int errorLength) {
        this.errorLength = errorLength;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getVocal_score() {
        return vocal_score;
    }

    public void setVocal_score(double vocal_score) {
        this.vocal_score = vocal_score;
    }

    public double getSmooth_score() {
        return smooth_score;
    }

    public void setSmooth_score(double smooth_score) {
        this.smooth_score = smooth_score;
    }

    public double getModeration_score() {
        return moderation_score;
    }

    public void setModeration_score(double moderation_score) {
        this.moderation_score = moderation_score;
    }

    public double getComplete_score() {
        return complete_score;
    }

    public void setComplete_score(double complete_score) {
        this.complete_score = complete_score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
