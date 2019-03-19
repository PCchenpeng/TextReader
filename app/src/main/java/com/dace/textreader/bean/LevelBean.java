package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/4/2 0002 上午 11:27.
 * Version   1.0;
 * Describe :  等级
 * History:
 * ==============================================================================
 */

public class LevelBean extends LitePalSupport {

    private int grade;
    private String gradeName;
    private boolean isSelected;

    public LevelBean() {
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
