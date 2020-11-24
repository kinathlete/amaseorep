package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
@WebServlet("/test")
public class test extends HttpServlet {

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
			Statement stmt = null;
			ResultSet rs = null;

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT count(*) FROM Bid_Information WHERE profileId = '" + profileId + "'");
			rs.next();
			String numberOfKeywords = Integer.toString(rs.getInt(1));
			System.out.println(rs.getInt(1));
			response.getWriter().write(numberOfKeywords);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.sendRedirect("test.jsp");

	}

}
