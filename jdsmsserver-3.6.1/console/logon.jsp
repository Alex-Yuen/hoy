<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>短信服务平台(WEB版)</title>
<link href="css.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
//alert('<%=request.getSession().getAttribute("randomCode")%>');
function check(){
	var usr = document.getElementById('username');
	if(usr.value==''){
		alert('请输入用户名');
		usr.focus();
		return false;
	}
	var pwd = document.getElementById('password');
	if(pwd.value==''){
		alert('请输入密码');
		pwd.focus();
		return false;
	}
	var va = document.getElementById('randomCode');
	if(va.value==''){
		alert('请输入验证码');
		va.focus();
		return false;
	}
	return true;
}
</script>
</head>
<body style="border:none;background:#dfdfdf">
<div class="denglu_form" >
<form action="/logon" method="post" onsubmit="return check();">
<table cellpadding="0" width="75%" cellspacing="0" border="0">
	<tr>
    	<td width="60%" align="center">
        	<table cellpadding="0" width="85%" cellspacing="0" border="0">
            	<tr>
                	<td rowspan="2"><img src="img/logo.gif"></td>
                    <td style="color:#126dc6;font-size:20px;font-family:黑体">短信服务平台web版V3.6</td>
                </tr>
            	<tr>
                	<td style="color:#9c9c9c;font-size:14px">Copyright &copy; 2012&nbsp;短信服务平台</td>
                </tr>
            </table>
        </td>
<td width="40%">
<table cellpadding="0" cellspacing="0" border="0" width="300px">
<tr><td width="25%" align="center">用户名：</td><td width="75%" colspan="2"><Input id="username" name="username" value="" size="20" maxlength="20" type="text" class="denglu_k"></td></tr>
<tr><td align="center" width="25%">密　码：</td>
<td width="75%" colspan="2"><Input id="password" name="password" value="" size="21" maxlength="20" type="password" class="denglu_k"></td></tr>
<tr><td align="center" width="25%">验证码：</td>
<td width="23%"><Input id="randomCode" name="randomCode" value="" size="5" maxlength="4" type="text" class="denglu_code"></td>
<td width="52%"><img alt="" src="randomCode.jsp"></td></tr>
<tr><td style="padding-top:10px;padding-left:40px;" colspan="3"><input value="提交" type="submit" style="background:url(img/login_dl_bt1.jpg);color:#fff" class="denglu_dl_bt"><input value="重置" type="reset" style="background:url(img/login_dl_bt2.jpg);color:#fff" class="denglu_dl_bt"></td></tr>
</table></td>
    </tr>
</table>
</form>
<%
if(request.getAttribute("global_info")!=null){	
%>

<script type="text/javascript">
alert('<%=request.getAttribute("global_info")%>');
</script>

<%} %>
</div>
</body>
</html>