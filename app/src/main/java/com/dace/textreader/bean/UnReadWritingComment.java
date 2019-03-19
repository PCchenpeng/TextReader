package com.dace.textreader.bean;

/**
 * 作文未读消息
 * Created by 70391 on 2017/8/13.
 */

public class UnReadWritingComment {

    private long commentId;  //评论ID
    private String commentTime;  //评论时间
    private String commentContent;  //评论内容
    private int commentUserId;  //用户ID
    private String commentUsername;  //用户名
    private String commentUserImg;  //用户头像
    private int replyCommentId;  //被回复的评论ID
    private String replyCommentTime;  //被回复的评论的发表时间
    private int replyUserId;  //被回复人的ID
    private String replyUsername;  //被回复人的名字
    private String replyUserImg;  //被回复人的头像
    private String replyCommentContent;  //被回复的评论的内容
    private int reReplyUserId;
    private String reReplyUsername;
    private String reReplyUserImg;
    private String id;
    private int type;
    private String title;

    public UnReadWritingComment() {
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

    public int getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(int commentUserId) {
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

    public int getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(int replyUserId) {
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

    public int getReReplyUserId() {
        return reReplyUserId;
    }

    public void setReReplyUserId(int reReplyUserId) {
        this.reReplyUserId = reReplyUserId;
    }

    public String getReReplyUsername() {
        return reReplyUsername;
    }

    public void setReReplyUsername(String reReplyUsername) {
        this.reReplyUsername = reReplyUsername;
    }

    public String getReReplyUserImg() {
        return reReplyUserImg;
    }

    public void setReReplyUserImg(String reReplyUserImg) {
        this.reReplyUserImg = reReplyUserImg;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
