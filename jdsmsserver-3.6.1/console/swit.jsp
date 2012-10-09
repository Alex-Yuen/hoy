<%@ include file="validate.jsp" %>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>主备状态</title>
<link href="css.css" type="text/css" rel="stylesheet">
</head>
<script type="text/javascript">


function submitto(url){
	document.all.settings.action = url;
	document.all.settings.submit();
}
function swi(obj){
	//alert(obj.checked);
	if(!obj.checked)
		obj.value="no";
	else obj.value="no";
	//alert(obj.value);
}
</script>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>主备状态</div>
<div id="center">

<table >
<form action="/system" id="system">
<input type="hidden" name="action" value="saveSystem">
<tr><td colspan="2">系统参数设置</td></tr>
<tr><td>接收短信处理</td><td><input type="checkbox" <%if("yes".equals(request.getAttribute("delete_after_processing")))out.print("checked"); %> name="delete_after_processing" id="delete_after_processing" value="" onclick="swi(this)">接收短信后将其从SIM卡中删除</td></tr>
<tr><td>工作模式</td><td><input type="radio" checked name="send_mode" id="send_mode" value="sync" <%if("sync".equals(request.getAttribute("send_mode")))out.print("checked"); %>>同步模式
<input type="radio" name="send_mode" id="send_mode" value="async" <%if("async".equals(request.getAttribute("send_mode")))out.print("checked"); %>>异步模式</td></tr>
<tr><td>日志级别</td><td>
<input type="radio" name="log_level" id="log_level" value="DEBUG" <%if("DEBUG".equals(request.getAttribute("log_level")))out.print("checked"); %>>DEBUG
<input type="radio"  name="log_level" id="log_level" value="INFO" <%if("INFO".equals(request.getAttribute("log_level")))out.print("checked"); %>>INFO
<input type="radio"  name="log_level" id="log_level" value="WARN" <%if("WARN".equals(request.getAttribute("log_level")))out.print("checked"); %>>WARN
<input type="radio"  name="log_level" id="log_level" value="ERROR" <%if("ERROR".equals(request.getAttribute("log_level")))out.print("checked"); %>>ERROR
<input type="radio"  name="log_level" id="log_level" value="FATAL" <%if("FATAL".equals(request.getAttribute("log_level")))out.print("checked"); %>>FATAL</td></tr>
<tr><td>短信接收间隔</td><td><input type="text" name="inbound_interval" id="inbound_interval" value="<%=request.getAttribute("inbound_interval") %>"></td></tr>
<tr><td>短信发送间隔</td><td><input type="text" name="outbound_interval" id="outbound_interval" value="<%=request.getAttribute("outbound_interval") %>"></td></tr>
<tr><td colspan="2" align="center"><input type="submit" value="保存">   <input type="reset" value="重置"></td></tr>
</form>
<form action="/system" id="httpserver">
<input type="hidden" name="action" value="saveHttpServer">
<tr><td colspan="2">HttpServer短信收发接口设置</td></tr>
<tr><td>端口</td><td><input name="port" id="port" value="<%=request.getAttribute("port") %>" type="text"></td></tr>
<tr><td>发送密码</td><td><input name="password_send" id="password_send" value="<%=request.getAttribute("password_send") %>" type="password"></td></tr>
<tr><td>接收密码</td><td><input name="password_read" id="password_read" value="<%=request.getAttribute("password_read") %>" type="password"></td></tr>

<%
	String sendURL = (String)request.getAttribute("sendURL");
	String readURL = (String)request.getAttribute("readURL");
	
	if(sendURL!=null||readURL!=null){
%>
	<tr><td colspan="2">
	<%
		if(sendURL!=null)
			out.println("短信发送URL：<BR>"+sendURL+"&encoding=U"+"  <BR>");
		if(readURL!=null)
			out.println("短信接收URL：<BR>"+readURL+"  <BR>");
	%>
	
	</td></tr>
<%} %>
<tr><td colspan="2" align="center"><input type="submit" value="保存">   <input type="reset" value="重置"></td></tr>

</form>
</table>
<%if(request.getAttribute("message")!=null){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<%} %>
</div>
</body>
</html>