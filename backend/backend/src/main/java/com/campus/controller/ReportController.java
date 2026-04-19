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
    private static final String ACTION_TAKEDOWN = "takedown";
    private static final String ACTION_REJECT = "reject";
    private static final String ACTION_PROCESSING = "processing";
    private static final String STATUS_PENDING = "\u5f85\u5904\u7406";
    private static final String STATUS_PROCESSING = "\u5904\u7406\u4e2d";
    private static final String STATUS_RESOLVED = "\u5df2\u5904\u7406";
    private static final String STATUS_REJECTED = "\u5df2\u9a73\u56de";

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

            Integer reportedUserId = resolveReportedUserId(report);

            String nextStatus;
            if (ACTION_TAKEDOWN.equals(action)) {
                jdbcTemplate.update("UPDATE items SET status = ? WHERE item_id = ?", ITEM_STATUS_CLOSED, report.getItemId());
                nextStatus = STATUS_RESOLVED;
            } else if (ACTION_REJECT.equals(action)) {
                nextStatus = STATUS_REJECTED;
            } else if (ACTION_PROCESSING.equals(action)) {
                nextStatus = STATUS_PROCESSING;
            } else {
                nextStatus = STATUS_RESOLVED;
            }

            String mergedDescription = mergeDescription(report.getDescription(), note, action);
            reportMapper.updateReportHandling(id, mergedDescription, nextStatus);
            createInboxMessage(
                    report.getReporterId(),
                    report.getReportId(),
                    report.getItemId(),
                    "reporter",
                    buildReporterTitle(action),
                    buildReporterContent(report, note, action));
            if (reportedUserId != null && !reportedUserId.equals(report.getReporterId())) {
                createInboxMessage(
                        reportedUserId,
                        report.getReportId(),
                        report.getItemId(),
                        "reported",
                        buildReportedTitle(action),
                        buildReportedContent(report, note, action));
            }

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
            if (ACTION_TAKEDOWN.equals(action)) {
                sb.append("[\u5904\u7406\u5907\u6ce8] ");
            } else if (ACTION_REJECT.equals(action)) {
                sb.append("[\u9a73\u56de\u539f\u56e0] ");
            } else {
                sb.append("[\u5907\u6ce8] ");
            }
            sb.append(note);
        }
        return sb.toString();
    }

    private Integer resolveReportedUserId(Report report) {
        if (report.getReportedUserId() != null && report.getReportedUserId() != 0) {
            return report.getReportedUserId();
        }
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT user_id FROM items WHERE item_id = ?",
                    Integer.class,
                    report.getItemId());
        } catch (Exception e) {
            return null;
        }
    }

    private void ensureInboxTable() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS inbox_messages (" +
                        "message_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id INT NOT NULL, " +
                        "report_id INT NULL, " +
                        "item_id INT NULL, " +
                        "title VARCHAR(120) NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "recipient_type VARCHAR(20) NOT NULL, " +
                        "is_read TINYINT(1) NOT NULL DEFAULT 0, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                        ")");
    }

    private void createInboxMessage(Integer userId, Integer reportId, Integer itemId, String recipientType, String title, String content) {
        if (userId == null || userId == 0) {
            return;
        }
        ensureInboxTable();
        jdbcTemplate.update(
                "INSERT INTO inbox_messages (user_id, report_id, item_id, title, content, recipient_type) VALUES (?, ?, ?, ?, ?, ?)",
                userId,
                reportId,
                itemId,
                title,
                content,
                recipientType);
    }

    private String buildReporterTitle(String action) {
        if (ACTION_TAKEDOWN.equals(action)) {
            return "\u4e3e\u62a5\u5904\u7406\u7ed3\u679c\uff1a\u5df2\u91c7\u7eb3";
        }
        if (ACTION_REJECT.equals(action)) {
            return "\u4e3e\u62a5\u5904\u7406\u7ed3\u679c\uff1a\u5df2\u9a73\u56de";
        }
        return "\u4e3e\u62a5\u5904\u7406\u8fdb\u5ea6\u901a\u77e5";
    }

    private String buildReportedTitle(String action) {
        if (ACTION_TAKEDOWN.equals(action)) {
            return "\u5e16\u5b50\u4e3e\u62a5\u5904\u7406\u7ed3\u679c\uff1a\u5df2\u4e0b\u67b6";
        }
        if (ACTION_REJECT.equals(action)) {
            return "\u5e16\u5b50\u4e3e\u62a5\u5904\u7406\u7ed3\u679c\uff1a\u5df2\u9a73\u56de";
        }
        return "\u5e16\u5b50\u4e3e\u62a5\u5904\u7406\u8fdb\u5ea6\u901a\u77e5";
    }

    private String buildReporterContent(Report report, String note, String action) {
        String itemName = report.getItemName() != null ? report.getItemName() : ("#" + report.getItemId());
        String actionText = ACTION_TAKEDOWN.equals(action)
                ? "\u5df2\u91c7\u7eb3\u60a8\u7684\u4e3e\u62a5\uff0c\u5e16\u5b50\u5df2\u4e0b\u67b6"
                : ACTION_REJECT.equals(action)
                ? "\u5df2\u9a73\u56de\u60a8\u7684\u4e3e\u62a5"
                : "\u60a8\u7684\u4e3e\u62a5\u6b63\u5728\u5904\u7406\u4e2d";
        return "\u5e16\u5b50\uff1a" + itemName +
                "\n\u4e3e\u62a5\u7c7b\u578b\uff1a" + safeText(report.getReason()) +
                "\n\u5904\u7406\u7ed3\u679c\uff1a" + actionText +
                "\n\u7ba1\u7406\u5458\u8bf4\u660e\uff1a" + safeText(note);
    }

    private String buildReportedContent(Report report, String note, String action) {
        String itemName = report.getItemName() != null ? report.getItemName() : ("#" + report.getItemId());
        String actionText = ACTION_TAKEDOWN.equals(action)
                ? "\u60a8\u7684\u5e16\u5b50\u5df2\u88ab\u4e0b\u67b6"
                : ACTION_REJECT.equals(action)
                ? "\u9488\u5bf9\u60a8\u5e16\u5b50\u7684\u4e3e\u62a5\u5df2\u88ab\u9a73\u56de"
                : "\u9488\u5bf9\u60a8\u5e16\u5b50\u7684\u4e3e\u62a5\u6b63\u5728\u5904\u7406\u4e2d";
        return "\u5e16\u5b50\uff1a" + itemName +
                "\n\u4e3e\u62a5\u7c7b\u578b\uff1a" + safeText(report.getReason()) +
                "\n\u5904\u7406\u7ed3\u679c\uff1a" + actionText +
                "\n\u7ba1\u7406\u5458\u8bf4\u660e\uff1a" + safeText(note);
    }

    private String safeText(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value.trim();
    }
}
