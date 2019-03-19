package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2017 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2017/12/7 0007 下午 4:04.
 * Version   1.0;
 * Describe :  课内文章类
 * History:
 * ==============================================================================
 */

public class Classes {

    private long id;
    private int type;
    private int likeNum;
    private String title;
    private String content;
    private String author;
    private String imagePath;

    public Classes() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
