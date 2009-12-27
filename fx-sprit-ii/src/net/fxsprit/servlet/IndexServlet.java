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

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

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
		out.println("<script type=\"text/javascript\" src=\"js/jquery-1.3.2.min.js\"></script>");
		out.println("<script>");
		out.println("	function post(){");
		out.println("		$(\"#submit\").attr(\"disabled\", \"true\");");
		out.println("		var really;");
		out.println("		//alert($(\"#really\").attr(\"checked\"));");
		out.println("		if($(\"#really\").attr(\"checked\")){really=$(\"#really\").val();}");
		out.println("		$.ajax({");
		out.println("			type: \"post\",		//请求方式");
		out.println("			url: \"fx-api/post\",	//发送请求地址");
		out.println("			data:{				//发送给数据库的数据");
		out.println("				message:$(\"#message\").val(),");
		out.println("				really:really");
		out.println("			},");
		out.println("								//请求成功后的回调函数有两个参数");
		out.println("			success:function(data){");
		out.println("				alert(data);");
		out.println("				window.location.reload();");
		out.println("				$(\"#message\").val(\"\")");
		out.println("				$(\"#submit\").removeAttr(\"disabled\");");
		out.println("			}");
		out.println("		});");
		out.println("	}");
		out.println("</script>");
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

		for (ForexNews fn : Messages.INFOMATION) {
			out.println("					<tr>");
			out.println("						<td><input type=\"checkbox\" name=\"ck\" value=\""
					+ fn.getId()
					+ "\"/> "
					+ sdf.format(new Date(fn.getTime())) + "</td>");
			out.println("						<td>" + fn.getContent() + "</td>");
			out.println("						<td align=\"center\">" + fn.getProvider()
					+ "</td>");
			out.println("					</tr>");
		}

		out.println("				</table>");
		out.println("			</div>");
		out.println("			<br/>");
		out.println("			<input type=\"button\" name=\"delete\" value=\"Delete\"/>");
		out.println("		</td>");
		out.println("		<td align=\"center\" valign=\"top\">");
		out.println("			<form action=\"fx-api/post\" method=\"post\">");
		out.println("			<textarea id=\"message\" name=\"message\" rows=\"10\" cols=\"50\"></textarea><br/>");
		out.println("			<input id=\"really\" name=\"really\" type=\"checkbox\" value=\"yes\"> <a href=\"#\" onClick=\"javascript: document.all('really').checked = !document.all('really').checked;\">I am sure.</a><br/><br/>");
		out.println("			<input id=\"submit\" name=\"submit\" type=\"button\" value=\"Submit\" onclick=\"post();\"/>");
		out.println("			</form>");
		out.println("		</td>");
		out.println("	</tr>");
		out.println("</table>");

		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires GET method.");
		out.flush();
		out.close();
	}

}
