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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.mapper.ReportMapper;
import com.campus.model.entity.Report;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin
public class ReportController {
    private static final int ITEM_STATUS_CLOSED = 4;
    private static final String STATUS_PENDING = "待处理";
    private static final String STATUS_PROCESSING = "处理中";
    private static final String STATUS_RESOLVED = "已处理";
    private static final String STATUS_REJECTED = "已驳回";

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Report> getAllReports() {
        return reportMapper.selectAllReports();
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        reportMapper.updateReportStatus(id, status);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        return res;
    }

    @PostMapping("/{id}/handle")
    public Map<String, Object> handleReport(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        String action = payload.getOrDefault("action", "");
        String note = payload.getOrDefault("note", "").trim();

        try {
            Report report = reportMapper.selectAllReports().stream()
                    .filter(item -> id.equals(item.getReportId()))
                    .findFirst()
                    .orElse(null);

            if (report == null) {
                result.put("code", 404);
                result.put("message", "Report not found");
                return result;
            }

            String nextStatus;
            if ("takedown".equals(action)) {
                jdbcTemplate.update("UPDATE items SET status = ? WHERE item_id = ?", ITEM_STATUS_CLOSED, report.getItemId());
                nextStatus = STATUS_RESOLVED;
            } else if ("reject".equals(action)) {
                nextStatus = STATUS_REJECTED;
            } else if ("processing".equals(action)) {
                nextStatus = STATUS_PROCESSING;
            } else {
                nextStatus = STATUS_RESOLVED;
            }

            String mergedDescription = mergeDescription(report.getDescription(), note, action);
            reportMapper.updateReportHandling(id, mergedDescription, nextStatus);

            result.put("code", 200);
            result.put("message", "Handled");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Server error: " + e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> addReport(@RequestBody Report report) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (report.getReportedUserId() == null || report.getReportedUserId() == 0) {
                Integer reportedUserId = jdbcTemplate.queryForObject(
                        "SELECT user_id FROM items WHERE item_id = ?",
                        Integer.class,
                        report.getItemId());
                report.setReportedUserId(reportedUserId);
            }

            int rows = reportMapper.insertReport(report);
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "Created");
            } else {
                result.put("code", 500);
                result.put("message", "Insert failed");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Server error: " + e.getMessage());
        }
        return result;
    }

    private String mergeDescription(String original, String note, String action) {
        StringBuilder sb = new StringBuilder();
        if (original != null && !original.trim().isEmpty()) {
            sb.append(original.trim());
        }
        if (!note.isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            if ("takedown".equals(action)) {
                sb.append("[处理备注] ");
            } else if ("reject".equals(action)) {
                sb.append("[驳回原因] ");
            } else {
                sb.append("[备注] ");
            }
            sb.append(note);
        }
        return sb.toString();
    }
}
