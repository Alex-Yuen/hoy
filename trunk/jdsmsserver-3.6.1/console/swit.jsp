<%@ include file="validate.jsp" %>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
    <%    	
    	byte[] st = (byte[])(request.getAttribute("st"));
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>主备状态</title>
<link href="css.css" type="text/css" rel="stylesheet">
</head>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>主备状态</div>
<div id="center">
<br/>
<br/>
<br/>
<span style="color:#00FF00">开机</span>&nbsp;<span style="color:#FF0000">关机</span>&nbsp;<span style="color:#0000FF">暂停</span>&nbsp;
<br/>
<br/>
主机状态&nbsp;&nbsp;<span style="color:<%if(st[0]==2){out.println("#00FF00");}else if(st[0]==0){out.println("#FF0000");}else{out.println("#0000FF");}%>">■</span>
<br/><br/>
备机状态&nbsp;&nbsp;<span style="color:<%if(st[1]==2){out.println("#00FF00");}else if(st[1]==0){out.println("#FF0000");}else{out.println("#0000FF");}%>">■</span>
<%if(request.getAttribute("message")!=null){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<%} %>
</div>
</body>
</html>