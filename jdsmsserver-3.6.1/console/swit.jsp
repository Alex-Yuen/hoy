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
<title>����״̬</title>
<link href="css.css" type="text/css" rel="stylesheet">
</head>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>����״̬</div>
<div id="center">
<br/>
<br/>
<br/>
<span style="color:#00FF00">����</span>&nbsp;<span style="color:#FF0000">�ػ�</span>&nbsp;<span style="color:#0000FF">��ͣ</span>&nbsp;
<br/>
<br/>
����״̬&nbsp;&nbsp;<span style="color:<%if(st[0]==2){out.println("#00FF00");}else if(st[0]==0){out.println("#FF0000");}else{out.println("#0000FF");}%>">��</span>
<br/><br/>
����״̬&nbsp;&nbsp;<span style="color:<%if(st[1]==2){out.println("#00FF00");}else if(st[1]==0){out.println("#FF0000");}else{out.println("#0000FF");}%>">��</span>
<%if(request.getAttribute("message")!=null){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<%} %>
</div>
</body>
</html>