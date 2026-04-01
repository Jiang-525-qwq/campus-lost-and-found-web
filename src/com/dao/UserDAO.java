package com.dao;

import com.entity.TUser;
import com.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // 登录方法（完整正确版，不会空指针）
    public TUser login(String studentId, String password) {
        String sql = "SELECT * FROM t_user WHERE student_id = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TUser user = null;

        try {
            conn = DBUtil.getConnection();

            // 如果数据库连接失败，直接返回，不报错
            if (conn == null) {
                System.out.println("==== 数据库连接失败 ====");
                return null;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new TUser();
                user.setStudentId(rs.getString("student_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                user.setStatus(rs.getInt("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, pstmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    // ===================== 我只新增这个方法 =====================
    // 查询所有用户（给用户管理页面使用）
    public List<TUser> findAllUsers() {
        String sql = "SELECT * FROM t_user";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TUser> userList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            if (conn == null) {
                return userList;
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TUser user = new TUser();
                user.setStudentId(rs.getString("student_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                user.setStatus(rs.getInt("status"));
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, pstmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userList;
    }
 // 1. 更新用户状态（封禁/解封）
    public void updateUserStatus(String studentId, int status) {
        String sql = "UPDATE t_user SET status = ? WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, pstmt, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 2. 更新角色（管理员/普通用户）
    public void updateUserRole(String studentId, int role) {
        String sql = "UPDATE t_user SET role = ? WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, role);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, pstmt, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 3. 删除用户
    public void deleteUser(String studentId) {
        String sql = "DELETE FROM t_user WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, pstmt, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}