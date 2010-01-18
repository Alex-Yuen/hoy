<%@ page language="java" %>
<%@ page contentType="text/html;charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="java.io.*" %>
<%-------------------------------------------------------------%>
<%-- JSP探针V0.05－怀才不遇                                ----%>
<%-- 此探针还有一些未完成的地方，我对此带来的不便深表歉意------%>
<%-- 欢迎您将建议发到 xyzhtml@163.com                      ----%>
<%-- 共同切磋提高。您的建议一旦采用，以后的版本将署上您的名字--%>
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
<TITLE>JSP探针V0.05－怀才不遇 http://xuyizhi.y365.com</TITLE>
<style>
<!--
BODY
{
	FONT-FAMILY: 宋体;
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
<a href="mailto:xyzhtml@163.com">怀才不遇</a>&nbsp;改写的JSP探针-<font class=fonts>V0.05</font><br><br>
<font class=fonts>是否支持JSP</font>
<br>出现以下情况即表示您的空间不支持JSP：
<br>1、访问本文件时提示下载。
<br>2、访问本文件时看到类似“&lt;%@ %&gt;import="***"”的文字。
<br><br>
<% out.print("<font class=fonts>服务器的有关参数</font>");%>
<table border=0 width=450 cellspacing=0 cellpadding=0 bgcolor="#3F8805">
<tr><td>
<table border=0 width=450 cellspacing=1 cellpadding=0>
          <tr bgcolor="#EEFEE0" >
          <td align=left width=150>&nbsp;服务器名</td><td width=300>&nbsp;<%= request.getServerName() %></td>
          </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;服务器IP</td><td>&nbsp;</td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;服务器端口</td><td>&nbsp;<%= request.getServerPort() %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;服务器时间</td><td>&nbsp;<% out.println(new java.util.Date()); %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;本文件路径</td><td>&nbsp;<%=request.getPathTranslated() %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;服务器解译引擎</td><td>&nbsp;<% String publish=getServletContext().getServerInfo(); out.println(publish); %></td>
	  </tr>
	  <tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;HTTP解译引擎</td><td>&nbsp;<%=request.getProtocol() %></td>
	  </tr>
<tr bgcolor="#EEFEE0" >
		<td align=left>&nbsp;服务器配置</td><td>&nbsp;<%=request.getInputStream() %></td>
	  </tr>
	</table>

</td></tr>
</table>
<br>

<font class=fonts>服务器属性</font>
<br>■ 服务器属数情况
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr  class=backs align=center><td width=320>参 数 名 称</td><td width=130>支持及版本</td></tr>
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
<br>■ 常见的文件上传和管理组件
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr  class=backs align=center><td width=320>组 件 名 称</td><td width=130>支持及版本</td></tr>
        <tr class=backq>
		<td align=left>&nbsp;(暂无此类组件)<font color=#888888>&nbsp;(文件上传)</font></td>
		<td align=left>&nbsp;<font color=red><b>×</b></font></td>
	</tr></table>
<br>■ 服务器邮件系统(暂无此功能)
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
	<tr height=18 class=backs align=center><td width=320>组 件 名 称</td><td width=130>支持及版本</td></tr>
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
		<td align=left>&nbsp;<font color=red><b>×</b></font></td>
	</tr>	
        <tr  class=backq>
		<td align=left>&nbsp;JSWDK<font color=#888888>&nbsp;</a></font></td>
		<td align=left>&nbsp;<font class=fonts><b></b></font> </td>
	</tr>
</table>
<br>
<font class=fonts>其他组件支持情况检测</font><br>
在下面的输入框中输入你要检测的组件的ProgId或ClassId。(暂无此功能)
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
<FORM action=/jspcheck.jsp method=post id=form1 name=form1>
	<tr  class=backq>
		<td align=center height=30><input class=input type=text value="" name="classname" size=40>
<INPUT type=submit value=" 确 定 " class=backc id=submit1 name=submit1>
<INPUT type=reset value=" 重 填 " class=backc id=reset1 name=reset1> 
</td>
	  </tr>
</FORM>
</table>
<BR>
<font class=fonts>JSP脚本解释和运算速度测试</font><br>
我们让服务器执行10万次“1＋1”的计算，记录其所使用的时间。
<table class=backq border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#3F8805" width="450">
  <tr  class=backs align=center>
	<td width=320>服&nbsp;&nbsp;&nbsp;务&nbsp;&nbsp;&nbsp;器</td><td width=130>完成时间</td></tr>
  </tr>
  <tr >
	<td align=left>&nbsp;isavvix.com(国外免费空间)</td><td>&nbsp;187毫秒</td>
  </tr>

  <form action="/jspcheck.jsp" method=post>

  <tr  >
	<td align=left>&nbsp;<font color=red>您正在使用的这台服务器</font>&nbsp;</td><td>&nbsp;<font color=red><%=ttime %>毫秒</font></td>
  </tr>
  </form>
</table>
<table border=0 width=450 cellspacing=0 cellpadding=0>
<tr><td align=center>
欢迎访问&nbsp【泽舟网】&nbsp<a href="http://xuyizhi.y365.com">http://xuyizhi.y365.com</a>
<br>本程序由怀才不遇(<a href="mailto:xyzhtml@163.com">xyzhtml@163.com</a>)编写，如果您有好的意见，请您告诉我。
<br>转载时请保留这些信息。
</td></tr>
</table>
</body> 
</html>