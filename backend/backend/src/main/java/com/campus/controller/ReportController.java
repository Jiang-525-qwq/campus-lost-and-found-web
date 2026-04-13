package com.campus.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.mapper.ReportMapper;
// 注意：如果这里报错，请点击红叉选择 "Import 'Report' (com.campus...)"
// 或者是手动修改为你 Report.java 实际所在的包名
import com.campus.model.entity.Report;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin // 解决跨域，防止前端 Axios 报错
public class ReportController {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * 接收前端举报提交
     * 对应前端 axios.post("http://localhost:8081/api/reports", reportData)
     */
 // 1. 获取所有举报列表
    @GetMapping
    public List<Report> getAllReports() {
        return reportMapper.selectAllReports(); 
    }

    // 2. 更新举报状态 (处理/驳回)
    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        reportMapper.updateReportStatus(id, status);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        return res;
    }
    @PostMapping
    public Map<String, Object> addReport(@RequestBody Report report) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 打印收到的数据，方便你在控制台调试
            System.out.println("收到举报: 帖子ID=" + report.getItemId() + ", 原因=" + report.getReason());

            // 执行插入数据库操作
            int rows = reportMapper.insertReport(report);

            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "举报提交成功");
            } else {
                result.put("code", 500);
                result.put("message", "提交失败，数据库未记录");
            }
        } catch (Exception e) {
            // 打印详细错误到 IDE 控制台
            e.printStackTrace(); 
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }
        
        return result;
    }
}