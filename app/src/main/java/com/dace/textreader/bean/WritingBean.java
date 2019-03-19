package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/3/13 0013 下午 4:22.
 * Version   1.0;
 * Describe :  作文
 * History:
 * ==============================================================================
 */

public class WritingBean {

    private String id;  //作文ID
    private String orderNum;  //订单号
    private String title;  //作文标题
    private String content;  //作文内容
    private String cover;  //作文封面
    private String date;  //写作时间
    private int format;
    //作文状态，作文比赛列表0 已结束 1进行中 2征稿中  3评选中，其他列表表示作文分数制0为等级 1为分数
    private int status;
    private String userId;  //写作人ID
    private String username;  //写作人的名字
    private String userGrade;  //写作人年级
    private String userImg;  //写作人的头像
    private int mark;  //作文分数
    private String prize;  //作文获奖情况（参加活动的作文，取缔分数）
    private String comment;  //老师评语
    private String views;  //阅读数量
    private String materialId;  //素材ID
    private int type;
    private int wordsNum;
    private String taskId;  //是否参加了活动
    private String taskName;  //活动名
    private int index;  //0草稿、1发布、2批改、3作业、4活动
    private int isPublic;  //是否公开
    private boolean isEditor;  //是否处于编辑状态
    private boolean isSelected;  //是否是选中状态

    public WritingBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWordsNum() {
        return wordsNum;
    }

    public void setWordsNum(int wordsNum) {
        this.wordsNum = wordsNum;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
