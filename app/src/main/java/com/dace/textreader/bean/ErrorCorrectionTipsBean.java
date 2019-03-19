package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 5:38.
 * Version   1.0;
 * Describe :  纠错提示
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionTipsBean {

    private String tips;
    private String error;

    public ErrorCorrectionTipsBean() {
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
