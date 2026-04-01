package com.dao;

import com.entity.Item;
import com.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // ====================== 【1】添加物品（发布用） ======================
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items(user_id, category_id, item_name, description, item_type, lost_found_time, location, status) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getUserId());
            pstmt.setInt(2, item.getCategoryId());
            pstmt.setString(3, item.getItemName());
            pstmt.setString(4, item.getDescription());
            pstmt.setString(5, item.getItemType());
            pstmt.setTimestamp(6, item.getLostFoundTime());
            pstmt.setString(7, item.getLocation());
            pstmt.setString(8, "pending"); // 默认待审核

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 【2】根据学号查我的发布 ======================
    public List<Item> getItemsByUserId(String userId) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = buildItemFromResultSet(rs);
                list.add(item);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====================== 【3】查询所有物品（管理员内容管理用） ======================
    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Item item = buildItemFromResultSet(rs);
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====================== 【4】管理员修改审核状态（通过/驳回） ======================
    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE items SET status = ? WHERE item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, itemId);

            int rows = pstmt.executeUpdate();
            System.out.println("✅ 更新物品状态成功！itemId=" + itemId + " 影响行数：" + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 【5】管理员删除物品 ======================
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 【6】统计：按状态查询数量（用于审核页统计） ======================
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM items WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    // ====================== 【7】统计：总记录数 ======================
    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM items";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    // ====================== 根据ID查询物品详情 ======================
    public Item getItemById(int itemId) {
        Item item = null;
        String sql = "SELECT * FROM items WHERE item_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setUserId(rs.getString("user_id"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setItemName(rs.getString("item_name"));
                item.setDescription(rs.getString("description"));
                item.setItemType(rs.getString("item_type"));
                item.setLostFoundTime(rs.getTimestamp("lost_found_time"));
                item.setLocation(rs.getString("location"));
                item.setImageUrl(rs.getString("image_url"));
                item.setStatus(rs.getString("status"));
                item.setCreatedAt(rs.getTimestamp("created_at"));
                item.setUpdatedAt(rs.getTimestamp("updated_at"));
                item.setReportStatus(rs.getInt("report_status"));
                item.setReportReason(rs.getString("report_reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return item;
    }

    // ====================== 【8】用户举报物品（新增） ======================
    public boolean reportItem(int itemId, String reportReason) {
        String sql = "UPDATE items SET report_status=1, report_reason=? WHERE item_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reportReason);
            pstmt.setInt(2, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 【9】管理员处理举报（标记已处理） ======================
    public boolean handleReport(int itemId) {
        String sql = "UPDATE items SET report_status=2 WHERE item_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 【10】管理员忽略举报（恢复正常） ======================
    public boolean ignoreReport(int itemId) {
        String sql = "UPDATE items SET report_status=0, report_reason=NULL WHERE item_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 查询所有物品信息（用于信息审核、内容管理）
     */
    public List<Item> findAllItems() {
        List<Item> itemList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1. 获取数据库连接（用你项目里现有的DBUtil工具类）
            conn = DBUtil.getConnection();
            // 2. 编写SQL：查询item表所有数据，按创建时间倒序
            String sql = "SELECT * FROM item ORDER BY created_at DESC";
            pstmt = conn.prepareStatement(sql);
            // 3. 执行查询
            rs = pstmt.executeQuery();

            // 4. 遍历结果集，封装成Item对象
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setItemType(rs.getString("item_type"));
                item.setItemName(rs.getString("item_name"));
                item.setUserId(rs.getString("user_id"));
                item.setDescription(rs.getString("description"));
                item.setStatus(rs.getString("status"));
                item.setReportStatus(rs.getInt("report_status"));
                item.setCreatedAt(rs.getTimestamp("created_at"));
                item.setUpdatedAt(rs.getTimestamp("updated_at"));
                itemList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
   
        	DBUtil.close(conn, pstmt, rs);
        }
        return itemList;
    }

    // ====================== 【私有工具方法】抽取 ResultSet 赋值逻辑 ======================
    private Item buildItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setUserId(rs.getString("user_id"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setItemName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        item.setItemType(rs.getString("item_type"));
        item.setLostFoundTime(rs.getTimestamp("lost_found_time"));
        item.setLocation(rs.getString("location"));
        item.setStatus(rs.getString("status"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setReportStatus(rs.getInt("report_status"));
        item.setReportReason(rs.getString("report_reason"));
        item.setImageUrl(rs.getString("image_url"));
        item.setUpdatedAt(rs.getTimestamp("updated_at"));
        return item;
    }
}