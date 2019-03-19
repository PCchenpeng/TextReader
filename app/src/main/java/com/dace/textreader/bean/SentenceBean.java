package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/5/16 0016 下午 3:47.
 * Version   1.0;
 * Describe :  每日一句
 * History:
 * ==============================================================================
 */

public class SentenceBean {

    private long id;
    private String date;
    private String author;
    private String content;
    private boolean collectOrNot;  //收藏与否
    private boolean isEditor;  //是否处于编辑状态
    private boolean isSelected;  //是否是选中状态

    public SentenceBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCollectOrNot() {
        return collectOrNot;
    }

    public void setCollectOrNot(boolean collectOrNot) {
        this.collectOrNot = collectOrNot;
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
