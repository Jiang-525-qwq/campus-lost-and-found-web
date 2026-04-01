package com.entity;

import java.util.Date;

public class Lost {

    private Integer id;
    private Integer userId;       // 对应用户id
    private String title;        // 物品名称
    private String content;      // 详情描述
    private String place;        // 丢失地点
    private Date lostTime;       // 丢失时间
    private Integer status;      // 0=未找回 1=已找回
    private Date createTime;     // 创建时间

    public Lost() {}

    // Getter + Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getLostTime() {
        return lostTime;
    }

    public void setLostTime(Date lostTime) {
        this.lostTime = lostTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}