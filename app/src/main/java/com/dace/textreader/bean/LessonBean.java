package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/3/14 0014 下午 4:46.
 * Version   1.0;
 * Describe :  课程
 * History:
 * ==============================================================================
 */

public class LessonBean {

    private String id;  //课程ID
    private String name;  //课程名称
    private String description;  //课程描述
    private String media;  //课程音频
    private String duration;  //课程时长
    private String image;  //课程图片
    private int free;  //课程音频是否可以试听
    private int playNum;  //课程音频播放次数
    private int status;  //文稿状态，0为没有文稿
    private boolean isPlaying;  //是否正在播放中

    public LessonBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
