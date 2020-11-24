package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

@WebServlet("/refreshaccesstokens")
public class RefreshAccessTokens extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DatabaseConnector dbConnector = null;
		Connection conn = null;
		try {
			conn = DatabaseConnector.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AmzApiConnector apiConn;
		try {
		apiConn = new AmzApiConnector("business", "", conn);		
		String businessToken = apiConn.refreshToken();
				
		apiConn = new AmzApiConnector("amazon", "", conn);
		String amazonToken = apiConn.refreshToken();
		
		apiConn = new AmzApiConnector("comAmazon", "", conn);
		String comAmazonToken = apiConn.refreshToken();
		
		PreparedStatement pStmt = conn
				.prepareStatement("update accessTokens set accessToken=? where account = ?");
		pStmt.setString(1, businessToken);
		pStmt.setString(2, "business");
		pStmt.addBatch();
		
		pStmt.setString(1, amazonToken);
		pStmt.setString(2, "amazon");
		pStmt.addBatch();
		
		pStmt.setString(1, comAmazonToken);
		pStmt.setString(2, "comAmazon");
		pStmt.addBatch();
		
		pStmt.executeUpdate();
		pStmt.close();
		conn.close();
		} catch (JSONException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}
}
