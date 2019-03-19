package com.dace.textreader.bean;

import java.util.List;

/**
 * 生词本的项目
 * Created by 70391 on 2017/10/24.
 */

public class GlossaryBean {

    private String id;  //生词的ID
    private long essayId;  //文章的ID
    private int type;  //文章的类型
    private int sourceType;  //资源类型，0表示作文，1表示文章，2表示词堆，3表示作者
    private String time;  //文章的时间
    private String title;  //文章的标题
    private List<String> list;  //生词集合
    private boolean isEditor;  //是否是编辑状态
    private boolean isChoose;
    private boolean isSelected;

    public GlossaryBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEssayId() {
        return essayId;
    }

    public void setEssayId(long essayId) {
        this.essayId = essayId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
