package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/1/26 0026 下午 3:31.
 * Version   1.0;
 * Describe :  会员卡记录
 * History:
 * ==============================================================================
 */
public class MemberCardRecordBean {

    private long cardId;
    private int count;
    private long recordId;

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    private String situation;

    public MemberCardRecordBean() {
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }
}
