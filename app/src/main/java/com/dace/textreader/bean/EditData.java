package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/16 0016 上午 11:51.
 * Version   1.0;
 * Describe :  富文本
 * History:
 * ==============================================================================
 */
public class EditData {
    private String content;
    private int type;  //0为图片，1为文字

    public EditData() {
    }

    public EditData(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
