package com.servlet;

import com.dao.ItemDAO;
import com.entity.Item;
import com.entity.TUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/addInfo")
public class AddInfoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 登录判断
        TUser user = (TUser) request.getSession().getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("logIn.html");
            return;
        }

        try {
            // ===================== 取值 =====================
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String place = request.getParameter("place");
            String type = request.getParameter("type");
            String timeStr = request.getParameter("time");
            String categoryIdStr = request.getParameter("categoryId");

            // ===================== 全部设置默认值！绝对不空！ =====================
            if (title == null) title = "无标题";
            if (content == null) content = "无描述";
            if (place == null) place = "无地点";
            if (type == null) type = "lost";

            // 分类ID默认1
            int categoryId = 1;
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            }

            // ===================== ✅ 时间终极防御：为空就用当前时间！ =====================
            Timestamp time = new Timestamp(System.currentTimeMillis());
            if (timeStr != null && !timeStr.isEmpty()) {
                try {
                    time = Timestamp.valueOf(timeStr.replace("T", " "));
                } catch (Exception e) {
                    // 格式错了也不怕，继续用当前时间
                }
            }

            // ===================== 封装 =====================
            Item item = new Item();
            item.setUserId(user.getStudentId());
            item.setCategoryId(categoryId);
            item.setItemName(title);
            item.setDescription(content);
            item.setItemType(type);
            item.setLostFoundTime(time);
            item.setLocation(place);
            item.setStatus("normal"); // 直接显示

            // ===================== 保存 =====================
            ItemDAO dao = new ItemDAO();
            boolean success = dao.addItem(item);

            if (success) {
                response.sendRedirect("home.jsp");
            } else {
                response.sendRedirect("post-post.html?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("post-post.html?error=1");
        }
    }
}