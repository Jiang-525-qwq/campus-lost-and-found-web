package com.servlet;

import com.dao.ItemDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/handleReport")
public class HandleReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            String act = request.getParameter("act");
            ItemDAO dao = new ItemDAO();
            if ("handle".equals(act)) dao.handleReport(itemId);
            if ("ignore".equals(act)) dao.ignoreReport(itemId);
            response.sendRedirect("adminAllItem");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}