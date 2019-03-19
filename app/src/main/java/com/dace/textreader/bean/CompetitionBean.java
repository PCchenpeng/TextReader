package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/4/25 0025 下午 4:28.
 * Version   1.0;
 * Describe :  活动、比赛
 * History:
 * ==============================================================================
 */

public class CompetitionBean {

    private String id;  //比赛、活动ID
    private String title;  //标题
    private String content;  //内容
    private String image;  //宣传图
    private int status;  //活动状态  0表示已结束、1表示征稿中、2表示评选中,-1表示已有活动但暂未开放
    private boolean isSelected;  //是否选中

    public CompetitionBean() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
