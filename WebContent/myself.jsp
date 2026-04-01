<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.entity.TUser" %>
<%@ page import="com.entity.Lost" %>
<%@ page import="com.entity.Found" %>
<%@ page import="java.util.List" %>
<%
    // 1. 后端登录校验（永不丢失状态）
    TUser currentUser = (TUser) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/logIn.html");
        return;
    }

    String username = currentUser.getUsername();
    Integer role = currentUser.getRole();
    boolean isAdmin = (role != null && role == 1);
    String contextPath = request.getContextPath();

    // 2. 从Request中获取当前用户发布的帖子（由MyPostServlet传过来）
    List<Lost> myLostList = (List<Lost>) request.getAttribute("myLostList");
    List<Found> myFoundList = (List<Found>) request.getAttribute("myFoundList");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人主页 - 校园寻物系统</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #90f5ddbd; min-height: 100vh; display: flex; flex-direction: column; }
        .title { background: #fff; color: #549687; text-align: center; padding: 15px 0; font-size: 24px; border-radius: 0 0 15px 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .user-info { background: #549687; color: #fff; padding: 10px 20px; display: flex; justify-content: space-between; align-items: center; }
        .logout-btn { background: #b71d1d90; color: #fff; border: none; padding: 8px 20px; border-radius: 8px; cursor: pointer; transition: 0.3s; }
        .logout-btn:hover { background: #8e2f2f; transform: translateY(-2px); }
        .main-content { flex: 1; padding: 30px 20px; display: flex; flex-direction: column; align-items: center; }
        .container { width: 100%; max-width: 1200px; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .page-title { text-align: center; color: #333; margin-bottom: 30px; font-size: 24px; border-bottom: 2px solid #549687; padding-bottom: 15px; }
        .info-card { display: flex; align-items: center; gap: 30px; padding: 25px; border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 30px; flex-wrap: wrap; background: #f9fffe; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
        .avatar { width: 120px; height: 120px; border-radius: 50%; background: #549687; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 40px; font-weight: bold; box-shadow: 0 4px 12px rgba(84,150,135,0.3); }
        .info-detail { flex: 1; min-width: 250px; }
        .info-item { margin-bottom: 15px; font-size: 16px; color: #555; display: flex; align-items: center; }
        .info-item label { font-weight: 600; color: #333; margin-right: 12px; min-width: 80px; }
        .tab-nav { display: flex; border-bottom: 1px solid #e0e0e0; margin-bottom: 25px; gap: 10px; }
        .tab-item { padding: 12px 25px; font-size: 16px; color: #666; cursor: pointer; border-bottom: 3px solid transparent; transition: 0.3s; border-radius: 8px 8px 0 0; background: #f0f9f7; }
        .tab-item:hover { background: #e1f3ef; color: #549687; }
        .tab-item.active { color: #fff; background: #549687; border-bottom-color: #478073; font-weight: 600; }
        .publish-list { display: none; }
        .publish-list.active { display: block; }
        .publish-item { padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 20px; transition: box-shadow 0.3s; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
        .publish-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); transform: translateY(-2px); }
        .publish-item h3 { color: #333; font-size: 18px; margin-bottom: 12px; }
        .publish-item p { color: #666; font-size: 14px; margin-bottom: 10px; line-height: 1.6; }
        .item-tag { display: inline-block; padding: 4px 12px; background: #f0f9f7; color: #549687; border-radius: 15px; font-size: 12px; margin-right: 10px; font-weight: 50; border: 1px solid #e1f3ef; }
        .item-btn-group { margin-top: 15px; display: flex; gap: 10px; }
        .btn { padding: 10px 25px; border: none; border-radius: 8px; font-size: 14px; cursor: pointer; transition: 0.3s; font-weight: 50; display: flex; align-items: center; gap: 8px; }
        .btn-primary { background: #549687; color: #fff; }
        .btn-primary:hover { background: #478073; transform: translateY(-2px); }
        .btn-secondary { background: #e1f3ef; color: #549687; border: 1px solid #549687; }
        .btn-secondary:hover { background: #549687; color: #fff; }
        .btn-danger { background: #d9534f; color: #fff; }
        .btn-danger:hover { background: #c9302c; transform: translateY(-2px); }
        .page-foot { background: #549687; color: #fff; text-align: center; padding: 20px 0; margin-top: auto; }
        .title-container { display: flex; justify-content: space-between; align-items: center; padding: 0 20px; }
        .back-to-home { color: #fff; background: #549687; padding: 8px 15px; border-radius: 8px; text-decoration: none; font-size: 14px; display: flex; align-items: center; gap: 5px; transition: 0.3s; font-weight: 50; }
        .back-to-home:hover { background: #478073; transform: translateY(-2px); }
        .empty-state { text-align: center; color: #666; padding: 50px; background: #fff; border-radius: 10px; width: 100%; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .content-section { display: none; }
        .content-section.active { display: block; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #333; }
        .form-group input { width: 100%; padding: 12px; border: 1px solid #e0e0e0; border-radius: 8px; font-size: 14px; }
    </style>

    <script>
        const contextPath = "<%= contextPath %>";
        window.onload = function () {
            const username = "<%= username.replace("\"", "\\\"") %>";
            const isAdmin = <%= isAdmin %>;
            document.getElementById("usernameDisplay").textContent = username;
            document.getElementById("displayUsername").textContent = username;
            document.getElementById("userAvatar").textContent = username.charAt(0);

            if (isAdmin) {
                document.getElementById("usernameDisplay").innerHTML += ' <span style="color:#ff9800; font-size:12px;">(管理员)>/span>';
                document.getElementById("adminTab").style.display = "block";
            }

            const tabItems = document.querySelectorAll('.tab-item');
            tabItems.forEach(tab => {
                tab.addEventListener('click', function() {
                    tabItems.forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
                    const target = this.getAttribute('data-target');
                    document.getElementById(target).classList.add('active');
                });
            });

            if (tabItems.length > 0) {
                tabItems[0].classList.add('active');
                document.getElementById(tabItems[0].dataset.target).classList.add('active');
            }
        };

        function viewPostDetail(postId, postType) {
            window.location.href = contextPath + `/detail.jsp?id=${postId}&type=${postType}`;
        }
        function editPost(postId, postType) {
            alert(`编辑帖子 ID: ${postId}，类型: ${postType}`);
        }
        function deletePost(postId, postType) {
            if (confirm(`确定删除帖子 ID: ${postId} 吗？`)) {
                window.location.reload();
            }
        }
        function logout() {
            if (confirm("确定退出登录？")) {
                window.location.href = contextPath + "/logoutServlet";
            }
        }
        function editProfile() {
            alert("编辑资料功能");
        }
        function changePassword() {
            alert("修改密码功能");
        }
    </script>
</head>
<body>
    <div class="title">
        <div class="title-container">
            <a href="<%= contextPath %>/home.jsp" class="back-to-home">← 返回主页</a>
            <h1>校园寻物系统</h1>
            <div class="title-placeholder"></div>
        </div>
    </div>

    <div class="user-info">
        <span>当前用户：<span id="usernameDisplay"></span></span>
        <button class="logout-btn" onclick="logout()">退出登录</button>
    </div>

    <div class="main-content">
        <div class="container">
            <h2 class="page-title">个人主页</h2>

            <div class="info-card">
                <div class="avatar" id="userAvatar"></div>
                <div class="info-detail">
                    <div class="info-item">
                        <label>用户名：</label>
                        <span id="displayUsername"></span>
                    </div>
                    <div class="info-item">
                        <label>注册时间：</label>
                        <span>2026-01-01</span>
                    </div>
                    <div class="info-item">
                        <label>发布帖子数：</label>
                        <span><%= (myLostList != null ? myLostList.size() : 0) + (myFoundList != null ? myFoundList.size() : 0) %></span>
                    </div>
                </div>
            </div>

            <div class="tab-nav">
                <div class="tab-item" data-target="myPosts">我的发布</div>
                <div class="tab-item" data-target="passwordSection">修改密码</div>
                <div class="tab-item" id="adminTab" style="display:none;" data-target="adminSection">管理员功能</div>
            </div>

            <div id="myPosts" class="content-section active">
                <%
                    if (myLostList != null && !myLostList.isEmpty()) {
                        for (Lost lost : myLostList) {
                %>
                <div class="publish-item">
                    <h3><%= lost.getTitle() %></h3>
                    <p>
                        <span class="item-tag">寻物</span>
                        <span class="item-tag"><%= lost.getStatus() %></span>
                    </p>
                    <p>地点：<%= lost.getPlace() %> | 时间：<%= lost.getLostTime() %></p>
                    <p><%= lost.getContent() %></p>
                    <div class="item-btn-group">
                        <button class="btn btn-primary" onclick="viewPostDetail(<%= lost.getId() %>, 'lost')">查看详情</button>
                        <button class="btn btn-secondary" onclick="editPost(<%= lost.getId() %>, 'lost')">编辑</button>
                        <button class="btn btn-danger" onclick="deletePost(<%= lost.getId() %>, 'lost')">删除</button>
                    </div>
                </div>
                <%
                        }
                    }
                    if (myFoundList != null && !myFoundList.isEmpty()) {
                        for (Found found : myFoundList) {
                %>
                <div class="publish-item">
                    <h3><%= found.getTitle() %></h3>
                    <p>
                        <span class="item-tag">招领</span>
                        <span class="item-tag"><%= found.getStatus() %></span>
                    </p>
                    <p>地点：<%= found.getPlace() %> | 时间：<%= found.getFoundTime() %></p>
                    <p><%= found.getContent() %></p>
                    <div class="item-btn-group">
                        <button class="btn btn-primary" onclick="viewPostDetail(<%= found.getId() %>, 'found')">查看详情</button>
                        <button class="btn btn-secondary" onclick="editPost(<%= found.getId() %>, 'found')">编辑</button>
                        <button class="btn btn-danger" onclick="deletePost(<%= found.getId() %>, 'found')">删除</button>
                    </div>
                </div>
                <%
                        }
                    }
                    if ((myLostList == null || myLostList.isEmpty()) && (myFoundList == null || myFoundList.isEmpty())) {
                %>
                <div class="empty-state">您还没有发布任何帖子</div>
                <%
                    }
                %>
            </div>

            <div id="passwordSection" class="content-section">
                <h3>修改密码</h3>
                <div class="form-group">
                    <label>原密码</label>
                    <input type="password">
                </div>
                <div class="form-group">
                    <label>新密码</label>
                    <input type="password">
                </div>
                <div class="form-group">
                    <label>确认新密码</label>
                    <input type="password">
                </div>
                <button class="btn btn-primary" onclick="changePassword()">修改</button>
            </div>

            <div id="adminSection" class="content-section">
                <h3>管理员功能</h3>
                <div style="display:flex; gap:15px; margin-top:20px;">
                    <a href="<%= contextPath %>/information-audit-manage.html" class="btn btn-primary">信息审核</a>
                    <a href="<%= contextPath %>/content-manage.html" class="btn btn-primary">内容管理</a>
                    <a href="<%= contextPath %>/user-manage.html" class="btn btn-primary">用户管理</a>
                </div>
            </div>
        </div>
    </div>

    <div class="page-foot">
        <p>版权所有 © 2026 校园寻物系统</p>
    </div>
</body>
</html>