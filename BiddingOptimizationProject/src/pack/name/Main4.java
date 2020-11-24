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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// GET THE REQUESTED REPORT
public class Main4 {
	private static String profileId = "390634971668519";
	// Id f�r Calintio: 16171321883463
	// Id f�r Carenesse: 390634971668519
	// Id f�r Fairprotein: 3033710893682392
	// 629726640971637

	public static void main(String[] args) {

	}

	public static JSONObject getPerformanceStatusFromTable(String profileId, Connection conn) throws SQLException, JSONException {

		Statement stmt = null;
		ResultSet rs = null;

		double costs = 0;
		double revenue = 0;
		double acos = 0;

		JSONObject KPIs = new JSONObject();

		LocalDate firstDayBefore3Month = LocalDate.now().minusMonths(3).withDayOfMonth(1);
		LocalDate firstDayBefore2Month = LocalDate.now().minusMonths(2).withDayOfMonth(1);
		LocalDate firstDayBefore1Month = LocalDate.now().minusMonths(1).withDayOfMonth(1);
		LocalDate firstDayThisMonth = LocalDate.now().withDayOfMonth(1);
		LocalDate day8daysAgo = LocalDate.now().minusDays(8);
		LocalDate day4daysAgo = LocalDate.now().minusDays(4);
		LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);

		// int days3MonthAgo = firstDayBefore3Month.getMonth().length(true);
		// int days2MonthAgo = firstDayBefore2Month.getMonth().length(true);
		// int days1MonthAgo = firstDayBefore1Month.getMonth().length(true);
		// Period period = Period.between(firstDayThisMonth, dayBeforeYesterday);
		// int daysThisMonth = period.getDays() + 1;

		// For 3 Months ago
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + firstDayBefore3Month + "' AND date < '" + firstDayBefore2Month + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);
		
		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ firstDayBefore3Month + "' AND date < '" + firstDayBefore2Month + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costs3MonthsAgo", costs);
		KPIs.put("revenue3MonthsAgo", revenue);
		KPIs.put("acos3MonthsAgo", acos);

		// For 2 Months ago
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + firstDayBefore2Month + "' AND date < '" + firstDayBefore1Month + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);

		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ firstDayBefore2Month + "' AND date < '" + firstDayBefore1Month + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costs2MonthsAgo", costs);
		KPIs.put("revenue2MonthsAgo", revenue);
		KPIs.put("acos2MonthsAgo", acos);

		// For 1 Months ago
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + firstDayBefore1Month + "' AND date < '" + firstDayThisMonth + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);

		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ firstDayBefore1Month + "' AND date < '" + firstDayThisMonth + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costs1MonthAgo", costs);
		KPIs.put("revenue1MonthAgo", revenue);
		KPIs.put("acos1MonthAgo", acos);

		// For this month
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + firstDayThisMonth + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);

		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ firstDayThisMonth + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costsThisMonth", costs);
		KPIs.put("revenueThisMonth", revenue);
		KPIs.put("acosThisMonth", acos);
		
		// Last 7 days
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + day8daysAgo + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);

		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ day8daysAgo + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costsLast7Days", costs);
		KPIs.put("revenueLast7Days", revenue);
		KPIs.put("acosLast7Days", acos);
		
		
		// Last 3 days
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT sum(costs), sum(revenue) FROM KPIs_Clients WHERE profileId = '" + profileId
				+ "' AND date >= '" + day4daysAgo + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = rs.getDouble(1);
		revenue = rs.getDouble(2);

		if(revenue == 0) {
			acos = 0;
		}else {
		acos = (costs / revenue) * 100;
		}

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM KPIs_Clients WHERE profileId = '" + profileId + "' AND date >= '"
				+ day4daysAgo + "' AND date <= '" + dayBeforeYesterday + "'");
		rs.next();

		costs = Math.round(costs / rs.getInt(1) * 100.0) / 100.0;
		revenue = Math.round(revenue / rs.getInt(1) * 100.0) / 100.0;
		acos = Math.round(acos * 100.0) / 100.0;

		KPIs.put("costsLast3Days", costs);
		KPIs.put("revenueLast3Days", revenue);
		KPIs.put("acosLast3Days", acos);		
		rs.close();
		stmt.close();

		return KPIs;
	}

	public static int fillKPIsClientswithThread(String account, String profileId, Connection conn, int actionId) throws SQLException, JSONException {
		//Insert action into database starts 
		
		//Random ID of action
		//action_id, profile_id, account, status
		//Insert action into database ends
		FillKpiClientsThread thread = new FillKpiClientsThread();
		Thread t = new Thread(thread);
		t.start();
		return actionId;
	}
	
	public static void fillKPIsClients(String account, String profileId, Connection conn) throws SQLException, JSONException {

		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;

		boolean foundPerfomanceData = false;
		boolean entryFound = false;

		String bigSqlU = "UPDATE KPIs_Clients SET costs=?, revenue = ?, acos = ? WHERE id=?";

		PreparedStatement prepStmt = null;
		prepStmt = conn.prepareStatement(bigSqlU);

		String bigSql = "INSERT INTO KPIs_Clients VALUES ";

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		List<Map<String, String>> reportIds = new ArrayList<>();

		// Populate the ArrayList with the report Ids from the last 30 Days
		int n = 2;
		while (n <= 60) {

			LocalDate date = LocalDate.now().minusDays(n);

			Map<String, String> map = new HashMap<String, String>();
			map.put("reportId", apiConn.requestCampaignReport(date));
			map.put("date", date.toString());
			map.put("reportIdHeadline", apiConn.requestHeadlineReport(date));

			reportIds.add(map);

			n++;
		}

		for (Map<String, String> map : reportIds) {

			// Getting the report downloads for sponsored products and accumulate the costs
			// and revenue
			String reportStatus = "IN_PROGRESS";
			while (reportStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				reportStatus = apiConn.getReportStatus(map.get("reportId"));

			}

			double costs = 0;
			double revenue = 0;

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

			System.out.println(map.get("date"));

			stmt = conn.createStatement();
			rs2 = stmt.executeQuery("SELECT id FROM KPIs_Clients where profileId = '" + profileId + "' AND date = '"
					+ map.get("date") + "'");
			entryFound = rs2.next();

			if (entryFound) {
				prepStmt.setDouble(1, costs);
				prepStmt.setDouble(2, revenue);
				prepStmt.setDouble(3, acos);
				prepStmt.setLong(4, rs2.getLong(1));
			} else {
				bigSql = bigSql + "(NULL, '" + profileId + "','" + map.get("date") + "'," + costs + "," + revenue + ","
						+ acos + "),";
				foundPerfomanceData = true;
			}
		}
		if (foundPerfomanceData) {
			bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";
			System.out.println(bigSql);

			try {
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(bigSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt2.close();
		}

		stmt.close();
		if (entryFound) {
			prepStmt.executeUpdate();
			prepStmt.close();

			rs2.close();
		}

	}

	public static JSONObject getPerformanceStatus(String account, String profileId, Connection conn) throws JSONException {
		double costs3days = 0;
		double revenue3days = 0;
		double acos3days = 0;

		double costs7days = 0;
		double revenue7days = 0;
		double acos7days = 0;

		double costs14days = 0;
		double revenue14days = 0;
		double acos14days = 0;

		double costs30days = 0;
		double revenue30days = 0;
		double acos30days = 0;

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		ArrayList<String> reportIds = new ArrayList<String>();

		// Populate the ArrayList with the report Ids from the last 30 Days (yesterday
		// excluded)
		int n = 2;
		while (n <= 30) {

			LocalDate date = LocalDate.now().minusDays(n);

			reportIds.add(apiConn.requestCampaignReport(date));

			n++;
		}

		for (int j = 0; j < reportIds.size(); j++) {
			String reportId = reportIds.get(j);

			String reportStatus = "IN_PROGRESS";
			while (reportStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				reportStatus = apiConn.getReportStatus(reportId);
			}

			String responseDownload = apiConn.downloadReport(reportId);
			JSONArray jArray = new JSONArray(responseDownload);

			for (int i = 0; i < jArray.length(); i++) {
				Object arrayItem = jArray.get(i);

				JSONObject json = new JSONObject(arrayItem.toString());

				double currentCosts = json.getDouble("cost");
				double currentRevenue = json.getDouble("attributedSales7d");

				if (j < 3) {
					costs3days = costs3days + currentCosts;
					revenue3days = revenue3days + currentRevenue;

					costs7days = costs7days + currentCosts;
					revenue7days = revenue7days + currentRevenue;

					costs14days = costs14days + currentCosts;
					revenue14days = revenue14days + currentRevenue;

					costs30days = costs30days + currentCosts;
					revenue30days = revenue30days + currentRevenue;
				} else if (j < 7) {
					costs7days = costs7days + currentCosts;
					revenue7days = revenue7days + currentRevenue;

					costs14days = costs14days + currentCosts;
					revenue14days = revenue14days + currentRevenue;

					costs30days = costs30days + currentCosts;
					revenue30days = revenue30days + currentRevenue;
				} else if (j < 14) {
					costs14days = costs14days + currentCosts;
					revenue14days = revenue14days + currentRevenue;

					costs30days = costs30days + currentCosts;
					revenue30days = revenue30days + currentRevenue;
				} else {
					costs30days = costs30days + currentCosts;
					revenue30days = revenue30days + currentRevenue;
				}
			}
		}

		acos3days = costs3days / revenue3days * 100.0;
		acos7days = costs7days / revenue7days * 100.0;
		acos14days = costs14days / revenue14days * 100.0;
		acos30days = costs30days / revenue30days * 100.0;

		costs3days = Math.round(costs3days / 3 * 100.0) / 100.0;
		revenue3days = Math.round(revenue3days / 3 * 100.0) / 100.0;
		acos3days = Math.round(acos3days * 100.0) / 100.0;

		costs7days = Math.round(costs7days / 7 * 100.0) / 100.0;
		revenue7days = Math.round(revenue7days / 7 * 100.0) / 100.0;
		acos7days = Math.round(acos7days * 100.0) / 100.0;

		costs14days = Math.round(costs14days / 14 * 100.0) / 100.0;
		revenue14days = Math.round(revenue14days / 14 * 100.0) / 100.0;
		acos14days = Math.round(acos14days * 100.0) / 100.0;

		costs30days = Math.round(costs30days / 30 * 100.0) / 100.0;
		revenue30days = Math.round(revenue30days / 30 * 100.0) / 100.0;
		acos30days = Math.round(acos30days * 100.0) / 100.0;

		JSONObject KPIs = new JSONObject();
		KPIs.put("costs3days", costs3days);
		KPIs.put("revenue3days", revenue3days);
		KPIs.put("acos3days", acos3days);

		KPIs.put("costs7days", costs7days);
		KPIs.put("revenue7days", revenue7days);
		KPIs.put("acos7days", acos7days);

		KPIs.put("costs14days", costs14days);
		KPIs.put("revenue14days", revenue14days);
		KPIs.put("acos14days", acos14days);

		KPIs.put("costs30days", costs30days);
		KPIs.put("revenue30days", revenue30days);
		KPIs.put("acos30days", acos30days);

		return KPIs;

	}

	public static void goThroughAllProfiles(String account, Connection conn) throws SQLException, JSONException {

		AmzApiConnector apiConn = null;
		Statement stmt = null;
		ResultSet rs = null;

		ArrayList<String> profilesWithNoTargetAcos = new ArrayList<String>();
		ArrayList<String> profilesWithNoPermission = new ArrayList<String>();

		// if Sql already has entries for keywords and targets only check if those
		// keywords/targets are still active. Otherwise get all the keywords/targets
		// from api
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT profileId,targetAcos FROM Clients WHERE status <> 'deleted' OR status IS null");

		while (rs.next()) {
			String profileId = Long.toString(rs.getLong(1));
			double targetAcos = rs.getDouble(2);

			if (targetAcos == 0) {
				profilesWithNoTargetAcos.add(profileId);
				break;
			}

			apiConn = new AmzApiConnector(account, profileId, conn);
			String responseContent1 = apiConn.checkClientStatus1();

			// when the response does not contain "[" there is an error message and we have
			// no permission
			if (!responseContent1.contains("[")) {
				profilesWithNoPermission.add(profileId);
				break;
			}

			fillBidInformationAll(account, profileId, conn);
			fillPerformanceData(account, profileId, conn);
			optimizeBiddings(account, profileId, conn);

		}

	}

	public static void getAllProfiles(String account, Connection conn) throws JSONException, SQLException {

		String profileId = Main4.profileId;

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		String responseContent = apiConn.getAllProfiles();

		JSONArray jArray = new JSONArray(responseContent);

		// List<Map<String, String>> profilesWithContryCodes = new ArrayList<>();

		String bigSql = "INSERT IGNORE INTO Clients VALUES(?,?,?,?,?,?,?,?,?,?)";
		
		String bigSql2 = "INSERT IGNORE INTO Clients VALUES ";
		PreparedStatement prepStmt = null;
		prepStmt = conn.prepareStatement(bigSql);

		for (int i = 0; i < jArray.length(); i++) {
			Object arrayItem = jArray.get(i);

			JSONObject json = new JSONObject(arrayItem.toString());

			JSONObject accountInfo = json.getJSONObject("accountInfo");

			// Map<String, String> profilesMap = new HashMap<String, String>();
			// profilesMap.put("profileId", Long.toString(json.getLong("profileId")));
			// System.out.println(json.getLong("profileId"));
			// profilesMap.put("countryCode", json.getString("countryCode"));
			// profilesMap.put("sellerId", accountInfo.getString("id"));
			//
			// profilesWithContryCodes.add(profilesMap);

			prepStmt.setNull(1, java.sql.Types.INTEGER);
			prepStmt.setString(2, Long.toString(json.getLong("profileId")));
			prepStmt.setString(3, "NULL");
			prepStmt.setNull(4, java.sql.Types.DOUBLE);
			prepStmt.setString(5, json.getString("countryCode"));
			prepStmt.setString(6, accountInfo.getString("id"));
			prepStmt.setString(7, account);
			prepStmt.setString(8, "NULL");
			prepStmt.setString(9, "NULL");
			prepStmt.setString(10, "NULL");

			prepStmt.addBatch();

			bigSql2 = bigSql2 + "(NULL, '" + Long.toString(json.getLong("profileId")) + "',NULL,NULL,'"
					+ json.getString("countryCode") + "','" + accountInfo.getString("id") + "','" + account
					+ "',NULL,NULL,NULL),";

		}

		bigSql2 = bigSql2.substring(0, bigSql2.length() - 1) + ";";
		System.out.println(bigSql2);

		// for (Map<String, String> profilesMap : profilesWithContryCodes) {
		// System.out.println(profilesMap.get("profileId")+":
		// "+profilesMap.get("countryCode") + ": "+profilesMap.get("sellerId"));
		//
		// }

		prepStmt.executeBatch();
		if (prepStmt != null) {
			prepStmt.close();
		}

	}

	public static void fillBidInformationAll(String account, String profileId, Connection conn) throws JSONException, SQLException {

		fillBidInformation(account, "keyword", profileId, conn);
		fillBidInformation(account, "target", profileId, conn);

	}

	private static void fillBidInformation(String account, String type, String profileId, Connection conn)
			throws JSONException, SQLException {

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);
		Statement stmt = null;
		ResultSet rs = null;

		String keywordTargetingIdPlaceholder = (type.equals("keyword")) ? "keywordId" : "targetId";

		String snapshotId = (type.equals("keyword")) ? apiConn.requestKeywordSnapshot()
				: apiConn.requestTargetSnapshot();

		String snapshotStatus = "IN_PROGRESS";
		while (snapshotStatus.equals("IN_PROGRESS")) {
			apiConn = new AmzApiConnector(account, profileId, conn);
			snapshotStatus = apiConn.getSnapshotStatus(snapshotId);
		}

		String responseDownload = apiConn.downloadSnapshot(snapshotId);

		JSONArray jArray = new JSONArray(responseDownload);

		String bigSqlU = "UPDATE Bid_Information SET bid=? WHERE id_keyword_target=?";
		PreparedStatement prepStmt = null;
		prepStmt = conn.prepareStatement(bigSqlU);

		String bigSql = "INSERT INTO Bid_Information VALUES ";
		boolean filledInsertStatement = false;

		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT id_keyword_target, bid FROM Bid_Information WHERE profileId = '"+profileId+"';");

		List<Map<String, Object>> oldBids = resultSetToList(rs);

		for (int i = 0; i < jArray.length(); i++) {
			Object arrayItem = jArray.get(i);

			JSONObject json = new JSONObject(arrayItem.toString());

			// stmt = conn.createStatement();
			// rs = stmt.executeQuery("SELECT bid, adGroupId FROM Bid_Information where
			// id_keyword_target = "
			// + json.getLong(keywordTargetingIdPlaceholder) + ";");

			// boolean entryFound = rs.next();

			double bid;

			// If the json has "bid" then there is a bidding otherwise it gets setted to 0.
			// Later on (when you need it) the ad group bidding will be found
			if (json.has("bid")) {
				bid = json.getDouble("bid");
			} else {
				bid = 0;
			}

			boolean entryFound = false;
			double oldBid = 0;
			for (Map<String, Object> row : oldBids) {
				if ((long) row.get("id_keyword_target") == json.getLong(keywordTargetingIdPlaceholder)) {
					oldBid = (double) row.get("bid");
					entryFound = true;
					// System.out.println("FORSCHLEIFEENTRY-FOUND "+i);
				}

			}

			if (entryFound && (oldBid != bid && bid != 0)) {

				// update the new bidding
				prepStmt.setDouble(1, bid);
				prepStmt.setLong(2, json.getLong(keywordTargetingIdPlaceholder));
				prepStmt.addBatch();

			} else if (!entryFound) {

				long adGroupId = json.getLong("adGroupId");
				if (bid == 0) {
					bid = apiConn.getAdGroupBid(adGroupId);
				}

				if (type.equals("keyword")) {

					String keyword = json.getString("keywordText");
					if (keyword.contains("'")) {
						keyword = keyword.replace("'", "''");
					}

					bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
							+ "keyword" + "','" + json.getLong("keywordId") + "','" + keyword + "','" + "-" + "','"
							+ bid + "','" + "1900-01-01" + "','" + adGroupId + "','" + json.getLong("campaignId")

							+ "'),";
				} else {

					JSONArray expression = json.getJSONArray("expression");
					JSONObject exjson = expression.getJSONObject(0);

					bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
							+ "target" + "','" + json.getLong("targetId") + "','" + "-" + "','"
							+ exjson.getString("type") + "','" + bid + "','" + "1900-01-01" + "','" + adGroupId + "','"
							+ json.getLong("campaignId") + "'),";

				}
				filledInsertStatement = true;
			}

		}

		prepStmt.executeBatch();
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

		// System.out.println(jArray.get(0).toString());

		rs.close();
		stmt.close();
	}

	public static void fillPerformanceData(String account, String profileId, Connection conn) throws JSONException, SQLException {

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		List<Map<String, String>> keywordDataArray = new ArrayList<>();
		List<Map<String, String>> targetDataArray = new ArrayList<>();

		// Populate the ArrayList with the report Ids from the last 30 Days
		int n = 30;
		while (n >= 2) {

			LocalDate date = LocalDate.now().minusDays(n);

			Map<String, String> keywordMap = new HashMap<String, String>();
			keywordMap.put("reportId", apiConn.requestKeywordReport(date));
			keywordMap.put("date", date.toString());

			keywordDataArray.add(keywordMap);

			n = n - 1;
		}

		n = 30;
		// While Loop for filling the Target Data Array
		while (n >= 2) {

			LocalDate date = LocalDate.now().minusDays(n);

			Map<String, String> targetMap = new HashMap<String, String>();
			targetMap.put("reportId", apiConn.requestTargetReport(date));
			targetMap.put("date", date.toString());

			targetDataArray.add(targetMap);

			n = n - 1;
		}

		fillPerformanceDataNow(account, profileId, "keyword", keywordDataArray, conn);
		fillPerformanceDataNow(account, profileId, "target", targetDataArray, conn);

	}

	private static void fillPerformanceDataNow(String account, String profileId, String type,
			List<Map<String, String>> dataArray, Connection conn) throws JSONException, SQLException {

		AmzApiConnector apiConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean entryFound = false;
		boolean foundPerfomanceData = false;

		String keywordTargetingIdPlaceholder = (type.equals("keyword")) ? "keywordId" : "targetId";
		String keywordTargetingTextPlaceholder = (type.equals("keyword")) ? "keywordText" : "targetingText";
		String matchtypePlaceholder = (type.equals("keyword")) ? "matchType" : "targetingType";

		for (Map<String, String> dataMap : dataArray) {

			String reportStatus = "IN_PROGRESS";
			while (reportStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				reportStatus = apiConn.getReportStatus(dataMap.get("reportId"));
			}

			String responseDownload = apiConn.downloadReport(dataMap.get("reportId"));
			JSONArray jArray = new JSONArray(responseDownload);

			String bigSqlU = "UPDATE Performance_Data SET Impressions=?, Clicks = ?, Cost = ?, Conversions30 = ?, Conversions30SameSKU = ?, UnitsOrdered30 = ?, UnitsOrdered30SameSKU = ?, Sales30 = ?, Sales30SameSKU = ? WHERE Keyword_TargetId=? AND DateData=?";
			PreparedStatement prepStmt = null;
			prepStmt = conn.prepareStatement(bigSqlU);

			String bigSql = "INSERT INTO Performance_Data VALUES ";
			foundPerfomanceData = false;

			for (int i = 0; i < jArray.length(); i++) {
				Object arrayItem = jArray.get(i);

				JSONObject json = new JSONObject(arrayItem.toString());

				if (json.getInt("clicks") == 0) {
					continue;
				}

				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT Id, Sales30 FROM Performance_Data where Keyword_TargetId = "
						+ json.getLong(keywordTargetingIdPlaceholder) + " and DateData = '" + dataMap.get("date")
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
					prepStmt.setLong(10, json.getLong(keywordTargetingIdPlaceholder));
					prepStmt.setString(11, dataMap.get("date"));
					prepStmt.addBatch();
					continue;
				}

				String keywordTargetingText = json.getString(keywordTargetingTextPlaceholder);
				if (keywordTargetingText.contains("'")) {
					keywordTargetingText = keywordTargetingText.replace("'", "''");
				}

				bigSql = bigSql + "(NULL, '" + "SellerName" + "','" + profileId + "','" + "Marketplace" + "','"
						+ dataMap.get("date") + "','" + json.getLong("campaignId") + "','" + json.getLong("adGroupId")
						+ "','" + json.getLong(keywordTargetingIdPlaceholder) + "','" + keywordTargetingText + "','"
						+ json.getString(matchtypePlaceholder) + "','" + json.getInt("impressions") + "','"
						+ json.getInt("clicks") + "','" + json.getDouble("cost") + "','"
						+ json.getInt("attributedConversions30d") + "','"
						+ json.getInt("attributedConversions30dSameSKU") + "','"
						+ json.getInt("attributedUnitsOrdered30d") + "','"
						+ json.getInt("attributedUnitsOrdered30dSameSKU") + "','" + json.getDouble("attributedSales30d")
						+ "','" + json.getDouble("attributedSales30dSameSKU") + "'),";
				foundPerfomanceData = true;

			}

			prepStmt.executeBatch();
			if (prepStmt != null) {
				prepStmt.close();
			}

			if (foundPerfomanceData) {
				bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";

				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(bigSql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (entryFound) {
			rs.close();
		}
		if (foundPerfomanceData) {
			stmt.close();
		}
	}

	public static void optimizeBiddings(String account, String profileId, Connection conn) throws SQLException, JSONException {
		BidOpt.optimizeKeywordsTargets(account, profileId, "keyword", conn);
		BidOpt.optimizeKeywordsTargets(account, profileId, "target", conn);
	}

	public static void refreshBidInformation(String account, String profileId, Connection conn) throws JSONException, SQLException {
		
		AmzApiConnector apiConn = null;

		apiConn = new AmzApiConnector(account, profileId, conn);
		String snapshotId = apiConn.requestPausedCampaignSnapshot();

		String snapshotStatus = "IN_PROGRESS";
		while (snapshotStatus.equals("IN_PROGRESS")) {
			apiConn = new AmzApiConnector(account, profileId, conn);
			snapshotStatus = apiConn.getSnapshotStatus( snapshotId);
		}

		String CampaignresponseDownload = apiConn.downloadSnapshot(snapshotId);
		System.out.println(CampaignresponseDownload);

		// JsonArray with the paused and archived campaigns
		JSONArray campaignjArray = new JSONArray(CampaignresponseDownload);

		refreshKeywordsTargets(account, "keyword", campaignjArray, profileId, conn);
		refreshKeywordsTargets(account, "target", campaignjArray, profileId, conn);

	}

	public static void refreshKeywordsTargets(String account, String type, JSONArray campaignjArray, String profileId, Connection conn)
			throws SQLException, JSONException {


		AmzApiConnector apiConn = null;
		Statement stmt = null;
		ResultSet rs = null;

		boolean entryFound;

		// if Sql already has entries for keywords and targets only check if those
		// keywords/targets are still active. Otherwise get all the keywords/targets
		// from api
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT id_keyword_target, campaignId FROM Bid_Information where keyword_or_target = '"
				+ type + "' AND profileId = " + profileId);
		entryFound = rs.next();

		String putJSON = "";

		if (entryFound) {

			long campaignId;
			String bigSqlD = "DELETE from Bid_Information WHERE id_keyword_target=?";
			PreparedStatement prepStmtD = null;
			prepStmtD = conn.prepareStatement(bigSqlD);

			int j = 1;

			while (rs.next()) {
				// goes through all the keywords & targets from the Bid_Information and checks
				// whether the campaignId is in the campaignjArray
				campaignId = rs.getLong(2);
				// System.out.println(campaignId);

				if (j == 1) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					putJSON = "[";
				}

				for (int i = 0; i < campaignjArray.length(); i++) {
					Object arrayItem = campaignjArray.get(i);

					JSONObject json = new JSONObject(arrayItem.toString());
					// System.out.println(json.getLong("campaignId"));

					// if the campaignId is found in the jArray the keyword has to be deleted from
					// Bid_Information and has to be paused in Amazon
					if (json.getLong("campaignId") == campaignId) {
						prepStmtD.setLong(1, rs.getLong(1));
						prepStmtD.addBatch();

						putJSON = putJSON + "{\"" + type + "Id\":" + rs.getLong(1) + ",\"state\":\"paused\"},";

						if (j == 1000) {
							putJSON = putJSON.substring(0, putJSON.length() - 1) + "]";
							String response = apiConn.updateKeywordTarget(putJSON, type);
							System.out.println(response);
							j = 0;
						}
						j++;
						break;

					}
				}

			}

			if (j != 1) {
				putJSON = putJSON.substring(0, putJSON.length() - 1) + "]";
				String response = apiConn.updateKeywordTarget(putJSON, type);
				System.out.println(response);
			}

			prepStmtD.executeBatch();
			if (prepStmtD != null) {
				prepStmtD.close();
			}

			rs.close();
			if (prepStmtD != null) {
				prepStmtD.close();
			}
			

		} else {

			String keywordTargetingIdPlaceholder = (type.equals("keyword")) ? "keywordId" : "targetId";

			apiConn = new AmzApiConnector(account, profileId, conn);
			String snapshotId = (type.equals("keyword")) ? apiConn.requestKeywordSnapshot()
					: apiConn.requestTargetSnapshot();

			String snapshotStatus = "IN_PROGRESS";
			while (snapshotStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				snapshotStatus = apiConn.getSnapshotStatus(snapshotId);
			}

			String responseDownload = apiConn.downloadSnapshot(snapshotId);

			JSONArray jArray = new JSONArray(responseDownload);

			long campaignId;
			int j = 1;

			for (int i = 0; i < jArray.length(); i++) {
				Object arrayItem2 = jArray.get(i);

				JSONObject json2 = new JSONObject(arrayItem2.toString());

				campaignId = json2.getLong("campaignId");

				if (j == 1) {
					apiConn = new AmzApiConnector(account, profileId, conn);
					putJSON = "[";
					break;
				}

				for (int u = 0; u < campaignjArray.length(); u++) {
					Object arrayItem = campaignjArray.get(u);
					JSONObject json = new JSONObject(arrayItem.toString());
					// System.out.println(json.getLong("campaignId"));

					// if the campaignId is found in the jArray the keyword has to be deleted from
					// Bid_Information and has to be paused in Amazon
					if (json.getLong("campaignId") == campaignId) {

						putJSON = putJSON + "{\"" + type + "Id\":" + json2.getLong(keywordTargetingIdPlaceholder)
								+ ",\"state\":\"paused\"},";

						if (j == 1000) {
							putJSON = putJSON.substring(0, putJSON.length() - 1) + "]";
							String response = apiConn.updateKeywordTarget(putJSON, type);
							System.out.println(response);
							j = 0;
						}
						j++;
						break;

					}

				}

			}

			if (j != 1) {
				putJSON = putJSON.substring(0, putJSON.length() - 1) + "]";
				String response = apiConn.updateKeywordTarget(putJSON, type);
				System.out.println(response);
			}

		}
	}

	public static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
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
	
	public static void fillTestProductAdsTable(String account, String profileId, Connection conn) throws SQLException, JSONException {

		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;

		boolean foundPerfomanceData = false;
		boolean entryFound = false;

		String bigSqlU = "UPDATE test_productAds SET impressions=?, cost=?, clicks = ?, attributed_units_ordered7d_same_sku = ?, attributed_units_ordered7d = ?, attributed_units_ordered14d_same_sku = ?, attributed_units_ordered14d = ?, attributed_sales7d_same_sku = ?, attributed_sales7d = ?, attributed_sales14d_same_sku = ?, attributed_sales14d = ?, attributed_conversions7d_same_sku = ?, attributed_conversions7d = ?, attributed_conversions14d_same_sku = ?, attributed_conversions14d = ? WHERE id=?";

		PreparedStatement prepStmt = null;
		prepStmt = conn.prepareStatement(bigSqlU);

		String bigSql = "INSERT INTO test_productAds VALUES ";

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		List<Map<String, String>> reportIds = new ArrayList<>();

		// Populate the ArrayList with the report Ids from the last 60 Days
		int n = 2;
		while (n <= 60) {

			LocalDate date = LocalDate.now().minusDays(n);

			Map<String, String> map = new HashMap<String, String>();
			map.put("reportId", apiConn.requestProductAdsReportSp(date));
			map.put("date", date.toString());
			map.put("reportIdHeadline", apiConn.requestHeadlineReport(date));

			reportIds.add(map);

			n++;
		}

		for (Map<String, String> map : reportIds) {

			// Getting the report downloads for sponsored products and accumulate the costs
			// and revenue
			String reportStatus = "IN_PROGRESS";
			while (reportStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				reportStatus = apiConn.getReportStatus(map.get("reportId"));

			}

			double costs = 0;
			double revenue = 0;

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

			System.out.println(map.get("date"));

			stmt = conn.createStatement();
			rs2 = stmt.executeQuery("SELECT id FROM KPIs_Clients where profileId = '" + profileId + "' AND date = '"
					+ map.get("date") + "'");
			entryFound = rs2.next();

			if (entryFound) {
				prepStmt.setDouble(1, costs);
				prepStmt.setDouble(2, revenue);
				prepStmt.setDouble(3, acos);
				prepStmt.setLong(4, rs2.getLong(1));
			} else {
				bigSql = bigSql + "(NULL, '" + profileId + "','" + map.get("date") + "'," + costs + "," + revenue + ","
						+ acos + "),";
				foundPerfomanceData = true;
			}
		}
		if (foundPerfomanceData) {
			bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";
			System.out.println(bigSql);

			try {
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(bigSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt2.close();
		}

		stmt.close();
		if (entryFound) {
			prepStmt.executeUpdate();
			prepStmt.close();

			rs2.close();
		}

	}
	
	public static void fillTestCampaignsTable(String account, String profileId, Connection conn) throws SQLException, JSONException {

		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;

		boolean foundPerfomanceData = false;
		boolean entryFound = false;

		String bigSqlU = "UPDATE test_campaigns SET impressions=?, cost=?, clicks = ?, attributed_units_ordered7d_same_sku = ?, attributed_units_ordered7d = ?, attributed_units_ordered30d_same_sku = ?, attributed_units_ordered30d = ?, attributed_units_ordered14d_same_sku = ?, attributed_units_ordered14d = ?, attributed_sales7d_same_sku = ?, attributed_sales7d = ?, attributed_sales30d_same_sku = ?, attributed_sales30d = ?, attributed_sales14d_same_sku = ?, attributed_sales14d = ?, attributed_conversions7d_same_sku = ?, attributed_conversions7d = ?, attributed_conversions30d_same_sku = ?, attributed_conversions30d = ?, attributed_conversions14d_same_sku = ?, attributed_conversions14d = ? WHERE id=?";

		PreparedStatement prepStmt = null;
		prepStmt = conn.prepareStatement(bigSqlU);

		String bigSql = "INSERT INTO test_campaigns VALUES ";

		AmzApiConnector apiConn = new AmzApiConnector(account, profileId, conn);

		List<Map<String, String>> reportIds = new ArrayList<>();

		// Populate the ArrayList with the report Ids from the last 60 Days
		int n = 2;
		while (n <= 60) {

			LocalDate date = LocalDate.now().minusDays(n);

			Map<String, String> map = new HashMap<String, String>();
			map.put("reportId", apiConn.requestCampaignsReportSp(date));
			map.put("date", date.toString());
			//map.put("reportIdHeadline", apiConn.requestHeadlineReport(date));

			reportIds.add(map);

			n++;
		}

		for (Map<String, String> map : reportIds) {

			// Getting the report downloads for sponsored products and accumulate the costs
			// and revenue
			String reportStatus = "IN_PROGRESS";
			while (reportStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				reportStatus = apiConn.getReportStatus(map.get("reportId"));

			}

			String responseDownload = apiConn.downloadReport(map.get("reportId"));
			JSONArray jArray = new JSONArray(responseDownload);
			
			stmt = conn.createStatement();
			rs2 = stmt.executeQuery("SELECT id FROM test_campaigns where profile_Id = '" + profileId + "' AND date = '"
					+ map.get("date") + "'");
			entryFound = rs2.next();

			for (int i = 0; i < jArray.length(); i++) {
				Object arrayItem = jArray.get(i);

				JSONObject json = new JSONObject(arrayItem.toString());

				if (entryFound) {
					prepStmt.setInt(1, json.getInt("impressions"));
					prepStmt.setDouble(2, json.getDouble("cost"));
					prepStmt.setInt(3, json.getInt("clicks"));
					prepStmt.setInt(4, json.getInt("attributedUnitsOrdered7dSameSKU"));
					prepStmt.setInt(5, json.getInt("attributedUnitsOrdered7d"));
					prepStmt.setInt(6, json.getInt("attributedUnitsOrdered30dSameSKU"));
					prepStmt.setInt(7, json.getInt("attributedUnitsOrdered30d"));
					prepStmt.setInt(8, json.getInt("attributedUnitsOrdered14dSameSKU"));
					prepStmt.setInt(9, json.getInt("attributedUnitsOrdered14d"));
					prepStmt.setDouble(10, json.getDouble("attributedSales7dSameSKU"));
					prepStmt.setDouble(11, json.getDouble("attributedSales7d"));
					prepStmt.setDouble(12, json.getDouble("attributedSales30dSameSKU"));
					prepStmt.setDouble(13, json.getDouble("attributedSales30d"));
					prepStmt.setDouble(14, json.getDouble("attributedSales14dSameSKU"));
					prepStmt.setDouble(15, json.getDouble("attributedSales14d"));
					prepStmt.setInt(16, json.getInt("attributedConversions7dSameSKU"));
					prepStmt.setInt(17, json.getInt("attributedConversions7d"));
					prepStmt.setInt(18, json.getInt("attributedConversions30dSameSKU"));
					prepStmt.setInt(19, json.getInt("attributedConversions30d"));
					prepStmt.setInt(20, json.getInt("attributedConversions14dSameSKU"));
					prepStmt.setInt(21, json.getInt("attributedConversions14d"));
					
					// Update Statement...
					
				} else {
					bigSql = bigSql + "(NULL, '" + map.get("date") + "','" + profileId + "'," + json.getInt("impressions") + "," 
				+ json.getDouble("cost") + "," + json.getInt("clicks")+ "," + json.getInt("attributedUnitsOrdered7dSameSKU") 
				+ "," + json.getInt("attributedUnitsOrdered7d")	+ "," + json.getInt("attributedUnitsOrdered30dSameSKU")
				+ "," + json.getInt("attributedUnitsOrdered30d") + "," + json.getInt("attributedUnitsOrdered14dSameSKU")
				+ "," + json.getInt("attributedUnitsOrdered14d") + "," + json.getDouble("attributedSales7dSameSKU") 
				+ "," + json.getDouble("attributedSales7d") + "," + json.getDouble("attributedSales30dSameSKU")
				+ "," + json.getDouble("attributedSales30d") + "," + json.getDouble("attributedSales14dSameSKU")
				+ "," + json.getDouble("attributedSales14d") + "," + json.getInt("attributedConversions7dSameSKU")
				+ "," + json.getInt("attributedConversions7d") + "," + json.getInt("attributedConversions30dSameSKU")
				+ "," + json.getInt("attributedConversions30d") + "," + json.getInt("attributedConversions14dSameSKU")
				+ "," + json.getInt("attributedConversions14d") + "),";
					
					foundPerfomanceData = true;
				}
			}

			// same process for the sponsored brands
//			reportStatus = "IN_PROGRESS";
//			while (reportStatus.equals("IN_PROGRESS")) {
//				apiConn = new AmzApiConnector(account, profileId, conn);
//				reportStatus = apiConn.getReportStatus(map.get("reportIdHeadline"));

//			}
//
//			responseDownload = apiConn.downloadReport(map.get("reportIdHeadline"));
//			jArray = new JSONArray(responseDownload);
//
//			for (int i = 0; i < jArray.length(); i++) {
//				Object arrayItem = jArray.get(i);
//
//				JSONObject json = new JSONObject(arrayItem.toString());
//
//				costs = costs + json.getDouble("cost");
//				revenue = revenue + json.getDouble("attributedSales14d");
//			}

			

			
		}
		if (foundPerfomanceData) {
			bigSql = bigSql.substring(0, bigSql.length() - 1) + ";";
			System.out.println(bigSql);

			try {
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(bigSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt2.close();
		}

		stmt.close();
		if (entryFound) {
			prepStmt.executeUpdate();
			prepStmt.close();

			rs2.close();
		}

	}

}
