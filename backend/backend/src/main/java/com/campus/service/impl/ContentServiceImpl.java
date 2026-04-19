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
    private ItemMapper itemMapper;

    @Override
    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    @Override
    public Item findById(Long id) {
        return itemMapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        itemMapper.deleteItemByAdmin(id.intValue());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        itemMapper.updateItemStatus(id.intValue(), status);
    }
}
