package com.campus.controller; // 如果这行报错，请点击 Eclipse 的修复选项

import com.campus.model.entity.Item;
import com.campus.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content")
@CrossOrigin 
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 获取内容列表接口
     */
    @GetMapping("/list")
    public Map<String, Object> getContentList() {
        List<Item> list = contentService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 统一状态更新接口（处理下架、恢复、逻辑删除）
     */
    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        // 1. 【核心修复】必须先从 params 中取出数据，确保变量在使用前已定义
        if (params.get("id") == null || params.get("action") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "参数缺失");
            return error;
        }

        Long id = Long.valueOf(params.get("id").toString());
        String action = params.get("action").toString();
        String statusValue;

        // 2. 翻译逻辑：将前端 action 转为数据库状态文字
        if ("hide".equals(action)) {
            statusValue = "已下架";
        } else if ("restore".equals(action)) {
            statusValue = "已发布";
        } else if ("delete".equals(action)) {
            statusValue = "已删除";
        } else {
            statusValue = action;
        }

        // 3. 【核心修复】调用 Service 中确切存在的方法名 updateStatus
        contentService.updateStatus(id, statusValue);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        return result;
    }
}