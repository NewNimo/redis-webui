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
				<h4>Type: <span style="user-select: text;">set</span></h4> 
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
				<div class="ui item checkbox">
					<input type="checkbox" tabindex="0" class="hidden" id="accurate" value="0">
					<label>精确查找</label>
				</div>
				<div class="item">
					<div class="ui icon input">
						<input type="text" placeholder="搜索..." id="match">
						<i class="search link icon" id="search"></i>
					</div>
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
				<a class="icon item disabled" id="prev">
				  <i class="left chevron icon"></i>
				</a>
				<a class="item" id="page">1</a>
				<a class="icon item" id="next">
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
		<div class="header green">
			  新建Member
		</div>
		<form class="ui content form">
			  <div class="field">
				  <label>Member:</label>
				  <div class="ui input ">
					  <input id="member" type="text" placeholder="请输入Member">
				  </div>
			  </div>
		</form>
		<div class="actions">
		  <div class="ui button approve green">OK</div>
		  <div class="ui button deny ">取消</div>
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
	var setCursor = new Map();
	function init(){
		$('.ui.checkbox').checkbox({
				onChecked: function() {
					$("#accurate").val(1)
				},
				onUnchecked: function() {
					$("#accurate").val(0)
				}
		});
		getvalue();
		$("#search").click(function(){
			getvalue($("#match").val());
		});
		$("#prev").click(function(){
			var page=$("#page").text();
			if(Number(page)-1<=0){
				return;
			}
			pagevalue(Number(page)-1);
		});
		$("#next").click(function(){
			var page=$("#page").text();
			if(setCursor.get(Number(page)+1)=="-1"){
				return;
			}
			pagevalue(Number(page)+1);
		});
		$("#refresh").click(function(){
			location.reload();
		});
		$("#tab tbody").on("click","button.del", function(){
				var member=$(this).parent().prev().text();
				var msg = "确认删除吗？\n\n"+member; 
				if (confirm(msg)==true){ 
					delmember(member);
				}
		});
		$("#newval").click(function(){
			$('#newbox').modal('show').modal({	
		 			 	  onApprove:function(){
		 			 		var member=$("#member").val();
		 			 		$.ajax({
		 						  type: 'post',
		 						  url: "/redis/member/new",
		 						  data:{"client":client,"key":key,"member":member},
		 						  dataType: "json",
		 						  success:function(data){
		 						  	 if(data.code==1){
		 						  	  	alert('添加成功');
		 						  	 }else{
										alert(data.msg);
		 						  	 }
		 						  }
		 					});
		 		  }
		 	 });
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

	}
	
	
	function getvalue(match){
				var accurate=$("#accurate").val();
				var data={};
				data["key"]=key;
				data["server"]=server;
				data["page"]=1;
				data["accurate"]=accurate==='1'?true:false;
				data["match"]=match;
 				$.ajax({
					  type: 'post',
					  url: "/redis/member/search",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
							    setCursor.clear();
							    setCursor.set(2,data.data.cursor);
					  		 	showdata(data.data);
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }
		
		function pagevalue(page){
			if(page===1){
				getvalue($("#match").val());
				return;
			}
			var accurate=$("#accurate").val();
			var data={};
			data["key"]=key;
			data["server"]=server;
			data["page"]=1;
			data["accurate"]=accurate==='1'?true:false;
			data["cursor"]=setCursor.get(page);
			data["match"]=$("#match").val();
			$.ajax({
				type: 'post',
				url: "/redis/member/search",
				contentType: "application/json;charset=UTF-8",
				data:JSON.stringify(data),
				success:function(data){
					if(data.ok){
						setCursor.set(page+1,data.data.cursor);
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
				$("#tab  tbody").empty().append('<tr><td colspan="3" style="align:center">无</td></tr>');
				$("#next").addClass("disabled");
				return;
			}else if(data.list.length==data.size){
				$("#next").removeClass("disabled");
			}
			$("#page").text(data.page);
			var start=(data.page-1)*data.size+1;
			$("#tab  tbody").empty();
			$.each(data.list,function(i,kv){
					var content="<tr>";
					content+="<td>"+(i+start)+"</td>";
					content+='<td >'+kv.member+'</td>';
			    	content+='<td><button class="ui icon mini button red del"><i class="delete icon"></i></button></td>';
			    	content+='</tr>';
				    $("#tab  tbody").append(content);
			});
			if(data.page==1){
				$("#prev").addClass("disabled");
			}else{
				$("#prev").removeClass("disabled");
			}
			
		}
		
		function delmember(member){
			var data={};
			data["key"]=key;
			data["server"]=server;
			data["member"]=member;
 				$.ajax({
					  type: 'post',
					  url: "/redis/member/del",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
					  		   	alert("删除成功");
								var page=$("#page").text();
								pagevalue(Number(page));
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }


	
</script>



</html>