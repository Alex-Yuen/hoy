<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="javax.naming.*"%>
<%
	final String JNDINAME = "java:comp/env/jdbc/buxland";
	Connection conn = null;
	Statement stmt = null;
  ResultSet rs = null;
  String id = request.getParameter("id");
  if(id==null){
  	session.setAttribute("ERROR_MSG", "ID为空或错误");
  	response.sendRedirect("/error");
  }
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Buxland</title>
		<link rel="shortcut icon" href="http://www.buxland.net/favicon.ico">
		<style type="text/css">
		<!--
		.star{
			float: left;
			height: 16px;
			width: 16px;
			background-image: url(images/star.png)!important;/* FF IE7 */
			background-repeat: no-repeat;
			_filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='images/star.png'); /* IE6 */
			_background-image: none; /* IE6 */
		}
		#gbar, #guser{
			font-size:13px;padding-top:1px !important
		}
		-->
		td{
			font-size:10pt;
			font-family:宋体;
		}
		</style>
	</head>
	<body topmargin="3" style="margin:0;padding:0;text-align:center;">
		<jsp:include page="header.jsp" flush="true"/>
		
		<table border="0" width="100%">
			<tr>
				<td colspan="2">
					<u><span>网站详情</span></u>
				</td>
			</tr>
			<tr>
				<td width="80%" valign="top">
					<table border="0" width="100%" align="left" valign="top">
						<tr style="margin:0px;padding:0px;height:8px;line-height:8px;">
							<td colspan="3">&nbsp;</td>
						</tr>
<%  
	try{
  	Context ctx = new InitialContext();
  	DataSource ds = (DataSource)ctx.lookup(JNDINAME);
  	conn = ds.getConnection();
  	stmt = conn.createStatement();
  	String sql = "select * from BUX_SITE where ID ='"+id+"'";
  	rs = stmt.executeQuery(sql);
  	while(rs.next()){
%>
						<tr>
							<td width="5%">
								&nbsp;
							</td>
							<td width="10%">
								网站:
							</td>
							<td>
								<%=rs.getString("DOMAIN")%>
							</td>
						</tr>
						<tr>
							<td width="5%">
								&nbsp;
							</td>
							<td>
								注册地址:
							</td>
							<td>
								<a target="_blank" href="<%=rs.getString("LINK")%>"><%=rs.getString("LINK")%></a>
							</td>
						</tr>
						<tr>
							<td width="5%">
								&nbsp;
							</td>
							<td>
								评分:
							</td>
							<td>
								<%=rs.getString("SCORE")%>
							</td>
						</tr>
<%
  	}
	}catch(Exception e){
		out.println(e);
		//out.println(e.getMessage());
  	//System.out.println(e) ;
  	//e.printStackTrace(new PrintWriter(out));
	}finally{
		if(conn!=null){
			conn.close();
		}
	}
%>
						<tr style="margin:0px;padding:0px;height:8px;line-height:8px;">
							<td colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3" align="left">
								<div style="background-color:#9BAFF1;width:80%;height:5px;left-margin:0;">&nbsp;</div>
							</td>
						</tr>
					</table>
				</td>
				<td align="right" valign="top">
					<jsp:include page="detail-adv.jsp" flush="true"/>
				</td>
			</tr>
		</table>
		
		<jsp:include page="tailer.jsp" flush="true"/>
		
	<body>
</html>