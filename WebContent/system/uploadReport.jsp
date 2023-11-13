<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="bean.User"%>
<% 
String path = request.getContextPath()+ "/system";
String basePath = request.getScheme() + "://"
		+ request.getServerName() + ":" + request.getServerPort()
		+ path + "/";
String result = "";
if(request.getAttribute("message")!=null){
	result = request.getAttribute("message").toString();
}
int projectId = Integer.valueOf(request.getParameter("projectId"));
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
</head>
<script language="javascript">
	window.onload = function() {
		var msg = document.getElementById("msg").value;
		if(msg != ""){
			alert(msg);
		}
	}
	//上传项目报告信息
  	function upload(projectId,studentId) {
        var file_obj = document.getElementById('inputfile').files[0];
        var formData = new FormData();
        formData.append("inputfile",file_obj);
        $.ajax({
            url: "reportManager?type=uploadReport&projectId="+projectId+"&id="+studentId,
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
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span> 上传实验报告信息</strong></div>
  <div class="body-content">
    <form id="updateAll" enctype="multipart/form-data" method="post">
        <div  class="form-group" style="width: 420px;">
           <div class="input-group" style="float: left;margin-right: 7px;">
               <label></label>
               <input type="file" class="button margin-left" id="inputfile" name ="upfile">
            </div>
        </div>
        <button type="button" onclick="upload('<%=projectId %>','<%=user.getId() %>')" class="button border-dot icon-upload" style="margin-top: -20px;">上传项目报告（仅限PDF文件）</button>
     </form>
    <input type="hidden" value="<%=result %>" id="msg"/>
  </div>
</div>
</body>
</html>