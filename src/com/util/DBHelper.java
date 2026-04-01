package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    // 数据库连接信息（根据你的MySQL修改！）
    private static final String URL = "jdbc:mysql://localhost:3306/lostfound?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String USER = "root"; // 你的MySQL用户名，一般是root
    private static final String PASSWORD = "sly031181YAN"; // 改成你自己的Navicat密码！
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // 静态块加载驱动
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL驱动加载失败！", e);
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 关闭资源（通用方法）
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}