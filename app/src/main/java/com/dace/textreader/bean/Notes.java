package com.dace.textreader.bean;

/**
 * 笔记
 * Created by 70391 on 2017/9/29.
 */

public class Notes {

    private String id;  //笔记ID
    private String note;  //笔记内容
    private long essayId;  //添加笔记的文章ID
    private int essayType;
    private String title;  //添加笔记的文章标题
    private String content;  //添加笔记的文章内容
    private String time;  //添加笔记的时间
    private boolean isEditor;  //是否处于编辑状态
    private boolean isSelected;  //是否被选中

    public Notes() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
