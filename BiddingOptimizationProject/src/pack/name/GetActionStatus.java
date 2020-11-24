package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/getactionstatus")
public class GetActionStatus extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int actionId = Integer.valueOf(request.getParameter("actionId"));
		DatabaseConnector dbConnector = null;
		Connection conn = null;

		try {
			dbConnector = new DatabaseConnector();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try(Connection con = DatabaseConnector.getConnection()) {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select profileId, status from actionStatus where actionId="+actionId);
			rs.next();
			JSONObject object = new JSONObject();
			object.put("profileId", rs.getString("profileId"));
			object.put("status", rs.getString("status"));
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(object.toString());
		    con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
