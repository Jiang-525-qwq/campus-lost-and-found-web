package com.campus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.mapper.UserMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PutMapping("/users/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params) {
        Integer userId = Integer.parseInt(params.get("userId"));
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");

        Map<String, Object> res = new HashMap<>();
        try {
            String currentPwd = jdbcTemplate.queryForObject(
                    "SELECT password FROM users WHERE user_id = ?",
                    String.class,
                    userId);

            if (currentPwd != null && currentPwd.equals(oldPwd)) {
                jdbcTemplate.update("UPDATE users SET password = ? WHERE user_id = ?", newPwd, userId);
                res.put("code", 200);
                res.put("message", "Password updated");
            } else {
                res.put("code", 400);
                res.put("message", "Old password is incorrect");
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("message", "Server error: " + e.getMessage());
        }
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, Object> response = new HashMap<>();
        String sql = "SELECT * FROM users WHERE student_id = ? AND password = ?";

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username, password);

            if (!users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                response.put("code", 200);
                response.put("message", "Login success");
                response.put("userId", user.get("user_id"));
                response.put("username", user.get("username"));
                response.put("role", user.get("role"));
            } else {
                response.put("code", 400);
                response.put("message", "Username or password is incorrect");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "Database error: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserProfile(@PathVariable Integer userId) {
        try {
            return jdbcTemplate.queryForMap(
                    "SELECT user_id AS userId, student_id AS studentId, username, email, phone, role, status, created_at AS registerTime " +
                            "FROM users WHERE user_id = ?",
                    userId);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @PutMapping("/users/{userId}")
    public Map<String, Object> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            int rows = jdbcTemplate.update(
                    "UPDATE users SET username = ?, email = ?, phone = ? WHERE user_id = ?",
                    payload.getOrDefault("username", ""),
                    payload.getOrDefault("email", ""),
                    payload.getOrDefault("phone", ""),
                    userId);
            response.put("code", rows > 0 ? 200 : 400);
            response.put("message", rows > 0 ? "Profile updated" : "Update failed");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "Server error: " + e.getMessage());
        }
        return response;
    }

    @PutMapping("/users/{userId}/ban")
    public Map<String, Object> banUser(@PathVariable Integer userId) {
        userMapper.updateUserStatus(userId, "\u5df2\u5c01\u7981");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Banned");
        return response;
    }
}
