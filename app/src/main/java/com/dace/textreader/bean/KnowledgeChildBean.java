package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/2/26 0026 上午 10:37.
 * Version   1.0;
 * Describe :  知识汇总子项
 * History:
 * ==============================================================================
 */
public class KnowledgeChildBean {

    private long knowledgeId;
    private String knowledgeIndexId;
    private String title;
    private String description;
    private String category;
    private String content;
    private String contents;

    public KnowledgeChildBean() {
    }

    public long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getKnowledgeIndexId() {
        return knowledgeIndexId;
    }

    public void setKnowledgeIndexId(String knowledgeIndexId) {
        this.knowledgeIndexId = knowledgeIndexId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
