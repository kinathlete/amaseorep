package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class AddData
 */
@WebServlet("/FillPerformanceData")
public class FillPerformanceData extends HttpServlet {


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		AmzApiConnector apiConn = null;
		
		DatabaseConnector dbConnector = null;
		Connection conn = null;

		try {
			conn = DatabaseConnector.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String profileId = request.getParameter("profileId");
		String account = request.getParameter("account");
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
		
		try {
			
			apiConn = new AmzApiConnector(account, profileId, conn);
			String responseContent1 = apiConn.checkClientStatus1();
			
			if(!responseContent1.contains("[")) {
				response.getWriter().write("No Permission");
				return;
			}

				Main4.fillPerformanceData(account, profileId, conn);
				response.getWriter().write("Success");

				conn.close();
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.sendRedirect("FillPerformanceData.jsp");

	}

}
