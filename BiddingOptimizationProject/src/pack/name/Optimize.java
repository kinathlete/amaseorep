package pack.name;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Optimize")
public class Optimize extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public Optimize() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		AmzApiConnector apiConn = null;
		
		String profileId = request.getParameter("profileId");
		String account = request.getParameter("account");
		double targetAcos = Double.parseDouble(request.getParameter("targetAcos"));
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
		
		try {
			System.out.println("PROF: "+profileId);
			System.out.println("TargetAcos: "+targetAcos);
			
			if(targetAcos == 0) {
			    response.getWriter().write("Target Acos is 0");
			    return;
			}
			
			DatabaseConnector dbConnector = null;
			Connection conn = null;

			try {
				conn = DatabaseConnector.getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			apiConn = new AmzApiConnector(account, profileId, conn);
			String responseContent1 = apiConn.checkClientStatus1();
			
			if(!responseContent1.contains("[")) {
				response.getWriter().write("No Permission");
				return;
			}
			
			
				Main4.fillBidInformationAll(account, profileId, conn);
			    response.getWriter().write("Success");

			conn.close();
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}
		
	
			
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
