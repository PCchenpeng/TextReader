package com.dace.textreader.bean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/2/26 0026 上午 10:36.
 * Version   1.0;
 * Describe :  已关注的订阅内容列表项
 * History:
 * ==============================================================================
 */
public class SubscriptionBean {

    private String retType;  //类型
    private List<SubscriptionChildBean> retList;  //子项列表

    public SubscriptionBean() {
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public List<SubscriptionChildBean> getRetList() {
        return retList;
    }

    public void setRetList(List<SubscriptionChildBean> retList) {
        this.retList = retList;
    }
}
