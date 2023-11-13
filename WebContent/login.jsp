<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String result = "";
if(request.getAttribute("msg")!=null){
	result = request.getAttribute("msg").toString();
}
%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta name="renderer" content="webkit">
    <title>登录</title>  
  	<link rel="stylesheet" href="<%=basePath %>/system/css/pintuer.css">
    <link rel="stylesheet" href="<%=basePath %>/system/css/admin.css">
    <script src="<%=basePath %>/system/js/jquery.js"></script>
    <script src="<%=basePath %>/system/js/pintuer.js"></script>  
</head>
<script language="javascript">
	window.onload = function() {
		var msg = document.getElementById("msg").value;
		if(msg != ""){
			alert(msg);
		}
	}
	function getPath(){
	    var pathName = document.location.pathname;
	    var index = pathName.substr(1).indexOf("/");
	    var result = pathName.substr(0,index+1);
	    return result;
	}
	function change()
	{
		var ss= getPath() + "/system/GeneImg";
		document.getElementById("verifycode").src=ss+"?name="+Math.random();
	}
</script>
<body class="bg">
<div class="container">
    <div class="line bouncein">
        <div class="xs6 xm4 xs3-move xm4-move">
            <div style="height:150px;"></div>
            <div class="media media-y margin-big-bottom">           
            </div>         
            <form action="/fileUploadSystem/login" method="post" >
            <div class="panel loginbox">
                <div class="text-center margin-big padding-big-top"><h1> 实验报告上传系统</h1></div>
                <div class="panel-body" style="padding:30px; padding-bottom:10px; padding-top:10px;">
                    <div class="form-group">
                        <div class="field field-icon-right">
                            <input type="text" class="input input-big" name="uid" placeholder="账号" data-validate="required:请填写账号" />
                            <span class="icon icon-user margin-small"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="field field-icon-right">
                            <input type="password" class="input input-big" name="password" placeholder="密码" data-validate="required:请填写密码" />
                            <span class="icon icon-key margin-small"></span>
                        </div>
                    </div> 
                    <div class="form-group">
                        <div class="field field-icon-right">
                            <select class="role" name="role">     
							  <option value="teacher">教师</option>     
						  	  <option value="student" selected>学生</option>
							 </select> 
                        </div>
                    </div> 
                    <div class="form-group">
                        <div class="field field-icon-right">
                            <input type="text" class="input input-big" style="width: 263px;display: initial;" name="verifyCode" placeholder="验证码" size="5" />
                            <img id="verifycode" src="/fileUploadSystem/system/GeneImg?name=<%=Math.random() %>" onclick="change()"/>
                        </div>
                    </div> 
                </div>
                <div style="padding:0 30px 25px;">
                <input type="submit" class="button button-block bg-main text-big input-big" value="登录">
                </div>
             </div>
            </form>  
            	<input type="hidden" value="<%=result %>" id="msg"/>       
        </div>
    </div>
</div>

</body>
</html>