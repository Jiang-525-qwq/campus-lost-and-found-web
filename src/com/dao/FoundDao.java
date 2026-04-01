package com.dao;

import com.entity.Found;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FoundDao {

    // 发布招领（你原来的代码，完全没动）
    public boolean addFound(Found found) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN";
            conn = DriverManager.getConnection(url, dbUser, dbPwd);

            String sql = "INSERT INTO found(user_id, title, content, place, found_time, status, create_time) "
                       + "VALUES(?,?,?,?,?,0,NOW())";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, found.getUserId());
            pstmt.setString(2, found.getTitle());
            pstmt.setString(3, found.getContent());
            pstmt.setString(4, found.getPlace());
            pstmt.setTimestamp(5, new java.sql.Timestamp(found.getFoundTime().getTime()));

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

    // 查询所有招领（你原来的代码，完全没动）
    public List<Found> findAllFound() {
        List<Found> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN";

            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            String sql = "SELECT * FROM found ORDER BY create_time DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Found found = new Found();
                found.setId(rs.getInt("id"));
                found.setUserId(rs.getInt("user_id"));
                found.setTitle(rs.getString("title"));
                found.setContent(rs.getString("content"));
                found.setPlace(rs.getString("place"));
                found.setFoundTime(rs.getTimestamp("found_time"));
                found.setStatus(rs.getInt("status"));
                found.setCreateTime(rs.getTimestamp("create_time"));
                list.add(found);
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
    // 【管理员功能】只新增这个方法！
    // 审核招领帖子：修改状态
    // ========================
    public boolean updateStatus(int id, int status) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lostfound?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String dbUser = "root";
            String dbPwd = "sly031181YAN";

            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            String sql = "UPDATE found SET status=? WHERE id=?";
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