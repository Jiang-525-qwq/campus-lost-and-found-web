package com.campus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController // 声明这是一个后端控制器，能够接收浏览器的请求
@CrossOrigin    // 允许跨域（非常重要，否则你的前端HTML无法访问这个接口）
public class CheckController {

    @Autowired // 自动连接你在 application.properties 里配置好的数据库
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/check") // 绑定浏览器访问的路径为 http://localhost:8080/api/check
    public String check() {
        try {
            // 尝试查询数据库中的 categories 表（这是你导入的 SQL 文件中自带的表）
            // 如果能查到数量，说明 Java -> Spring Boot -> MySQL 这条线全通了
            Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM categories", Integer.class);
            return "✅ 后端启动成功！已成功连接数据库。目前分类表（categories）中有 " + count + " 条数据。";
        } catch (Exception e) {
            // 如果连接失败，浏览器会显示具体的报错原因（比如密码错误、库名不对等）
            return "❌ 后端已启动，但数据库连接失败！请检查 application.properties。错误信息：" + e.getMessage();
        }
    }
}