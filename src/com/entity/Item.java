package com.entity;

import java.sql.Timestamp;

public class Item {
    private int itemId;
    private String userId;      // 对应 t_user 的 student_id（学号）
    private int categoryId;
    private String itemName;
    private String description;
    private String itemType;    // "lost"=寻物 / "found"=招领
    private Timestamp lostFoundTime;
    private String location;
    private String imageUrl;
    private String status;      // "pending"/"claimed"/"resolved"
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int reportStatus;    // 新增
    private String reportReason; // 新增

    // 无参构造
    public Item() {}

    // Getter & Setter
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Timestamp getLostFoundTime() {
        return lostFoundTime;
    }

    public void setLostFoundTime(Timestamp lostFoundTime) {
        this.lostFoundTime = lostFoundTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    public int getReportStatus() { return reportStatus; }
    public void setReportStatus(int reportStatus) { this.reportStatus = reportStatus; }

    public String getReportReason() { return reportReason; }
    public void setReportReason(String reportReason) { this.reportReason = reportReason; }
}