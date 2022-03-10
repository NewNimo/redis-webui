<!doctype html>
<html lang="zh">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
	<title>Redis控制台</title>

	<!-- jQuery文件 -->
	<script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href="/static/css/semantic.min.css">
	<script src="/static/js/semantic.min.js"></script>
<style>
	body{margin: 0;padding: 5px;height: 100%;height: 100%;overflow: hidden;}
	 .info{width: 98%;height: 50px;margin:0 auto;}
	 .list{width: 98%;max-height: 500px;margin: 0 auto;overflow: hidden}
	 textarea{max-height: 500px;overflow-y:scroll;}
	 .header h4 span{color:#21ba45;}
</style>
</head>
<body>
	<input type="text" id="key" value="${key}" hidden="hidden">
	<input type="text" id="server" value="${server}" hidden="hidden">
	<div class="info" id="info">
		<div class="ui mini secondary menu">
			<div class="header item">
				<h4>Key: <span style="user-select: text;">${key}</span> </h4>
			</div>
			<div class="header  item">
				<h4>Type: <span style="user-select: text;">string</span></h4> 
			</div>
			<div class="header  item">
				<h4>TTL(s): <span style="user-select: text;">${ttl}</span></h4> 
			</div>
			<div class="item">
				<div class="ui compact teal mini button " id="newttl">TTL</div>
			</div>
			<div class="right menu">
				<div class="item">
					<div class="ui olive button" id="refresh">刷新</div>
				</div>
			</div>
		</div>
	</div>
	<div class="list">
		<div class="ui form">
			<div class="field">
			  <label>Value</label>
			  <textarea id="value">${value}</textarea>
			</div>
			<div class="ui  button" id="update">修改</div>
		  </div>
	</div>
	
	<div class="ui modal mini" id="exptime">
		<div class="header green">
			  更新过期时间
		</div>
		<form class="ui content form">
			  <div class="field">
				  <label>TTL:</label>
				  <div class="ui input ">
					  <input id="ttl" type="text" placeholder="请输入新过期时间">
				  </div>
			  </div>
		</form>
		<div class="actions">
		  <div class="ui button approve green">OK</div>
		  <div class="ui button deny  ">取消</div>
		</div>
	</div>






</body>
<script>
	var key=document.getElementById('key').value;
	var server=document.getElementById('server').value;
	$(document).ready(function(){
		$("#refresh").click(function(){
			location.reload();
		});
		$("#newttl").click(function(){
			$('#exptime').modal('show').modal({	
		 			 	  onApprove:function(){
		 			 		var ttl=$("#ttl").val();
							  var data={};
							  data["key"]=key;
							  data["server"]=server;
							  data["ttl"]=ttl;
		 			 		$.ajax({
		 						  type: 'post',
		 						  url: "/redis/key/ttl",
								  contentType: "application/json;charset=UTF-8",
								  data:JSON.stringify(data),
		 						  success:function(data){
		 						  	 if(data.ok){
		 						  	  	alert('修改成功,请刷新');
		 						  	 }else{
										alert(data.message);
		 						  	 }
		 						  }
		 					});
		 				 }
		 	 });
		});
		$("#update").click(function(){
			var msg = "确认修改吗？"; 
			if (confirm(msg)==true){ 
				var value= $("#value").val();
				var data={};
				data["key"]=key;
				data["server"]=server;
				data["value"]=value;
				$.ajax({
					type: 'post',
					url: "/redis/member/new",
					contentType: "application/json;charset=UTF-8",
					data:JSON.stringify(data),
					success:function(data){
						if(data.ok){
							alert('修改成功');
							location.reload();
						}else{
							alert(data.message);
						}
					}
			});
			}
		   
		});

	});
</script>



</html>