package net.fxsprit.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7195225526662457905L;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public IndexServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		out.println("<html>");
		out.println("<head>");
		out.println("<Meta http-equiv=\"Content-Type\" Content=\"text/html; Charset=utf-8\">");
		out.println("<title>");
		out.println("Forex Sprit Console");
		out.println("</title>");
		out.println("</head>");
		out.println("<body>");

		out.println("<p><b>Forex Sprit Console</b><br/><br/></p>");
		out.println("<table width=\"100%\">");
		out.println("	<tr>");
		out.println("		<td width=\"50%\" valign=\"top\">");
		out.println("			<div width=\"100%\" style=\"PADDING-RIGHT:10px;OVERFLOW-Y:auto;PADDING-LEFT:10px;SCROLLBAR- FACE-COLOR:#ffffff;FONT-SIZE:11pt;PADDING-BOTTOM:0px;SCROLLBAR- HIGHLIGHT-COLOR:#ffffff;SCROLLBAR-SHADOW- COLOR:#919192;COLOR:blue;SCROLLBAR-3DLIGHT-COLOR:#ffffff;LINE- HEIGHT:100%;SCROLLBAR-ARROW-COLOR:#919192;PADDING-TOP:0px;SCROLLBAR- TRACK-COLOR:#ffffff;FONT-FAMILY:宋体;SCROLLBAR-DARKSHADOW- COLOR:#ffffff;LETTER-SPACING:1pt;HEIGHT:400px;TEXT-ALIGN:left\">");
		out.println("				<table width=\"100%\" cellspacing=\"5\">");
		out.println("					<tr>");
		out.println("						<td width=\"15%\"><b>Time</b></td>");
		out.println("						<td width=\"75%\"><b>Content</b></td>");
		out.println("						<td width=\"10%\" align=\"center\"><b>Provider</b></td>");
		out.println("					</tr>");
		
		for(ForexNews fn: Messages.INFOMATION){
			out.println("					<tr>");
			out.println("						<td><input type=\"checkbox\" name=\"ck\" value=\""+fn.getId()+"\"/> "+sdf.format(new Date(fn.getTime()))+"</td>");
			out.println("						<td>"+fn.getContent()+"</td>");
			out.println("						<td align=\"center\">"+fn.getProvider()+"</td>");
			out.println("					</tr>");
		}
		
//		out.println("					<tr>");
//		out.println("						<td><input type=\"checkbox\" name=\"ck\"/> 2009-11-23 12:43:34</td>");
//		out.println("						<td>美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升美元飚升</td>");
//		out.println("						<td align=\"center\">和讯</td>");
//		out.println("					</tr>");		
		out.println("				</table>");
		out.println("			</div>");
		out.println("			<br/>");
		out.println("			<input type=\"button\" name=\"delete\" value=\"Delete\"/>");
		out.println("		</td>");
		out.println("		<td align=\"center\" valign=\"top\">");
		out.println("			<form action=\"../fx-api/post\" method=\"post\">");
		out.println("			<textarea name=\"message\" rows=\"10\" cols=\"50\"></textarea><br/>");
		out.println("			<input name=\"really\" type=\"checkbox\" value=\"yes\"> <a href=\"#\" onClick=\"javascript: document.all('really').checked = !document.all('really').checked;\">I am sure.</a><br/><br/>");
		out.println("			<input name=\"submit\" type=\"Submit\" value=\"Submit\"/>");
		out.println("			</form>");
		out.println("		</td>");
		out.println("	</tr>");
		out.println("</table>");

		out.println("</body>");
		out.println("</html>");
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires GET method.");
		out.close();
	}

}
