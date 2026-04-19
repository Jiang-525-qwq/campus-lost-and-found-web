package com.campus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@CrossOrigin
public class UserManageController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public List<Map<String, Object>> getAllUsers() {
        String sql = "SELECT u.user_id AS id, u.student_id AS studentId, u.username, u.email, u.phone, " +
                "u.status, u.role, u.created_at AS registerTime, " +
                "(SELECT COUNT(*) FROM items i WHERE i.user_id = u.user_id) AS postCount " +
                "FROM users u ORDER BY u.user_id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @PostMapping("/update-status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        String status = params.get("status");

        Map<String, Object> resp = new HashMap<>();
        try {
            String sql = "UPDATE users SET status = ? WHERE user_id = ?";
            int rows = jdbcTemplate.update(sql, status, id);
            resp.put("success", rows > 0);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
        }
        return resp;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteUser(@RequestBody Map<String, String> params) {
        String id = params.get("id");

        Map<String, Object> response = new HashMap<>();
        try {
            String sql = "DELETE FROM users WHERE user_id = ? AND role != 'admin'";
            int rows = jdbcTemplate.update(sql, id);
            response.put("success", rows > 0);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
