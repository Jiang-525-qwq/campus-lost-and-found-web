package com.campus.controller;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin // 解决跨域
public class UserManageController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. 获取所有用户列表
 // 在 UserManageController.java 中
    @GetMapping("/list")
    public List<Map<String, Object>> getAllUsers() {
        // 使用 AS 起别名，让后端返回的 JSON 键名与前端 JS 变量名一致
        String sql = "SELECT user_id AS id, student_id AS studentId, username, email, phone, status, role, created_at AS registerTime FROM users";
        return jdbcTemplate.queryForList(sql);
    }
    // 2. 更新用户状态（封禁/解封）
    @PostMapping("/update-status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        String status = params.get("status");
        
        Map<String, Object> resp = new HashMap<>();
        try {
            // 注意：这里的 user_id 是你数据库真实的列名
            String sql = "UPDATE users SET status = ? WHERE user_id = ?";
            int rows = jdbcTemplate.update(sql, status, id);
            resp.put("success", rows > 0);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
        }
        return resp;
    }

    // 3. 删除用户
    @PostMapping("/delete")
    public Map<String, Object> deleteUser(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        String sql = "DELETE FROM users WHERE id = ? AND role != 'admin'"; // 安全起见，不让删管理员
        int rows = jdbcTemplate.update(sql, id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", rows > 0);
        return response;
    }
}