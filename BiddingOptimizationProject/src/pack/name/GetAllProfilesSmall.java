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

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Servlet implementation class GetAllProfiles
 */
@WebServlet("/GetAllProfilesSmall")
public class GetAllProfilesSmall extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllProfilesSmall() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
//		String specificProfileId = " AND profileId = '2809619863267108'";
		String specificProfileId = "";
//		String specificProfileId = " AND amaseoAccount = 'comAmazon'";
		try {
			conn = DatabaseConnector.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//if Sql already has entries for keywords and targets only check if those keywords/targets are still active. Otherwise get all the keywords/targets from api
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT profileId,targetAcos, sellerName, marketplace, amaseoAccount FROM Clients_small WHERE (status <> 'deleted' OR status IS null)" + specificProfileId);
			JSONArray jsonProfiles = ResultSetToJsonMapper.convert(rs);
			
			System.out.println(jsonProfiles.toString());
			
			response.setContentType("text/plain");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(jsonProfiles.toString());
		    System.out.println("test");
			
			conn.close();
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
