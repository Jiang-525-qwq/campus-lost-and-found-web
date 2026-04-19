package com.campus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.mapper.ItemMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ItemController {
    private static final String DEFAULT_IMAGE_URL = "blank-image.svg";
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_APPROVED = 1;
    private static final int STATUS_REJECTED = 2;
    private static final int STATUS_HIDDEN = 3;
    private static final int STATUS_CLOSED = 4;
    private static final int STATUS_DELETED = 5;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/items")
    public List<Map<String, Object>> getItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") Boolean includeAll) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.*, u.username FROM items i LEFT JOIN users u ON i.user_id = u.user_id WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (!includeAll) {
            sql.append(" AND i.status = ?");
            params.add(STATUS_APPROVED);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (i.item_name LIKE ? OR i.description LIKE ?)");
            String queryParam = "%" + keyword.trim() + "%";
            params.add(queryParam);
            params.add(queryParam);
        }

        sql.append(" ORDER BY i.item_id DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    @PostMapping("/items")
    public Map<String, Object> addItem(@RequestBody Map<String, Object> itemData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String name = (String) itemData.get("item_name");
            String type = (String) itemData.get("item_type");
            String loc = (String) itemData.get("location");
            String desc = (String) itemData.get("description");
            String time = (String) itemData.get("lost_found_time");
            String imageUrl = itemData.get("image_url") == null
                    || String.valueOf(itemData.get("image_url")).trim().isEmpty()
                    ? DEFAULT_IMAGE_URL
                    : String.valueOf(itemData.get("image_url")).trim();

            Object userIdObj = itemData.get("user_id");
            if (userIdObj == null || "undefined".equals(userIdObj.toString())) {
                response.put("code", 401);
                response.put("message", "Please login first");
                return response;
            }

            Integer userId = Integer.parseInt(userIdObj.toString());
            Object catIdObj = itemData.get("category_id");
            Integer categoryId = (catIdObj != null) ? Integer.parseInt(catIdObj.toString()) : 1;

            String sql = "INSERT INTO items (item_name, item_type, location, description, status, " +
                    "lost_found_time, user_id, category_id, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, name, type, loc, desc, STATUS_PENDING, time, userId, categoryId, imageUrl);

            response.put("code", 200);
            response.put("message", "Created");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "Server error: " + e.getMessage());
        }
        return response;
    }

    @PutMapping("/items/{itemId}/status")
    public Map<String, Object> changeStatus(
            @PathVariable Integer itemId,
            @RequestParam Integer status,
            @RequestParam(required = false) Integer userId) {
        int rows;
        if (userId != null) {
            rows = jdbcTemplate.update(
                    "UPDATE items SET status = ? WHERE item_id = ? AND user_id = ? AND status <> ?",
                    status, itemId, userId, STATUS_DELETED);
        } else {
            rows = itemMapper.updateItemStatus(itemId, status);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("code", rows > 0 ? 200 : 400);
        res.put("message", rows > 0 ? "Updated" : "Update failed");
        return res;
    }

    @DeleteMapping("/items/{itemId}")
    public Map<String, Object> delete(
            @PathVariable Integer itemId,
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "false") Boolean isAdmin) {
        int rows = isAdmin
                ? itemMapper.deleteItemByAdmin(itemId)
                : itemMapper.deleteItemByUser(itemId, userId);

        Map<String, Object> res = new HashMap<>();
        if (rows > 0) {
            res.put("code", 200);
            res.put("message", "Deleted");
        } else {
            res.put("code", 403);
            res.put("message", isAdmin ? "Delete failed" : "Only pending or rejected items can be deleted");
        }
        return res;
    }

    @GetMapping("/comments")
    public List<Map<String, Object>> getComments(@RequestParam Integer itemId) {
        String sql = "SELECT c.*, u.username FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.item_id = ? ORDER BY c.create_time DESC";
        return jdbcTemplate.queryForList(sql, itemId);
    }

    @GetMapping("/items/{id}")
    public Map<String, Object> getItemDetail(@PathVariable("id") Integer id) {
        String sql = "SELECT i.*, u.username FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.user_id " +
                "WHERE i.item_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @PostMapping("/comments")
    public Map<String, Object> addComment(@RequestBody Map<String, Object> commentData) {
        String sql = "INSERT INTO comments (item_id, user_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                commentData.get("item_id"),
                commentData.get("user_id"),
                commentData.get("content"));
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    @GetMapping("/admin/audit-list")
    public List<Map<String, Object>> getAuditList() {
        String sql = "SELECT i.*, u.username FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.user_id " +
                "WHERE i.status IN (?, ?, ?) ORDER BY i.item_id DESC";
        return jdbcTemplate.queryForList(sql, STATUS_PENDING, STATUS_APPROVED, STATUS_REJECTED);
    }

    @PostMapping("/admin/update-status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();
        try {
            Integer itemId = Integer.valueOf(payload.get("item_id").toString());
            Integer status = mapAuditStatus(payload.get("status"));
            int rows = jdbcTemplate.update("UPDATE items SET status = ? WHERE item_id = ?", status, itemId);
            res.put("success", rows > 0);
            res.put("message", rows > 0 ? "Updated" : "Item not found");
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Error: " + e.getMessage());
        }
        return res;
    }

    @GetMapping("/my-items")
    public List<Map<String, Object>> getMyItems(@RequestParam Integer userId) {
        String sql = "SELECT * FROM items WHERE user_id = ? ORDER BY item_id DESC";
        return jdbcTemplate.queryForList(sql, userId);
    }

    @PutMapping("/items/{itemId}/takedown")
    public Map<String, Object> takeDown(@PathVariable Integer itemId) {
        itemMapper.updateItemStatus(itemId, STATUS_CLOSED);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("message", "Taken down");
        return res;
    }

    private Integer mapAuditStatus(Object rawStatus) {
        if (rawStatus instanceof Number) {
            return ((Number) rawStatus).intValue();
        }

        String status = String.valueOf(rawStatus);
        switch (status) {
            case "approved":
                return STATUS_APPROVED;
            case "rejected":
                return STATUS_REJECTED;
            case "pending":
                return STATUS_PENDING;
            default:
                return Integer.valueOf(status);
        }
    }
}
