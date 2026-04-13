package com.campus.service;

import java.util.List;
import com.campus.model.entity.Item;

public interface ContentService {
    // 之前已有的查询所有
    List<Item> findAll();

    // 1. 新增：根据 ID 查询详情
    Item findById(Long id);

    // 2. 新增：根据 ID 删除
    void deleteById(Long id);

    // 3. 新增：更新状态
    void updateStatus(Long id, Integer status);
}