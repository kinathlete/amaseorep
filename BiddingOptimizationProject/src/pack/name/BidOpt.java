package pack.name;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

/**
 * Servlet implementation class BidOpt
 */
@WebServlet("/BidOpt")
public class BidOpt extends HttpServlet {
	
	static long totalRevenue;
	static long totalOrders;
	static double avrgOrderValue;
	static double costsForTargetAcos; //Costs to reach with one order the target acos
	
	static double targetAcos;
	static double bufferAcos;
	
	static double b; //either the bidding in the row or the standard bidding
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ArrayList<BulkRow> array = new ArrayList<BulkRow>();
		
		BulkRow bulkRow1 = new BulkRow(1,"shoes",0.4,98.8,0.52,5,1000,10,190);
		BulkRow bulkRow2 = new BulkRow(2,"gürtel",0.4,45,0.3,12,400,29,150);
		BulkRow bulkRow3 = new BulkRow(3,"maus",0.4,12,0.1,2,600,17,120);
		BulkRow bulkRow4 = new BulkRow(4,"tisch",0.4,0,0,0,5,0,0);
		BulkRow bulkRow5 = new BulkRow(5,"uhr",0.4,1,0,0,5,1,0);
		BulkRow bulkRow6 = new BulkRow(6,"flasche",0.4,10,0,0,400,14,0);
		BulkRow bulkRow7 = new BulkRow(7,"shirt",0.4,24,0.04,33,1200,37,600);
				
		array.add(bulkRow1);
		array.add(bulkRow2);
		array.add(bulkRow3);
		array.add(bulkRow4);
		array.add(bulkRow5);
		array.add(bulkRow6);
		array.add(bulkRow7);
		
		ArrayList<BulkRow> newArray = new ArrayList<BulkRow>();
		
		BulkRow bulkRow1n = new BulkRow(1,"shoes",0.4,98.8,0.52,5,1000,10,190);
		BulkRow bulkRow2n = new BulkRow(2,"gürtel",0.4,45,0.3,12,400,29,150);
		BulkRow bulkRow3n = new BulkRow(3,"maus",0.4,12,0.1,2,600,17,120);
		BulkRow bulkRow4n = new BulkRow(4,"tisch",0.4,0,0,0,5,0,0);
		BulkRow bulkRow5n = new BulkRow(5,"uhr",0.4,1,0,0,5,1,0);
		BulkRow bulkRow6n = new BulkRow(6,"flasche",0.4,10,0,0,400,14,0);
		BulkRow bulkRow7n = new BulkRow(7,"shirt",0.4,24,0.04,33,1200,37,600);
				
		newArray.add(bulkRow1n);
		newArray.add(bulkRow2n);
		newArray.add(bulkRow3n);
		newArray.add(bulkRow4n);
		newArray.add(bulkRow5n);
		newArray.add(bulkRow6n);
		newArray.add(bulkRow7n);
		

	
		//The variables get their values
		//Target Acos depends on the customer
		targetAcos= 0.2;
		bufferAcos = targetAcos * 1.2;
		
		//The total Revenue is the sum of all the single Revenues from each bulkrow
		totalRevenue = 0;
		
		for (BulkRow br : array) {			
			totalRevenue = totalRevenue + br.revenue;			
		}
		
		//The total orders are the sum of all the single orders from each bulkrow
		totalOrders = 0;
		for (BulkRow br : array) {
			totalOrders = totalOrders + br.orders;
		}
		
		// The Average Order Value is calculated
		avrgOrderValue = totalRevenue / totalOrders;
		
		//The costs to reach with one single order the target acos are calculated
		costsForTargetAcos = avrgOrderValue * targetAcos;
		
		
		
		//For every element in the array there has to be a check whether an optimization is necessary
		for (BulkRow br : newArray) {
			//Here is a check, whether the bulk row is a row where you can change the bidding
			
			br = optimizeBidding(br);
		}
		
		for (BulkRow bR : newArray) {
			System.out.println(bR.id + ": Bidding= "+bR.bidding);
		}
		
		for (BulkRow bR : array) {
			System.out.println(bR.id + ": Bidding= "+bR.bidding);
		}	
		
		
		
		//PUTTING ATTRIBUTES IN THE REQUEST
		
		request.setAttribute("oldArray", array);
		
		request.setAttribute("newArray", newArray);
		
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/optDone.jsp");
		dispatcher.forward(request, response);
		
		response.sendRedirect("optDone.jsp");
		
		}
		

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
	
	public static BulkRow optimizeBidding(BulkRow currentBulkRow) {
		
		//Either there is a bidding in the current row or the standard bidding of the campaign has to be found
		
		
		b = currentBulkRow.bidding;
		
		//When the costs are 0 increase the bidding with 1 or 2 ct
		if (currentBulkRow.costs == 0) {
			double centIncrease = (double) getRandomNumberInRange(1, 2)/100;
			currentBulkRow.bidding = b + centIncrease;
			//When the bidding is higher than 3, change the bidding to 3 (3 should be the maximum for this optimization
			if (currentBulkRow.bidding > 3) {
				currentBulkRow.bidding = 3;
			}
			
		//When the acos is higher than the buffer acos, the bidding should be decreased	
		} else if (currentBulkRow.acos > bufferAcos) {
				currentBulkRow.bidding = b * bufferAcos / currentBulkRow.acos * (2 - bufferAcos / currentBulkRow.acos);			
			
		//When The costs higher than 0 and smaller than costs for target acos --> change the bidding randomly	
		} else if (currentBulkRow.costs > 0 && currentBulkRow.costs < costsForTargetAcos) {
			double centIncrease = (double) getRandomNumberInRange(1, 4)/100;
			currentBulkRow.bidding = b - 0.02 + centIncrease;
			
		//When orders are 0 and the costs are higher than the cost for target acos the bidding has to be decreased --> Therefore the difference between the costs and the costs for target acos is crucial 	
		} else if (currentBulkRow.costs > costsForTargetAcos && currentBulkRow.orders == 0) {
			currentBulkRow.bidding = b * costsForTargetAcos / currentBulkRow.costs * (2 - costsForTargetAcos / currentBulkRow.costs);
			
		//When there are more than 2 orders, the costs are higher than the costs for target acos and the acos is smaller than 40% of the target acos (so very small)	
		} else if (currentBulkRow.orders > 2 && currentBulkRow.costs > costsForTargetAcos && currentBulkRow.acos < targetAcos * 0.4) {
			currentBulkRow.bidding = b * targetAcos * 0.4 / currentBulkRow.acos;
			//When the bidding is higher than 5, change the bidding to 5 (5 should be the maximum for bidding)
			if (currentBulkRow.bidding > 5) {
				currentBulkRow.bidding = 5;
			}
		}	

		
		//When the bidding is due to the optimization smaller than 0.02 then change it to 0.02
		if (currentBulkRow.bidding < 0.02) {
			currentBulkRow.bidding = 0.02;
		}
		
		currentBulkRow.bidding = Math.round(currentBulkRow.bidding * 100.0) / 100.0;
	
		return currentBulkRow;
	}

	
public static Object[] optimizeBidding(int impressions, double costs, int orders, double revenue, double bidding, double targetAcos, double costsForTargetAcos, double bufferAcos) {
	
	//Either there is a bidding in the current row or the standard bidding of the campaign has to be found
	b = bidding;
	
	double acos = (revenue == 0) ? 0 : costs/revenue;
	boolean updateLastOptimization = false;
	
	//When the costs are 0 increase the bidding with 1 or 2 ct (only increase in 5% of the cases)
	if (costs == 0) {
		int r = 10;
		if(r < getRandomNumberInRange(0, 100)) {
			double centIncrease = (double) getRandomNumberInRange(1, 3)/100;
			bidding = b - 0.01 + centIncrease;
			//When the bidding is higher than 3, change the bidding to 3 (3 should be the maximum for this optimization
			if (bidding > 3) {
				bidding = 3;
			}
		}
		
	//When the acos is higher than the buffer acos, the bidding should be decreased	
	} else if (acos > bufferAcos) {
			bidding = b * bufferAcos / acos * (2 - bufferAcos / acos);	
			updateLastOptimization = true;
		
	//When The costs higher than 0 and smaller than costs for target acos --> change the bidding randomly	
	} else if (costs > 0 && costs < costsForTargetAcos) {
		double centIncrease = (double) getRandomNumberInRange(1, 3)/100;
		bidding = b - 0.02 + centIncrease;
		
	//When orders are 0 and the costs are higher than the cost for target acos the bidding has to be decreased --> Therefore the difference between the costs and the costs for target acos is crucial 	
	} else if (costs > costsForTargetAcos && orders == 0) {
		bidding = b * costsForTargetAcos / costs * (2 - costsForTargetAcos / costs);
		updateLastOptimization = true;
		
	//When there are more than 2 orders, the costs are higher than the costs for target acos and the acos is smaller than 40% of the target acos (so very small)	
	} else if (orders > 2 && costs > costsForTargetAcos && acos < targetAcos * 0.4) {
		bidding = b * targetAcos * 0.4 / acos;
		//When the bidding is higher than 5, change the bidding to 5 (5 should be the maximum for bidding)
		if (bidding > 5) {
			bidding = 5;
		}
		updateLastOptimization = true;
	}	

	
	//When the bidding is due to the optimization smaller than 0.02 then change it to 0.02
	if (bidding < 0.02) {
		bidding = 0.02;
	}
	
	bidding = Math.round(bidding * 100.0) / 100.0;
	

	return new Object[]{bidding, updateLastOptimization};
}
	
	
	public static int getRandomNumberInRange(int min, int max) {

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static boolean optimizeKeywordsTargets(String account, String profileId, String type, Connection conn) throws SQLException, JSONException {
			
			String lastOptimization;
			
			Statement stmt = null;
		    ResultSet rs = null;
		    
		    	stmt = conn.createStatement();
		    	rs = stmt.executeQuery("SELECT targetAcos FROM Clients WHERE profileId="+profileId);
		    	rs.next();
		    	double targetAcos = rs.getDouble(1);
		    	if(targetAcos == 0) {
		    		System.out.println("There is no target acos for the profileId:"+profileId);
		    		return false;
		    	}
		    	
		    	System.out.println(targetAcos + " targetAcos");
		    	
		    	stmt = conn.createStatement();
		    	rs = stmt.executeQuery("SELECT Sum(Sales30SameSKU), Sum(UnitsOrdered30SameSKU) FROM Performance_Data WHERE DateData > date_sub(curdate(), interval 60 day)");
		    	rs.next();
		    	double totalRevenue = rs.getDouble(1);
		    	int totalOrders = rs.getInt(2);
		    	if(totalOrders == 0) {
		    		System.out.println("No orders in the Performance_Data table for:"+profileId);
		    		return false;
		    	}
		    	double costsForTargetAcos = totalRevenue / totalOrders * targetAcos;
		    	double bufferAcos = targetAcos * 1.2;
		    	
		    	System.out.println(costsForTargetAcos + " costsForTargetAcos");
		    	System.out.println(bufferAcos + " bufferAcos");
		    	
//		    	stmt = conn.createStatement();
//		    	String testTable = (type.equals("keyword")) ? "TestJoin" : "TestJoinTargets";
//		    	rs = stmt.executeQuery("SELECT * FROM "+testTable);
		    	
		        stmt = conn.createStatement();
		        System.out.println("Getting resultset");
		        rs = stmt.executeQuery("SELECT b.id_keyword_target, sum(p.Impressions), sum(p.Cost), sum(p.Conversions30SameSKU), sum(p.Sales30SameSKU), b.bid, b.adGroupId " + 
		        		"FROM Bid_Information AS b " + 
		        		"LEFT JOIN Performance_Data AS p " + 
		        		"ON b.id_keyword_target = p.Keyword_TargetId " + 
						"WHERE b.keyword_or_target = '"+ type +"' " + 
						"AND b.profileId = '"+ profileId +"' " +
		        		"AND (p.DateData > b.last_optimization " + 
		        		"OR p.DateData is null) " + 
		        		"GROUP BY b.id_keyword_target ");
		        
		        int i = 1;
		        
		       
		    	//String input = "[{\"keywordId\":277713142739289,\"bid\":0.99}]";
		        AmzApiConnector apiConn = null;
		        String putJSON = "";
		        String bigSql = "UPDATE Bid_Information SET last_optimization=? WHERE id_keyword_target=?";
		        PreparedStatement prepStmt = null;
		        prepStmt = conn.prepareStatement(bigSql);
		    	
		        System.out.println("starting optimizing");
		        while(rs.next()) {
		        	if(i==1) {
		        		apiConn = new AmzApiConnector(account, profileId, conn);
		            	putJSON = "[";
		        	}
		        	
		        	long typeId = rs.getLong(1);
		        	int impressions = rs.getInt(2);
		        	double costs = rs.getLong(3);
		        	int orders = rs.getInt(4);
		        	double revenue = rs.getDouble(5);
		        	double bid;
		        	
		        	if(rs.getDouble(6) == 0) {
		        		long adGroupId = rs.getLong(7);
		        		bid = apiConn.getAdGroupBid(adGroupId);
		        		
		        	}else {
		        	bid = rs.getDouble(6);
		        	}
		        	
		        	Object[] optimizedArray = BidOpt.optimizeBidding(impressions, costs, orders, revenue, bid, targetAcos, costsForTargetAcos, bufferAcos);
		        	double newBidding = (double) optimizedArray[0];
		        	boolean updateLastOptimization = (boolean) optimizedArray[1];
		        	
		        	if(i==1) {
		        		putJSON = putJSON + "{\""+type+"Id\":"+typeId+",\"bid\":"+newBidding+"}";
		      
		        	} else {
		        		putJSON = putJSON + ",{\""+type+"Id\":"+typeId+",\"bid\":"+newBidding+"}";
		        	}
		        	
		        	if(i==1000) {
		        		putJSON = putJSON + "]";
	                	//String response = apiConn.updateKeywordTarget(putJSON, type);
		                i = 0;
		        	}
		        	
		        	if(updateLastOptimization) {
		        		prepStmt.setString(1, LocalDate.now().toString());
		            	prepStmt.setLong(2, typeId);
		            	prepStmt.addBatch();
		        	}
		        	
		        	
		        	i++;
		        }
		        if(i!=1) {
		        	putJSON = putJSON + "]";
		        	//String response = apiConn.updateKeywordTarget(putJSON, type);
		        }
		        
		        int[] affectedRecords = prepStmt.executeBatch();
		        if(prepStmt != null) {
		        	prepStmt.close();
		        }
		      
		    rs.close();
		    stmt.close();
		    return true;
		}


}
