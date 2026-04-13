package com.campus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.campus.model.entity.Item;

@Mapper
public interface ItemMapper {
    
    // 获取所有帖子
    @Select("SELECT * FROM items")
    List<Item> findAll();

    // 根据ID查询帖子
    @Select("SELECT * FROM items WHERE item_id = #{id}")
    Item findById(Long id);

    /**
     * 核心修改：统一使用 Integer 类型的 status
     * 这里的 updateStatus 将作为通用的状态更新方法
     */

    // 2. 用户删除：只有状态为 0 (审核中) 或 2 (已驳回) 时才允许物理删除
    @Delete("DELETE FROM items WHERE item_id = #{itemId} AND user_id = #{userId} AND (status = 0 OR status = 2)")
    int deleteItemByUser(@Param("itemId") Integer itemId, @Param("userId") Integer userId);
 // 管理员删除
    @Delete("DELETE FROM items WHERE item_id = #{itemId}")
    int deleteItemByAdmin(@Param("itemId") Integer itemId);

    // 更新状态
    @Update("UPDATE items SET status = #{status} WHERE item_id = #{itemId}")
    int updateItemStatus(@Param("itemId") Integer itemId, @Param("status") Integer status);
    
    // 如果你有发帖功能，请确保插入的是数字 0 (审核中)
    @Insert("INSERT INTO items (item_name, item_type, location, lost_found_time, description, user_id, status) " +
            "VALUES (#{itemName}, #{itemType}, #{location}, #{lostFoundTime}, #{description}, #{userId}, 0)")
    int insertItem(Item item);
}