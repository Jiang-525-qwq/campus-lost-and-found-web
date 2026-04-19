package com.campus.service;

import java.util.List;

import com.campus.model.entity.Item;

public interface ContentService {
    List<Item> findAll();

    Item findById(Long id);

    void deleteById(Long id);

    void updateStatus(Long id, Integer status);
}
