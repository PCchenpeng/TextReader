package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/1/26 0026 下午 2:41.
 * Version   1.0;
 * Describe :  直播课
 * History:
 * ==============================================================================
 */
public class LiveShowLessonBean {

    private long lessonId;
    private String title;
    private String content;
    private String image;
    private boolean isSelected;

    public LiveShowLessonBean() {
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
