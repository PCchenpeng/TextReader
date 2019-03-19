package com.dace.textreader.bean;

import android.text.SpannableString;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 3:12.
 * Version   1.0;
 * Describe :  包含富文本的纠错实体类
 * History:
 * ==============================================================================
 */
public class ErrorClickBean {

    private int type;
    private SpannableString content;
    private String imagePath;
    private List<ErrorCorrectionBean> errorList;

    public ErrorClickBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SpannableString getContent() {
        return content;
    }

    public void setContent(SpannableString content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<ErrorCorrectionBean> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorCorrectionBean> errorList) {
        this.errorList = errorList;
    }
}
