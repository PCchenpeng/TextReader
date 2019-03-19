package com.dace.textreader.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/8/24 0024 上午 10:01.
 * Version   1.0;
 * Describe :  优惠券
 * History:
 * ==============================================================================
 */

public class CouponBean {

    private long id;
    private String stopTime;
    private int status;
    private int couponId;
    private int category;  //-1 所有地方都可以用，1 作文，2 微课
    private int type;  //1 满减券，2 折扣券，3 免单券，4 抵用券
    private String typeName;  //类型名称
    private String couponCode;  //优惠码
    private String content;
    private String title;
    private String couponPackageId;
    private int isActivated;  //是否激活，0表示未激活需要激活，1表示已激活，可以使用
    private double consumption;  //满减券中的“满”
    private double subtract;  //满减券中的“减”
    private double discount;  //折扣券参数
    private String prizeId;  //充值送礼品券中的“礼品券”
    private double chargePrice;  //激活需要充值的价钱
    private String packageId;  //激活需要充值的ID
    private String activeNote;  //激活内容
    private boolean isSelected;  //是否选中

    public CouponBean() {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
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

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getSubtract() {
        return subtract;
    }

    public void setSubtract(double subtract) {
        this.subtract = subtract;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(String prizeId) {
        this.prizeId = prizeId;
    }

    public String getCouponPackageId() {
        return couponPackageId;
    }

    public void setCouponPackageId(String couponPackageId) {
        this.couponPackageId = couponPackageId;
    }

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int isActivated) {
        this.isActivated = isActivated;
    }

    public double getChargePrice() {
        return chargePrice;
    }

    public void setChargePrice(double chargePrice) {
        this.chargePrice = chargePrice;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getActiveNote() {
        return activeNote;
    }

    public void setActiveNote(String activeNote) {
        this.activeNote = activeNote;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
