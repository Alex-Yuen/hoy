package ws.hoyland.qqgm.server;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class GEServlet extends HttpServlet
{

    private static final long serialVersionUID = 0x79d4b749cac9462L;

    public GEServlet()
    {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
    	throw new ServletException("Unsupported method");
        //RequestDispatcher reqDispatcher = req.getRequestDispatcher("/WEB-INF/view/index.jsp");
        //reqDispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
