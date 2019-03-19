package com.dace.textreader.bean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/2/26 0026 上午 10:36.
 * Version   1.0;
 * Describe :  知识汇总列表项
 * History:
 * ==============================================================================
 */
public class KnowledgeBean {

    private String title;  //标题
    private List<KnowledgeChildBean> list;  //子项列表

    public KnowledgeBean() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<KnowledgeChildBean> getList() {
        return list;
    }

    public void setList(List<KnowledgeChildBean> list) {
        this.list = list;
    }
}
