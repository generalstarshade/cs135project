package ucsd.shoppingApp.models;

public class AnalyticsModel {
	private String product_name;
	private String customer_or_state_name;
	private double total_sales;
	
	public AnalyticsModel(String product_name, String customer_or_state_name, double total_sales) {
		this.product_name = product_name;
		this.customer_or_state_name = customer_or_state_name;
		this.total_sales = total_sales;
	}
	
	public String getProductName() {
		return this.product_name;
	}
	
	public String getName() {
		return this.customer_or_state_name;
	}
	
	public double getSales() {
		return this.total_sales;
	}
	
	public void setProductName(String product_name) {
		this.product_name = product_name;
	}
	
	public void setName(String customer_or_state_name) {
		this.customer_or_state_name = customer_or_state_name;
	}
	
	public void setSales(double total_sales) {
		this.total_sales = total_sales;
	}

}
