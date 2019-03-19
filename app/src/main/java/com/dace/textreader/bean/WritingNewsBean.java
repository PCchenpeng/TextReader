package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/9/21 0021 下午 3:31.
 * Version   1.0;
 * Describe :  作文通知消息
 * History:
 * ==============================================================================
 */

public class WritingNewsBean {

    private String id;
    private String title;
    private String time;
    private int type;
    private int status;
    private String orderNum;
    private int isViewed;

    public WritingNewsBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public int getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(int isViewed) {
        this.isViewed = isViewed;
    }
}
