package com.campus.model.entity;

import java.util.Date;

/**
 * 举报实体类
 * 对应数据库表：reports
 */
public class Report {
    private Integer reportId;   // 举报主键ID
    private Integer itemId;     // 被举报的物品/帖子ID
    private Integer reporterId; // 举报人用户ID
    private String reason;      // 举报原因（简述）
    private String description; // 详细描述
    private String status;      // 状态：待处理、处理中、已处理、已驳回
    private Date createdAt;     // 创建时间

    // 无参构造函数（MyBatis 必须）
    public Report() {}

    // Getter 和 Setter 方法 (必须要写，否则 Controller 会报 undefined 错误)
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}