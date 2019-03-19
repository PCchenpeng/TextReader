package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * 文章
 * Created by 70391 on 2017/7/25.
 */

public class Article extends LitePalSupport {

    private long essayId;  //文章id
    private int type;  //文章类型
    private String title;  //文章标题
    private String content;  //文章内容
    private int grade;  //文章等级
    private String pyScore;  //PY值
    private int likeNum;  //文章被点赞次数
    private int views;  //文章浏览次数
    private String imagePath;  //文章图片地址
    private String time;  //文章内容改变时间
    private int status;  //文章是否已读，0表示未读，1表示已读
    private boolean isEditor;  //是否处于编辑状态
    private boolean isSelected;  //是否是选中状态

    public Article() {
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public long getId() {
        return essayId;
    }

    public void setId(long essayId) {
        this.essayId = essayId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getPyScore() {
        return pyScore;
    }

    public void setPyScore(String pyScore) {
        this.pyScore = pyScore;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
