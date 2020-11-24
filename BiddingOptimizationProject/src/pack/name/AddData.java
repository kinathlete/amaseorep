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

import javafx.scene.chart.PieChart.Data;

/**
 * Servlet implementation class AddData
 */
@WebServlet("/AddData")
public class AddData extends HttpServlet {


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

			try {
				String profileId = "665743598278254";
				String account = "amazon";
				
				Connection conn = null;

				try {
					conn = DatabaseConnector.getConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Main4.fillBidInformationAll();
				Main4.fillPerformanceData(account, profileId, conn);
				
				conn.close();

			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
			response.sendRedirect("AddingDone.jsp");
	}

}
