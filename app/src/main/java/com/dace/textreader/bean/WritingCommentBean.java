package com.dace.textreader.bean;

/**
 * 评论
 * Created by 70391 on 2017/7/28.
 */

public class WritingCommentBean {

    private long commentId;  //评论ID
    private String commentTime;  //评论时间
    private String commentContent;  //评论内容
    private long commentUserId;  //用户ID
    private String commentUsername;  //用户名
    private String commentUserImg;  //用户头像
    private int replyCommentId;  //被回复的评论ID
    private String replyCommentTime;  //被回复的评论的发表时间
    private long replyUserId;  //被回复人的ID
    private String replyUsername;  //被回复人的名字
    private String replyUserImg;  //被回复人的头像
    private String replyCommentContent;  //被回复的评论的内容
    private String id;
    private int type;

    public WritingCommentBean() {
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public long getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(long commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getCommentUserImg() {
        return commentUserImg;
    }

    public void setCommentUserImg(String commentUserImg) {
        this.commentUserImg = commentUserImg;
    }

    public int getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(int replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getReplyCommentTime() {
        return replyCommentTime;
    }

    public void setReplyCommentTime(String replyCommentTime) {
        this.replyCommentTime = replyCommentTime;
    }

    public long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public String getReplyUserImg() {
        return replyUserImg;
    }

    public void setReplyUserImg(String replyUserImg) {
        this.replyUserImg = replyUserImg;
    }

    public String getReplyCommentContent() {
        return replyCommentContent;
    }

    public void setReplyCommentContent(String replyCommentContent) {
        this.replyCommentContent = replyCommentContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
