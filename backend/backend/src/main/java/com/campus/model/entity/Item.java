package com.campus.model.entity;

/**
 * 内容实体类
 * 对应数据库中的 items 表
 */
public class Item {
    // 1. 属性定义（严格对应数据库字段的驼峰形式）
    private Long itemId;        // 对应数据库 item_id
    private String itemType;    // 对应数据库 item_type
    private String itemName;    // 对应数据库 item_name
    private String userId;      // 对应数据库 user_id
    private String createdAt;   // 对应数据库 created_at
    private Integer status;
    // 2. 无参构造方法
    public Item() {}

    // 3. Getter 和 Setter 方法
    // Spring 和 MyBatis 必须依靠这些方法来读写数据
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}