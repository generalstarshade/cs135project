package ucsd.shoppingApp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ucsd.shoppingApp.models.AnalyticsModel;

public class AnalyticsDAO {

		private static String GET_NUM_PRODUCTS_LEFT = "SELECT COUNT(*) AS count FROM product WHERE id > (10 * ?)";
		
		private static String GET_NUM_PERSONS_LEFT = "SELECT COUNT(*) AS count FROM person WHERE id > (20 * ?) AND role_id = 2";
		
		private static String GET_NUM_STATES_LEFT = "SELECT COUNT(*) AS count FROM state WHERE id > (20 * ?)";
	
		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}

		public int getNumProductsLeft(int prod_offset) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			pstmt = con.prepareStatement(GET_NUM_PRODUCTS_LEFT);
			pstmt.setInt(1,  prod_offset);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public int getNumCustsLeft(int cust_offset, int cvs) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			if (cvs == 0) {
				// then customer
				pstmt = con.prepareStatement(GET_NUM_PERSONS_LEFT);
			} else {
				pstmt = con.prepareStatement(GET_NUM_STATES_LEFT);
			}
			
			pstmt.setInt(1,  cust_offset);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public int getNumStatesLeft(int cust_offset) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			pstmt = con.prepareStatement(GET_NUM_PERSONS_LEFT);
			pstmt.setInt(1,  cust_offset);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public List<AnalyticsModel> getAnalytics(int customer_or_state, int alpha_or_sales, int category_id, int product_offset, int customer_offset) throws SQLException {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query = "";
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
			// if we are to display by customer
			if (customer_or_state == 0) {
				
				// if we are to display alphabetically
				if (alpha_or_sales == 0) {
						
					if (category_id != -1) {
						// display by category
						
						// get the sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_alphabetical_category.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  category_id);
					    pstmt.setInt(2,  product_offset);
					    pstmt.setInt(3,  customer_offset);
					} else {
						// display all
						
						// get the sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  customer_offset);
					}
				} else
					
				// if we are to display by top k
				if (alpha_or_sales == 1) {
					// if we are to display by particular category
					if (category_id != -1) {
						// display by category
						
						// get the sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_topk_category.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  category_id);
					    pstmt.setInt(2,  product_offset);
					    pstmt.setInt(3,  customer_offset);
					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_topk.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  customer_offset);
					}
				}
				
			} else
				
			// if we are to display by state	
			if (customer_or_state == 1) {
				
				// if we are to display alphabetically
				if (alpha_or_sales == 0) {
					// if we are to display by particular category
					if (category_id != -1) {
						// display by category
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_alphabetical_category.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  category_id);
					    pstmt.setInt(2,  product_offset);
					    pstmt.setInt(3,  customer_offset);
					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  customer_offset);
					}
				} else
					
				// if we are to display by top k
				if (alpha_or_sales == 1) {
					// if we are to display by particular category
					if (category_id != -1) {
						// display by category
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_topk_category.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  category_id);
					    pstmt.setInt(2,  product_offset);
					    pstmt.setInt(3,  customer_offset);
					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_topk.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  customer_offset);
					}
				}
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String product_name = rs.getString("product_name");
				String name = rs.getString("name");
				double sales = rs.getDouble("total");
				
				AnalyticsModel working = new AnalyticsModel(product_name, name, sales);
				analytics.add(working);
			}
			return analytics;
		}
	}
