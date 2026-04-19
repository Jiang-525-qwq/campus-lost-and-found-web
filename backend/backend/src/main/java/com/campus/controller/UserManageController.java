package com.campus.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_USER = "user";
    private static final String USER_STATUS_ACTIVE = "\u6b63\u5e38";

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
            String sql = "DELETE FROM users WHERE user_id = ? AND role != ?";
            int rows = jdbcTemplate.update(sql, id, ROLE_ADMIN);
            response.put("success", rows > 0);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/batch-register")
    @Transactional
    public Map<String, Object> batchRegister(@RequestBody Map<String, String> params) {
        String minStudentId = params.get("minStudentId");
        String maxStudentId = params.get("maxStudentId");

        Map<String, Object> response = new HashMap<>();
        try {
            if (minStudentId == null || maxStudentId == null ||
                    !minStudentId.matches("\\d+") || !maxStudentId.matches("\\d+")) {
                response.put("success", false);
                response.put("message", "\u5b66\u53f7\u8303\u56f4\u5fc5\u987b\u4e3a\u7eaf\u6570\u5b57");
                return response;
            }

            if (minStudentId.length() != maxStudentId.length()) {
                response.put("success", false);
                response.put("message", "\u6700\u5c0f\u548c\u6700\u5927\u5b66\u53f7\u4f4d\u6570\u5fc5\u987b\u4e00\u81f4");
                return response;
            }

            long min = Long.parseLong(minStudentId);
            long max = Long.parseLong(maxStudentId);
            if (min > max) {
                response.put("success", false);
                response.put("message", "\u6700\u5c0f\u5b66\u53f7\u4e0d\u80fd\u5927\u4e8e\u6700\u5927\u5b66\u53f7");
                return response;
            }

            long total = max - min + 1;
            if (total > 5000) {
                response.put("success", false);
                response.put("message", "\u5355\u6b21\u6700\u591a\u652f\u6301\u6279\u91cf\u6ce8\u518c 5000 \u4e2a\u8d26\u53f7");
                return response;
            }

            int width = minStudentId.length();
            List<String> existingStudentIds = jdbcTemplate.queryForList(
                    "SELECT student_id FROM users WHERE CAST(student_id AS UNSIGNED) BETWEEN ? AND ?",
                    String.class,
                    min,
                    max);
            Set<String> existingSet = new HashSet<>(existingStudentIds);

            String insertSql = "INSERT INTO users " +
                    "(student_id, username, password, phone, email, role, created_at, updated_at, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)";

            int createdCount = 0;
            for (long current = min; current <= max; current++) {
                String studentId = String.format("%0" + width + "d", current);
                if (existingSet.contains(studentId)) {
                    continue;
                }

                String password = studentId.length() <= 6
                        ? studentId
                        : studentId.substring(studentId.length() - 6);
                jdbcTemplate.update(insertSql, studentId, studentId, password, "", "", ROLE_USER, USER_STATUS_ACTIVE);
                createdCount++;
            }

            int skippedCount = (int) total - createdCount;
            response.put("success", true);
            response.put("createdCount", createdCount);
            response.put("skippedCount", skippedCount);
            response.put("defaultPasswordRule", "\u9ed8\u8ba4\u5bc6\u7801\u4e3a\u5b66\u53f7\u540e\u516d\u4f4d");
            response.put("message", "\u6279\u91cf\u6ce8\u518c\u5b8c\u6210");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
