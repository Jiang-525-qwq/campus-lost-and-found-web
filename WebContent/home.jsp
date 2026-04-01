<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.entity.Lost" %>
<%@ page import="com.entity.Found" %>
<%@ page import="com.entity.TUser" %>

<%
    // 1. 强制校验登录，防止直接访问
    TUser currentUser = (TUser) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/logIn.html");
        return;
    }

    // 2. 提取用户信息
    String username = currentUser.getUsername();
    Integer role = currentUser.getRole();
    boolean isAdmin = (role != null && role == 1);

    // 3. 从request取数据（HomeServlet传过来的）
    List<Lost> lostList = (List<Lost>) request.getAttribute("lostList");
    List<Found> foundList = (List<Found>) request.getAttribute("foundList");
%>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>校园寻物主页</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #90f5ddbd; min-height: 100vh; display: flex; flex-direction: column; }
        .title { background: #fff; color: #549687; text-align: center; padding: 15px 0; font-size: 24px; border-radius: 0 0 15px 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .user-info { background: #549687; color: #fff; padding: 10px 20px; display: flex; justify-content: space-between; align-items: center; }
        .logout-btn { background: #b71d1d90; color: #fff; border: none; padding: 8px 20px; border-radius: 8px; cursor: pointer; transition: 0.3s; }
        .logout-btn:hover { background: #8e2f2f; transform: translateY(-2px); }
        .main-content { flex: 1; padding: 30px 20px; display: flex; flex-direction: column; align-items: center; }
        .post-my { width: 100%; max-width: 1200px; display: flex; justify-content: flex-end; gap: 15px; margin-bottom: 25px; }
        .post-my a { background: #549687; color: #fff; padding: 12px 25px; border-radius: 8px; text-decoration: none; transition: 0.3s; }
        .post-my a:hover { background: #478073; transform: translateY(-2px); }
        .admin-link { background: #d9534f !important; display: none; }
        .post-list { width: 100%; max-width: 1200px; display: flex; flex-direction: column; gap: 20px; }
        .post-item { background: #fff; padding: 25px; border-radius: 10px; box-shadow: 0 3px 15px rgba(0,0,0,0.05); }
        .page-foot { background: #549687; color: #fff; text-align: center; padding: 20px 0; margin-top: auto; }
        .tag-lost { background: #e37979cb; }
        .tag-found { background: #63a7dfc9; }
        .tag-pending { background: #efc136; }
    </style>

    <script>
        window.onload = function () {
            const username = "<%= username.replace("\"", "\\\"") %>";
            const isAdmin = <%= isAdmin %>;
            document.getElementById("usernameDisplay").textContent = username;
            if (isAdmin) {
                document.querySelector(".admin-link").style.display = "inline-flex";
                document.getElementById("usernameDisplay").innerHTML += ' <span style="color:#ff9800; font-size:12px;">(管理员)</span>';
            }
        };
        function logout() {
            if (confirm("确定退出登录？")) {
                window.location.href = "<%= request.getContextPath() %>/logoutServlet";
            }
        }
    </script>
</head>
<body>
    <div class="title"><h1>校园寻物系统</h1></div>
    <div class="user-info">
        <span>当前用户：<span id="usernameDisplay"></span></span>
        <button class="logout-btn" onclick="logout()">退出登录</button>
    </div>

    <div class="main-content">
        <div class="post-my">
            <a href="<%= request.getContextPath() %>/user-manage.jsp" class="admin-link">管理员后台</a>
            <a href="<%= request.getContextPath() %>/post-post.html" class="post-btn">发布新帖子</a>
           <a href="<%= request.getContextPath() %>/myself.jsp" class="post-btn">我的主页</a>
        </div>

        <div class="post-list">
            <%-- 渲染失物列表 --%>
            <% if (lostList != null && !lostList.isEmpty()) { %>
                <% for (Lost lost : lostList) { %>
                <div class="post-item">
                    <h3><%= lost.getTitle() %></h3>
                    <span class="tag tag-lost">寻物</span>
                    <p>地点：<%= lost.getPlace() %> | 时间：<%= lost.getLostTime() %></p>
                    <p><%= lost.getContent() %></p>
                </div>
                <% } %>
            <% } else { %>
                <div class="post-item"><p>暂无失物信息</p></div>
            <% } %>

            <%-- 渲染招领列表 --%>
            <% if (foundList != null && !foundList.isEmpty()) { %>
                <% for (Found found : foundList) { %>
                <div class="post-item">
                    <h3><%= found.getTitle() %></h3>
                    <span class="tag tag-found">招领</span>
                    <p>地点：<%= found.getPlace() %> | 时间：<%= found.getFoundTime() %></p>
                    <p><%= found.getContent() %></p>
                </div>
                <% } %>
            <% } else { %>
                <div class="post-item"><p>暂无招领信息</p></div>
            <% } %>
        </div>
    </div>

    <div class="page-foot"><p>版权所有 © 2026 校园寻物系统</p></div>
</body>
</html>