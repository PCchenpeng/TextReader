package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2018/4/25 0025 上午 9:22.
 * Version   1.0;
 * Describe :  系统消息
 * History:
 * ==============================================================================
 */

public class SettingsNews {

    private String id;
    private String title;
    private String content;
    private String time;
    private int status;
    private int type;
    private String cargoId;
    private boolean viewOrNot;

    public SettingsNews() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCargoId() {
        return cargoId;
    }

    public void setCargoId(String cargoId) {
        this.cargoId = cargoId;
    }

    public boolean isViewOrNot() {
        return viewOrNot;
    }

    public void setViewOrNot(boolean viewOrNot) {
        this.viewOrNot = viewOrNot;
    }
}
