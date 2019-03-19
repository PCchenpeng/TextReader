package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/10/23 0023 下午 4:42.
 * Version   1.0;
 * Describe :  首页推荐页数据类
 * History:
 * ==============================================================================
 */
public class HomeRecommendationBean extends LitePalSupport {

    private int type;  //数据类型,0作文，1阅读，2微课，3比赛或者活动或者广告
    private String compositionId;  //作文id
    private int compositionArea;  //作文区域
    private String compositionScore;  //作文分数
    private String compositionPrize;  //作文获奖内容
    private String compositionAvgScore;  //作文用户评分
    private String title;  //标题
    private String content;  //内容
    private String image;  //图片
    private String date;  //时间
    private String views;  //浏览数量
    private long userId;  //用户id
    private String userName;  //用户名
    private String userImage;  //用户头像
    private String userGrade; //用户年级

    public HomeRecommendationBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompositionId() {
        return compositionId;
    }

    public void setCompositionId(String compositionId) {
        this.compositionId = compositionId;
    }

    public int getCompositionArea() {
        return compositionArea;
    }

    public void setCompositionArea(int compositionArea) {
        this.compositionArea = compositionArea;
    }

    public String getCompositionScore() {
        return compositionScore;
    }

    public void setCompositionScore(String compositionScore) {
        this.compositionScore = compositionScore;
    }

    public String getCompositionPrize() {
        return compositionPrize;
    }

    public void setCompositionPrize(String compositionPrize) {
        this.compositionPrize = compositionPrize;
    }

    public String getCompositionAvgScore() {
        return compositionAvgScore;
    }

    public void setCompositionAvgScore(String compositionAvgScore) {
        this.compositionAvgScore = compositionAvgScore;
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

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }
}
