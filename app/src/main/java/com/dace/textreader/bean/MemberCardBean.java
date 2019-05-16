package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/1/21 0021 下午 4:23.
 * Version   1.0;
 * Describe :  会员卡功能
 * History:
 * ==============================================================================
 */
public class MemberCardBean {

    private long cardId;
    private long cardRecordId;
    private String cardName;
    private String cardDescription;
    private String cardImage;
    private int count;  //剩余次数
    private int cardType;  //-1表示没有类型，1表示写作，2表示微课，3表示直播，4表示语音测评，5表示一对一答疑

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    private String situation;

    public String getOutSourcing() {
        return outSourcing;
    }

    public void setOutSourcing(String outSourcing) {
        this.outSourcing = outSourcing;
    }

    private String outSourcing;

    public MemberCardBean() {
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getCardRecordId() {
        return cardRecordId;
    }

    public void setCardRecordId(long cardRecordId) {
        this.cardRecordId = cardRecordId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
}
