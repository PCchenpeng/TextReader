package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/3/19 0019 下午 2:46.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class MaterialBean {

    private String id;  //素材ID
    private long essayId;
    private int essayType;
    private String title;  //文章标题
    private String image;
    private String time;  //文章最后修改时间
    private String score;


    public MaterialBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEssayId() {
        return essayId;
    }

    public void setEssayId(long essayId) {
        this.essayId = essayId;
    }

    public int getEssayType() {
        return essayType;
    }

    public void setEssayType(int essayType) {
        this.essayType = essayType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
