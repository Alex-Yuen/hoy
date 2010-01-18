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
		-->
		</style>
	</head>
	<body topmargin="3" style="margin:0;padding:0;text-align:center;">
		<jsp:include page="header.jsp" flush="true"/>
		<br/><br/>
		<div style="margin:0 auto;background: #EAF1FB;width: 35%;height:200px;border-width: 1px; border-style: solid;border-color: #DDDDDD;">
		  <form action="/register" method="post">
			<table border="0" width="100%" align="center" style="font-size:11pt;font-family:宋体;">
				<tr>
					<td colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">&nbsp;<u><b>注册</b></u></td>
				</tr>
				<tr>
					<td width="30%" align="right">用户名:</td>
					<td style="width:140px;"><input style="width:140px;" type="text" name="username" id="username"/></td>
					<td>&nbsp;*</td>
				</tr>
				<tr>
					<td align="right">密码:</td>
					<td><input style="width:140px;" type="password" name="password" id="password"/></td>
					<td>&nbsp;*</td>
				</tr>
				<tr>
					<td align="right">确认密码:</td>
					<td><input style="width:140px;" type="password" name="repassword" id="repassword"/></td>
					<td>&nbsp;*</td>
				</tr>
				<tr>
					<td align="right">Email:</td>
					<td><input style="width:140px;" type="text" name="email" id="email"/></td>
					<td>&nbsp;*</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td align="right"><input style="width:60px;" type="submit" name="register" id="register" value=" 注 册 "/></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">&nbsp;</td>
				</tr>
			</table>
		  </form>
		</div>
		<br/><br/>
		<jsp:include page="tailer.jsp" flush="true"/>
		
	<body>
</html>