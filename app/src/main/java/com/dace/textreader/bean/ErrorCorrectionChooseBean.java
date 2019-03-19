package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 5:38.
 * Version   1.0;
 * Describe :  纠错选择
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionChooseBean {

    private String text;
    private boolean isSelected;

    public ErrorCorrectionChooseBean() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
