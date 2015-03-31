<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"%>">
<meta charset="utf-8">
<title>注册</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="./css/bootstrap-responsive.min.css" rel="stylesheet">
</head>
<body>

	<div id="regDiglog" class="modal hide fade">
		<div class="modal-header">
			<h3>注册</h3>
		</div>
		<div class="modal-body">
			<form id="regform" class="form-horizontal" method="post" action="./talkService.srv?action=reg">
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
				<div class="control-group">
				 	<label class="control-label" for="passConfirm">密码确认</label>
					<div class="controls">
						<input type="password" id="passConfirm" name="passConfirm">
						<span name="nullTip" class="help-inline hide">请再次输入密码！</span>
						<span name="errorTip" class="help-inline hide">密码不一致！</span>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button id="regBtn" class="btn btn-primary">注册</button>
			<a id="loginBtn" class="btn btn-success" href="./talkService.srv?action=loginPage">登录</a>
		</div>
	</div>
	
<script src="./js/jquery-1.8.3.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function(){
		$("#regDiglog").modal({backdrop:"static"});
			
		$("#regBtn").click(function(){
			
			var userName=$("#userName").val();
			var password=$("#password").val();
			var passConfirm=$("#passConfirm").val();
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
			
			if(passConfirm==null||passConfirm==''){
				$("#passConfirm").parents(".control-group").addClass("error");
				$("#passConfirm").siblings(".help-inline[name='nullTip']").show();
				$("#passConfirm").one("focus",function(){
					$(this).parents(".control-group").removeClass("error");
					$(this).siblings(".help-inline[name='nullTip']").hide();
				});
				return;
			}
			
			if(passConfirm!=password){
				$("#passConfirm").parents(".control-group").addClass("error");
				$("#passConfirm").siblings(".help-inline[name='errorTip']").show();
				$("#passConfirm").one("focus",function(){
					$(this).parents(".control-group").removeClass("error");
					$(this).siblings(".help-inline[name='errorTip']").hide();
				});
				return;
			}
			
			$("#regform").submit();
		});
	});
</script>
</body>
</html>