package com.dace.textreader.bean;

/**
 * 更多推荐文章
 * Created by 70391 on 2017/7/25.
 */

public class MoreArticle {

    private long id;  //文章id
    private int type;  //文章类型
    private String title;  //文章标题
    private String content;  //文章内容
    private String correlation;  //内容相关性
    private String imagePath;  //文章图片
    private int grade;  //文章等级
    private String pyScore;  //PY值
    private int views;  //文章浏览次数

    public MoreArticle() {
    }

    public String getCorrelation() {
        return correlation;
    }

    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
}
