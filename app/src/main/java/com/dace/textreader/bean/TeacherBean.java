package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/9/17 0017 上午 9:42.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class TeacherBean {

    private long teacherId;
    private String teacherName;
    private String organization;
    private int relationStatus;  //1表示已绑定，0表示等待老师确认，-1表示老师拒绝绑定，-2表示可绑定
    private boolean isSelected;

    public TeacherBean() {
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public int getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(int relationStatus) {
        this.relationStatus = relationStatus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
