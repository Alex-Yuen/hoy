<%@ include file="validate.jsp" %>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>Insert title here</title>
<link href="css.css" type="text/css" rel="stylesheet">
</head>
<body>
<div>

<div id="leftMenu">
<div class="logo" style="background:url(img/logo6.jpg) no-repeat;width:194px;height:70px">
	<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
    	<tr>
        	<td rowspan="2" width="37%"><img src="img/logo.png" width="70"></td>
            <td style="color:#2d77b6;font-family:΢���ź�;font-weight:bold;font-size:16px" valign="bottom">��Ѷ����м��</td>
        </tr>
        <tr>
            <td valign="top">&nbsp;&nbsp;&nbsp;&nbsp;WEB��V3.6</td>
        </tr>
    </table>
</div>
<div class="sx" style="height:35px;width:155px;padding-left:35px;background:url(img/title1_bg.jpg);line-height:35px;font-weight:bold;margin-top:10px">����˵�</div>
<ul>
<li><a href="welcome.jsp" target="main"><img src="img/sy.png">��ҳ</a></li>
<li><a href="/device-config?action=refresh" target="main"><img src="img/sbpz.png">�豸����</a></li>
<li><a href="/datasource-config?action=Init" target="main"><img src="img/sjypz.png">����Դ����</a></li>
<li><a href="/smsAction?action=Init" target="main"><img src="img/fsdx.png">���Ͷ���</a></li>
<li><a href="/smsAction?action=unsentRecord&pageIndex=1" target="main"><img src="img/dfslb.png">�������б�</a></li>
<li><a href="/smsAction?action=msgRecord" target="main"><img src="img/dxjl.png">���ż�¼</a></li>
<li><a href="/log?action=init&pageIndex1=1" target="main"><img src="img/xtrz.png">ϵͳ��־</a></li>
<li><a href="/system?action=Init" target="main"> <img src="img/xtsz.png">ϵͳ����</a></li>
<li><a href="modify-password.jsp" target="main"> <img src="img/xgmm.png">�޸�����</a></li>
<li><a href="/logout" target="_top"> <img src="img/tckzt.png">�˳�����̨</a></li>
</ul>

</div><div style="clear:both"><img src="img/left_btbg.jpg"/></div>
<div>&nbsp;<a style="height:25px;line-height:25px;color:#333;text-decoration:none" href="http://www.sendsms.com.cn/"  target="_blank">Copyright &copy; 2012&nbsp;������</a></div>
</div>
</body>
</html>