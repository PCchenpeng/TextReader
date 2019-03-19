package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/3/22 0022 下午 3:50.
 * Version   1.0;
 * Describe :  交易条目
 * History:
 * ==============================================================================
 */

public class TransactionBean {

    private String id;  //交易ID
    private String content;  //交易内容
    private String cost;  //交易价格
    private String time;  //交易时间
    private int status;  //交易状态
    private int category;  //交易类型
    private int payChannel;  //支付方式

    public TransactionBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(int payChannel) {
        this.payChannel = payChannel;
    }
}
