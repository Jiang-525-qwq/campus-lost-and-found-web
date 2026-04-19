package com.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/check")
    public String check() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM categories", Integer.class);
            return "\u540e\u7aef\u542f\u52a8\u6210\u529f\uff0c\u5df2\u6210\u529f\u8fde\u63a5\u6570\u636e\u5e93\uff0ccategories \u8868\u4e2d\u5171\u6709 "
                    + count + " \u6761\u6570\u636e\u3002";
        } catch (Exception e) {
            return "\u540e\u7aef\u5df2\u542f\u52a8\uff0c\u4f46\u6570\u636e\u5e93\u8fde\u63a5\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5 application.properties\u3002\u9519\u8bef\u4fe1\u606f\uff1a"
                    + e.getMessage();
        }
    }
}
