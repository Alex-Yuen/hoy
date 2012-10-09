<%@ include file="validate.jsp"%>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>首页</title>
<link href="css.css" type="text/css" rel="stylesheet">
</head>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>首页</div>
<div id="center">
<div id="welcome" class="content" style="display:block;">
  <div align="center">
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p><strong>您好，<%
String username1 = (String)request.getSession().getAttribute("console.username");
out.print(username1);
%>！欢迎您使用短信服务平台。</strong></p>
    
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    <table border="0" style="border:none"><tr>
    <td style="border:none"><a href="/device-config?action=refresh" target="main"><img border="0" src="img/id_sp1.gif"></a></td>
    <td style="border:none"> <img src="img/id_tu1.gif"></td>
    <td style="border:none"><a href="/datasource-config?action=Init" target="main"><img border="0" src="img/id_sp2.gif"></a></td>
    <td style="border:none"> <img src="img/id_tu1.gif"></td>
    <td style="border:none"><a href="/smsAction?action=Init" target="main"><img border="0" src="img/id_sp3.gif"></a></td></tr></table>
   
    </div>
</div>
</div>
</body>
</html>