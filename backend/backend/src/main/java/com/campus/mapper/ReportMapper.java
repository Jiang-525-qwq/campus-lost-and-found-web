package com.campus.mapper;

import com.campus.model.entity.Report;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReportMapper {

    // 1. 获取所有举报列表
    @Select("SELECT report_id AS reportId, item_id AS itemId, reporter_id AS reporterId, " +
            "reason, description, status, created_at AS createdAt FROM reports")
    List<Report> selectAllReports();

    // 2. 更新举报状态
    @Update("UPDATE reports SET status = #{status} WHERE report_id = #{id}")
    int updateReportStatus(@Param("id") Integer id, @Param("status") String status);

    // 3. 提交举报 (你原本的代码)
    @Insert("INSERT INTO reports (item_id, reporter_id, reason, description, status) " +
            "VALUES (#{itemId}, #{reporterId}, #{reason}, #{description}, '待处理')")
    int insertReport(Report report);
}