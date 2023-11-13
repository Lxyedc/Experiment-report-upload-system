<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.Project"%>
<%@page import="bean.User"%>
<%@page import="bean.PageBean"%>
<%
	String path = request.getContextPath() + "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	PageBean<Project> pageBean = (PageBean<Project>)request.getAttribute("projectList");
	List<Project> projectList = (List<Project>)pageBean.getList();
	String pageNum = request.getParameter("pageNum").toString();
	int start = pageBean.getStart();
	int totalPage = pageBean.getTotalPage();
	String result = "";
	if(request.getAttribute("msg")!=null){
		result = request.getAttribute("msg").toString();
	}
	String searchName = "";
	if(request.getAttribute("searchName")!=null){
		searchName = request.getAttribute("searchName").toString();
	}
	User user = null;
	if(request.getSession().getAttribute("user")!=null){
		user = (User)request.getSession().getAttribute("user");
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" href="css/pintuer.css">
<link rel="stylesheet" href="css/admin.css">
<script src="js/jquery.js"></script>
<script src="js/pintuer.js"></script>
</head>
<script language="javascript">
	window.onload = function() {
		var msg = document.getElementById("msg").value;
		if(msg != ""){
			alert(msg);
		}
	}
</script>
<body>
 <div class="panel admin-panel">
   <div class="panel-head"><strong class="icon-reorder"> 实验项目列表</strong></div>
   <div class="padding border-bottom">
  	 <form action="reportManager?pageNum=1&type=selProject&id=<%=user.getId() %>" method="post">
	     <ul class="search" style="padding-left:10px;">
	       <li><input type="text" placeholder="请输入项目名称" value="<%=searchName %>" name="searchName" class="input" style="width:250px; line-height:17px;display:inline-block" />
	       <button class="button border-main icon-search" type="submit" >搜索</button></li>
	      </ul>
     </form>
   </div>
   <table class="table table-hover text-center">
     <tr>
       <th>课程名称</th>
       <th>课程班级</th>
       <th>项目名称</th>
       <th>截止时间</th>
       <th>操作</th>
     </tr>
     <volist name="list" id="vo">
     <% 
     	if(projectList!=null){
	 	for(Project p:projectList){
  	 %>
       <tr>
         <td><%=p.getClasses().getName() %></td>
         <td><%=p.getClasses().getGrade() %></td>
         <td><%=p.getName() %></td>
         <td><%=p.getTime() %></td>
         <% if(p.getStopFlag().equals("false")){ %>
         <td><div class="button-group">
        	   <a class="button border-main" href="<%=path %>/uploadReport.jsp?projectId=<%=p.getId() %>"><span class="icon-edit"></span> 上传实验报告 </a>
         	 </div>
         </td>
         <%}else{ %>
         <td>
         	<div class="button-group">
        	   <a class="button border-red" href="#"><span class="icon-edit"></span> 上传时间已过 </a>
         	 </div>
         </td>
         <%} %>
        </tr>
      <%}
	 	} %>
     <tr>
        <td colspan="11">
        <div class="pagelist"> 
	        <span class="current">当前为第<%=pageNum %>页,共<%=totalPage %>页</span>
	        	<% if(Integer.valueOf(pageNum) > 1){ %>
					<a href="<%=path %>/reportManager?pageNum=<%=Integer.valueOf(pageNum)-1 %>&type=selProject&searchName=<%=searchName %>&id=<%=user.getId() %>">上一页</a>
				<%}else{ %>
					<a href="#">上一页 </a>
				<%} %>
				<% if(start <= totalPage){%> 
					<a href="<%=path %>/reportManager?pageNum=<%=start %>&type=selProject&searchName=<%=searchName %>&id=<%=user.getId() %>">下一页</a> 
					<a href="<%=path %>/reportManager?pageNum=<%=totalPage %>&type=selProject&searchName=<%=searchName %>&id=<%=user.getId() %>">尾页</a>
				<%}else{ %>
					<a href="#">下一页</a>  
					<a href="#">尾页 </a>
				<% }%>
         </div>
        </td>
      </tr>
           <input type="hidden" value="<%=result %>" id="msg"/>       
   </table>
 </div>
</body>
</html>