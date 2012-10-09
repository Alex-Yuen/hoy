<%@ include file="validate.jsp" %>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
    <%    	
    	boolean[] st = (boolean[])(request.getAttribute("st"));
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
主机状态&nbsp;&nbsp;<span style="color:<%if(st[0]){out.println("#00FF00");}else{out.println("#0000FF");}%>">■</span>
<br/><br/>
备机状态&nbsp;&nbsp;<span style="color:<%if(st[1]){out.println("#00FF00");}else{out.println("#0000FF");}%>">■</span>
<%if(request.getAttribute("message")!=null){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<%} %>
</div>
</body>
</html>