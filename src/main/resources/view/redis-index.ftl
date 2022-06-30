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
	    body{height:100%;width:100%;overflow:hidden;}
		.databox{margin:5px auto;width:99%;}
		.ui.menu{border-radius: 0;}
		.ui.icon.button, .ui.icon.buttons .button { padding: .5em;}
		.ui.compact.table td {padding: .2em .3em;}
		.searchbox,.tabledata{margin-left:10px;}
		.keybox{max-height:650px;overflow-y:scroll;}
		table#keyhead{margin: 0; border-bottom:none;border-bottom-right-radius:0;border-bottom-left-radius:0;}
		table#keyhead th{padding: 0.2em 0;text-align: left;}
		table#keytable{border-top:none;border-top-left-radius:0;border-top-right-radius:0;}
		table#keytable th{height: 0; padding: 0;border-bottom:none;}
		.tablebox{width: 100%;height: 100%;}
		#keybanner a.item{position: relative;}
		.ui.menu .item >i.bannericon{position: absolute; right: 0; top: 0;margin: 0; background: rgb(250, 43, 43); z-index: 9999; color: #fff;}
				div.keyinfo{position: fixed;top:50%;left:50%;display: none;
			background-color: #dff0ff;color: #2185d0;box-shadow: 0 0 0 1px #2185d0 inset, 0 0 0 0 transparent;
			border-radius: .28571429rem;
		}
		div.blocks{position: absolute;width: 6px;height: 6px;z-index: 999;background-color: #dff0ff; border-bottom: 1px solid #2185d0;border-left: 1px solid #2185d0;   transform: rotate(45deg);
			left: -3px;bottom: 6px;
		}
	</style>
</head>
<body>


<div class="ui mini inverted menu">
	<div class="item">
		<h4>Redis控制台</h4> 
	</div>
	<div class="active orange item" id="server" >无连接</div>
	<div class="menu">
		<div class="ui dropdown item" id="dropdown">连接实例<i class="dropdown icon"></i> 
		  <div class="menu" id="serverlist">
				<a class="item " data-server="0" id="addserver" style="background:#21ba45 !important;color:#fff !important;">新增实例</a>
		  </div>
		</div>
	</div>
	<div class="item" id="connect" style="width: 300px;"></div>
	<div class="right item">
		<i class="wrench icon"></i>
	</div>
</div>


<div class="databox ui  column doubling stackable  grid">
	<div class="four wide column">
		<form class="ui form segment green searchbox">
			<div class="field">
				<div class="ui action input">
					<input type="text" placeholder="搜索key" id="key">
					<button class="ui button" id="find" type='button'>搜索</button>
				</div>
			</div>
			<div class="field" style="text-align: right;">
				<div class="ui checkbox">
					<input type="checkbox" tabindex="0" class="hidden" id="accurate" value="0">
					<label>精确匹配</label>
				</div>
			</div>
		</form>
		<div class="tabledata">
				<table class="ui compact small single line table" id="keyhead">
					<thead>
					<tr>
						<th class="one wide">序号</th>
						<th class="one wide">类型</th>
						<th class="six wide">键名</th>
						<th class="two wide">操作</th>
					</tr>
					</thead>
				</table>
				<div class="keybox" id="keybox">
						<table class="ui celled compact small fixed single line table" id="keytable">
							<thead>
								<tr >
								<th class="one wide"></th>
								<th class="one wide"></th>
								<th class="six wide"></th>
								<th class="two wide"></th>
								</tr>
							</thead>
							<tbody>
								<tr><td style="text-align: center;" colspan="5"><div class="ui active centered inline ">请点击搜索</div></td></tr>	
							</tbody>
					</table>
				</div>
				<div class="ui mini menu" style="margin: 0;">
					<div class="item header" id="total">总计：0</div>
					<div class="right menu" id="pagebox">
						<a class="item disabled" id="prev">
							<i class="left chevron icon"></i>
						</a>
						<a class="item " id="keypage">1</a>
						<a class="item disabled" id="next">
							<i class="right chevron icon"></i>
						</a>
					</div>
				</div>
		</div>
	</div>
	<div class="ui twelve wide column  grid">
		<div class="ten wide column">
		<div class="ui segment">
			<div class="ui top attached tabular menu" id="keybanner">
				<a class="item active">
					新建KEY
				</a>
			</div>
			<div class="ui bottom attached segment" id="keyvaluebox">
				<div  class="tablebox" >
					<form class="content ui form " id="taskform">
						<div class="field">
							<label>Key:</label>
							<div class="ui input ">
								<input id="keyname" name="keyname" type="text" placeholder="KeyName">
							</div>
						</div>
						<div class="field">
							<label>类型:</label>
							<div class="ui input ">
								<select id="keytype" name="keytype" class="ui  dropdown">
									<option value="string">String</option>
									<option value="set">Set</option>
									<option value="zset">SortSet</option>
									<option value="hash">Hash</option>
									<option value="list">List</option>
								</select>
							</div>
						</div>
						<div class="field">
							<label>Member:</label>
							<div class="ui input ">
								<input id="member"name="member" type="text" placeholder="Member">
							</div>
						</div>
						<div class="field">
							<label>Value:</label>
							<div class="ui input ">
								<input id="keyvalue" name="keyvalue" type="text" placeholder="keyValue">
							</div>
						</div>
						<div class="field">
							<label>TTL:</label>
							<div class="ui input ">
								<input id="ttl"name="ttl" type="text" placeholder="过期时间(s)">
							</div>
						</div>
						<div class="ui error message">
							<p id="addtips"></p>
						</div>
					</form>
					<div class="ui " style="text-align: left;">
						<div class="ui button  blue"  id="addkey" >添加</div>
					</div>
				</div>
			</div>
		</div>
		</div>

		<div class="five wide column">
			<div class="ui segment">
				<div class="ui bottom attached" >
					<h4 class="ui horizontal divider header">
						服务
					</h4>
					<table class="ui definition table">
						<tbody>
						<tr>
							<td class="five wide column">版本</td>
							<td id="version">-</td>
						</tr>
						<tr>
							<td>模式</td>
							<td id="mode">-</td>
						</tr>
						<tr>
							<td>OS</td>
							<td id="os">-</td>
						</tr>
						<tr>
							<td>上线时长</td>
							<td id="uptime">-</td>
						</tr>
						<tr>
							<td>系统内存</td>
							<td id="systemMemory">-</td>
						</tr>
						</tbody>
					</table>
					<h4 class="ui horizontal divider header">
						客户端
					</h4>
					<table class="ui definition table">
						<tbody>
						<tr>
							<td class="five wide column">最大支持数量</td>
							<td id="maxClient">-</td>
						</tr>
						<tr>
							<td>当前连接数量</td>
							<td id="client">-</td>
						</tr>
						</tbody>
					</table>
					<h4 class="ui horizontal divider header">
						内存
					</h4>
					<table class="ui definition table">
						<tbody>
						<tr>
							<td class="five wide column">当前用量</td>
							<td id="userdMemory">-</td>
						</tr>
						<tr>
							<td>系统分配</td>
							<td id="userdMemoryRss">-</td>
						</tr>
						<tr>
							<td>峰值消耗</td>
							<td id="userdMemoryPeak">-</td>
						</tr>
						<tr>
							<td>最大内存</td>
							<td id="maxMemory">-</td>
						</tr>
						</tbody>
					</table>
					<h4 class="ui horizontal divider header">
						统计
					</h4>
					<table class="ui definition table">
						<tbody>
						<tr>
							<td class="five wide column">命中率</td>
							<td id="hitRate">-</td>
						</tr>
						<tr>
							<td>命中次数</td>
							<td id="keyspaceHits">-</td>
						</tr>
						<tr>
							<td>miss次数</td>
							<td id="keyspaceMisses">-</td>
						</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>


  </div>




</div>	


<div class="keyinfo ui list" id="keyinfo" style="padding:0.5em 1em">
	<div class="item">
		<div class="header">KEY</div><span>key</span>
	</div>
	<div class="item">
		<div class="header">TTL</div><span>-1</span>
	</div>
	<div class="blocks"></div>
</div>



<div class="ui modal mini" id="addserverbox">
	<div class="header green">
		新增实例
	</div>
	<div class="content">
		<div class="ui input " style="width:260px;padding: 10px 0">
			<input id="name" type="text" placeholder="连接名(小写字母)">
		</div>
		<div class="ui input " style="width:260px;padding: 10px 0">
			<input id="host" type="text" placeholder="host">
		</div>
		<div class="ui input " style="width:260px;padding: 10px 0">
			<input id="port" type="text" placeholder="端口">
		</div>
		<div class="ui input " style="width:260px;padding: 10px 0">
			<input id="user" type="text" placeholder="用户名">
		</div>
		<div class="ui input " style="width:260px;padding: 10px 0">
			<input id="password" type="text" placeholder="密码(可选)">
		</div>
	</div>
	<div class="actions">
		<div class="ui button green" onclick="addserver()">新增</div>
		<div class="ui button approve" >取消</div>
	</div>
</div>
    
</body>
	<script>
		$(document).ready(function(){
			init();
			keybtn();
			loadserver();
		});
		var cursor = new Map();
		function init(){
			$('.ui.checkbox').checkbox({
				onChecked: function() {
					$("#accurate").val(1)
				},
				onUnchecked: function() {
					$("#accurate").val(0)
				}
			});
			$('#dropdown').dropdown({
				onChange: function(value, text, $choice) {
					if($choice.attr("data-server")!=='0'){
						$("#server").text($choice.attr("data-server").toUpperCase());
						$("#connect").text($choice.attr("data-host"));
						localStorage.setItem("server",$choice.attr("data-server"));
						loadinfo();
					}
				},
			});
			var wh=$(window).height();
			var t=$("#keyhead").offset().top+$("#keyhead").height();
			$("#keybox").css("max-height",wh-t-40);
			var t=$("#keyvaluebox").offset().top+$("#keybanner").height();
			$("#keyvaluebox").css("height",wh-t);
		}

		function loadserver(){
			$.ajax({
				type: 'get',
				url: "/redis/server/list",
				dataType:'json',
				success:function(data){
					if(data.ok){
						$("#serverlist").empty();
						var server =localStorage.getItem("server");
						$.each(data.data,function(i,n){
							$("#serverlist").append('<a class="item" data-server="'+n.serverName+'" data-host="'+n.host+'" >'+n.serverName.toUpperCase()+'</a>');
							if(server!=null&&server!=''&&server==n.serverName){
								$("#server").text(server.toUpperCase());
								loadinfo();
							}
						});
						$("#serverlist").append('<a class="item " data-server="0" id="addserver" style="background:#21ba45 !important;color:#fff !important;">新增实例</a>');
					}else{
						alert(data.message);
					}
				}
			});
		}


		function loadinfo(){
			var server=$("#server").text();
			var data={};
			data["server"]=server;
			$.ajax({
				type: 'post',
				url: "/redis/info",
				dataType:'json',
				contentType: "application/json;charset=UTF-8",
				data:JSON.stringify(data),
				success:function(data){
					if(data.ok){
						$("#version").text(data.data.version);
						$("#mode").text(data.data.mode);
						$("#os").text(data.data.os);
						$("#uptime").text(data.data.uptime);
						$("#client").text(data.data.client);
						$("#maxClient").text(data.data.maxClient);
						$("#userdMemory").text(data.data.userdMemory);
						$("#userdMemoryRss").text(data.data.userdMemoryRss);
						$("#userdMemoryPeak").text(data.data.userdMemoryPeak);
						$("#maxMemory").text(data.data.maxMemory);
						$("#systemMemory").text(data.data.systemMemory);
						$("#keyspaceHits").text(data.data.keyspaceHits);
						$("#keyspaceMisses").text(data.data.keyspaceMisses);
						$("#hitRate").text(data.data.hitRate);
					}
				}
			});
		}
		
		function keybtn(){
			$(".menu>").on("click","#addserver",function (){
				$('#addserverbox').modal('show');
			});
			$("#find").click(function(){
				findkey($("#key").val());
			});
			$("#prev").click(function(){
				var page=$("#keypage").text();
				if(Number(page)-1<=0){
					return;
				}
				pagekey(Number(page)-1);
			});
			$("#next").click(function(){
				var page=$("#keypage").text();
				if(cursor.get(Number(page)+1)=="-1"){
					return;
				}
				pagekey(Number(page)+1);
			});
			$("#keytable tbody").on("click","button.del", function(){
				var key=$(this).parent().prev().text();
				var msg = "确认删除删除吗？\n\n"+key; 
				if (confirm(msg)==true){ 
					delkey(key);
				}
			});
			$("#keytable tbody").on("click","button.get",function(){
				var key=$(this).parent().prev().text();
				getkey(key);
			});	
			$("#keybanner").on("click","a",function(){
					$(this).addClass("active").siblings().removeClass("active");
					$(this).siblings().find("i").remove();
					var i=$(this).index();
					if(i!=0){
						$(this).append('<i class="delete bannericon icon "></i>');
					}
					$(".tablebox").hide().eq(i).show();
			 });
			 $("#addkey").click(function(){
				addkey();
			});
			$("#keybanner").on("click","i.bannericon",function(){
				var i=$(this).parent().index()-1;
				var key=$(this).parent().attr("data-key");
				var div=document.getElementById(key).remove();
				$(this).parent().remove();
				$(".tablebox").eq(i).show();
				var obj=$("#keybanner").find("a").eq(i);
				obj.addClass("active");
				if(i!=0){
					obj.append('<i class="delete bannericon icon "></i>').siblings().find("i").remove();
				}
			 });
			$("#keytable tbody").on("mouseenter","td.keyname", function(){
					var key=$(this);
					$("#keyinfo div span").eq(0).text(key.attr("data-key"));
					$("#keyinfo div span").eq(1).text(key.attr("data-ttl"));
					$("#keyinfo").show();
			});
			$("#keytable tbody").on("mouseleave","td.keyname", function(){
					$("#keyinfo").hide();
			});
			$("#keytable tbody").on("mousemove","td.keyname", function(e){
				$("#keyinfo").css({"left":e.pageX+30,"top":e.pageY-100});
			});
		}
		function addserver(){
			var name=$("#name").val();
			var host=$("#host").val();
			var port=$("#port").val();
			var user=$("#user").val();
			var password=$("#password").val();
			$.ajax({
				type: 'post',
				url: "/redis/add/server",
				dataType:'json',
				data:{"name":name,"host":host,"port":port,"user":user,"password":password},
				success:function(data){
					if(data.ok){
						alert("添加成功");
					}else{
						alert(data.message);
					}
					loadserver();
				}
			});
		}
		
		
		function addkey(){
				var keyname=$("#keyname").val();
				var keytype=$("#keytype").val();
				var keyvalue=$("#keyvalue").val();
				var member=$("#member").val();
				var ttl=$("#ttl").val();
				var server=$("#server").text();
				var data={};
				data["key"]=keyname;
				data["type"]=keytype;
				data["value"]=keyvalue;
				data["member"]=member;
				data["ttl"]=ttl;
				data["server"]=server;
 				$.ajax({
					  type: 'post',
					  url: "/redis/add/key",
					  dataType:'json',
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
								alert("添加成功");
								$("#keyname").val("");
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }
		
		
		function findkey(key){
				var data={};
				var accurate=false;
				if($("#accurate").val()=='0'){
					accurate=false;
				}
				data["accurate"]=false;
				data["server"]=$.trim($("#server").text().toLowerCase());
			    data["page"]=1;
				data["key"]=$.trim($("#key").val());
				$("#keytable  tbody").html('<tr><td style="text-align: center;" colspan="5"><div class="ui active centered inline loader"></div></td></tr>');	
 				$.ajax({
					  type: 'post',
					  url: "/redis/key/search",
					  dataType: "json",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
								$("#total").text("总计："+data.data.count+"个key");
							   	cursor.clear();
							    cursor.set(2,data.data.cursor);
					  		 	showdata(data.data);
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }
		
		function pagekey(page){
			if(page===1){
				findkey($("#key").val());
				return;
			}
			$("#keytable  tbody").html('<tr><td style="text-align: center;" colspan="5"><div class="ui active centered inline loader"></div></td></tr>');
			var data={};
			var accurate=false;
			if($("#accurate").val()=='0'){
				accurate=false;
			}
			data["accurate"]=false;
			data["server"]=$.trim($("#server").text().toLowerCase());
			data["page"]=page;
			data["key"]=$.trim($("#key").val());
			data["cursor"]=cursor.get(page);
			$.ajax({
				type: 'post',
				url: "/redis/key/search",
				dataType: "json",
				contentType: "application/json;charset=UTF-8",
				data:JSON.stringify(data),
				success:function(data){
					if(data.ok){
						cursor.set(page+1,data.data.cursor);
						//$("#total").text("总计："+data.data.count+"个key");
						showdata(data.data);
					}else{
						alert(data.message);
					}
				}
			});
		 }
		
		
		function showdata(data){
			$("#keytable  tbody").empty();
			$("#keypage").text(data.page);
			if(data.list.length==0){
				$("#keytable  tbody").append('<tr><td colspan="4" style="text-align:center">无</td></tr>');
				$("#next").addClass("disabled");
				return;
			}else if(data.list.length==data.size){
				$("#next").removeClass("disabled");
			}else if(data.list.length<data.size){
				$("#next").addClass("disabled");
			}
			var start=(data.page-1)*data.size+1;
			$.each(data.list,function(i,info){
					var content="<tr class='data'>";
					content+="<td>"+(i+start)+"</td>";
					content+="<td>"+info.type+"</td>";
					content+='<td class="keyname" data-key="'+info.key+'" data-ttl="'+info.ttl+'">'+info.key+'</td>';
			    	content+='<td><button class="ui icon mini button get"><i class="search icon"></i></button>';
			    	content+='<button class="ui icon mini button red del"><i class="delete icon"></i></button></td>';
			    	content+='</tr>';
				    $("#keytable  tbody").append(content);
			});
			if(data.page===1){
				$("#prev").addClass("disabled");
			}else{
				$("#prev").removeClass("disabled");
			}
		}
		
		function delkey(key){
			    var server=$("#server").text();
				var data={};
				data["key"]=key;
				data["server"]=server;
 				$.ajax({
					  type: 'post',
					  url: "/redis/del/key",
					  contentType: "application/json;charset=UTF-8",
					  data:JSON.stringify(data),
					  success:function(data){
					  	   if(data.ok){
					  		    alert("删除成功");
								//pagekey(Number($("#keypage").text()));
					  		}else{
					  			alert(data.message);
					  		}
					  }
				});
		 }
		 
		 
		function getkey(key){
				var div=document.getElementById(key);
				if(div != "" && div != null & div != undefined){
					alert('已经存在');
					return;
				}
				$("#keybanner a").removeClass("active").find("i").remove();
				$(".tablebox").hide();
				var showkey=key;
				if(key.length>10){
					showkey=key.substring(0,8)+"..";
				}
				$("#keybanner").append('<a class="item active compact single line" data-key="'+key+'">'+showkey+'<i class="delete bannericon icon "></i></a>');
				var server=$("#server").text();
				var url="/redis/key/index?key="+key+"&server="+server;
				var content='<div id="'+key+'" class="tablebox">';
					content+='<iframe src="'+url+'" width="100%" height="100%" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no"  ></iframe></div>'
				$("#keyvaluebox").append(content);
		}
			
	</script>




</html>