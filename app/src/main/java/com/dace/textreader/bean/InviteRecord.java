package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/8/29 0029 下午 3:39.
 * Version   1.0;
 * Describe :  邀请记录
 * History:
 * ==============================================================================
 */

public class InviteRecord {

    private String phoneNumInvited;
    private String registerTime;
    private int ifcPrizeNum;

    public InviteRecord() {
    }

    public String getPhoneNumInvited() {
        return phoneNumInvited;
    }

    public void setPhoneNumInvited(String phoneNumInvited) {
        this.phoneNumInvited = phoneNumInvited;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public int getIfcPrizeNum() {
        return ifcPrizeNum;
    }

    public void setIfcPrizeNum(int ifcPrizeNum) {
        this.ifcPrizeNum = ifcPrizeNum;
    }
}
