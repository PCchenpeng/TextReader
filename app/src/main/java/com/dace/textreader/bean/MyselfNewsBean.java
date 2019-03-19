package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/9/21 0021 下午 3:47.
 * Version   1.0;
 * Describe :  我的消息
 * History:
 * ==============================================================================
 */

public class MyselfNewsBean {

    private long id;
    private int isViewed;
    private String time;
    private String title;
    private int status;
    private int type;

    public MyselfNewsBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(int isViewed) {
        this.isViewed = isViewed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
