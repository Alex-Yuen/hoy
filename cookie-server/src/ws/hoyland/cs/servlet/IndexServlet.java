package ws.hoyland.cs.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class IndexServlet extends HttpServlet
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1846454243708798335L;

	public IndexServlet()
    {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        RequestDispatcher reqDispatcher = req.getRequestDispatcher("/WEB-INF/view/index.jsp");
        reqDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
