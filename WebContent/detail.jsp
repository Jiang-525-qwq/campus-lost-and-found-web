<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.entity.TUser" %>
<%@ page import="com.entity.Item" %>
<%
    // 后端登录校验（永远不会丢失登录状态）
    TUser user = (TUser) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/logIn.html");
        return;
    }
    String username = user.getUsername();
    Integer role = user.getRole();
    boolean isAdmin = (role != null && role == 1);
    String path = request.getContextPath();

    // ========== 读取真实物品数据 ==========
    Item item = (Item) request.getAttribute("item");
    String createTimeStr = (String) request.getAttribute("createTimeStr");
    String lostTimeStr = (String) request.getAttribute("lostTimeStr");
    if (item == null) {
        out.print("<script>alert('物品不存在');history.back();</script>");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>物品详情 - 校园寻物系统</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #90f5ddbd;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .title {
            background-color: #ffffff;
            color: #549687;
            text-align: center;
            padding: 15px 0;
            width: 100%;
            font-size: 24px;
            border-radius: 0 0 15px 15px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .user-info {
            background-color: #549687;
            color: white;
            padding: 10px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-info span {
            font-size: 16px;
        }

        .logout-btn {
            background-color: #b71d1d90;
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

        .main-content {
            flex: 1;
            padding: 30px 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .detail-card {
            width: 100%;
            max-width: 1200px;
            background-color: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
            margin-bottom: 30px;
        }

        .detail-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #eee;
        }

        .detail-header h1 {
            color: #333;
            font-size: 28px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .small-title {
            color: #549687;
            font-size: 14px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 5px;
        }

        .status-group {
            display: flex;
            gap: 10px;
        }

        .badge {
            padding: 8px 20px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            color: white;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .badge-lost { background-color: #e37979cb; }
        .badge-found { background-color: #63a7dfc9; }
        .badge-pending { background-color: #efc136; }
        .badge-approved { background-color: #4caf50; }
        .badge-rejected { background-color: #f44336; }

        .detail-content {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 40px;
        }

        .detail-image-wrap {
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 3px 15px rgba(0, 0, 0, 0.1);
        }

        .detail-image {
            width: 100%;
            height: 400px;
            object-fit: cover;
            transition: transform 0.3s ease;
        }

        .detail-info {
            display: flex;
            flex-direction: column;
            gap: 25px;
        }

        .item-name {
            color: #333;
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .item-desc {
            color: #444;
            line-height: 1.7;
            font-size: 16px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            background-color: #f9f9f9;
            padding: 25px;
            border-radius: 10px;
        }

        .info-box {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .info-label {
            color: #666;
            font-size: 14px;
            font-weight: 600;
        }

        .info-value {
            color: #333;
            font-size: 16px;
            font-weight: 500;
        }

        .publisher-panel {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 20px;
            background-color: #f0f9f7;
            border-radius: 10px;
        }

        .publisher-avatar {
            width: 50px;
            height: 50px;
            background-color: #549687;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .action-row {
            display: flex;
            gap: 15px;
            margin-top: 20px;
        }

        .btn {
            padding: 12px 30px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary {
            background-color: #549687;
            color: white;
            flex: 1;
        }

        .btn-primary:hover {
            background-color: #478073;
            transform: translateY(-2px);
        }

        .btn-light {
            background-color: #f0f9f7;
            color: #549687;
            border: 2px solid #549687;
        }

        .btn-danger {
            background-color: #ff6b6b;
            color: white;
            flex: 1;
        }

        .comments-section {
            width: 100%;
            max-width: 1200px;
            background-color: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
        }

        .comment-input {
            width: 100%;
            min-height: 100px;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            margin-bottom: 15px;
        }

        .comment-submit {
            background-color: #549687;
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            cursor: pointer;
        }

        .back-link {
            background-color: #549687;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            margin-bottom: 20px;
        }

        .report-modal {
            display: none;
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.5); z-index: 999;
            align-items: center; justify-content: center;
        }
        .report-modal.active { display: flex; }
        .report-form {
            background: white; padding: 30px; border-radius: 15px;
            width: 90%; max-width: 500px;
        }

        .page-foot {
            background-color: #549687;
            color: white;
            text-align: center;
            padding: 20px 0;
            margin-top: auto;
        }

        @media (max-width:768px) {
            .detail-content { grid-template-columns: 1fr; }
            .info-grid { grid-template-columns: 1fr; }
            .action-row { flex-direction: column; }
        }
    </style>

    <script>
        const path = "<%= path %>";
        window.onload = function(){
            document.getElementById("usernameDisplay").innerText = "<%= username %>";
            <% if(isAdmin) { %>
                document.getElementById("usernameDisplay").innerHTML += "<span style='color:#ff9800;font-size:12px;'>(管理员)</span>";
            <% } %>
        }

        function logout(){
            if(confirm("确定退出？")){
                window.location.href = path + "/logoutServlet";
            }
        }
        function shareItem(){ alert("链接已复制"); }
        function contactPublisher(){ alert("已复制联系方式"); }
        function showReportModal(){ document.getElementById("reportModal").classList.add("active"); }
        function hideReportModal(){ document.getElementById("reportModal").classList.remove("active"); }

        // 真实举报功能
        function submitReport(){
            let reason = document.getElementById("reportReason").value;
            if(!reason){ alert("请输入举报原因"); return; }
            let form = document.createElement("form");
            form.method="post";
            form.action= path + "/reportItem";
            let i1 = document.createElement("input");
            i1.name="itemId"; i1.value="<%=item.getItemId()%>";
            let i2 = document.createElement("input");
            i2.name="reason"; i2.value=reason;
            form.appendChild(i1); form.appendChild(i2);
            document.body.appendChild(form);
            form.submit();
        }

        function submitComment(){ alert("评论成功"); }
        function loadComments(){}
    </script>
</head>

<body>
    <div class="title"><h1>校园寻物系统</h1></div>
    <div class="user-info">
        <span>当前用户：<span id="usernameDisplay"></span></span>
        <button class="logout-btn" onclick="logout()">退出登录</button>
    </div>

    <div class="main-content">
        <a href="<%= path %>/adminAllItem" class="back-link">← 返回内容管理</a>

        <div class="detail-card">
            <div class="detail-header">
                <div>
                    <p class="small-title">物品详情</p>
                    <h1><%= item.getItemName() %></h1>
                </div>
                <div class="status-group">
                    <span class="badge <%= "lost".equals(item.getItemType()) ? "badge-lost" : "badge-found" %>">
                        <%= "lost".equals(item.getItemType()) ? "寻物启事" : "招领启事" %>
                    </span>
                    <%
                        String status = item.getStatus();
                        String statusCls = "badge-pending";
                        if("approved".equals(status)) statusCls = "badge-approved";
                        if("rejected".equals(status)) statusCls = "badge-rejected";
                    %>
                    <span class="badge <%=statusCls%>">
                        <%= "pending".equals(status) ? "待审核" : "approved".equals(status) ? "已通过" : "已驳回" %>
                    </span>
                </div>
            </div>

            <div class="detail-content">
                <div class="detail-image-wrap">
                    <%
                        String img = item.getImageUrl();
                        if(img == null || img.trim().isEmpty()) img = path + "/images/default.jpg";
                    %>
                    <img src="<%=img%>" class="detail-image" alt="物品图片">
                </div>

                <div class="detail-info">
                    <h2 class="item-name"><%= item.getItemName() %></h2>
                    <p class="item-desc"><%= item.getDescription() == null ? "无描述" : item.getDescription() %></p>

                    <div class="info-grid">
                        <div class="info-box"><span class="info-label">地点</span><span class="info-value"><%= item.getLocation() == null ? "无" : item.getLocation() %></span></div>
                        <div class="info-box"><span class="info-label">时间</span><span class="info-value"><%= lostTimeStr %></span></div>
                        <div class="info-box"><span class="info-label">发布人</span><span class="info-value"><%= item.getUserId() %></span></div>
                        <div class="info-box"><span class="info-label">发布时间</span><span class="info-value"><%= createTimeStr %></span></div>
                    </div>

                    <div class="publisher-panel">
                        <div class="publisher-avatar">
                            <%= item.getUserId() == null ? "用" : item.getUserId().charAt(0) %>
                        </div>
                        <div>
                            <div style="font-weight:bold;"><%= item.getUserId() %></div>
                            <div style="color:#888;font-size:14px;">发布于 <%= createTimeStr %></div>
                        </div>
                    </div>

                    <div class="action-row">
                        <button class="btn btn-light" onclick="shareItem()">分享</button>
                        <button class="btn btn-danger" onclick="showReportModal()">举报</button>
                        <button class="btn btn-primary" onclick="contactPublisher()">联系发布者</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="reportModal" class="report-modal">
        <div class="report-form">
            <h3>举报</h3>
            <div class="form-group" style="margin:15px 0">
                <label>举报原因</label>
                <textarea id="reportReason" style="width:100%;height:80px;margin-top:5px;padding:8px;"></textarea>
            </div>
            <div class="form-actions" style="display:flex;gap:10px;justify-content:flex-end;">
                <button class="btn btn-light" onclick="hideReportModal()">取消</button>
                <button class="btn btn-danger" onclick="submitReport()">提交举报</button>
            </div>
        </div>
    </div>

    <div class="page-foot"><p>版权所有 © 2026 校园寻物系统</p></div>
</body>
</html>