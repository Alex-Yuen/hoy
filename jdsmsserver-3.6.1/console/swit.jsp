<%@ include file="validate.jsp" %>
<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ page import="cn.sendsms.jdsmsserver.web.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%!
String dtformat(Date time)
    {
        if(time == null)
        {
            return "";
        } else
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(time);
        }
    }
Object[] st = (Object[])(request.getAttribute("st"));
String[] sts = new String[]{"OFF", "WAIT", "ON"};
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>����״̬</title>
<link href="css.css" type="text/css" rel="stylesheet">
<script type="text/javascript">
function getvalueofselect(id){
	var index = document.getElementById(id).selectedIndex;
	var value = document.getElementById(id).options[index].value;
	return value;
}
function next(){
	
	var pageIndex = getvalueofselect('condition.pageIndex');
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	++pageIndex;
	url="/system?action=swit&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function pre(){
	
	var pageIndex = getvalueofselect('condition.pageIndex');
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	--pageIndex;
	url="/system?action=swit&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function turn(){
	
	var pageIndex = getvalueofselect('condition.pageIndex');
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	url="/system?action=swit&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function change(flag){
	var pageIndex = getvalueofselect('condition.pageIndex');
	if(flag)pageIndex=1;
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	url="/system?action=swit&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
</script>
</head>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>����״̬</div>
<div id="center">
<div>
	<br/>
	<br/>
	<br/>
	<span style="color:#00FF00">��</span>����&nbsp;<span style="color:#0000FF">��</span>��ͣ&nbsp;<span style="color:#FF0000">��</span>�ػ�&nbsp;
	<br/>
	<br/>
	<img src="img/computer_<%=st[0]%>.png"/>&nbsp;&nbsp;����IP:<%=st[1]%>&nbsp;&nbsp;
	<br/><br/>
	<img src="img/computer_<%=st[2]%>.png"/>&nbsp;&nbsp;����IP:<%=st[3]%>&nbsp;&nbsp;
</div>

<div id="table" >
<%
	Page page1 = (Page)request.getAttribute("page");
	Condition condition = (Condition)request.getAttribute("condition");
%>
<table cellpadding="0" cellspacing="1" border="0" width="100%">
<tr>
<td colspan="3">
<input type="hidden" name="condition.type" id="condition.type" value="1"/>
</td>
<td colspan="4">
<select name="condition.order" id="condition.order" onchange="change(false)">
<option <%if(condition.getOrder()==0)out.print("selected"); %> value="0">��ʱ�併������</option>
<option <%if(condition.getOrder()==1)out.print("selected"); %> value="1">��ʱ����������</option>
</select>
</td>
</tr>
<%
	if(condition.getType()==1)
	{ 
%>
<tr align="center">
<th width="10%">ID</th><th width="10%">����״̬</th><th width="10%">����IP</th><th width="10%">����״̬</th><th width="10%">����IP</th><th width="20%">�л�ʱ��</th><th>��ע</th>
</tr>
<%
	for(int i = 0;i<page1.getData().size();i++){
		SwitRecord rec =(SwitRecord) page1.getData().get(i);
%>
<tr align="center">
<td><%=rec.getId()%></td>
<td><%=sts[rec.getMaster()]%></td>
<td><%=rec.getMip()%></td>
<td><%=sts[rec.getSlaver()]%></td>
<td><%=rec.getSip()%></td>
<td><%=dtformat(rec.getSwitTime())%></td>
<td align="left"><span style="word-warp:break-word;word-break:break-all"><%=rec.getMemo() %>
</td>
</tr>
<%}%>
<%}else { %>
<tr align="center">
<th width="10%">ID</th><th width="10%">��������</th><th width="10%">����״̬</th><th width="10%">�ļ���</th><th width="10%">�ļ���С</th><th width="20%">����ʱ��</th><th>��ע</th>
</tr>
<%
	for(int i = 0;i<page1.getData().size();i++){
		BackupRecord rec = (BackupRecord)page1.getData().get(i);
%>
<tr align="center">
<td><%=rec.getId() %></td>
<td><%if(rec.isMachine()){out.println("����");}else{out.println("����");}%></td>
<td><%=rec.getState()%></td>
<td><%=rec.getFileName()%></td>
<td><%=rec.getFileSize()%></td>
<td><%=dtformat(rec.getBackupTime())%></td>
<td align="left"><span style="word-warp:break-word;word-break:break-all"><%=rec.getMemo() %></span></td>
</tr>
<%
	}
} %>
<tr>
<td colspan="8" align="right">
��¼:<%=page1.getTotalNum() %>ҳ��:<%=page1.getPageNum()%>ÿҳ��¼��:<%=page1.getPageSize() %>
<input type="button" <%if(page1.getPageIndex()==1)out.print("disabled"); %> value="��һҳ" onclick="pre()"><input type="button" value="��һҳ" <%if(page1.getPageIndex()>=page1.getPageNum())out.print("disabled"); %> onclick="next()">
��<select id="condition.pageIndex" name="condition.pageIndex" onchange="turn()">
<%
	if(page1.getPageNum()==0){
%>
	<option value="1" > 1</option>
<%
	}
%>
	
<%
for(int i=0;i<page1.getPageNum();i++){
%>
<option value="<%=i+1 %>" <%if(page1.getPageIndex()==i+1)out.print("selected"); %>> <%=i+1 %></option>
<%} %>
</select>ҳ
</td>
</tr>
</table>
</div>

<%if(request.getAttribute("message")!=null){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<%} %>
</div>
</body>
</html>