package com.campus.controller;

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
import com.campus.mapper.UserMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ItemController {

	@Autowired
    private ItemMapper itemMapper;

    @Autowired // 必须加上这个注入
    private UserMapper userMapper; 

    @Autowired // jdbcTemplate 也需要注入才能使用
    private JdbcTemplate jdbcTemplate;
    
 // 在 ItemController.java 中
    @GetMapping("/items")
    public List<Map<String, Object>> getItems(@RequestParam(required = false) String keyword) {
        // 使用 StringBuilder 或者确保每一段拼接都有明确的空格
        String sql = "SELECT * FROM items";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 在 WHERE 前面一定要留空格
            sql += " WHERE (item_name LIKE ? OR description LIKE ?)";
            String queryParam = "%" + keyword + "%";
            // 排序永远写在最后
            sql += " ORDER BY item_id DESC";
            return jdbcTemplate.queryForList(sql, queryParam, queryParam);
        }
        
        // 默认查询
        sql += " ORDER BY item_id DESC";
        return jdbcTemplate.queryForList(sql);
    }
    @PostMapping("/items")
    public Map<String, Object> addItem(@RequestBody Map<String, Object> itemData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 获取前端传来的所有字段
            String name = (String) itemData.get("item_name");
            String type = (String) itemData.get("item_type");
            String loc = (String) itemData.get("location");
            String desc = (String) itemData.get("description");
            String time = (String) itemData.get("lost_found_time");
            
            // 2. 获取并校验 user_id
            Object userIdObj = itemData.get("user_id");
            if (userIdObj == null || userIdObj.toString().equals("undefined")) {
                response.put("code", 401);
                response.put("message", "请先登录后再发布");
                return response;
            }
            Integer userId = Integer.parseInt(userIdObj.toString());

            // 3. 【重点】获取 category_id，如果没有传，默认给 1
            Object catIdObj = itemData.get("category_id");
            Integer categoryId = (catIdObj != null) ? Integer.parseInt(catIdObj.toString()) : 1;

            // 4. 【重点】更新 SQL 语句，增加 category_id 字段和对应的一个 ? 占位符
            String sql = "INSERT INTO items (item_name, item_type, location, description, status, lost_found_time, user_id, category_id) VALUES (?, ?, ?, ?, 0, ?, ?, ?)";
            
            // 5. 执行更新，注意参数顺序要和 SQL 里的问号一一对应（共 7 个参数）
            jdbcTemplate.update(sql, name, type, loc, desc, time, userId, categoryId);
            
            response.put("code", 200);
            response.put("message", "发布成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "服务器错误：" + e.getMessage());
        }
        
        return response;
    }

    // 修改状态接口：用于 隐藏(3)、下架(4) 或管理员审核(1, 2)
    @PutMapping("/{itemId}/status")
    public Map<String, Object> changeStatus(@PathVariable Integer itemId, @RequestParam Integer status) {
        int rows = itemMapper.updateItemStatus(itemId, status);
        Map<String, Object> res = new HashMap<>();
        res.put("code", rows > 0 ? 200 : 400);
        res.put("message", rows > 0 ? "状态更新成功" : "更新失败");
        return res;
    }

    // 删除接口：根据参数判断是用户行为还是管理员行为
    @DeleteMapping("/{itemId}")
    public Map<String, Object> delete(@PathVariable Integer itemId, 
                                      @RequestParam Integer userId,
                                      @RequestParam(defaultValue = "false") Boolean isAdmin) {
        int rows;
        if (isAdmin) {
            rows = itemMapper.deleteItemByAdmin(itemId);
        } else {
            rows = itemMapper.deleteItemByUser(itemId, userId);
        }
        
        Map<String, Object> res = new HashMap<>();
        if (rows > 0) {
            res.put("code", 200);
            res.put("message", "删除成功");
        } else {
            res.put("code", 403);
            res.put("message", isAdmin ? "删除失败" : "审核通过的帖子无法删除，请选择隐藏或下架");
        }
        return res;
    }
 // 获取某帖子的所有评论
    @GetMapping("/comments")
    public List<Map<String, Object>> getComments(@RequestParam Integer itemId) {
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "WHERE c.item_id = ? ORDER BY c.create_time DESC";
        return jdbcTemplate.queryForList(sql, itemId);
    }

    // 详情接口
    @GetMapping("/items/{id}")
    public Map<String, Object> getItemDetail(@PathVariable("id") Integer id) {
        // 确保 WHERE 后面接的是 item_id
        String sql = "SELECT i.*, u.username FROM items i " +
                     "LEFT JOIN users u ON i.user_id = u.user_id " +
                     "WHERE i.item_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
            // 如果查不到数据，queryForMap 会抛异常，返回一个空 Map 避免前端报 500
            return new HashMap<>(); 
        }
    }
    // 发表评论
    @PostMapping("comments")
    public Map<String, Object> addComment(@RequestBody Map<String, Object> commentData) {
        String sql = "INSERT INTO comments (item_id, user_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, 
            commentData.get("item_id"), 
            commentData.get("user_id"), 
            commentData.get("content")
        );
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }
 // 获取待审核的列表
    @GetMapping("/admin/audit-list")
    public List<Map<String, Object>> getAuditList() {
        return jdbcTemplate.queryForList("SELECT * FROM items WHERE status = 0 ORDER BY item_id DESC");
    }

    // 必须要加上这个注解，路径要和前端 axios 请求的一致
    @PostMapping("/admin/update-status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 获取并安全转换 ID
            Object idObj = payload.get("item_id");
            Integer itemId = null;
            
            if (idObj instanceof Number) {
                itemId = ((Number) idObj).intValue();
            } else if (idObj instanceof String) {
                itemId = Integer.parseInt((String) idObj);
            }

            String status = (String) payload.get("status");

            // 执行 SQL
            // 建议增加 updated_at 更新，方便追踪审核时间
            int rows = jdbcTemplate.update(
                "UPDATE items SET status = ? WHERE item_id = ?", 
                status, itemId
            );

            res.put("success", rows > 0);
            res.put("message", rows > 0 ? "审核成功" : "未找到对应条目");
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "错误: " + e.getMessage());
        }
        return res;
    }
 // 在 ItemController.java 中添加
    @GetMapping("/my-items")
    public List<Map<String, Object>> getMyItems(@RequestParam Integer userId) {
        // 根据用户 ID 查询，并按时间倒序排列
        String sql = "SELECT * FROM items WHERE user_id = ? ORDER BY item_id DESC";
        return jdbcTemplate.queryForList(sql, userId);
    }
    @PutMapping("/items/{itemId}/takedown")
    public Map<String, Object> takeDown(@PathVariable Integer itemId) {
        // 1. 调用 Mapper 更新数据库状态
        itemMapper.updateItemStatus(itemId, 4);
        
        // 2. 创建返回给前端的 Map 对象
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("message", "帖子已成功下架");
        
        return res;
    }
}