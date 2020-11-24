package pack.name;

public class BulkRow {
	
	int id;
	String keyword;
	double bidding;
	double costs;
	double acos;
	int orders;
	long impressions;
	long clicks;
	long revenue;

	public BulkRow(int id, String keyword, double bidding, double costs, double acos, int orders, long impressions,
			long clicks, long revenue) {
		this.id = id;
		this.keyword = keyword;
		this.bidding = bidding;
		this.costs = costs;
		this.acos = acos;
		this.orders = orders;
		this.impressions = impressions;
		this.clicks = clicks;
		this.revenue = revenue;
	}
	


	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}

	public long getImpressions() {
		return impressions;
	}

	public void setImpressions(long impressions) {
		this.impressions = impressions;
	}

	public long getClicks() {
		return clicks;
	}

	public void setClicks(long clicks) {
		this.clicks = clicks;
	}

	public long getRevenue() {
		return revenue;
	}

	public void setRevenue(long revenue) {
		this.revenue = revenue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public double getBidding() {
		return bidding;
	}

	public void setBidding(double bidding) {
		this.bidding = bidding;
	}

	public double getCosts() {
		return costs;
	}

	public void setCosts(double costs) {
		this.costs = costs;
	}

	public double getAcos() {
		return acos;
	}

	public void setAcos(double acos) {
		this.acos = acos;
	}
	

}
