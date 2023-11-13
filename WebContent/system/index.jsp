<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="bean.User" %>
<%
	String path = request.getContextPath() + "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	User user = null;
	if(request.getSession().getAttribute("user")!=null){
		user = (User)request.getSession().getAttribute("user");
	}
%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=0.90, maximum-scale=0.90, user-scalable=no" />
    <title>实验报告上传系统</title>  
    <link rel="stylesheet" href="system/css/pintuer.css">
    <link rel="stylesheet" href="system/css/admin.css">
    <script src="system/js/jquery.js"></script>   
</head>
<body style="background-color:#f2f9fd;">
<div class="header bg-main">
  <div class="logo margin-big-left fadein-top">
    <h1><img src="system/images/logo.png" class="radius-circle rotate-hover" height="50" alt="" />实验报告上传系统</h1>
  </div>
  <div class="head-l">
  <span style="padding: 20px;color: #fff;font-size: 13px;"><% if(user!=null){ if(user.getRole().equals("teacher")){ %>教师<%}else{%> 学生<%} %>:&nbsp;&nbsp;<strong style="color: black;font-size: 13px;"><%=user.getName() %></strong><%} %></span> 
  <a class="button button-little bg-red" href="<%=path %>/loginOut"><span class="icon-power-off"></span> 退出登录</a> </div>
</div>
<div class="leftnav">
  <div class="leftnav-title"><strong><span class="icon-list"></span>菜单列表</strong></div>
	  <% if(user!=null){
	   	if(user.getRole().equals("teacher")){ %>
	  <h2><span class="icon-user"></span>学生信息管理</h2>
	  <ul>
	  	<li><a href="<%=path %>/userManager?pageNum=1&type=sel" target="right"><span class="icon-caret-right"></span>查看学生信息</a></li>
	  </ul>   
	  <h2><span class="icon-pencil-square-o"></span>课程信息管理</h2>
	  <ul>
   		<li><a href="<%=path %>/addClasses.jsp" target="right"><span class="icon-caret-right"></span>录入课程信息</a></li>
   		<li><a href="<%=path %>/classesManager?pageNum=1&type=sel&id=<%=user.getId() %>" target="right"><span class="icon-caret-right"></span>查看课程信息</a></li>
  	 </ul>
  	 <h2><span class="icon-pencil-square-o"></span>实验项目管理</h2>
	  <ul>
   		<li><a href="<%=path %>/classesManager?pageNum=1&type=selClasses&id=<%=user.getId() %>" target="right"><span class="icon-caret-right"></span>导入实验项目</a></li>
  	 </ul>
  	  <h2><span class="icon-pencil-square-o"></span>实验报告管理</h2>
	  <ul>
   		<li><a href="<%=path %>/reportManager?pageNum=1&type=selAllProject&id=<%=user.getId() %>" target="right"><span class="icon-caret-right"></span>下载实验报告</a></li>
	  </ul>
	<%}else{%>
  	 <h2><span class="icon-user"></span>实验报告</h2>
	  <ul>
	    <li><a href="<%=path %>/reportManager?type=selProject&pageNum=1&id=<%=user.getId() %>" target="right"><span class="icon-caret-right"></span>上传实验报告</a></li>
  	   <li><a href="<%=path %>/reportManager?type=selSigle&pageNum=1&id=<%=user.getId() %>" target="right"><span class="icon-caret-right"></span>浏览实验报告</a></li>
  	 </ul>
  	<%}
  	}%>
</div>
<script type="text/javascript">
$(function(){
  $(".leftnav h2").click(function(){
	  $(this).next().slideToggle(200);	
	  $(this).toggleClass("on"); 
  }) 
  $(".leftnav ul li a").click(function(){
	    $("#a_leader_txt").text($(this).text());
  		$(".leftnav ul li a").removeClass("on");
		$(this).addClass("on");
  })
}); 
</script>
<ul class="bread">
  <li><a class="icon-home"> 首页</a></li>
</ul>
<div class="admin">
  <iframe scrolling="auto" rameborder="0" src="./system/info.jsp" name="right" width="100%" height="100%"></iframe>
</div>
<div style="text-align:center;">
</div>
</body>
</html>