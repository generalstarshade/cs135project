package ucsd.shoppingApp.models;

import java.sql.Timestamp;

public class SimilarProductModel {
	private ProductModel p1;
	private ProductModel p2;

	public int getProduct1_id() {
		return p1.getProduct_id();
	}
	
	public int getProduct2_id() {
		return p2.getProduct_id();
	}

	public void setProduct1_id(int product_id) {
		this.p1.setProduct_id(product_id);
	}
	
	public void setProduct2_id(int product_id) {
		this.p2.setProduct_id(product_id);
	}

	public String getSku1_id() {
		return p1.getSku_id();
	}
	
	public String getSku2_id() {
		return p2.getSku_id();
	}

	public void setSku1_id(String sku_id) {
		this.p1.setSku_id(sku_id);
	}
	
	public void setSku2_id(String sku_id) {
		this.p2.setSku_id(sku_id);
	}

	public String getProduct1_name() {
		return p1.getProduct_name();
	}
	
	public String getProduct2_name() {
		return p2.getProduct_name();
	}

	public void setProduct1_name(String product_name) {
		this.p1.setProduct_name(product_name);
	}
	
	public void setProduct2_name(String product_name) {
		this.p2.setProduct_name(product_name);
	}

	public int getCategory1_id() {
		return this.p1.getCategory_id();
	}
	
	public int getCategory2_id() {
		return this.p2.getCategory_id();
	}

	public void setCategory1_id(int category_id) {
		this.p1.setCategory_id(category_id);
	}
	public void setCategory2_id(int category_id) {
		this.p2.setCategory_id(category_id);
	}

	public String getCategory1_name() {
		return this.p1.getCategory_name();
	}
	
	public String getCategory2_name() {
		return this.p1.getCategory_name();
	}

	public void setCategory1_name(String category_name) {
		this.p1.setCategory_name(category_name);
	}
	
	public void setCategory2_name(String category_name) {
		this.p2.setCategory_name(category_name);
	}

	public Timestamp getCreated1_date() {
		return this.p1.getCreated_date();
	}
	
	public Timestamp getCreated2_date() {
		return this.p2.getCreated_date();
	}

	public void setCreated1_date(Timestamp created_date) {
		this.p1.setCreated_date(created_date);
	}
	
	public void setCreated2_date(Timestamp created_date) {
		this.p2.setCreated_date(created_date);
	}

	public String getCreated1_by() {
		return this.p1.getCreated_by();
	}
	
	public String getCreated2_by() {
		return this.p2.getCreated_by();
	}

	public void setCreated1_by(String created_by) {
		this.p1.setCreated_by(created_by);
	}
	
	public void setCreated2_by(String created_by) {
		this.p2.setCreated_by(created_by);
	}

	public Double getPrice1() {
		return this.p1.getPrice();
	}
	
	public Double getPrice2() {
		return this.p2.getPrice();
	}

	public void setPrice1(Double price) {
		this.p1.setPrice(price);
	}
	
	public void setPrice2(Double price) {
		this.p2.setPrice(price);
	}

	public SimilarProductModel(ProductModel p1, ProductModel p2) {
		this.p1 = new ProductModel(p1);
		this.p2 = new ProductModel(p2);
	}

}
