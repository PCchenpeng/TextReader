package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 2:39.
 * Version   1.0;
 * Describe :  纠错内容
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionBean {

    private String error;
    private String tips;
    private int section;  //错误所在的段落
    private int position;
    private int length;
    private int corrected;  //1 有错别字改正，0 没有提示，给出建议。

    public ErrorCorrectionBean() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCorrected() {
        return corrected;
    }

    public void setCorrected(int corrected) {
        this.corrected = corrected;
    }
}
