package pack.name;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

//REQUEST A Report WITH SPECIFIC METRICS
public class AmzApiConnector {
	
	private static HttpURLConnection connection;
	
//	BufferedReader reader;
//	String line;
//	StringBuffer responseContent = new StringBuffer();
	//String accessToken = Main2.accessToken;
	String accessToken = "";
	Connection conn;
	String clientId = "amzn1.application-oa2-client.cb53385b125e496caef753cc8555b5f5";
	String clientSecret = "31cdfb0a9a56ef35c74c30bb72f64612edede74a9d2469c7cda1fbe61449eee3";
	String profileId = "";
	String account;
	String businessRefreshToken = "Atzr|IwEBIOpK7cQOmiuMMWJIYeOnMPj-p6xZ3c5jBitEGkU9mioRNkfmeyYbK2_WSYhDwVk5olFoVZNUFB3jP5UgPBNhZED0_9TlUkLWoHLoIpMRvoPrWhfK2XTU2TkH_hzWCX4dGevcHXYx4ockikf4bDVCCiO5gQlBkptX9Y8gHGuh4gR8aDFZmUJUgGULNmFwcDZhPJeE-lnbV1igiWQESBwXskHa1x49dm42MXOX8gC1K7byTXxVtAeauyUA5LBedl8SiNM5HXK7sXfE_q2upGFJwy_cs-9nsE9nC-8VfhWbJWD6VSic3Aw0pe6AE6etSvKQcJKVQONDu8BevwSfwe3fPnL0i3hwOwq8mhNpXTtX85ouz8e5HsZzb2cVC1S32J9af-nKxVF6p1LZIkHHXnKUYzQFPCfIuL-9yo35u1JfDum2uik_hwhl2DamgEEthVTs1SY";
	String amazonRefreshToken = "Atzr|IwEBIBU193ZS0pqu1ZYwzibBQGxd9RuP-R6bFNH-ld9MDk-v6osPiqoNXui5ckpdDH-CrKmeYiCNl2Dwg5UXxB9u6DcKyjW4qYgKvUwyaVBrp4GapJORNs3jrEc7zkwB0pzCoguwRNMInjwNe4RfEg6THAjtM96ApOnkmcJNI2gTAMTZAkwQBY57UXfsigJdAgwIpQnn9peF6APaUg2a1AdYzjH27LdUDvjcz9luqe8DuZrU23n75rAqkS_AU3p4aMDucRQgzv7PzBrN8z-GyHfzXDLBjCnwZmMCTSXMCESvmDdvKep-ZyruFnqmaFz_AMy09WKhfPVdke8T81v3ECsekXw0KsEzd27codeb5qRw5WvlcARGAZcjYYUjraHdsK-MdhPMJQedWj6u4E_t5Np9t_kbbm86p-oAm7j8MbBvtSFrK__TVWnIlZf5yenVogfBtQaJa6oCPqSQ2SpuHfWLp2lubLN8sY4RLzumZ-YyNzxXIaih77s9Uf72SPNUCLGnSMA";
	String comAmazonRefreshToken = "Atzr|IwEBIK8jg1jj99Gxq7maz2-2BazClAcGgoRxeB5RfYNPadT7VxfT0ZstaTDDOKoUqzx32uSk89cRBz7UTqacHflo07QqOm8N6DqsakOD9wXzFSwuCkfX25wtOrW3ZmsFUDDX69eu9We26aBZh-KJI_tkcG2gSjRvgZQ5u4jG8eqyYpqVxsHdaUR-cTJsN-mGhQjikMlAMancVWdWTeLRbRwV9OEQbhsHonWCySXkwgocYTdrr2RtTP06NKiUDvVCpa_XvVfIXA2od5_0hh-9jMFxTJ6oMrDxRbGRNPXcibIh_BJrli_PSZc5CmdqWle9fqRRuMyE7sivEp9FJqwORUeH9vsoHcqzwazV-ugjIG20K_c2vdEHAHduwpyWo2aQG2JUTlq8ATSksBxY-fsD5MPKtaGqAlcHm9jI3e36Z4sqlNowgv4Lei6uw_Q_siI0JshVIjc-afXmeJtldQI72xZmBxwxTn3PQ93YKC12ulDnEd584Ig_qX7rIr6V2f2VvR6MwEw";
	
	public AmzApiConnector(String account, String profileId, Connection conn) {
		this.profileId = profileId;
		this.account = account;
		this.conn = conn;
	}
	
	public String getAccessToken(Connection conn) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
			
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT accessToken FROM accessTokens WHERE account = '"+account+"'");
		rs.next();
		String accessToken = rs.getString(1);
		
		return accessToken;
	}
	
	public String requestKeywordReport(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
		
		String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,clicks,cost,keywordText,matchType,attributedSales30dSameSKU,campaignId,adGroupId,keywordId,attributedConversions30d,attributedConversions30dSameSKU,attributedUnitsOrdered30d,attributedUnitsOrdered30dSameSKU,attributedSales30d,attributedSales30dSameSKU\"}";
		//String input = "{\"stateFilter\":enabled}";
		String responseContent = requestApi("POST", "sp/keywords/report", input);

		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/keywords/report", input);
			json = new JSONObject(responseContent);
		}

			return json.getString("reportId");

	}
	
public String requestKeywordReportSmall(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
		
		String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,clicks,cost,keywordText,matchType,attributedSales30dSameSKU,campaignId,adGroupId,keywordId,attributedConversions30d,attributedConversions30dSameSKU,attributedUnitsOrdered30d,attributedUnitsOrdered30dSameSKU,attributedSales30d,attributedSales30dSameSKU\"}";
		//String input = "{\"stateFilter\":enabled}";
		String responseContent = requestApi("POST", "sp/keywords/report", input);

		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/keywords/report", input);
			json = new JSONObject(responseContent);
		}

			return json.getString("reportId");

	}
	
	public String requestCampaignReport(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
		
		String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"cost,attributedSales7d\"}";
		String responseContent = requestApi("POST", "sp/campaigns/report", input);
		
		//System.out.println(responseContent);
		JSONObject json = new JSONObject(responseContent);
		
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/campaigns/report", input);
			json = new JSONObject(responseContent);
		}
			
			return json.getString("reportId");
		
	}
	
	public String requestHeadlineReport(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
	    String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"cost,attributedSales14d\"}";
	    
		String responseContent = requestApi("POST", "hsa/campaigns/report", input);
		
		JSONObject json = new JSONObject(responseContent);
		
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/campaigns/report", input);
			json = new JSONObject(responseContent);
		}
			
			return json.getString("reportId");
		
	}
	
	public String requestHeadlineKeywordReport(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
	    String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,clicks,cost,keywordText,matchType,attributedSales14dSameSKU,campaignId,adGroupId,keywordId,attributedConversions14d,attributedConversions14dSameSKU,unitsSold14d,attributedSales14d,attributedSales14dSameSKU\"}";
	    
		String responseContent = requestApi("POST", "hsa/keywords/report", input);
		
		JSONObject json = new JSONObject(responseContent);
		
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/keywords/report", input);
			json = new JSONObject(responseContent);
		}
			
			return json.getString("reportId");
		
	}
	
	public String updateKeywordTarget(String input, String type) throws JSONException {
		
		//String input = "{\"stateFilter\":enabled}";
		String responseContent = requestApi("PUT", "sp/"+type+"s", input);
		
		//JSONObject json = new JSONObject(responseContent);
		
		return responseContent;
	}
	
	public String requestTargetReport(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
		
		String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,clicks,cost,targetingText,targetingType,attributedSales30dSameSKU,campaignId,adGroupId,targetId,attributedConversions30d,attributedConversions30dSameSKU,attributedUnitsOrdered30d,attributedUnitsOrdered30dSameSKU,attributedSales30d,attributedSales30dSameSKU\"}";
		//String input = "{\"stateFilter\":enabled}";
		String responseContent = requestApi("POST", "sp/targets/report", input);
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/targets/report", input);
			json = new JSONObject(responseContent);
		}
			
			return json.getString("reportId");
	}
	
	public String getKeywordInformation() throws JSONException {
		
		String responseContent = requestApi("GET", "sp/keywords/?stateFilter=enabled&count=10000", "");
		return responseContent;
	}
	
	public double getAdGroupBid(long adGroupId) throws JSONException {
		
		String responseContent = requestApi("GET", "sp/adGroups/"+adGroupId, "");
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("defaultBid")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			responseContent = requestApi("GET", "sp/adGroups/"+adGroupId, "");
			json = new JSONObject(responseContent);
		}
		
		double bid = json.getDouble("defaultBid");
		return bid;
	}
	
	public double getHeadlineStandardBid(long adGroupId) throws JSONException {
		
		String responseContent = requestApi("GET", "sp/adGroups/"+adGroupId, "");
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("defaultBid")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			responseContent = requestApi("GET", "sp/adGroups/"+adGroupId, "");
			json = new JSONObject(responseContent);
		}
		
		double bid = json.getDouble("defaultBid");
		return bid;
	}
	
	public String getTargetInformation() throws JSONException {
		
		String responseContent = requestApi("GET", "sp/targets/?stateFilter=enabled", "");
		return responseContent;
	}
	
	public String requestKeywordSnapshot() throws JSONException {
		
		String input = "{\"stateFilter\":\"enabled\"}";
		
		String responseContent = requestApi("POST", "sp/keywords/snapshot", input);
		
		JSONObject json = new JSONObject(responseContent);

		while(!json.has("snapshotId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/keywords/snapshot", input);
			json = new JSONObject(responseContent);
		}		
			
			return json.getString("snapshotId");

	}
	
	public String requestHeadlineKeywordSnapshot() throws JSONException {
		
		String input = "{\"stateFilter\":\"enabled\"}";
		
		String responseContent = requestApi("POST", "hsa/keywords/snapshot", input);
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("snapshotId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/keywords/snapshot", input);
			json = new JSONObject(responseContent);
		}	
			
			return json.getString("snapshotId");

	}
	
	public String requestPausedCampaignSnapshot() throws JSONException {
		
		String input = "{\"stateFilter\":\"paused,archived\"}";
		
		String responseContent = requestApi("POST", "sp/campaigns/snapshot", input);
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("snapshotId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			responseContent = requestApi("POST", "sp/campaigns/snapshot", input);
			json = new JSONObject(responseContent);
		}
		return json.getString("snapshotId");

	}
	
	
	public String requestTargetSnapshot() throws JSONException {
		
		String input = "{\"stateFilter\":\"enabled\"}";
		
		String responseContent = requestApi("POST", "sp/targets/snapshot", input);
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("snapshotId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			responseContent = requestApi("POST", "sp/targets/snapshot", input);
			json = new JSONObject(responseContent);
		}
		
		return json.getString("snapshotId");

	}
	
	public String getSnapshotStatus(String snapshotId) throws JSONException {
		String responseContent = requestApi("GET", "sp/snapshots/"+snapshotId, "");
		
		JSONObject json = new JSONObject(responseContent);
		
		while(!json.has("status")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			responseContent = requestApi("GET", "sp/snapshots/"+snapshotId, "");
			json = new JSONObject(responseContent);
		}
		
		return json.getString("status");
	}
	
	public String downloadSnapshot(String snapshotId) throws JSONException {
		
		String responseContent = requestApi("GET", "snapshots/"+snapshotId+"/download", "");

		return responseContent;
		
	}
	
	public String getReportStatus(String reportId) throws JSONException {
		String responseContent = requestApi("GET", "reports/"+reportId, "");
		JSONObject json = new JSONObject(responseContent);
		while(!json.has("status")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				System.out.println(responseContent);
				e.printStackTrace();
			}
			responseContent = requestApi("GET", "reports/"+reportId, "");
			json = new JSONObject(responseContent);
		}
		
		return json.getString("status");
	}
	
	public String downloadReport(String reportId) throws JSONException {
		String responseContent = requestApi("GET", "reports/"+reportId+"/download", "");
		//JSONObject json = new JSONObject(responseContent);
		return responseContent;
	}
	
	public String getAllProfiles() throws JSONException {

		try {
			accessToken = getAccessToken(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		StringBuffer responseContent = new StringBuffer();
		String line;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		
		try {
			URL url = new URL("https://advertising-api-eu.amazon.com/v2/profiles");
			connection = (HttpURLConnection) url.openConnection();			
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Amazon-Advertising-API-ClientId", clientId);
			
			
			int status = connection.getResponseCode();
			
			if(status == 401) {
				refreshToken();
				url = new URL("https://advertising-api-eu.amazon.com/v2/profiles");
				connection = (HttpURLConnection) url.openConnection();			
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(50000);
				connection.setReadTimeout(50000);
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Authorization", "Bearer " + accessToken);
				connection.setRequestProperty("Amazon-Advertising-API-ClientId", clientId);
				
				status = connection.getResponseCode();
			}							
				if (status > 299) {
					reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					while((line = reader.readLine()) != null) {
						responseContent.append(line);
						
					}
				} else {
					
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
					
				}

				}
			
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
				if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				
			}
			return responseContent.toString();
		}
			
		
	private String requestApi(String requestMethod, String urlEnding, String input) throws JSONException {
		StringBuffer responseContent = new StringBuffer();
		String line;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			accessToken = getAccessToken(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		//Comment By Abhishek
		//Get the access token from Database 
		try {
			URL url = new URL("https://advertising-api-eu.amazon.com/v2/"+urlEnding);
			connection = (HttpURLConnection) url.openConnection();			
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Amazon-Advertising-API-ClientId", clientId);
			connection.setRequestProperty("Amazon-Advertising-API-Scope", profileId);
			
			if(requestMethod.equals("POST") || requestMethod.equals("PUT")) {	
				OutputStream os = connection.getOutputStream();
				os.write(input.getBytes());
				os.flush();
			}
			
			int status = connection.getResponseCode();
			
			if(status == 401) {
				refreshToken();
				url = new URL("https://advertising-api-eu.amazon.com/v2/"+urlEnding);
				connection = (HttpURLConnection) url.openConnection();			
				connection.setRequestMethod(requestMethod);
				connection.setConnectTimeout(50000);
				connection.setReadTimeout(50000);
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Authorization", "Bearer " + accessToken);
				connection.setRequestProperty("Amazon-Advertising-API-ClientId", clientId);
				connection.setRequestProperty("Amazon-Advertising-API-Scope", profileId);
				
				if(requestMethod.equals("POST") || requestMethod.equals("PUT")) {	
					OutputStream os = connection.getOutputStream();
					os.write(input.getBytes());
					os.flush();
				}
				status = connection.getResponseCode();
			}
			
			if (status > 299) {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
					
				}
				
			} else {
								
				if(urlEnding.contains("download")) {
					GZIPInputStream gzis = new GZIPInputStream(connection.getInputStream());				
					reader = new BufferedReader(new InputStreamReader(gzis));
				} else {
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				}
						
			while((line = reader.readLine()) != null) {
					responseContent.append(line);
					
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connection.disconnect();
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return responseContent.toString();
	}
	
	
	public String refreshToken() throws JSONException {
		//checkIfTokenValid();
		
		String refreshToken = "";
		
		if(account.equals("business")) {
			refreshToken = businessRefreshToken;
		}else if(account.equals("amazon")) {
			refreshToken = amazonRefreshToken;
		}else {
			refreshToken = comAmazonRefreshToken;
		}
		
		StringBuffer responseContent = new StringBuffer();
		String line;
		BufferedReader reader = null;
		
		try {
			//URL url = new URL("https://api.amazon.co.uk/auth/o2/token");
			Map<String,Object> params = new LinkedHashMap<>();
			params.put("grant_type", "refresh_token");
			params.put("client_id", clientId);
			params.put("refresh_token", refreshToken);
			params.put("client_secret", clientSecret);
			
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0) postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			
			URL url = new URL("https://api.amazon.co.uk/auth/o2/token");
			
			connection = (HttpURLConnection) url.openConnection();			
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.getOutputStream().write(postDataBytes);
			
			
			int status = connection.getResponseCode();
			
			
			
			if (status > 299) {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
					
				}
			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
						
				while((line = reader.readLine()) != null) {
					responseContent.append(line);				
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}		
		System.out.println(responseContent);
	
		JSONObject json = new JSONObject(responseContent.toString());
		accessToken = json.getString("access_token");
		//Comment By Abhishek
		//Store the access token to Database instead of setting on static variable 
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = DatabaseConnector.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String sql = "UPDATE accessTokens SET accessToken = '"+accessToken+"' WHERE account = '"+account+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return json.getString("access_token");
		
	}
	
	
	public String checkClientStatus1() throws JSONException {
		
		String responseContent1 = requestApi("GET", "sp/campaigns/extended?count=1","");
		
		return responseContent1;
	}
	
	
	public String checkClientStatus() throws JSONException {
		
		String responseContent1 = requestApi("GET", "sp/campaigns/extended?count=1","");
		String input = "[{\"campaignId\":259543686378869,\"keywordText\":\"xyz\",\"matchType\":\"negativeExact\",\"state\":\"enabled\"}]";
		String responseContent = requestApi("POST", "sp/campaignNegativeKeywords", input);

		return responseContent1;
	}
	
public String requestProductAdsReportSp(LocalDate date) throws JSONException {
		
	    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String dateText = date.format(formatters);
		
		String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,cost,clicks,campaignName,campaignId,adGrouopName,adGroupId,attributedConversions7dSameSKU,attributedConversions7d,attributedConversions14dSameSKU,attributedConversions14d,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered7d,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered14d,attributedSales7dSameSKU,attributedSales7d,attributedSales14dSameSKUattributedSales14d\"}";
		String responseContent = requestApi("POST", "sp/productAds/report", input);
		
		//System.out.println(responseContent);
		JSONObject json = new JSONObject(responseContent);
		
		
		while(!json.has("reportId")) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			responseContent = requestApi("POST", "sp/productAds/report", input);
			json = new JSONObject(responseContent);
		}
			
			return json.getString("reportId");
		
	}

public String requestProductAdsReportSb(LocalDate date) throws JSONException {
	
    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
    String dateText = date.format(formatters);
	
	String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,cost,clicks,campaignName,campaignId,adGrouopName,adGroupId,attributedConversions7dSameSKU,attributedConversions7d,attributedConversions14dSameSKU,attributedConversions14d,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered7d,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered14d,attributedSales7dSameSKU,attributedSales7d,attributedSales14dSameSKUattributedSales14d\"}";
	String responseContent = requestApi("POST", "hsa/productAds/report", input);
	
	//System.out.println(responseContent);
	JSONObject json = new JSONObject(responseContent);
	
	
	while(!json.has("reportId")) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		responseContent = requestApi("POST", "hsa/productAds/report", input);
		json = new JSONObject(responseContent);
	}
		
		return json.getString("reportId");
	
}

public String requestCampaignsReportSp(LocalDate date) throws JSONException {
	
    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
    String dateText = date.format(formatters);
	
	String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"impressions,cost,clicks,campaignName,campaignId,attributedConversions7dSameSKU,attributedConversions7d,attributedConversions14dSameSKU,attributedConversions14d,attributedConversions30dSameSKU,attributedConversions30d,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered7d,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered14d,attributedUnitsOrdered30dSameSKU,attributedUnitsOrdered30d,attributedSales7dSameSKU,attributedSales7d,attributedSales14dSameSKU,attributedSales14d,attributedSales30dSameSKU,attributedSales30d\"}";
	String responseContent = requestApi("POST", "sp/campaigns/report", input);
	
	//System.out.println(responseContent);
	JSONObject json = new JSONObject(responseContent);
	
	
	while(!json.has("reportId")) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		responseContent = requestApi("POST", "sp/campaigns/report", input);
		json = new JSONObject(responseContent);
	}
		
		return json.getString("reportId");
	
}

public String requestCampaignsReportSb(LocalDate date) throws JSONException {
	
    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
    String dateText = date.format(formatters);
    String input = "{\"reportDate\":\""+dateText+"\",\"metrics\":\"cost,attributedSales14d\"}";
    
	String responseContent = requestApi("POST", "hsa/campaigns/report", input);
	
	JSONObject json = new JSONObject(responseContent);
	
	
	while(!json.has("reportId")) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		responseContent = requestApi("POST", "sp/campaigns/report", input);
		json = new JSONObject(responseContent);
	}
		
		return json.getString("reportId");
	
}
	
	
}
