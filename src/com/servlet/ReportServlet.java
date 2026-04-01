package com.servlet;

import com.dao.ItemDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/reportItem")
public class ReportServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            String reason = request.getParameter("reason");
            ItemDAO dao = new ItemDAO();
            dao.reportItem(itemId, reason);
            response.sendRedirect("adminAllItem");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}