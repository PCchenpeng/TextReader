package com.dace.textreader.bean;

/**
 * 读后感
 * Created by 70391 on 2017/9/29.
 */

public class AfterReading {

    private String id;  //学生ID_文章ID
    private String userImg;     //用户的头像
    private String username;  //用户名
    private String date;  //时间
    private int likeNum;  //点赞数
    private int isLiked;  //是否点赞过
    private String feeling;  //读后感内容
    private int ispriviate;  //1表示公开，0表示私密

    public AfterReading() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int isLiked() {
        return isLiked;
    }

    public void setLiked(int liked) {
        isLiked = liked;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public int getIspriviate() {
        return ispriviate;
    }

    public void setIspriviate(int ispriviate) {
        this.ispriviate = ispriviate;
    }
}
