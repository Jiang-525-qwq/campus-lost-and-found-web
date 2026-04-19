package com.campus.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.campus.model.entity.Item;

@Mapper
public interface ItemMapper {

    @Select("SELECT i.*, u.username FROM items i LEFT JOIN users u ON i.user_id = u.user_id ORDER BY i.item_id DESC")
    List<Item> findAll();

    @Select("SELECT * FROM items WHERE item_id = #{id}")
    Item findById(Long id);

    @Delete("DELETE FROM items WHERE item_id = #{itemId} AND user_id = #{userId} AND (status = 0 OR status = 2)")
    int deleteItemByUser(@Param("itemId") Integer itemId, @Param("userId") Integer userId);

    @Delete("DELETE FROM items WHERE item_id = #{itemId}")
    int deleteItemByAdmin(@Param("itemId") Integer itemId);

    @Update("UPDATE items SET status = #{status} WHERE item_id = #{itemId}")
    int updateItemStatus(@Param("itemId") Integer itemId, @Param("status") Integer status);

    @Insert("INSERT INTO items (item_name, item_type, location, lost_found_time, description, user_id, status) " +
            "VALUES (#{itemName}, #{itemType}, #{location}, #{lostFoundTime}, #{description}, #{userId}, 0)")
    int insertItem(Item item);
}
