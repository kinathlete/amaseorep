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

/**
 * Servlet implementation class AddData
 */
@WebServlet("/UpdateClients")
public class UpdateClients extends HttpServlet {


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			DatabaseConnector dbConnector = null;
			Connection conn = null;

				try {
					dbConnector = new DatabaseConnector();
					conn = dbConnector.getConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					System.out.println("get all profiles");
					Main4.getAllProfiles("business", conn);
					Main4.getAllProfiles("amazon", conn);
					Main4.getAllProfiles("comAmazon", conn);

					conn.close();
				} catch (JSONException | SQLException e) {
					e.printStackTrace();
				}
			response.sendRedirect("AddingDone.jsp");
	}

}
