package com.campus.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.campus.model.entity.Report;

@Mapper
public interface ReportMapper {

    @Select("SELECT r.report_id AS reportId, r.item_id AS itemId, r.reporter_id AS reporterId, " +
            "r.reported_user_id AS reportedUserId, i.user_id AS itemOwnerId, i.item_name AS itemName, " +
            "reporter.username AS reporterName, reported.username AS reportedUsername, " +
            "r.reason, r.description, r.status, CAST(r.created_at AS CHAR) AS createdAt " +
            "FROM reports r " +
            "LEFT JOIN items i ON r.item_id = i.item_id " +
            "LEFT JOIN users reporter ON r.reporter_id = reporter.user_id " +
            "LEFT JOIN users reported ON COALESCE(NULLIF(r.reported_user_id, 0), i.user_id) = reported.user_id " +
            "ORDER BY r.report_id DESC")
    List<Report> selectAllReports();

    @Update("UPDATE reports SET status = #{status} WHERE report_id = #{id}")
    int updateReportStatus(@Param("id") Integer id, @Param("status") String status);

    @Update("UPDATE reports SET description = #{description}, status = #{status} WHERE report_id = #{id}")
    int updateReportHandling(@Param("id") Integer id,
                             @Param("description") String description,
                             @Param("status") String status);

    @Insert("INSERT INTO reports (item_id, reporter_id, reason, description, status, created_at, reported_user_id) " +
            "VALUES (#{itemId}, #{reporterId}, #{reason}, #{description}, '\u5f85\u5904\u7406', CURRENT_TIME(), #{reportedUserId})")
    int insertReport(Report report);
}
