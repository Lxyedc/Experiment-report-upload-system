<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.Report"%>
<%@page import="bean.User"%>
<%@page import="bean.PageBean"%>
<%
	String path = request.getContextPath() + "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	PageBean<Report> pageBean = (PageBean<Report>)request.getAttribute("reportList");
	List<Report> reportList = (List<Report>)pageBean.getList();
	String pageNum = request.getParameter("pageNum").toString();
	int start = pageBean.getStart();
	int totalPage = pageBean.getTotalPage();
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
<body>
 <div class="panel admin-panel">
   <div class="panel-head"><strong class="icon-reorder"> 个人项目报告列表</strong></div>
   <div class="padding border-bottom">
  	 <form action="reportManager?pageNum=1&type=selSigle&id=<%=user.getId() %>" method="post">
	     <ul class="search" style="padding-left:10px;">
	       <li><input type="text" placeholder="请输入项目名称" value="<%=searchName %>" name="searchName" class="input" style="width:250px; line-height:17px;display:inline-block" />
	       <button class="button border-main icon-search" type="submit" >搜索</button></li>
	     </ul>
     </form>
   </div>
   <table class="table table-hover text-center">
     <tr>
       <th>项目名称</th>
       <th>学生学号</th>
       <th>学生姓名</th>
       <th>操作</th>
     </tr>
     <volist name="list" id="vo">
     <% 
     	if(reportList!=null){
	 	for(Report r:reportList){
  	 %>
       <tr>
         <td><%=r.getProject().getName() %></td>
         <td><%=r.getUser().getUid() %></td>
         <td><%=r.getUser().getName() %></td>
         <td><div class="button-group">
        	   <a class="button border-main" href="<%=path %>/reportManager?url=<%=r.getUrl() %>&type=downloadReport"><span class="icon-trash-edit"></span> 浏览实验报告</a>
 		     </div>
         </td>
        </tr>
      <%}
	 	} %>
     <tr>
        <td colspan="11">
        <div class="pagelist"> 
	        <span class="current">当前为第<%=pageNum %>页,共<%=totalPage %>页</span>
	        	<% if(Integer.valueOf(pageNum) > 1){ %>
					<a href="<%=path %>/reportManager?pageNum=<%=Integer.valueOf(pageNum)-1 %>&type=selSigle&id=<%=user.getId() %>&searchName=<%=searchName %>">上一页</a>
				<%}else{ %>
					<a href="#">上一页 </a>
				<%} %>
				<% if(start <= totalPage){%> 
					<a href="<%=path %>/reportManager?pageNum=<%=start %>&type=selSigle&id=<%=user.getId() %>&searchName=<%=searchName %>">下一页</a> 
					<a href="<%=path %>/reportManager?pageNum=<%=totalPage %>&type=selSigle&id=<%=user.getId()%>&searchName=<%=searchName %>">尾页</a>
				<%}else{ %>
					<a href="#">下一页</a>  
					<a href="#">尾页 </a>
				<% }%>
         </div>
        </td>
      </tr>    
   </table>
 </div>
</body>
</html>