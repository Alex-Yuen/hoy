<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="javax.naming.*"%>
<%!
final String JNDINAME = "java:comp/env/jdbc/buxland" ;
%>
<%
Connection conn = null ;
try{
  // 初始化查找命名空间
  Context ctx = new InitialContext() ;
  // 找到DataSource
  DataSource ds = (DataSource)ctx.lookup(JNDINAME) ;
  conn = ds.getConnection() ;  
}catch(Exception e){
  System.out.println(e) ;
}
%>
<%=conn%>
<%
// 将连接重新放回到池中
conn.close() ;
%> 