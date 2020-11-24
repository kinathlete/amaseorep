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
import org.json.JSONException;
import org.json.JSONObject;

public class RefreshKeywordsTargetsThread implements Runnable {

	private String account;
	private String profileId;
	private Connection conn;
	private int actionId;

	public RefreshKeywordsTargetsThread(String account, String profileId, int actionId) {
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

			String snapshotId = apiConn.requestPausedCampaignSnapshot();

			String snapshotStatus = "IN_PROGRESS";
			while (snapshotStatus.equals("IN_PROGRESS")) {
				apiConn = new AmzApiConnector(account, profileId, conn);
				snapshotStatus = apiConn.getSnapshotStatus( snapshotId);
			}

			String CampaignresponseDownload = apiConn.downloadSnapshot(snapshotId);

			// JsonArray with the paused and archived campaigns
			JSONArray campaignjArray = new JSONArray(CampaignresponseDownload);

			refreshKeywordsTargets(account, "keyword", campaignjArray, profileId, conn);
			refreshKeywordsTargets(account, "target", campaignjArray, profileId, conn);
			
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
	
	private void refreshKeywordsTargets(String account, String type, JSONArray campaignjArray, String profileId, Connection conn)
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
				+ type + "' AND profileId = '" + profileId + "'");
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
			
			if (prepStmtD != null) {
				prepStmtD.executeBatch();
				prepStmtD.close();
			}
			rs.close();

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

}
