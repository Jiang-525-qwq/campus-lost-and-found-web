package com.campus.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    /**
     * 更新用户状态（如：封禁）
     * @param userId 用户唯一ID
     * @param status 目标状态（'正常', '已封禁'）
     */
    @Update("UPDATE users SET status = #{status} WHERE user_id = #{userId}")
    int updateUserStatus(@Param("userId") Integer userId, @Param("status") String status);
}