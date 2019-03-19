package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/9/25 0025 上午 11:13.
 * Version   1.0;
 * Describe :  文章分类
 * History:
 * ==============================================================================
 */

public class ReaderSortBean extends LitePalSupport {

    private int type;
    private String name;
    private int status;
    private String image;

    public ReaderSortBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
