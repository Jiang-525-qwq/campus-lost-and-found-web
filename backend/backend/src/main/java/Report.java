

import java.util.Date;

/**
 * 举报信息实体类
 */
public class Report {
    private Integer reportId;    // 举报ID（自增主键）
    private Integer itemId;      // 被举报的帖子ID
    private Integer reporterId;  // 举报人的用户ID
    private String reason;       // 举报类型（信息虚假、广告骚扰等）
    private String description;  // 详细描述
    private String status;       // 状态（待处理、已处理）
    private Date createdAt;      // 举报时间

    // --- 无参构造函数（MyBatis 映射需要） ---
    public Report() {}

    // --- Getter 和 Setter 方法 (解决 Controller 报错的关键) ---

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