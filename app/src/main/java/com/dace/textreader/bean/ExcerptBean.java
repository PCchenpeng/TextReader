package com.dace.textreader.bean;

/**
 * 摘抄
 * Created by 70391 on 2017/11/24.
 */

public class ExcerptBean {

    private String id;
    private String time;
    private String from;
    private long essayId;
    private int essayType;
    private String essayTitle;
    private String excerpt;
    private int sourceType;
    private boolean isEditor;
    private boolean isSelected;

    public ExcerptBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
