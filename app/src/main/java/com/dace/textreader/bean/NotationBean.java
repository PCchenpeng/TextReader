package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/4/17 0017 上午 9:20.
 * Version   1.0;
 * Describe :  注释
 * History:
 * ==============================================================================
 */

public class NotationBean {

    private String original;  //原文
    private String notation;  //注释
    private boolean isExpand;

    public NotationBean() {
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
