package ucsd.shoppingApp.models;

public class AnalyticsModel {
	private String product_name;
	private String state_name;
	private double total_sales;
	private double product_total_sales;
	
	private int product_id;
	private int state_id;
	
	public AnalyticsModel(String product_name, String state_name, double total_sales, double product_total_sales) {
		this.product_name = product_name;
		this.state_name = state_name;
		this.total_sales = total_sales;
		this.product_total_sales = product_total_sales;
	}
	
	public AnalyticsModel(String product_name, double product_total_sales) {
		this.product_name = product_name;
		this.product_total_sales = product_total_sales;
	}
	
	public AnalyticsModel(int product_id, String product_name, int state_id, String state_name, double product_total_sales) {
		this.product_id = product_id;
		this.state_id = state_id;
		this.product_total_sales = product_total_sales;
	}
	
	public int getProductId() {
		return this.product_id;
	}
	
	public int getStateId() {
		return this.state_id;
	}
	
	public String getProductName() {
		return this.product_name;
	}
	
	public String getName() {
		return this.state_name;
	}
	
	public double getSales() {
		return this.total_sales;
	}
	
	public double getProductTotalSales() {
		return this.product_total_sales;
	}
	
	public void setProductName(String product_name) {
		this.product_name = product_name;
	}
	
	public void setProductId(int product_id) {
		this.product_id = product_id;
	}
	
	public void setStateId(int state_id) {
		this.state_id = state_id;
	}
	
	public void setName(String state_name) {
		this.state_name = state_name;
	}
	
	public void setSales(double total_sales) {
		this.total_sales = total_sales;
	}

	public void setProductTotalSales(double product_total_sales) {
		this.product_total_sales = product_total_sales;
	}
	
}
