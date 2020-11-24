package pack.name;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
@WebServlet("/SemrushTest")
public class SemrushTest extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("start");
		Statement stmt = null;
		ResultSet rs = null;
		String domain = "";
		
		DatabaseConnector dbConnector = null;
		Connection conn = null;

		try {
			conn = DatabaseConnector.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String bigSqlU = "INSERT INTO semrush_data Values(?,?,?,?,?,?,?,?,?)";

			PreparedStatement prepStmt = null;
			prepStmt = conn.prepareStatement(bigSqlU);			
			
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT domain FROM semrush_domains");
			
			int j = 1;
			int y = 1;
			while(rs.next()) {
				domain = rs.getString(1);
				
				StringBuffer responseContent = new StringBuffer();
				String line;
				HttpURLConnection connection = null;
				BufferedReader reader = null;
				
		 

					URL url = new URL("https://api.semrush.com/?type=domain_rank&key=0176d0440cb4227260c5418c90f779bd&domain="+domain+"&database=de");
					connection = (HttpURLConnection) url.openConnection();			
					connection.setRequestMethod("GET");					

					int status = connection.getResponseCode();
					
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					int i = 1;
					while((line = reader.readLine()) != null) {
						if(i == 2) {
						responseContent.append(line);
						}
					i++;	
					}
//					System.out.println(responseContent);
					System.out.println(domain);
					String dataString = responseContent.toString();
					
					if(!dataString.equals("")) {
						ArrayList<String> data = new ArrayList<>(Arrays.asList(dataString.split(";")));
						
//						for (String string : data) {
//							System.out.println(string);
//						}
						
						System.out.println(y);
						prepStmt.setNull(1, java.sql.Types.INTEGER);
						prepStmt.setString(2, domain);
						prepStmt.setInt(3, Integer.parseInt(data.get(1)));
						prepStmt.setInt(4, Integer.parseInt(data.get(2)));
						prepStmt.setInt(5, Integer.parseInt(data.get(3)));
						prepStmt.setInt(6, Integer.parseInt(data.get(4)));
						prepStmt.setInt(7, Integer.parseInt(data.get(5)));
						prepStmt.setInt(8, Integer.parseInt(data.get(6)));
						prepStmt.setInt(9, Integer.parseInt(data.get(7)));
						prepStmt.addBatch();
						y++;
						
						if(y==500) {
							prepStmt.executeBatch();
						y=1;
						}
					}
					
					System.out.println(j);
					
				j++;	
			}
			prepStmt.executeBatch();
			prepStmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		

			

	}
	

}
