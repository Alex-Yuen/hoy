<%@ page language="java" %>
<%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="java.io.*" %>
<%-------------------------------------------------------------%>
<%-- JSP̽��V0.05�����Ų���                                ----%>
<%-- ��̽�뻹��һЩδ��ɵĵط����ҶԴ˴����Ĳ������Ǹ��------%>
<%-- ��ӭ�������鷢�� xyzhtml@163.com                      ----%>
<%-- ��ͬ�д���ߡ����Ľ���һ�����ã��Ժ�İ汾��������������--%>
<%-- 2003/3/15                               ----%>
<%-------------------------------------------------------------%>
<%
int temp = 0;
long star = 0;
long end = 0;
long ttime = 0;
Date before = new Date();
star = before.getTime();
for(int i=0;i<100000; i++)
{
temp=1+1;
}
Date after = new Date();
end = after.getTime();
ttime = end-star ;
%>

<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache"> 
<META HTTP-EQUIV="Expires" CONTENT="0"> 
<TITLE>JSP̽��V0.05�����Ų��� http://xuyizhi.y365.com</TITLE>
<style>
<!--
BODY
{
	FONT-FAMILY: ����;
	FONT-SIZE: 9pt
}
TD
{
	FONT-SIZE: 9pt;
}
TR
{
    HEIGHT: 18px;
}
A
{
	COLOR: #000000;
	TEXT-DECORATION: none
}
A:hover
{
	COLOR: #3F8805;
	TEXT-DECORATION: underline
}
.input
{
	BORDER: #111111 1px solid;
	FONT-SIZE: 9pt;
	BACKGROUND-color: #F8FFF0
}
.backs
{
	BACKGROUND-COLOR: #3F8805;
	COLOR: #ffffff;

}
.backq
{
	BACKGROUND-COLOR: #EEFEE0
}
.backc
{
	BACKGROUND-COLOR: #3F8805;
	BORDER: medium none;
	COLOR: #ffffff;
	HEIGHT: 18px;
	font-size: 9pt
}
.fonts
{
	COLOR: #3F8805
}
-->
</STYLE>
</head>

<body>
<a href="mailto:xyzhtml@163.com">���Ų���</a>&nbsp;��д��JSP̽��-<font class=fonts>V0.05</font><br><br>
<font class=fonts>�Ƿ�֧��JSP</font>
<br>���������������ʾ���Ŀռ䲻֧��JSP��
<br>1�����ʱ��ļ�ʱ��ʾ���ء�
<br>2�����ʱ��ļ�ʱ�������ơ�&lt;%@ %&gt;import="***"�������֡�
<br><br>
<% out.print("<font class=fonts>���������йز���</font>");%>
<table border=0 width=450 cellspacing=0 cellpadding=0 bgcolor="#3F8805">
<tr><td>
<table border=0 width=450 cellspacing=1 cellpadding=0>
          <tr bgcolor="#EEFEE0" >
          <td align=left width=150>&nbsp;��������</td><td width=300>&nbsp;<%= request.getServerName() %></td>
          </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;������IP</td><td>&nbsp;</td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;�������˿�</td><td>&nbsp;<%= request.getServerPort() %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;������ʱ��</td><td>&nbsp;<% out.println(new java.util.Date()); %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;���ļ�·��</td><td>&nbsp;<%=request.getPathTranslated() %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;��������������</td><td>&nbsp;<% String publish=getServletContext().getServerInfo(); out.println(publish); %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;HTTP��������</td><td>&nbsp;<%=request.getProtocol() %></td>
	  </tr>
<tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;����������</td><td>&nbsp;<%=request.getInputStream() %></td>
	  </tr>
	</table>

</td></tr>
</table>
<br>

<font class=fonts>����������</font>
<br>�� �������������
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr  class=backs align=center><td width=320>�� �� �� ��</td><td width=130>֧�ּ��汾</td></tr>
<%
	ServletContext context = getServletContext();
	Enumeration enumx = context.getAttributeNames();
	while (enumx.hasMoreElements()) {
	    String key = (String)enumx.nextElement();
            Object value = context.getAttribute(key);
            out.println("<tr bgcolor=#EEFEE0><td>" + key + "</td><td>" + value + "</td></tr>");
	}
%>
</table>
<br>�� �������ļ��ϴ��͹������
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr  class=backs align=center><td width=320>�� �� �� ��</td><td width=130>֧�ּ��汾</td></tr>
        <tr class=backq>
		<td align=left>&nbsp;(���޴������)<font color=#888888>&nbsp;(�ļ��ϴ�)</font></td>
		<td align=left>&nbsp;<font color=red><b>��</b></font></td>
	</tr></table>
<br>�� �������ʼ�ϵͳ(���޴˹���)
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr height=18 class=backs align=center><td width=320>�� �� �� ��</td><td width=130>֧�ּ��汾</td></tr>
	<tr  class=backq>
		<td align=left>&nbsp;POP Mail<font color=#888888>&nbsp;</a></font></td>
		<td align=left>&nbsp;<font class=fonts><b></b></font> </td>
	</tr>
	
	<tr class=backq>
		<td align=left>&nbsp;SMTP<font color=#888888>&nbsp;</font></td>
		<td align=left>&nbsp;<font class=fonts><b></b></font> </td>
	</tr>
	
	<tr  class=backq>
		<td align=left>&nbsp;IMAP<font color=#888888>&nbsp;</font></td>
		<td align=left>&nbsp;<font color=red><b>��</b></font></td>
	</tr>	
        <tr  class=backq>
		<td align=left>&nbsp;JSWDK<font color=#888888>&nbsp;</a></font></td>
		<td align=left>&nbsp;<font class=fonts><b></b></font> </td>
	</tr>
</table>
<br>
<font class=fonts>�������֧��������</font><br>
��������������������Ҫ���������ProgId��ClassId��(���޴˹���)
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
<FORM action=/jspcheck.jsp method=post id=form1 name=form1>
	<tr  class=backq>
		<td align=center height=30><input class=input type=text value="" name="classname" size=40>
<INPUT type=submit value=" ȷ �� " class=backc id=submit1 name=submit1>
<INPUT type=reset value=" �� �� " class=backc id=reset1 name=reset1> 
</td>
	  </tr>
</FORM>
</table>
<BR>
<font class=fonts>JSP�ű����ͺ������ٶȲ���</font><br>
�����÷�����ִ��10��Ρ�1��1���ļ��㣬��¼����ʹ�õ�ʱ�䡣
<table class=backq border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
  <tr  class=backs align=center>
	<td width=320>��&nbsp;&nbsp;&nbsp;��&nbsp;&nbsp;&nbsp;��</td><td width=130>���ʱ��</td></tr>
  </tr>
  <tr >
	<td align=left>&nbsp;isavvix.com(������ѿռ�)</td><td>&nbsp;187����</td>
  </tr>

  <form action="/jspcheck.jsp" method=post>

  <tr  >
	<td align=left>&nbsp;<font color=red>������ʹ�õ���̨������</font>&nbsp;</td><td>&nbsp;<font color=red><%=ttime %>����</font></td>
  </tr>
  </form>
</table>
<table border=0 width=450 cellspacing=0 cellpadding=0>
<tr><td align=center>
��ӭ����&nbsp����������&nbsp<a href="http://xuyizhi.y365.com">http://xuyizhi.y365.com</a>
<br>�������ɻ��Ų���(<a href="mailto:xyzhtml@163.com">xyzhtml@163.com</a>)��д��������кõ���������������ҡ�
<br>ת��ʱ�뱣����Щ��Ϣ��
</td></tr>
</table>
</body> 
</html>