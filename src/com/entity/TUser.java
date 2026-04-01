package com.entity;

public class TUser {
    // 主键：student_id（学号）
    private String studentId;
    private String username;
    private String password;
    private int role;    // 0=学生 1=管理员
    private int status;  // 1=正常 0=禁用

    // 无参构造（必须）
    public TUser() {}

    // 全参构造（可选）
    public TUser(String studentId, String username, String password, int role, int status) {
        this.studentId = studentId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // Getter & Setter
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // 管理员判断方法
    public boolean isAdmin() {
        return this.role == 1;
    }
}