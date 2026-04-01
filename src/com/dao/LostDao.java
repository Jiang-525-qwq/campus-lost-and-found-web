package com.dao;

import com.entity.Lost;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LostDao {

    // 发布失物（你原来的代码，完全没动）
    public boolean addLost(Lost lost) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN"; // ✅ 改成最常用密码 root
            conn = DriverManager.getConnection(url, dbUser, dbPwd);

            String sql = "INSERT INTO lost(user_id, title, content, place, lost_time, status, create_time) "
                       + "VALUES(?,?,?,?,?,0,NOW())";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, lost.getUserId());
            pstmt.setString(2, lost.getTitle());
            pstmt.setString(3, lost.getContent());
            pstmt.setString(4, lost.getPlace());
            pstmt.setTimestamp(5, new java.sql.Timestamp(lost.getLostTime().getTime()));

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 查询所有失物（你原来的代码，完全没动）
    public List<Lost> findAllLost() {
        List<Lost> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN"; // ✅ 改成最常用密码 root
            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            String sql = "SELECT * FROM lost ORDER BY create_time DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Lost lost = new Lost();
                lost.setId(rs.getInt("id"));
                lost.setUserId(rs.getInt("user_id"));
                lost.setTitle(rs.getString("title"));
                lost.setContent(rs.getString("content"));
                lost.setPlace(rs.getString("place"));
                lost.setLostTime(rs.getTimestamp("lost_time"));
                lost.setStatus(rs.getInt("status"));
                lost.setCreateTime(rs.getTimestamp("create_time"));
                list.add(lost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return list;
    }

    // ========================
    // 【管理员功能】我只加了这一个方法！
    // 审核帖子：修改状态（0待审核 1通过 2驳回 3下架）
    // ========================
    public boolean updateStatus(int id, int status) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN"; // ✅ 改成最常用密码 root

            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            String sql = "UPDATE lost SET status=? WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}