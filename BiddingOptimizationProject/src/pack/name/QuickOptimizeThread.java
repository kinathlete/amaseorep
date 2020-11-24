 package pack.name;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuickOptimizeThread implements Runnable {

	private String account;
	private String profileId;
	private Connection conn;
	private int actionId;

	public QuickOptimizeThread(String account, String profileId, int actionId, Connection conn) {
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
		System.out.println("Executing thread");
		
		ArrayList<String> snapshotIds = requestSnapshots();
		fillPerformanceData();
		fillBidInformation(snapshotIds);
		
		System.out.println("Start optimize biddings: "+profileId);
		boolean completedSuccessfully = optimizeBiddings(account, profileId, conn);
		
		String status = (completedSuccessfully) ? "SUCCESS"	: "Not optimized";
		
		try {
		System.out.println("Completed processing for profile ID: "+profileId);
		PreparedStatement pStmt;
		pStmt = conn
					.prepareStatement("update actionStatus set status='"+status+"' where actionId = ?");
		pStmt.setInt(1, actionId);
		pStmt.executeUpdate();
		pStmt.close();
		// Update action status for specific action id
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
		
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}
	private void fillPerformanceData() {
		try {
			Statement stmt = null;
			ResultSet rs = null;

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
			while (n <= 32) {

				LocalDate date = LocalDate.now().minusDays(n);
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("reportIdKeywords", apiConn.requestKeywordReport(date));
				map.put("reportIdTargets", apiConn.requestTargetReport(date));
				map.put("reportIdHeadlineKeywords", apiConn.requestHeadlineKeywordReport(date));
				map.put("date", date.toString());				

				reportIds.add(map);

				n++;
			}

			boolean foundPerfomanceData = false;
			boolean entryFound = false;
			boolean updateData = false;

			String bigSqlU = "UPDATE Performance_Data SET Impressions=?, Clicks = ?, Cost = ?, Conversions30 = ?, Conversions30SameSKU = ?, UnitsOrdered30 = ?, UnitsOrdered30SameSKU = ?, Sales30 = ?, Sales30SameSKU = ? WHERE Keyword_TargetId=? AND DateData=?";

			PreparedStatement prepStmt = null;
			prepStmt = conn.prepareStatement(bigSqlU);

			String bigSql = "INSERT INTO Performance_Data VALUES ";

			int numberOfReportIds = reportIds.size();

			stmt = conn.createStatement();
			System.out.println("Processing reports for: "+profileId);
			int j = 1;
			for (Map<String, String> map : reportIds) {				
				
				String date = map.get("date");

				// Process for keyword reports
				String reportStatus = "IN_PROGRESS";
				
				while (reportStatus.equals("IN_PROGRESS")) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					reportStatus = apiConn.getReportStatus(map.get("reportIdKeywords"));
					
					if(reportStatus.equals("IN_PROGRESS")) {
						TimeUnit.SECONDS.sleep(1);
						}
				}

				apiConn = new AmzApiConnector(account, profileId, conn);

				String responseDownload = apiConn.downloadReport(map.get("reportIdKeywords"));
				JSONArray jArray = new JSONArray(responseDownload);				

				for (int i = 0; i < jArray.length(); i++) {
					Object arrayItem = jArray.get(i);
					
//					System.out.println(i + "/" + jArray.length() + " for "+profileId);
					JSONObject json = new JSONObject(arrayItem.toString());

					if (json.getInt("clicks") == 0) {
						continue;
					}

					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT Id, Sales30 FROM Performance_Data where Keyword_TargetId = "
							+ json.getLong("keywordId") + " and DateData = '" + date
							+ "';");
					entryFound = rs.next();
					
					if (entryFound) {

						if (rs.getDouble(2) == json.getDouble("attributedSales30d")) {
							continue;
						}

						prepStmt.setInt(1, json.getInt("impressions"));
						prepStmt.setInt(2, json.getInt("clicks"));
						prepStmt.setDouble(3, json.getDouble("cost"));
						prepStmt.setDouble(4, json.getDouble("attributedConversions30d"));
						prepStmt.setInt(5, json.getInt("attributedConversions30dSameSKU"));
						prepStmt.setInt(6, json.getInt("attributedUnitsOrdered30d"));
						prepStmt.setInt(7, json.getInt("attributedUnitsOrdered30dSameSKU"));
						prepStmt.setDouble(8, json.getDouble("attributedSales30d"));
						prepStmt.setDouble(9, json.getDouble("attributedSales30dSameSKU"));
						prepStmt.setLong(10, json.getLong("keywordId"));
						prepStmt.setString(11, date);
						prepStmt.addBatch();
						
						updateData = true;
						continue;
					}

					String keywordTargetingText = json.getString("keywordText");
					if (keywordTargetingText.contains("'")) {
						keywordTargetingText = keywordTargetingText.replace("'", "''");
					}

					bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
							+ date + "','" + json.getLong("campaignId") + "','" + json.getLong("adGroupId")
							+ "','" + json.getLong("keywordId") + "','" + keywordTargetingText + "','"
							+ json.getString("matchType") + "','" + json.getInt("impressions") + "','"
							+ json.getInt("clicks") + "','" + json.getDouble("cost") + "','"
							+ json.getInt("attributedConversions30d") + "','"
							+ json.getInt("attributedConversions30dSameSKU") + "','"
							+ json.getInt("attributedUnitsOrdered30d") + "','"
							+ json.getInt("attributedUnitsOrdered30dSameSKU") + "','" + json.getDouble("attributedSales30d")
							+ "','" + json.getDouble("attributedSales30dSameSKU") + "'),";
					foundPerfomanceData = true;
				}
					
					// Process for target reports
					reportStatus = "IN_PROGRESS";
					
					while (reportStatus.equals("IN_PROGRESS")) {
						apiConn = new AmzApiConnector(account, profileId, conn);
						reportStatus = apiConn.getReportStatus(map.get("reportIdTargets"));
						
						if(reportStatus.equals("IN_PROGRESS")) {
							TimeUnit.SECONDS.sleep(1);
							}
					}

					apiConn = new AmzApiConnector(account, profileId, conn);

					responseDownload = apiConn.downloadReport(map.get("reportIdTargets"));
					jArray = new JSONArray(responseDownload);				

					for (int i = 0; i < jArray.length(); i++) {
						Object arrayItem = jArray.get(i);

						JSONObject json = new JSONObject(arrayItem.toString());

						if (json.getInt("clicks") == 0) {
							continue;
						}

						stmt = conn.createStatement();
						rs = stmt.executeQuery("SELECT Id, Sales30 FROM Performance_Data where Keyword_TargetId = "
								+ json.getLong("targetId") + " and DateData = '" + date
								+ "';");
						entryFound = rs.next();
						
						if (entryFound) {

							if (rs.getDouble(2) == json.getDouble("attributedSales30d")) {
								continue;
							}

							prepStmt.setInt(1, json.getInt("impressions"));
							prepStmt.setInt(2, json.getInt("clicks"));
							prepStmt.setDouble(3, json.getDouble("cost"));
							prepStmt.setDouble(4, json.getDouble("attributedConversions30d"));
							prepStmt.setInt(5, json.getInt("attributedConversions30dSameSKU"));
							prepStmt.setInt(6, json.getInt("attributedUnitsOrdered30d"));
							prepStmt.setInt(7, json.getInt("attributedUnitsOrdered30dSameSKU"));
							prepStmt.setDouble(8, json.getDouble("attributedSales30d"));
							prepStmt.setDouble(9, json.getDouble("attributedSales30dSameSKU"));
							prepStmt.setLong(10, json.getLong("targetId"));
							prepStmt.setString(11, date);
							prepStmt.addBatch();
							
							updateData = true;
							continue;
						}

						String keywordTargetingText = json.getString("targetingText");
						if (keywordTargetingText.contains("'")) {
							keywordTargetingText = keywordTargetingText.replace("'", "''");
						}

						bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
								+ date + "','" + json.getLong("campaignId") + "','" + json.getLong("adGroupId")
								+ "','" + json.getLong("targetId") + "','" + keywordTargetingText + "','"
								+ json.getString("targetingType") + "','" + json.getInt("impressions") + "','"
								+ json.getInt("clicks") + "','" + json.getDouble("cost") + "','"
								+ json.getInt("attributedConversions30d") + "','"
								+ json.getInt("attributedConversions30dSameSKU") + "','"
								+ json.getInt("attributedUnitsOrdered30d") + "','"
								+ json.getInt("attributedUnitsOrdered30dSameSKU") + "','" + json.getDouble("attributedSales30d")
								+ "','" + json.getDouble("attributedSales30dSameSKU") + "'),";
						foundPerfomanceData = true;	
				}
				

					
					// Process for Headline Ads
					reportStatus = "IN_PROGRESS";
					
					while (reportStatus.equals("IN_PROGRESS")) {
						apiConn = new AmzApiConnector(account, profileId, conn);
						reportStatus = apiConn.getReportStatus(map.get("reportIdHeadlineKeywords"));
						
						if(reportStatus.equals("IN_PROGRESS")) {
							TimeUnit.SECONDS.sleep(1);
							}
					}

					apiConn = new AmzApiConnector(account, profileId, conn);

					responseDownload = apiConn.downloadReport(map.get("reportIdHeadlineKeywords"));
					jArray = new JSONArray(responseDownload);				

					for (int i = 0; i < jArray.length(); i++) {
						Object arrayItem = jArray.get(i);

						JSONObject json = new JSONObject(arrayItem.toString());

						if (json.getInt("clicks") == 0) {
							continue;
						}

						stmt = conn.createStatement();
						rs = stmt.executeQuery("SELECT Id, Sales30 FROM Performance_Data where Keyword_TargetId = "
								+ json.getLong("keywordId") + " and DateData = '" + date
								+ "';");
						entryFound = rs.next();
						
						if (entryFound) {

							if (rs.getDouble(2) == json.getDouble("attributedSales14d")) {
								continue;
							}

							prepStmt.setInt(1, json.getInt("impressions"));
							prepStmt.setInt(2, json.getInt("clicks"));
							prepStmt.setDouble(3, json.getDouble("cost"));
							prepStmt.setDouble(4, json.getDouble("attributedConversions14d"));
							prepStmt.setInt(5, json.getInt("attributedConversions14dSameSKU"));
							prepStmt.setInt(6, 0);
							prepStmt.setInt(7, json.getInt("unitsSold14d"));
							prepStmt.setDouble(8, json.getDouble("attributedSales14d"));
							prepStmt.setDouble(9, json.getDouble("attributedSales14dSameSKU"));
							prepStmt.setLong(10, json.getLong("keywordId"));
							prepStmt.setString(11, date);
							prepStmt.addBatch();
							
							updateData = true;
							continue;
						}

						String keywordTargetingText = json.getString("keywordText");
						if (keywordTargetingText.contains("'")) {
							keywordTargetingText = keywordTargetingText.replace("'", "''");
						}

						bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
								+ date + "','" + json.getLong("campaignId") + "','" + json.getLong("adGroupId")
								+ "','" + json.getLong("keywordId") + "','" + keywordTargetingText + "','"
								+ json.getString("matchType") + "','" + json.getInt("impressions") + "','"
								+ json.getInt("clicks") + "','" + json.getDouble("cost") + "','"
								+ json.getInt("attributedConversions14d") + "','"
								+ json.getInt("attributedConversions14dSameSKU") + "','0','"
								+ json.getInt("unitsSold14d") + "','" + json.getDouble("attributedSales14d")
								+ "','" + json.getDouble("attributedSales14dSameSKU") + "'),";
						foundPerfomanceData = true;	
				}
					j++;
			}
					
				
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
			
			if(updateData) {
				prepStmt.executeBatch();
				prepStmt.close();
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			markActionFailed();
		}
}
	private ArrayList<String> requestSnapshots(){
		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);
		
		ArrayList<String> snapshotIds = new ArrayList<String>();
		
		String snapshotId;
		try {
			snapshotId = apiConn.requestKeywordSnapshot();
			snapshotIds.add(snapshotId);
			
			snapshotId = apiConn.requestTargetSnapshot();
			snapshotIds.add(snapshotId);
			
			snapshotId = apiConn.requestHeadlineKeywordSnapshot();
			snapshotIds.add(snapshotId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return snapshotIds;
	}
	private void fillBidInformation(ArrayList<String> snapshotIds) {
		try {
		
		boolean update = false;	
		System.out.println("start fillBidInfo for: "+profileId);
		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);
		Statement stmt = null;
		ResultSet rs = null;
		
		String bigSqlU = "UPDATE Bid_Information SET bid=? WHERE id_keyword_target=?";
		PreparedStatement prepStmt = null;
		
			prepStmt = conn.prepareStatement(bigSqlU);
		

		String bigSql = "INSERT INTO Bid_Information VALUES ";
		boolean filledInsertStatement = false;
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT id_keyword_target, bid FROM Bid_Information WHERE profileId = '"+profileId+"';");
		
		
		List<Map<String, Object>> oldBids = new ArrayList<Map<String, Object>>();
		if(rs.next()) {
			oldBids = resultSetToList(rs);
		}else {
			oldBids = null;
		}		
		
		//Process for keywords
		String snapshotId = snapshotIds.get(0);
		
		String keywordTargetingIdPlaceholder = "keywordId";
		
		String snapshotStatus = "IN_PROGRESS";
		while (snapshotStatus.equals("IN_PROGRESS")) {
			apiConn = new AmzApiConnector(account, profileId, conn);
			snapshotStatus = apiConn.getSnapshotStatus(snapshotId);
		}
		
		System.out.println("process keyword snapshot for: "+profileId);
		String responseDownload = apiConn.downloadSnapshot(snapshotId);

		JSONArray jArray = new JSONArray(responseDownload);

		for (int i = 0; i < jArray.length(); i++) {
			Object arrayItem = jArray.get(i);	
			JSONObject json = new JSONObject(arrayItem.toString());

			double bid;

			if (json.has("bid")) {
				bid = json.getDouble("bid");
			} else {
				bid = 0;
			}

			boolean entryFound = false;
			double oldBid = 0;
			
			if(oldBids == null) {
				entryFound = false;
			}else {
				for (Map<String, Object> row : oldBids) {
					if ((long) row.get("id_keyword_target") == json.getLong(keywordTargetingIdPlaceholder)) {
						oldBid = (double) row.get("bid");
						entryFound = true;
						// System.out.println("FORSCHLEIFEENTRY-FOUND "+i);
					}
				}
			}

			if (entryFound && (oldBid != bid && bid != 0)) {

				// update the new bidding
				prepStmt.setDouble(1, bid);
				prepStmt.setLong(2, json.getLong(keywordTargetingIdPlaceholder));
				prepStmt.addBatch();
				
				update = true;

			} else if (!entryFound) {

				long adGroupId = json.getLong("adGroupId");
				if (bid == 0) {
					bid = apiConn.getAdGroupBid(adGroupId);
				}


					String keyword = json.getString("keywordText");
					if (keyword.contains("'")) {
						keyword = keyword.replace("'", "''");
					}
					bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
							+ "keyword" + "','" + json.getLong("keywordId") + "','" + keyword + "','" + "-" + "','"
							+ bid + "','" + "1900-01-01" + "','" + adGroupId + "','" + json.getLong("campaignId")

							+ "'),";

				filledInsertStatement = true;
			}

		}
		System.out.println("Done: process keyword snapshot for: "+profileId);
		//Process for targets
				snapshotId = snapshotIds.get(1);
				
				keywordTargetingIdPlaceholder = "targetId";
				snapshotStatus = "IN_PROGRESS";
				while (snapshotStatus.equals("IN_PROGRESS")) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					snapshotStatus = apiConn.getSnapshotStatus(snapshotId);
				}

				responseDownload = apiConn.downloadSnapshot(snapshotId);

				jArray = new JSONArray(responseDownload);

				for (int i = 0; i < jArray.length(); i++) {
					Object arrayItem = jArray.get(i);

					JSONObject json = new JSONObject(arrayItem.toString());

					double bid;

					if (json.has("bid")) {
						bid = json.getDouble("bid");
					} else {
						bid = 0;
					}

					boolean entryFound = false;
					double oldBid = 0;
					
					if(oldBids == null) {
						entryFound = false;
					}else {
					for (Map<String, Object> row : oldBids) {
						if ((long) row.get("id_keyword_target") == json.getLong(keywordTargetingIdPlaceholder)) {
							oldBid = (double) row.get("bid");
							entryFound = true;
							// System.out.println("FORSCHLEIFEENTRY-FOUND "+i);
						}

					}
					}
					if (entryFound && (oldBid != bid && bid != 0)) {

						// update the new bidding
						prepStmt.setDouble(1, bid);
						prepStmt.setLong(2, json.getLong(keywordTargetingIdPlaceholder));
						prepStmt.addBatch();
						update = true;
						
					} else if (!entryFound) {

						long adGroupId = json.getLong("adGroupId");
						if (bid == 0) {
							bid = apiConn.getAdGroupBid(adGroupId);
						}

							JSONArray expression = json.getJSONArray("expression");
							JSONObject exjson = expression.getJSONObject(0);

							bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
									+ "target" + "','" + json.getLong("targetId") + "','" + "-" + "','"
									+ exjson.getString("type") + "','" + bid + "','" + "1900-01-01" + "','" + adGroupId + "','"
									+ json.getLong("campaignId") + "'),";


						filledInsertStatement = true;
					}

				}
				System.out.println("Done: process target snapshot for: "+profileId);
				//Process for keywords Headline
				snapshotId = snapshotIds.get(2);
				
				keywordTargetingIdPlaceholder = "keywordId";

				snapshotStatus = "IN_PROGRESS";
				while (snapshotStatus.equals("IN_PROGRESS")) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					snapshotStatus = apiConn.getSnapshotStatus(snapshotId);
				}

				responseDownload = apiConn.downloadSnapshot(snapshotId);

				jArray = new JSONArray(responseDownload);

				for (int i = 0; i < jArray.length(); i++) {
					Object arrayItem = jArray.get(i);

					JSONObject json = new JSONObject(arrayItem.toString());

					double bid;

					if (json.has("bid")) {
						bid = json.getDouble("bid");
					} else {
						bid = 0;
					}

					boolean entryFound = false;
					double oldBid = 0;
					if(oldBids == null) {
						entryFound = false;
					}else {
					for (Map<String, Object> row : oldBids) {
						if ((long) row.get("id_keyword_target") == json.getLong(keywordTargetingIdPlaceholder)) {
							oldBid = (double) row.get("bid");
							entryFound = true;
							// System.out.println("FORSCHLEIFEENTRY-FOUND "+i);
						}

					}
					}
					if (entryFound && (oldBid != bid && bid != 0)) {

						// update the new bidding
						prepStmt.setDouble(1, bid);
						prepStmt.setLong(2, json.getLong(keywordTargetingIdPlaceholder));
						prepStmt.addBatch();
						update = true;
						
					} else if (!entryFound) {

							String keyword = json.getString("keywordText");
							if (keyword.contains("'")) {
								keyword = keyword.replace("'", "''");
							}

							bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
									+ "keyword" + "','" + json.getLong("keywordId") + "','" + keyword + "','" + "-" + "','"
									+ bid + "','" + "1900-01-01" + "','0','" + json.getLong("campaignId")

									+ "'),";

						filledInsertStatement = true;
					}

				}
				System.out.println("Done: process headline snapshot for: "+profileId);
		if(update) {
		prepStmt.executeBatch();			
		}
		if (prepStmt != null) {
		prepStmt.close();
		}

		if (filledInsertStatement) {
			bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";

			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(bigSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		rs.close();
		stmt.close();
		
		} catch (SQLException | JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			rows.add(row);
		}
		return rows;
	}
	
	private boolean optimizeBiddings(String account, String profileId, Connection conn){
		boolean completedSuccessfully = false;
		try {			
			completedSuccessfully = BidOpt.optimizeKeywordsTargets(account, profileId, "keyword", conn);
			if(completedSuccessfully) {
			BidOpt.optimizeKeywordsTargets(account, profileId, "target", conn);
			}
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return completedSuccessfully;
	}	
	
	
	
	private void showDatabaseError() {

	}

	private void markActionFailed() {

	}

}
