package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/11/20 0020 下午 5:09.
 * Version   1.0;
 * Describe :  全局搜索历史
 * History:
 * ==============================================================================
 */
public class SearchHistoryBean extends LitePalSupport {

    private long id;
    private String words;

    public SearchHistoryBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
