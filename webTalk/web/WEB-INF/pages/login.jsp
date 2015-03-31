<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"%>">
<meta charset="utf-8">
<title>登录</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="./css/bootstrap-responsive.min.css" rel="stylesheet">
</head>
<body>

	<div id="loginDiglog" class="modal hide fade">
		<div class="modal-header">
			<h3>登录</h3>
		</div>
		<div class="modal-body">
			<form id="loginform" class="form-horizontal" method="post" action="./talkService.srv?action=login">
				<div class="control-group">
				 	<label class="control-label" for="userName">用户名</label>
					<div class="controls">
						<input type="text" id="userName" name="userName">
						<span class="help-inline hide">请输入用户名！</span>
					</div>
				</div>
				<div class="control-group">
				 	<label class="control-label" for="password">密码</label>
					<div class="controls">
						<input type="password" id="password" name="password">
						<span class="help-inline hide">请输入用户密码！</span>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button id="loginBtn" class="btn btn-primary">登录</button>
			<a id="regBtn" class="btn btn-success" href="./talkService.srv?action=regPage">注册</a>
			<a href="./login.do"><img src="http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/img/Connect_logo_7.png" /></a>
		</div>
	</div>
	
<script src="./js/jquery-1.8.3.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function(){
		$("#loginDiglog").modal({backdrop:"static"});
			
		$("#loginBtn").click(function(){
			
			var userName=$("#userName").val();
			var password=$("#password").val();
			if(userName==null||userName==''){
				$("#userName").parents(".control-group").addClass("error");
				$("#userName").siblings(".help-inline").show();
				$("#userName").one("focus",function(){
					$(this).parents(".control-group").removeClass("error");
					$(this).siblings(".help-inline").hide();
				});
				return;
			}
			
			if(password==null||password==''){
				$("#password").parents(".control-group").addClass("error");
				$("#password").siblings(".help-inline").show();
				$("#password").one("focus",function(){
					$(this).parents(".control-group").removeClass("error");
					$(this).siblings(".help-inline").hide();
				});
				return;
			}
			
			$("#loginform").submit();
		});
	});
</script>
</body>
</html>