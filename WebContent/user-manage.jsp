<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.entity.TUser" %>
<% TUser currentUser = (TUser) session.getAttribute("currentUser"); %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>用户管理 - 校园寻物系统</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #b0f4e6;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .header {
            background-color: #ffffff;
            color: #549687;
            text-align: center;
            padding: 15px 0;
            width: 100%;
            font-size: 24px;
            border-radius: 0 0 15px 15px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            position: relative;
        }

        .back-to-home {
            position: absolute;
            left: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: white;
            background-color: #549687;
            padding: 8px 15px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s ease;
            font-weight: 500;
        }

        .back-to-home:hover {
            background-color: #478073;
            transform: translateY(-50%) translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .user-info {
            background-color: #5f9e8f;
            color: white;
            padding: 12px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-info span {
            font-size: 16px;
        }

        .logout-btn {
            background-color: #a84444;
            color: white;
            border: none;
            padding: 8px 20px;
            border-radius: 8px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .logout-btn:hover {
            background-color: #8e2f2f;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .admin-nav {
            background-color: #5f9e8f;
            padding: 15px 20px;
            display: flex;
            justify-content: center;
            gap: 20px;
            flex-wrap: wrap;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .admin-nav a {
            color: white;
            text-decoration: none;
            padding: 8px 20px;
            border-radius: 8px;
            transition: all 0.3s ease;
            font-weight: 500;
            font-size: 16px;
        }

        .admin-nav a:hover {
            background-color: #549687;
            transform: translateY(-2px);
        }

        .admin-nav a.active {
            background-color: white;
            color: #549687;
            font-weight: 600;
        }

        .main-content {
            flex: 1;
            padding: 30px 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .container {
            width: 100%;
            max-width: 1400px;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .page-title {
            color: #333;
            margin-bottom: 25px;
            font-size: 24px;
            padding-bottom: 15px;
            border-bottom: 2px solid #5f9e8f;
        }

        .stats-bar {
            display: flex;
            gap: 20px;
            margin-bottom: 25px;
            flex-wrap: wrap;
        }

        .stat-card {
            flex: 1;
            min-width: 200px;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #e1f3ef;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .stat-title {
            color: #666;
            font-size: 14px;
            margin-bottom: 10px;
        }

        .stat-value {
            color: #333;
            font-size: 32px;
            font-weight: 600;
        }

        .table-container {
            overflow-x: auto;
            border-radius: 8px;
            border: 1px solid #e0e0e0;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        thead {
            background-color: #5f9e8f;
        }

        th {
            color: white;
            font-weight: 600;
            text-align: left;
            padding: 15px;
            white-space: nowrap;
            border-right: 1px solid #478073;
        }

        th:last-child {
            border-right: none;
        }

        tbody tr {
            border-bottom: 1px solid #e0e0e0;
            transition: background-color 0.3s ease;
        }

        tbody tr:hover {
            background-color: #f9fffe;
        }

        tbody tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        tbody tr:hover:nth-child(even) {
            background-color: #f9fffe;
        }

        td {
            padding: 15px;
            color: #333;
        }

        .tag {
            padding: 4px 12px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: inline-block;
        }

        .tag-active {
            background-color: #4caf50;
        }

        .tag-banned {
            background-color: #f44336;
        }

        .tag-admin {
            background-color: #ff9800;
        }

        .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            margin: 2px;
            text-decoration: none;
        }

        .btn-ban {
            background-color: #f44336;
            color: white;
        }

        .btn-ban:hover {
            background-color: #d32f2f;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .btn-enable {
            background-color: #4caf50;
            color: white;
        }

        .btn-enable:hover {
            background-color: #45a049;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .btn-set-admin {
            background-color: #ff9800;
            color: white;
        }

        .btn-set-admin:hover {
            background-color: #f57c00;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .btn-remove-admin {
            background-color: #795548;
            color: white;
        }

        .btn-remove-admin:hover {
            background-color: #5d4037;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .btn-delete {
            background-color: #f44336;
            color: white;
        }

        .btn-delete:hover {
            background-color: #d32f2f;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .action-buttons {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .page-foot {
            background-color: #5f9e8f;
            color: white;
            text-align: center;
            padding: 20px 0;
            width: 100%;
            margin-top: auto;
        }

        .copyright {
            font-size: 14px;
            opacity: 0.9;
        }
    </style>
</head>
<body>

<div class="header">
    <a href="home.jsp" class="back-to-home">← 返回主页</a>
    <h1>校园寻物系统 - 管理员后台</h1>
</div>

<div class="user-info">
    <span>当前用户：${currentUser.username} <span style="color:#ffd700; font-size:12px;">(管理员)</span></span>
    <button class="logout-btn" onclick="location.href='logout'">退出登录</button>
</div>

<div class="admin-nav">
    <a href="home.jsp">首页</a>
    <a href="admin" class="active">用户管理</a>
    <a href="adminAllItem">内容管理</a>
    <a href="adminAuditList">信息审核</a>
    <a href="report.jsp">举报处理</a>
</div>

<div class="main-content">
    <div class="container">
        <h2 class="page-title">用户管理</h2>

        <%
            // 从AdminServlet获取用户列表
            List<TUser> userList = (List<TUser>) request.getAttribute("userList");
            int activeCount = 0, bannedCount = 0, adminCount = 0, totalCount = 0;
            if (userList != null) {
                totalCount = userList.size();
                for (TUser user : userList) {
                    // 0=正常，1=封禁（你可以根据自己的状态字段调整）
                    if (user.getStatus() == 0) activeCount++;
                    else if (user.getStatus() == 1) bannedCount++;
                    // 1=管理员
                    if (user.getRole() == 1) adminCount++;
                }
            }
        %>

        <div class="stats-bar">
            <div class="stat-card">
                <div class="stat-title">正常用户</div>
                <div class="stat-value"><%= activeCount %></div>
            </div>
            <div class="stat-card">
                <div class="stat-title">已封禁用户</div>
                <div class="stat-value"><%= bannedCount %></div>
            </div>
            <div class="stat-card">
                <div class="stat-title">管理员</div>
                <div class="stat-value"><%= adminCount %></div>
            </div>
            <div class="stat-card">
                <div class="stat-title">总计</div>
                <div class="stat-value"><%= totalCount %></div>
            </div>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>学号</th>
                        <th>用户名</th>
                        <th>角色</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (userList != null && !userList.isEmpty()) {
                            for (TUser user : userList) {
                    %>
                    <tr>
                        <td><%= user.getStudentId() %></td>
                        <td><%= user.getUsername() %></td>
                        <td>
                            <% if (user.getRole() == 1) { %>
                                <span class="tag tag-admin">管理员</span>
                            <% } else { %>
                                <span class="tag tag-active">普通用户</span>
                            <% } %>
                        </td>
                        <td>
                            <% if (user.getStatus() == 0) { %>
                                <span class="tag tag-active">正常</span>
                            <% } else { %>
                                <span class="tag tag-banned">已封禁</span>
                            <% } %>
                        </td>
                        <td class="action-buttons">
                            <%-- 封禁/解封 --%>
                            <% if (user.getStatus() == 0 && user.getRole() != 1) { %>
                                <a class="btn btn-ban" href="updateUserStatus?studentId=<%= user.getStudentId() %>&status=1">封禁</a>
                            <% } else if (user.getStatus() == 1) { %>
                                <a class="btn btn-enable" href="updateUserStatus?studentId=<%= user.getStudentId() %>&status=0">解封</a>
                            <% } %>

                            <%-- 设为管理员/撤销管理员 --%>
                            <% if (user.getRole() != 1) { %>
                                <a class="btn btn-set-admin" href="updateUserRole?studentId=<%= user.getStudentId() %>&role=1">设为管理员</a>
                            <% } else if (!user.getStudentId().equals(currentUser.getStudentId())) { %>
                                <a class="btn btn-remove-admin" href="updateUserRole?studentId=<%= user.getStudentId() %>&role=0">撤销管理员</a>
                            <% } %>

                            <%-- 删除用户（不能删自己，不能删管理员） --%>
                            <% if (user.getRole() != 1 && !user.getStudentId().equals(currentUser.getStudentId())) { %>
                                <a class="btn btn-delete" href="deleteUser?studentId=<%= user.getStudentId() %>" onclick="return confirm('确定要删除该用户吗？此操作不可恢复！')">删除</a>
                            <% } %>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="page-foot">
    <p class="copyright">版权所有 © 2026 校园寻物系统</p>
</div>

</body>
</html>