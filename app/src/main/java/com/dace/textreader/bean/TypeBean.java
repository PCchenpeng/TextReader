package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/4/2 0002 上午 11:29.
 * Version   1.0;
 * Describe :  文章类型
 * History:
 * ==============================================================================
 */

public class TypeBean extends LitePalSupport {

    private int type;
    private String typeName;
    private boolean isSelected;

    public TypeBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
