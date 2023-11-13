<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="bean.User" %>
<% 	
String result = "";
if(request.getAttribute("msg")!=null){
	result = request.getAttribute("msg").toString();
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
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span> 新增课程</strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="classesManager?type=add">  
    	<input type="hidden" value="<%=user.getId() %>" name="teacherId">
      	<div class="form-group">
         <div class="label">
           <label>课程名称：</label>
         </div>
        <div class="field">
           <input type="text" name="name" class="input w50" required/>
           <div class="tips"></div>
         </div>
      </div>
      <div class="form-group">
         <div class="label">
           <label>班级名称：</label>
         </div>
        <div class="field">
           <input type="text" name="grade" class="input w50" required/>
           <div class="tips"></div>
         </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" type="submit">确  认</button>
        </div>
      </div>
    </form>
    <input type="hidden" value="<%=result %>" id="msg"/>
  </div>
</div>
</body>
</html>