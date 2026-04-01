<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.entity.Lost" %>
<%@ page import="com.entity.Found" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>管理员后台</title>
    <style>
        body{font-family: Arial;margin:30px;}
        .item{border:1px solid #ccc;padding:15px;margin:10px 0;}
        .btn{padding:5px 10px;margin:0 5px;text-decoration:none;color:#fff;border-radius:3px;}
        .pass{background:#549687;}
        .reject{background:#e37979;}
        .offline{background:#888;}
    </style>
</head>
<body>
    <h1>管理员审核面板</h1>
    <a href="home.jsp">返回首页</a>

    <h2>失物信息管理</h2>
    <%
    List<Lost> lostList = (List<Lost>) request.getAttribute("lostList");
    if(lostList != null) {
        for(Lost lost : lostList) {
    %>
    <div class="item">
        <h3><%=lost.getTitle()%></h3>
        <p>状态：
        <% if(lost.getStatus()==0) { %>待审核
        <% } else if(lost.getStatus()==1) { %>通过
        <% } else if(lost.getStatus()==2) { %>驳回
        <% } else { %>下架<% } %>
        </p>
        <a href="audit?type=lost&id=<%=lost.getId()%>&status=1" class="btn pass">通过</a>
        <a href="audit?type=lost&id=<%=lost.getId()%>&status=2" class="btn reject">驳回</a>
        <a href="audit?type=lost&id=<%=lost.getId()%>&status=3" class="btn offline">下架</a>
    </div>
    <% }} %>

    <h2>招领信息管理</h2>
    <%
    List<Found> foundList = (List<Found>) request.getAttribute("foundList");
    if(foundList != null) {
        for(Found found : foundList) {
    %>
    <div class="item">
        <h3><%=found.getTitle()%></h3>
        <p>状态：
        <% if(found.getStatus()==0) { %>待审核
        <% } else if(found.getStatus()==1) { %>通过
        <% } else if(found.getStatus()==2) { %>驳回
        <% } else { %>下架<% } %>
        </p>
        <a href="audit?type=found&id=<%=found.getId()%>&status=1" class="btn pass">通过</a>
        <a href="audit?type=found&id=<%=found.getId()%>&status=2" class="btn reject">驳回</a>
        <a href="audit?type=found&id=<%=found.getId()%>&status=3" class="btn offline">下架</a>
    </div>
    <% }} %>
</body>
</html>