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
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Buxland-列表</title>
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
		td{
			font-size:10pt;
			font-family:宋体;
		}
		-->
		</style>
	</head>
	<body topmargin="3" style="margin:0;padding:0;text-align:center;">
		<jsp:include page="header.jsp" flush="true"/>
		
		<table border="0" width="100%">
			<tr>
				<td colspan="2">
					<img src="images/total.png"/ border="0">
				</td>
			</tr>
			<tr>
				<td width="80%" valign="top">
					<table border="0" width="100%" align="left" valign="top">
<%  
	try{
  	Context ctx = new InitialContext();
  	DataSource ds = (DataSource)ctx.lookup(JNDINAME);
  	conn = ds.getConnection();
  	stmt = conn.createStatement();
  	String sql = "select * from BUX_SITE order by SCORE desc";
  	rs = stmt.executeQuery(sql);
  	int i = 0;
  	while(rs.next()){
  		i++;
%>
									<tr>
										<td width="45%">
											<%=(i<10?"0"+i:""+i)%>. <a target="_blank" href="/detail?id=<%=rs.getString("ID")%>"><%=rs.getString("DOMAIN")%></a>
										</td>
										<td width="20%">
										</td>
										<td width="35%">
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
									<!--tr>
										<td>
											02. <a target="_blank" href="http://www.upbux.com/r?rh=9300450574B17242C7257C">www.upbux.com</a>
										</td>
										<td>
											&nbsp;
										</td>
										<td>
											<div class="star"></div>
										</td>
									</tr-->
					</table>
				</td>
				<td align="right" valign="top">
					<jsp:include page="main-adv.jsp" flush="true"/>
				</td>
			</tr>
		</table>
		
		<jsp:include page="tailer.jsp" flush="true"/>

	<body>
</html>