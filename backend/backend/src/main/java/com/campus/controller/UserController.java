package com.campus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.mapper.ItemMapper;
import com.campus.mapper.UserMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin // 这个非常重要，否则你的 HTML 无法访问后端
public class UserController {

	@Autowired
    private ItemMapper itemMapper;

    @Autowired // 必须加上这个注入
    private UserMapper userMapper; 

    @Autowired // jdbcTemplate 也需要注入才能使用
    private JdbcTemplate jdbcTemplate;
    
    @PutMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params) {
        Integer userId = Integer.parseInt(params.get("userId"));
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");

        Map<String, Object> res = new HashMap<>();
        
        // 1. 校验旧密码
        String checkSql = "SELECT password FROM users WHERE user_id = ?";
        try {
            String currentPwd = jdbcTemplate.queryForObject(checkSql, String.class, userId);
            
            if (currentPwd != null && currentPwd.equals(oldPwd)) {
                // 2. 更新密码
                String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
                jdbcTemplate.update(updateSql, newPwd, userId);
                res.put("code", 200);
                res.put("message", "密码修改成功");
            } else {
                res.put("code", 400);
                res.put("message", "原密码错误");
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("message", "服务器异常: " + e.getMessage());
        }
        return res;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        // 这里的 username 和 password 对应你前端 axios 发过来的 key
        String username = loginData.get("username"); 
        String password = loginData.get("password");

        Map<String, Object> response = new HashMap<>();

        // SQL 解释：去 users 表里找 student_id 和 password 都对得上的行
        String sql = "SELECT * FROM users WHERE student_id = ? AND password = ?";
        
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username, password);

            if (!users.isEmpty()) {
                // 1. 获取查询到的第一行用户信息
                Map<String, Object> user = users.get(0);
                
                response.put("code", 200);
                response.put("message", "登录成功");
                
                // 2. 【关键修正】把数据库里的 id 传给前端
                // 请确认你数据库 users 表里的字段名，如果是 user_id 就写 user.get("user_id")
                response.put("userId", user.get("user_id")); 
                
                response.put("username", user.get("username"));
                response.put("role", user.get("role"));
            }else {
                // 账号或密码不对
                response.put("code", 400);
                response.put("message", "学号或密码错误");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "服务器数据库连接异常: " + e.getMessage());
        }
        
        return response;
    }
    @PutMapping("/users/{userId}/ban")
    public Map<String, Object> banUser(@PathVariable Integer userId) {
        userMapper.updateUserStatus(userId, "已封禁");
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户已封禁");
        return response;
    }
}