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
	.list{width: 98%;height: 500px;margin: 0 auto;}
	.valuebox{overflow-y:scroll;}
	.ui.compact.table td {padding: .2em .3em;}
	 table#thead{margin: 0; border-bottom:none;border-bottom-right-radius:0;border-bottom-left-radius:0;}
	 table#thead th{padding: 0.2em 0;text-align: left;}
	 table#tab{border-top:none;border-top-left-radius:0;border-top-right-radius:0;}
	 table#tab th{height: 0; padding: 0;border-bottom:none;}
	 .info{width: 98%;height: 50px;margin: 0 auto;}
	 .header h4 span{color:#21ba45;}
</style>
</head>
<body>
	<input type="text" id="key" value="${key}" hidden="hidden">
	<input type="text" id="server" value="${server}" hidden="hidden">
	<input type="text" id="matchid" value="" hidden="hidden">
	<div class="info" id="info">
		<div class="ui mini secondary menu">
			<div class="header item">
				<h4>Key: <span style="user-select: text;">${key}</span> </h4>
			</div>
			<div class="header  item">
				<h4>Type: <span style="user-select: text;">list</span></h4> 
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
				<div class="item">
					<div class="ui  green button " id="newval">新建</div>
				</div>
			</div>
		</div>
	</div>
	<div class="list">
		<table class="ui compact small single line table red" id="thead">
			<thead>
			  <tr>
				<th class="one wide">序号</th>
				<th class="eight wide">Member</th>
				<th class="one wide">操作</th>
			  </tr>
			</thead>
		 </table>
	   <div class="valuebox" id="value">
			   <table class="ui celled compact small fixed single line table" id="tab">
				<thead>
					<tr >
					  <th class="one wide"></th>
					  <th class="eight wide"></th>
					  <th class="one wide"></th>
					</tr>
				  </thead>
				<tbody>
					<tr><td colspan="3">
						<div class="ui active inline centered loader"></div>
					</td></tr>
				</tbody>
		  </table>
	   </div>
		 <div class="ui right floated pagination menu mini" id="pagebox">
		 		<a class="item disabled" id="count">0</a>
				<a class="icon item" id="sort" data-sort="1">
					<i class="arrow down icon disabled"></i>
					<i class="arrow up icon "></i>
			  	</a>
				<a class="icon item disabled" id="prev">
				  <i class="left chevron icon"></i>
				</a>
				<a class="item" id="page">1</a>
				<a class="icon item disabled" id="next">
				  <i class="right chevron icon"></i>
				</a>
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

	<div class="ui modal mini" id="newbox">
		<div class="header green" id="title">新建Value</div>
		<form class="ui content form">
			  <div class="field">
				  <label>Value:</label>
				  <div class="ui input ">
					  <input id="values" type="text" placeholder="请输入Value">
				  </div>
			  </div>
			  <div class="inline field" id="leftbox">
				<div class="ui toggle checkbox" id="left" data-left="0">
				  <input type="checkbox" tabindex="0" class="hidden">
				  <label>头部插入(默认尾部插入)</label>
				</div>
			  </div>
			  <div class="ui warning message" id="tips">
				<div class="header ">注意</div>
				<p>如果列表变更中，可能无法正常修改为预期值</p>
			</div>
		</form>
		<div class="actions">
		  <div class="ui button approve green">OK</div>
		  <div class="ui button deny  ">取消</div>
		</div>
	  </div>

	  <div class="ui modal mini" id="delbox">
		<div class="header green">删除Value</div>
		<form class="ui content form">
			  <div class="field">
				  <label>Value:<span id="delvalue"></span></label>
			  </div>
			  <div class="field">
				<label>Count:</label>
				<div class="ui input ">
					<input id="count" type="text" placeholder="请输入count">
				</div>
				<div class="ui message">
					<p>count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT </p>
					<p>count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值 </p>
					<p>count = 0 : 移除表中所有与 VALUE 相等的值。</p>
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
		var wh=$(window).height();
		var i=$("#info").height();
		var t=$("#thead").height();
		var p=$("#pagebox").height();
		$("#value").css("height",wh-t-i-p-30);
		init();
	});
	
	function init(){
		list(1);
		$("#prev").click(function(){
			var page=$("#page").text();
			if(Number(page)-1<=0){
				return;
			}
			list(Number(page)-1);
		});
		$("#next").click(function(){
			var page=$("#page").text();
			list(Number(page)+1);
		});
		$("#refresh").click(function(){
			location.reload();
		});
		$("#sort").click(function(){
			var i=$(this).attr("data-sort");
			if(i==0){
				$(this).attr("data-sort",1);
				$(this).find("i").eq("0").addClass("disabled");
				$(this).find("i").eq("1").removeClass("disabled");

			}else{
				$(this).attr("data-sort",0);
				$(this).find("i").eq("0").removeClass("disabled");
				$(this).find("i").eq("1").addClass("disabled");
			}
			var page=$("#page").text();
			list(Number(page));
		});
		$("#tab tbody").on("click","button.del", function(){
				var value=$(this).parent().prev().text();
				$("#delvalue").text(value);
				$('#delbox').modal({	
		 			 	  onApprove:function(){
							var value=$("#values").val();
							var count=$("#count").val();
							delmember(value,count);
		 				 }
		 		 }).modal('show');	
		});
		$("#tab tbody").on("click","button.edit", function(){
				var index=$(this).parent().siblings().eq(0).text();
				var value=$(this).parent().prev().text();
				$("#values").val(value);
				$("#newbox #title").text("更新Member");
				$("#tips").show();
				$("#leftbox").hide();
				$('#newbox').modal({	
		 			 	  onApprove:function(){
							var value=$("#values").val();
							var data={};
							data["key"]=key;
							data["server"]=server;
							data["member"]=value;
							data["index"]=index;
		 			 		$.ajax({
		 						  type: 'post',
		 						  url: "/redis/member/set",
								  contentType: "application/json;charset=UTF-8",
								  data:JSON.stringify(data),
		 						  success:function(data){
		 						  	 if(data.ok){
		 						  	  	alert('修改成功');
										var page=$("#page").text();
										list(Number(page));
		 						  	 }else{
										alert(data.message);
		 						  	 }
		 						  }
		 					});
		 				 }
		 	 }).modal('show');
		});
		$("#newval").click(function(){
			$("#newbox #title").text("添加Member");
			$("#tips").hide();
			$("#leftbox").show();
			$("#values").val('');
			$('#newbox').modal({	
		 			 	  onApprove:function(){
							var value=$("#values").val();
							var left=$("#left").attr("data-left");
						    var data={};
						    data["key"]=key;
						    data["server"]=server;
						    data["member"]=value;
							data["left"]=left==='0'?true:false;
		 			 		$.ajax({
		 						  type: 'post',
		 						  url: "/redis/member/new",
								  contentType: "application/json;charset=UTF-8",
								  data:JSON.stringify(data),
		 						  success:function(data){
		 						  	 if(data.ok){
		 						  	  	alert('添加成功');
									    list(1);
		 						  	 }else{
										alert(data.message);
		 						  	 }
		 						  }
		 					});
		 				 }
		 	 }).modal('show');
			$("#newbox").find('.ui.checkbox').checkbox({
				onChecked: function() {
					$("#left").attr("data-left",1);
				},
				onUnchecked: function() {
					$("#left").attr("data-left",0);
				}
			});
		});
		$("#newttl").click(function(){
			$('#exptime').modal({
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
		 	 }).modal('show');
		});
	}
	
	
	function list(page){
				var sort=$("#sort").attr("data-sort");
				var data={};
				data["key"]=key;
				data["server"]=server;
				data["page"]=1;
				data["sort"]=sort;
 				$.ajax({
					  type: 'post',
					  url: "/redis/member/list",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
					  		 	showdata(data.data);
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
	 }
		
		function showdata(data){
			$("#count").text(data.count);
			if(data.list.length==0){
				$("#tab  tbody").empty().append('<tr><td colspan="3" style="text-align:center">无</td></tr>');
				$("#next").addClass("disabled");
				return;
			}else if(data.list.length==data.size){
				$("#next").removeClass("disabled");
			}
			$("#page").text(data.page);
			var start=(data.last*-1)+(data.page-1)*data.size;
			var sort=$("#sort").attr("data-sort");
			if(sort=='1'){
				 start=(data.page-1)*data.size+1;
			}
			$("#tab  tbody").empty();
			$.each(data.list,function(i,v){
					var content="<tr>";
					content+="<td>"+(start+i)+"</td>";
					content+='<td >'+v.member+'</td>';
			    	content+='<td><button class="ui icon mini button edit"><i class="edit icon"></i></button><button class="ui icon mini button red del"><i class="delete icon"></i></button></td>';
			    	content+='</tr>';
				    $("#tab  tbody").append(content);
			});
			if(data.page==1){
				$("#prev").addClass("disabled");
			}else{
				$("#prev").removeClass("disabled");
			}
			
		}
		
		function delmember(member,count){
				var data={};
				data["key"]=key;
				data["server"]=server;
				data["member"]=member;
				data["count"]=count;
 				$.ajax({
					  type: 'post',
					  url: "/redis/member/del",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
								alert("删除成功");
								var page=$("#page").text();
								list(Number(page));
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }

</script>



</html>