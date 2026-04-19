package com.campus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.model.entity.Item;
import com.campus.service.ContentService;

@RestController
@RequestMapping("/api/admin/content")
@CrossOrigin
public class ContentController {
    private static final int STATUS_APPROVED = 1;
    private static final int STATUS_HIDDEN = 3;
    private static final int STATUS_DELETED = 5;

    @Autowired
    private ContentService contentService;

    @GetMapping("/list")
    public Map<String, Object> getContentList() {
        List<Item> list = contentService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        if (params.get("id") == null || params.get("action") == null) {
            result.put("code", 400);
            result.put("message", "Missing params");
            return result;
        }

        Long id = Long.valueOf(params.get("id").toString());
        String action = params.get("action").toString();
        Integer status;

        if ("hide".equals(action)) {
            status = STATUS_HIDDEN;
        } else if ("restore".equals(action)) {
            status = STATUS_APPROVED;
        } else if ("delete".equals(action)) {
            status = STATUS_DELETED;
        } else {
            status = Integer.valueOf(action);
        }

        contentService.updateStatus(id, status);
        result.put("code", 200);
        result.put("message", "Updated");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteContent(@PathVariable Long id) {
        contentService.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }
}
