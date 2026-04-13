package com.campus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus.mapper.ItemMapper;
import com.campus.model.entity.Item;
import com.campus.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ItemMapper itemMapper; // 注入 Mapper 才能操作数据库

    @Override
    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    // 1. 实现查询详情
    @Override
    public Item findById(Long id) {
        return itemMapper.findById(id);
    }

    // 2. 实现删除
    @Override
    public void deleteById(Long id) {
        // 1. 将 Long 转换为 Integer (因为 Mapper 接收的是 Integer)
        // 2. 调用 Mapper 中正确的管理员删除方法名
        itemMapper.deleteItemByAdmin(id.intValue()); 
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 确保这里的参数类型是 Integer，并对应你定义的 0-4 状态码
        itemMapper.updateItemStatus(id.intValue(), status);
   
    }}