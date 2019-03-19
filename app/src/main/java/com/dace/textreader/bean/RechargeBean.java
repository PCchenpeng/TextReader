package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/3/12 0012 下午 1:40.
 * Version   1.0;
 * Describe :充值
 * History:
 * ==============================================================================
 */

public class RechargeBean {

    private long id;  //充值条目ID
    private String productName;  //产品名字
    private String title;  //充值标题
    private String content;  //充值内容
    private int status;  //充值条目状态
    private double price;  //充值价格
    private int giving;  //充值优惠

    private boolean isSelected;  //是否选中

    public RechargeBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getGiving() {
        return giving;
    }

    public void setGiving(int giving) {
        this.giving = giving;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
