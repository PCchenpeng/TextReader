package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/10/25 0025 上午 10:23.
 * Version   1.0;
 * Describe :  用户
 * History:
 * ==============================================================================
 */
public class UserBean {

    private long userId;
    private String username;
    private String userImage;
    private String userGrade;
    private String userDescription;
    private String userBackgroundImage;
    private String followerNum;  //粉丝数量
    private String compositionNum;  //作文数量
    private String followingNum;  //关注人数
    private int followed;  //是否关注 0否1是

    public UserBean() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserBackgroundImage() {
        return userBackgroundImage;
    }

    public void setUserBackgroundImage(String userBackgroundImage) {
        this.userBackgroundImage = userBackgroundImage;
    }

    public String getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(String followerNum) {
        this.followerNum = followerNum;
    }

    public String getCompositionNum() {
        return compositionNum;
    }

    public void setCompositionNum(String compositionNum) {
        this.compositionNum = compositionNum;
    }

    public String getFollowingNum() {
        return followingNum;
    }

    public void setFollowingNum(String followingNum) {
        this.followingNum = followingNum;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }
}
