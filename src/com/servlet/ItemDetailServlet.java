package com.servlet;

import com.dao.ItemDAO;
import com.entity.Item;
import com.entity.TUser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet("/itemDetail")
public class ItemDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 权限校验
        HttpSession session = request.getSession();
        TUser currentUser = (TUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.getWriter().write("<script>alert('请先登录！');window.location.href='logIn.html';</script>");
            return;
        }

        // 2. 获取物品ID
        int itemId;
        try {
            itemId = Integer.parseInt(request.getParameter("itemId"));
        } catch (NumberFormatException e) {
            response.getWriter().write("<script>alert('物品ID无效！');window.history.back();</script>");
            return;
        }

        // 3. 查询物品详情
        ItemDAO itemDAO = new ItemDAO();
        Item item = itemDAO.getItemById(itemId);

        if (item == null) {
            response.getWriter().write("<script>alert('物品不存在！');window.history.back();</script>");
            return;
        }

        // 4. 格式化时间（给前端显示）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTimeStr = item.getCreatedAt() != null ? sdf.format(item.getCreatedAt()) : "无";
        String lostTimeStr = item.getLostFoundTime() != null ? sdf.format(item.getLostFoundTime()) : "无";

        // 5. 把数据传给详情页 ✅ 关键修改：改成你实际的文件名 detail.jsp
        request.setAttribute("item", item);
        request.setAttribute("createTimeStr", createTimeStr);
        request.setAttribute("lostTimeStr", lostTimeStr);
        // 原来的 /item-detail.jsp 改成 /detail.jsp
        request.getRequestDispatcher("/detail.jsp").forward(request, response);
    }
}