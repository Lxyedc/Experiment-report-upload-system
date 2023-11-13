<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.User"%>
<%@page import="bean.PageBean"%>
<%
	String path = request.getContextPath() + "/system";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	PageBean<User> pageBean = (PageBean<User>)request.getAttribute("userList");
	List<User> userList = (List<User>)pageBean.getList();
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
	int classesId = Integer.valueOf(request.getParameter("id"));
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
	function downloadData(path){
		window.location.href = path + "/classesManager?type=download";
	};
	window.onload = function() {
		var msg = document.getElementById("msg").value;
		if(msg != ""){
			alert(msg);
		}
	}
	//上传课程学生
  	function upload(classesId) {
        var file_obj = document.getElementById('inputfile').files[0];
        var formData = new FormData();
        formData.append("inputfile",file_obj);
        $.ajax({
            url: "classesManager?type=export&id="+classesId,
            type: "POST",
            data:  formData,
            async: true,
            processData: false,
            contentType: false,
            cache: false,
            mimeType: "multipart/form-data", 
            success: function (data, status) {
            	data = eval('(' + data + ')');
                if(data == null){
                    alert("暂无数据");
                }else {
                    alert(data.message);
                }
            },
            error: function () {
                alert("上传失败");
            }
        });
    };
</script>
<body>
 <div class="panel admin-panel">
   <div class="panel-head"><strong class="icon-reorder"> 课程对应学生列表</strong></div>
   <div class="padding border-bottom">
  	 <form action="classesManager?pageNum=1&type=info&id=<%=classesId %>" method="post">
	     <ul class="search" style="padding-left:10px;">
	       <li><input type="text" placeholder="请输入学生学号或姓名" value="<%=searchName %>" name="searchName" class="input" style="width:250px; line-height:17px;display:inline-block" />
	       <button class="button border-main icon-search" type="submit" >搜索</button></li>
	       <button class="button border-dot icon-download" type="button" onclick="downloadData('<%=path %>')" >下载课程学生模板</button> 
      	</ul>
     </form>
     <form id="updateAll" style="margin-top: 10px;" enctype="multipart/form-data" method="post">
          <div  class="form-group" style="width: 420px;">
             <div class="input-group" style="float: left;margin-right: 7px;">
                 <label></label>
                 <input type="file" class="button margin-left" id="inputfile" name ="upfile">
              </div>
          </div>
          <button type="button" onclick="upload('<%=classesId %>')" class="button border-dot icon-upload" style="margin-top: -20px;">上传课程学生</button>
      </form> 
   </div>
   <table class="table table-hover text-center">
     <tr>
       <th>学生学号</th>
       <th>学生姓名</th>
       <th>操作</th>
     </tr>
     <volist name="list" id="vo">
     <% 
     	if(userList!=null){
	 	for(User u:userList){
  	 %>
       <tr>
         <td><%=u.getUid() %></td>
         <td><%=u.getName() %></td>
         <td><div class="button-group">
        	     <a class="button border-red" href="<%=path %>/classesManager?id=<%=u.getId() %>&searchName=<%=searchName %>&type=delUser&pageNum=<%=pageNum%>"><span class="icon-trash-o"></span> 删除</a>
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
					<a href="<%=path %>/classesManager?pageNum=<%=Integer.valueOf(pageNum)-1 %>&type=info&id=<%=classesId %>&searchName=<%=searchName %>">上一页</a>
				<%}else{ %>
					<a href="#">上一页 </a>
				<%} %>
				<% if(start <= totalPage){%> 
					<a href="<%=path %>/classesManager?pageNum=<%=start %>&type=info&id=<%=classesId %>&searchName=<%=searchName %>">下一页</a> 
					<a href="<%=path %>/classesManager?pageNum=<%=totalPage %>&type=info&id=<%=classesId %>&searchName=<%=searchName %>">尾页</a>
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