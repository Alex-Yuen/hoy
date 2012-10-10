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

%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>主备切换和备份记录</title>
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
	url="/system?action=list&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function pre(){
	
	var pageIndex = getvalueofselect('condition.pageIndex');
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	--pageIndex;
	url="/system?action=list&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function turn(){
	
	var pageIndex = getvalueofselect('condition.pageIndex');
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	url="/system?action=list&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
function change(flag){
	var pageIndex = getvalueofselect('condition.pageIndex');
	if(flag)pageIndex=1;
	var type = getvalueofselect('condition.type');
	var order = getvalueofselect('condition.order');
	url="/system?action=list&condition.pageIndex="+pageIndex+"&condition.type="+type+"&condition.order="+order;
	window.location.href=url;
}
</script>
</head>
<body>
<div id="top"><span><img src="img/tu1.jpg"></span>主备切换和备份记录</div>
<div id="center" >
<div id="table" >
<%
	Page page1 = (Page)request.getAttribute("page");
	Condition condition = (Condition)request.getAttribute("condition");
%>
<table cellpadding="0" cellspacing="1" border="0" width="100%">
<tr>
<td colspan="3">
<select name="condition.type" id="condition.type" onchange="change(true)">
<option <%if(condition.getType()==1)out.print("selected"); %> value="1">主备切换记录</option>
<option <%if(condition.getType()==2)out.print("selected"); %> value="2">备份记录</option>
</select>
</td>
<td colspan="3">
<select name="condition.order" id="condition.order" onchange="change(false)">
<option <%if(condition.getOrder()==0)out.print("selected"); %> value="0">按时间降序排列</option>
<option <%if(condition.getOrder()==1)out.print("selected"); %> value="1">按时间升序排列</option>
</select>
</td>
</tr>
<%
	if(condition.getType()==1)
	{ 
%>
<tr align="center">
<th width="10%">ID</th><th width="10%">主机状态</th><th width="10%">备机状态</th><th width="20%">切换时间</th><th colspan="2">备注</th>
</tr>
<%
	for(int i = 0;i<page1.getData().size();i++){
		SwitRecord rec =(SwitRecord) page1.getData().get(i);
%>
<tr align="center">
<td><%=rec.getId() %></td>
<td><%
if(rec.isMaster()){
	out.println("ON");
}else{
	out.println("OFF");
}
%></td>
<td><%
if(rec.isSlaver()){
	out.println("ON");
}else{
	out.println("OFF");
}
%></td>
<td><%=dtformat(rec.getSwitTime())%></td>
<td align="left"><span style="word-warp:break-word;word-break:break-all"><%=rec.getMemo() %>
</td>
</tr>
<%}%>
<%}else { %>
<tr align="center">
<th width="10%">ID</th><th width="10%">主机</th><th width="10%">备份状态</th><th width="10%">文件大小</th><th width="20%">备份时间</th><th>备注</th>
</tr>
<%
	for(int i = 0;i<page1.getData().size();i++){
		BackupRecord rec = (BackupRecord)page1.getData().get(i);
%>
<tr align="center">
<td><%=rec.getId() %></td>
<td><%if(rec.isMachine()){out.println("主机");}else{out.println("备机");}%></td>
<td><%=rec.getState()%></td>
<td><%=rec.getFileSize()%></td>
<td><%=dtformat(rec.getBackupTime())%></td>
<td align="left"><span style="word-warp:break-word;word-break:break-all"><%=rec.getMemo() %></span></td>
</tr>
<%
	}
} %>
<tr>
<td colspan="8" align="right">
记录:<%=page1.getTotalNum() %>页数:<%=page1.getPageNum()%>每页记录数:<%=page1.getPageSize() %>
<input type="button" <%if(page1.getPageIndex()==1)out.print("disabled"); %> value="上一页" onclick="pre()"><input type="button" value="下一页" <%if(page1.getPageIndex()>=page1.getPageNum())out.print("disabled"); %> onclick="next()">
第<select id="condition.pageIndex" name="condition.pageIndex" onchange="turn()">
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
</select>页
</td>
</tr>
</table>
</div>

</div>
<%if(request.getAttribute("message")!=null) {%>
<script type="text/javascript">
alert('<%=request.getAttribute("message")%>');
</script>
<% }%>
</body>
</html>