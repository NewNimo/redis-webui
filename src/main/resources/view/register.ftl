<!doctype html>
<html lang="zh">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>注册</title>

	<!-- jQuery文件 -->
	<script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
	<script src="/static/js/semantic.min.js"></script>
	<link rel="stylesheet" type="text/css" href="/static/css/semantic.min.css">
		<style>
		form{border: 1px solid #ccc;padding: 10px 20px; border-radius: 5px;margin-top: 200px;}
	</style>
	
</head>
<body>
	<div class="ui middle aligned center aligned grid">
		<form class="ui form ">
			<div class="inline field ">
			  <label>账号</label>
			  <input type="text" name="user" id="user" placeholder="用户名">
			</div>
			<div class="inline field ">
			  <label>密码</label>
			  <input type="password" name="password" id="password" placeholder="密码">
			</div>
			<div class="ui error message" id="error">
				<div class="header">错误</div>
				<p id="msg"></p>
			  </div>
			<div class="ui button teal fluid" id="submit">注册</div>
		  </form>
</div>

	<div class="ui modal mini" id="tipsbox">
		<div class="header green">
			温馨提示
		</div>
		<div class="content">
			<p id="result">注册成功</p>
		</div>
		<div class="actions">
			<div class="ui button approve green">OK</div>
		</div>
	</div>

</body>
	<script>
		$(document).ready(function(){
				 $("#submit").click(function(){
					 reg();
		 		});
		});
		
		$(document).keyup(function(event){
		  if(event.keyCode ===13){
			  reg();
		  }
		});
		
		function reg(){
					var data={};
					data["username"]=$.trim($("#user").val());
					data["password"]=$.trim($("#password").val());
		 			$.ajax({
					  type: 'post',
					  url: "/register",
					  dataType: "json", contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	 if(data.ok){
							 $('#tipsbox').modal({
								 onApprove:function(){
									 window.location.href="/index";
								 }
							 }).modal('show');
					  	 }else{
							$("#msg").text(data.message);
							$("#error").show();
					  	 }
					  }
					});
		}
	</script>
</html>