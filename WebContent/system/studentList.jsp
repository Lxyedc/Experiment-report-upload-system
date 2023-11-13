<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.User"%>
<%@page import="bean.PageBean"%>
<%
	String path = request.getContextPath()+ "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	PageBean pageBean = (PageBean)request.getAttribute("studentList");
	List<User> studentList = (List<User>)pageBean.getList();
	String pageNum = request.getParameter("pageNum").toString();
	int start = pageBean.getStart();
	int totalPage = pageBean.getTotalPage();
	String searchName = "";
	if(request.getAttribute("searchName")!=null){
		searchName = request.getAttribute("searchName").toString();
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
   <div class="panel-head"><strong class="icon-reorder"> 学生信息列表</strong></div>
   <div class="padding border-bottom">
      <form action="userManager?pageNum=1&type=sel" method="post">
	     <ul class="search" style="padding-left:10px;">
	       <li><input type="text" placeholder="请输入学号/姓名" value="<%=searchName %>" name="searchName" class="input" style="width:250px; line-height:17px;display:inline-block" />
	       <button class="button border-main icon-search" type="submit" >搜索</button></li>
	      </ul>
     </form>
   </div>
   <table class="table table-hover text-center">
     <tr>
       <th>学号</th>
       <th>姓名</th>
       <th>登录密码</th>
       <th>操作</th>
     </tr>
     <volist name="list" id="vo">
     <% 
     	if(studentList!=null){
	 	for(User u:studentList){
  	 %>
       <tr>
         <td><%=u.getUid() %></td>
         <td><%=u.getName() %></td>
         <td><%=u.getPassword() %></td>
         <td>
         <div class="button-group">
         	<a class="button border-main" href="<%=path %>/userManager?userId=<%=u.getId() %>&type=info"><span class="icon-edit"></span> 编辑</a> 
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
					<a href="<%=path %>/userManager?pageNum=<%=Integer.valueOf(pageNum)-1 %>&type=sel&searchName=<%=searchName %>">上一页</a>
				<%}else{ %>
					<a href="#">上一页 </a>
				<%} %>
				<% if(start <= totalPage){%> 
					<a href="<%=path %>/userManager?pageNum=<%=start %>&type=sel&searchName=<%=searchName %>">下一页</a> 
					<a href="<%=path %>/userManager?pageNum=<%=totalPage %>&type=sel&searchName=<%=searchName %>">尾页</a>
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