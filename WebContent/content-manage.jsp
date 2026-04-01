<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.entity.Item" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>内容管理 - 校园寻物系统</title>
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
            padding: 6px 14px;
            border-radius: 15px;
            font-size: 14px;
            font-weight: 600;
            color: white;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: inline-block;
            /* 修复文字看不见：增加不透明度、加粗 */
            opacity: 1;
            font-weight: bold;
        }

        .tag-lost {
            background-color: #e37979;
        }

        .tag-found {
            background-color: #63a7df;
        }

        .tag-status-pending {
            background-color: #efc136;
            color: #333;
        }

        .tag-status-approved {
            background-color: #4caf50;
        }

        .tag-status-rejected {
            background-color: #f44336;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            margin: 2px;
            text-decoration: none;
        }

        .btn-detail {
            background-color: #549687;
            color: white;
        }

        .btn-detail:hover {
            background-color: #478073;
            transform: translateY(-2px);
        }

        .btn-report {
            background-color: #ff9800;
            color: white;
        }

        .btn-report:hover {
            background-color: #f57c00;
            transform: translateY(-2px);
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
            gap: 8px;
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
    <a href="admin">用户管理</a>
    <a href="adminAllItem" class="active">内容管理</a>
   <a href="adminAuditList">信息审核</a>
</div>

<div class="main-content">
    <div class="container">
        <h2 class="page-title">内容管理</h2>

        <div class="stats-bar">
            <div class="stat-card">
                <div class="stat-title">寻物</div>
                <div class="stat-value">
                    <%
                        List<Item> allItems = (List<Item>) request.getAttribute("allItems");
                        int lostCount = 0, foundCount = 0, totalCount = 0;
                        if (allItems != null) {
                            totalCount = allItems.size();
                            for (Item item : allItems) {
                                if ("lost".equals(item.getItemType())) lostCount++;
                                else if ("found".equals(item.getItemType())) foundCount++;
                            }
                        }
                    %>
                    <%= lostCount %>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-title">招领</div>
                <div class="stat-value"><%= foundCount %></div>
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
                        <th>ID</th>
                        <th>类型</th>
                        <th>物品名称</th>
                        <th>发布人学号</th>
                        <th>时间</th>
                        <th>状态</th>
                        <th>举报状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (allItems != null && !allItems.isEmpty()) {
                            for (Item item : allItems) {
                    %>
                    <tr>
                        <td><%= item.getItemId() %></td>
                        <td>
                            <span class="<%= "lost".equals(item.getItemType()) ? "tag-lost" : "tag-found" %> tag">
                                <%= "lost".equals(item.getItemType()) ? "寻物" : "招领" %>
                            </span>
                        </td>
                        <td><%= item.getItemName() %></td>
                        <td><%= item.getUserId() %></td>
                        <td><%= item.getCreatedAt() %></td>
                        <td>
                            <%
                                String statusClass = "";
                                String statusText = "";
                                if ("pending".equals(item.getStatus())) {
                                    statusClass = "tag-status-pending";
                                    statusText = "待审核";
                                } else if ("approved".equals(item.getStatus())) {
                                    statusClass = "tag-status-approved";
                                    statusText = "已通过";
                                } else {
                                    statusClass = "tag-status-rejected";
                                    statusText = "已驳回";
                                }
                            %>
                            <span class="tag <%= statusClass %>">
                                <%= statusText %>
                            </span>
                        </td>
                        <td>
                            <%
                                int reportStatus = item.getReportStatus();
                                String reportText = "";
                                String reportClass = "";
                                if (reportStatus == 0) {
                                    reportText = "正常";
                                    reportClass = "tag-status-approved";
                                } else if (reportStatus == 1) {
                                    reportText = "被举报";
                                    reportClass = "tag-status-rejected";
                                } else {
                                    reportText = "已处理";
                                    reportClass = "tag-status-pending";
                                }
                            %>
                            <span class="tag <%= reportClass %>"><%= reportText %></span>
                        </td>
                        <td class="action-buttons">
                            <a class="btn btn-detail" href="itemDetail?itemId=<%= item.getItemId() %>">详情</a>
                            <% if (item.getReportStatus() == 1) { %>
                                <a class="btn btn-report" href="handleReport?itemId=<%= item.getItemId() %>&act=handle">处理举报</a>
                                <a class="btn btn-report" href="handleReport?itemId=<%= item.getItemId() %>&act=ignore">忽略</a>
                            <% } %>
                            <a class="btn btn-delete" href="deleteItem?itemId=<%= item.getItemId() %>" onclick="return confirm('确定删除？')">删除</a>
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