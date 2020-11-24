 package pack.name;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

public class FillKpiClientsThread2 implements Runnable {

	private String account;
	private String profileId;
	private Connection conn;
	private int actionId;

	public FillKpiClientsThread2(String account, String profileId, int actionId) {
		this.account = account;
		this.profileId = profileId;
		this.actionId = actionId;
		try {
			this.conn = DatabaseConnector.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Statement stmt = null;
			ResultSet rs = null;

			System.out.println("Executing thread");

			List<Map<String, String>> reportIds = new ArrayList<>();

			AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

			String responseContent1 = apiConn.checkClientStatus1();

			if (!responseContent1.contains("[")) {
				String sql = "UPDATE actionStatus SET status = 'No Permission' WHERE actionId = " + actionId;
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				return;
			}

			// Populate the ArrayList with the report Ids
			int n = 2;
			while (n <= 37) {

				LocalDate date = LocalDate.now().minusDays(n);

				Map<String, String> map = new HashMap<String, String>();
				map.put("reportId", apiConn.requestCampaignReport(date));
				map.put("date", date.toString());
				map.put("reportIdHeadline", apiConn.requestHeadlineReport(date));

				reportIds.add(map);

				n++;
			}

			boolean foundPerfomanceData = false;
			boolean entryFound = false;
			boolean executeUpdate = false;

			String bigSqlU = "UPDATE KPIs_Clients SET costs=?, revenue = ?, acos = ? WHERE id=?";

			PreparedStatement prepStmt = null;
			prepStmt = conn.prepareStatement(bigSqlU);

			String bigSql = "INSERT INTO KPIs_Clients VALUES ";

			int numberOfReportIds = reportIds.size();

			stmt = conn.createStatement();
			int j = 1;
			for (Map<String, String> map : reportIds) {				
				String date = map.get("date");
				
				// Getting the report downloads for sponsored products and accumulate the costs
				// and revenue
				String reportStatus = "IN_PROGRESS";
				
				while (reportStatus.equals("IN_PROGRESS")) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					reportStatus = apiConn.getReportStatus(map.get("reportId"));
					
					if(reportStatus.equals("IN_PROGRESS")) {
						TimeUnit.SECONDS.sleep(1);
						}
				}

				double costs = 0;
				double revenue = 0;

				apiConn = new AmzApiConnector(account, profileId, conn);

				String responseDownload = apiConn.downloadReport(map.get("reportId"));
				JSONArray jArray = new JSONArray(responseDownload);

				for (int i = 0; i < jArray.length(); i++) {
					Object arrayItem = jArray.get(i);

					JSONObject json = new JSONObject(arrayItem.toString());

					costs = costs + json.getDouble("cost");
					revenue = revenue + json.getDouble("attributedSales7d");
				}

				// same process for the sponsored brands
				reportStatus = "IN_PROGRESS";
				while (reportStatus.equals("IN_PROGRESS")) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					reportStatus = apiConn.getReportStatus(map.get("reportIdHeadline"));

				}

				responseDownload = apiConn.downloadReport(map.get("reportIdHeadline"));
				jArray = new JSONArray(responseDownload);

				for (int i = 0; i < jArray.length(); i++) {
					Object arrayItem = jArray.get(i);

					JSONObject json = new JSONObject(arrayItem.toString());

					costs = costs + json.getDouble("cost");
					revenue = revenue + json.getDouble("attributedSales14d");
				}

 				double acos = (costs / revenue) * 100;

				costs = Math.round(costs * 100.0) / 100.0;
				revenue = Math.round(revenue * 100.0) / 100.0;
				acos = Math.round(acos * 100.0) / 100.0;

				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery("SELECT id FROM KPIs_Clients where profileId = '" + profileId
						+ "' AND date = '" + map.get("date") + "'");
				entryFound = rs2.next();

				if (entryFound) {
					prepStmt.setDouble(1, costs);
					prepStmt.setDouble(2, revenue);
					prepStmt.setDouble(3, acos);
					prepStmt.setLong(4, rs2.getLong(1));
					
					prepStmt.addBatch();
					
					executeUpdate = true;
				} else {
					bigSql = bigSql + "(NULL, '" + profileId + "','" + map.get("date") + "'," + costs + "," + revenue
							+ "," + acos + "),";
					foundPerfomanceData = true;
				}

				j++;
			}
			System.out.println("processing done for " + profileId);
			if (foundPerfomanceData) {
				bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";
				Statement stmt2 = null;
				try {
					stmt2 = conn.createStatement();
					stmt2.execute(bigSql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				stmt2.close();
			}

			stmt.close();
			if (executeUpdate) {
				prepStmt.executeBatch();
				prepStmt.close();
			}
			System.out.println("Completed processing for profile ID: "+profileId);
			PreparedStatement pStmt = conn
					.prepareStatement("update actionStatus set status='SUCCESS' where actionId = ?");
			pStmt.setInt(1, actionId);
			pStmt.executeUpdate();
			pStmt.close();
			// Update action status for specific action id
			this.conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			markActionFailed();
		}

	}

	private void showDatabaseError() {

	}

	private void markActionFailed() {

	}

}
