package com.dace.textreader.bean;

/**
 * 周排行榜
 * Created by 70391 on 2017/8/15.
 */

public class WeekRankUser {

    private long id;  // ID
    private String name;  //用户名
    private String image;  //用户头像
    private String duration;  //阅读时长
    private int rank;  //排名

    public WeekRankUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
