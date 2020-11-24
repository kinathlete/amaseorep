package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
@WebServlet("/ShowPerformanceStatus")
public class ShowPerformanceStatus extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String profileId = request.getParameter("profileId");

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		DatabaseConnector dbConnector = null;
		Connection conn = null;

		try {
			conn = DatabaseConnector.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {


			JSONObject KPIs = new JSONObject();
			// KPIs = Main4.getPerformanceStatus(account, profileId);
			KPIs = Main4.getPerformanceStatusFromTable(profileId, conn);
			response.getWriter().write(KPIs.toString());
			conn.close();
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}
		

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.sendRedirect("ShowPerformanceStatus.jsp");

	}

}
