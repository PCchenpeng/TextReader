package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/8/31 0031 下午 2:45.
 * Version   1.0;
 * Describe :  卡包
 * History:
 * ==============================================================================
 */

public class CardBean {

    private long id;
    private String stopTime;
    private int frequency;
    private int type;
    private String cardCode;
    private int status;
    private double discount;
    private String title;
    private String validImage;
    private String invalidImage;
    private long recommendCardId;
    private boolean isSelected;
    private int category;  //区分卡的跳转页面：目前规定category=5时为多功能卡,2019/2/13

    public CardBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValidImage() {
        return validImage;
    }

    public void setValidImage(String validImage) {
        this.validImage = validImage;
    }

    public String getInvalidImage() {
        return invalidImage;
    }

    public void setInvalidImage(String invalidImage) {
        this.invalidImage = invalidImage;
    }

    public long getRecommendCardId() {
        return recommendCardId;
    }

    public void setRecommendCardId(long recommendCardId) {
        this.recommendCardId = recommendCardId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
