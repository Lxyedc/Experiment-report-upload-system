<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.Classes"%>
<%@page import="bean.User"%>
<%@page import="bean.PageBean"%>
<%
	String path = request.getContextPath() + "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	PageBean<Classes> pageBean = (PageBean<Classes>)request.getAttribute("classesList");
	List<Classes> classesList = (List<Classes>)pageBean.getList();
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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
   <div class="panel-head"><strong class="icon-reorder"> 课程列表</strong></div>
   <div class="padding border-bottom">
  	 <form action="classesManager?pageNum=1&type=sel&id=<%=user.getId() %>" method="post">
	     <ul class="search" style="padding-left:10px;">
	       <li><input type="text" placeholder="请输入课程名称或班级" value="<%=searchName %>" name="searchName" class="input" style="width:250px; line-height:17px;display:inline-block" />
	       <button class="button border-main icon-search" type="submit" >搜索</button></li>
	      </ul>
     </form>
   </div>
   <table class="table table-hover text-center">
     <tr>
       <th>课程名称</th>
       <th>课程班级</th>
       <th>对应学生数量</th>
       <th>操作</th>
     </tr>
     <volist name="list" id="vo">
     <% 
     	if(classesList!=null){
	 	for(Classes c:classesList){
  	 %>
       <tr>
         <td><%=c.getName() %></td>
         <td><%=c.getGrade() %></td>
         <td><%=c.getCount() %>&nbsp;人</td>
         <td><div class="button-group">
        	   <a class="button border-main" href="<%=path %>/classesManager?id=<%=c.getId() %>&pageNum=1&type=info"><span class="icon-edit"></span> 详情 </a>
         	   <a class="button border-red" href="<%=path %>/classesManager?id=<%=c.getId() %>&teacherId=<%=user.getId() %>&searchName=<%=searchName %>&type=del&pageNum=<%=pageNum%>"><span class="icon-trash-o"></span> 删除</a>
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
					<a href="<%=path %>/classesManager?pageNum=<%=Integer.valueOf(pageNum)-1 %>&type=sel&searchName=<%=searchName %>&id=<%=user.getId() %>">上一页</a>
				<%}else{ %>
					<a href="#">上一页 </a>
				<%} %>
				<% if(start <= totalPage){%> 
					<a href="<%=path %>/classesManager?pageNum=<%=start %>&type=sel&searchName=<%=searchName %>&id=<%=user.getId() %>">下一页</a> 
					<a href="<%=path %>/classesManager?pageNum=<%=totalPage %>&type=sel&searchName=<%=searchName %>&id=<%=user.getId() %>">尾页</a>
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