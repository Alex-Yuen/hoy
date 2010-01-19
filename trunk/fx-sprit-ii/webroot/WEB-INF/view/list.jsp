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
 	
 	String p = "1";
 	int showinonepage = 20;
 	int recordcount = 0;
    int pagecount = 0;
    
 	if(request.getParameter("page")!=null){
 		p = request.getParameter("page");
 	}
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
		<script type="text/javascript" src="/js/page.js"></script>
	</head>
	<body topmargin="3" style="margin:0;padding:0;text-align:center;">
		<jsp:include page="header.jsp" flush="true"/>
<%	
	try{
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup(JNDINAME);
		conn = ds.getConnection();
		stmt = conn.createStatement();
		String c = "select count(*) from BUX_SITE";
		rs = stmt.executeQuery(c);
		
		if(rs.next()){
			recordcount = rs.getInt(1);  		
		}
		rs.close();
		stmt.close();
		
		if(recordcount%showinonepage!=0){
			pagecount = recordcount/showinonepage + 1;
		}else{
			pagecount = recordcount/showinonepage;
		}		
%>		
		<table border="0" width="100%">
			<tr>
				<td colspan="2">
					<b><u>TOTAL: <%=recordcount%></u></b>
					<!--img src="images/total.png"/ border="0"-->
				</td>
			</tr>
			<tr>
				<td width="80%" valign="top">
					<table border="0" width="80%" align="left">
<%  
  	stmt = conn.createStatement();
 	
  	String sql = "select * from BUX_SITE order by SCORE desc limit "+ (Integer.parseInt(p)-1)*showinonepage +", " + showinonepage;

  	//out.println("<tr><td cospan=2>"+sql+"</td></tr>");
  	
  	rs = stmt.executeQuery(sql);
  	int i = (Integer.parseInt(p)-1)*showinonepage;
  	while(rs.next()){
  		i++;
%>
									<tr>
										<td width="45%">
											<%=(i<10?"0"+i:""+i)%>. <a href="/detail?id=<%=rs.getString("ID")%>"><%=rs.getString("DOMAIN")%></a>
										</td>
										<td width="20%">
										</td>
										<td width="35%">
											<%=rs.getString("SCORE")%>
										</td>
									</tr>
<%
  	}
	
	for(int j=i;j<(Integer.parseInt(p))*showinonepage;j++){
%>
						<tr>
							<td colspan="3">
							&nbsp;
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
						<tr>
							<td colspan="3">
							&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="3" align="center" valign="top">
							<!-- 翻页 -->
							<%@ include file="page.jsp"%>
							</td>
						</tr>
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