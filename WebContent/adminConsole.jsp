<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>管理员控制台</title>
    <style>
        *{margin:0;padding:0;box-sizing:border-box;}
        body{background:#90f5ddbd;font-family:Arial;padding:30px;}
        .container{
            max-width:1000px;margin:0 auto;background:white;
            padding:30px;border-radius:12px;box-shadow:0 2px 10px #0000001a;
        }
        .title{font-size:24px;color:#549687;margin-bottom:20px;}
        .user-info{color:#666;margin-bottom:30px;}
        .menu{display:grid;grid-template-columns:1fr 1fr 1fr;gap:20px;}
        .menu a{
            padding:25px 20px;text-align:center;background:#549687;
            color:white;text-decoration:none;border-radius:10px;font-size:16px;
        }
        .menu a:hover{background:#478073;}
        .logout{margin-top:20px;color:red;}
    </style>
</head>
<body>
<div class="container">
    <div class="title">管理员控制台</div>
    <div class="user-info">
        欢迎：${currentUser.username}（管理员）
    </div>

    <div class="menu">
        <a href="admin">用户管理</a>
        <a href="adminAllItem">内容管理</a>
        <a href="adminAuditList">信息审核</a>
    </div>

    <div class="logout">
        <a href="logout">退出登录</a>
    </div>
</div>
</body>
</html>