<%@ page contentType="text/html; charset=utf-8"%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Buxland-注册</title>
		<link rel="shortcut icon" href="http://www.buxland.net/favicon.ico">
		<style type="text/css">
		<!--
		td{
			font-size:10pt;
			font-family:宋体;
		}
		tr{
			height:16px;
			line-height:16px;
		}
		-->
		</style>
	</head>
	<body topmargin="3" style="margin:0;padding:0;">
		<jsp:include page="header.jsp" flush="true"/>
		
		<br/>
		<span style="font-size: 10pt; font-family:宋体;">
		<%
			if("1".equals((String)session.getAttribute("REGISTER_RESULT"))){
				out.println("注册成功");
			}else{
				out.println("注册失败:<br/><br/>"+(String)session.getAttribute("REGISTER_RESULT_MSG"));
			}
		%>
		</span>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<jsp:include page="tailer.jsp" flush="true"/>
		
	<body>
</html>