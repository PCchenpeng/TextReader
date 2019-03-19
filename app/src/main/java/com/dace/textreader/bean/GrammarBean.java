package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/10/31 0031 下午 3:02.
 * Version   1.0;
 * Describe :  语法
 * History:
 * ==============================================================================
 */
public class GrammarBean {

    private String grammarId;
    private String grammar;
    private String content;

    private long essayId;
    private int essayType;
    private String essayTitle;

    private String date;

    private long userId;
    private String username;

    private boolean isSelected;
    private boolean isEditor;

    public GrammarBean() {
    }

    public String getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(String grammarId) {
        this.grammarId = grammarId;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getEssayId() {
        return essayId;
    }

    public void setEssayId(long essayId) {
        this.essayId = essayId;
    }

    public int getEssayType() {
        return essayType;
    }

    public void setEssayType(int essayType) {
        this.essayType = essayType;
    }

    public String getEssayTitle() {
        return essayTitle;
    }

    public void setEssayTitle(String essayTitle) {
        this.essayTitle = essayTitle;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }
}
