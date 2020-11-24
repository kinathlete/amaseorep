package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

/**
 * Servlet implementation class AddData
 */
@WebServlet("/FillPerformanceAndBidData")
public class FillPerformanceAndBidDataServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AmzApiConnector apiConn = null;

		String profileId = request.getParameter("profileId");
		String account = request.getParameter("account");

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
			
			apiConn = new AmzApiConnector(account, profileId, conn);
			String responseContent1 = apiConn.checkClientStatus1();

			if (!responseContent1.contains("[")) {
				response.getWriter().write("No Permission");
				System.out.println(responseContent1);
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "INSERT INTO actionStatus VALUES(null, '" + profileId + "','PENDING')";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.execute();
			ResultSet rs2 = ps.getGeneratedKeys();
			int actionId = 0;
			if (rs2.next()) {
				actionId = rs2.getInt(1);
			}
			FillPerformanceAndBidDataThread thread = new FillPerformanceAndBidDataThread(account, profileId, actionId, conn);
			Thread t = new Thread(thread);
			t.start();
			

			response.getWriter().write(actionId+"");
			conn.close();
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.sendRedirect("FillPerformanceAndBidData.jsp");

	}

}
