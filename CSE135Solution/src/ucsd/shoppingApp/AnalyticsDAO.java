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

		public int getNumProductsLeft(int prod_offset, int category_id) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query;
			System.out.println("numproductsleft");
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			
			if (category_id != -1) {
				// specify category
				// get the sql query from filesystem
				InputStream is = classLoader.getResourceAsStream("jsp_get_products_left.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";
			    
			    pstmt = con.prepareStatement(sql_query);
			    System.out.println("about to fail");
			    pstmt.setInt(1,  category_id);
			    pstmt.setInt(2,  prod_offset);
			} else {
				pstmt = con.prepareStatement(GET_NUM_PRODUCTS_LEFT);
				pstmt.setInt(1,  prod_offset);
			}
			
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public int getNumCustsLeft(int cust_offset, int cvs, int category_id) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query;
			System.out.println("numcustsleft");
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			
			if (category_id != -1) {
				// specify category
				if (cvs == 0) {
					// then customer
					InputStream is = classLoader.getResourceAsStream("jsp_get_persons_left.sql");
					java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
				    sql_query = s.hasNext() ? s.next() : "";
					pstmt = con.prepareStatement(sql_query);
					pstmt.setInt(1,  category_id);
					pstmt.setInt(2,  cust_offset);
				} else {
					InputStream is = classLoader.getResourceAsStream("jsp_get_states_left.sql");
					java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
				    sql_query = s.hasNext() ? s.next() : "";
					pstmt = con.prepareStatement(sql_query);
					pstmt.setInt(1,  category_id);
					pstmt.setInt(2,  cust_offset);
				}
			} else {
				if (cvs == 0) {
					// then customer
					pstmt = con.prepareStatement(GET_NUM_PERSONS_LEFT);
					pstmt.setInt(1,  cust_offset);
				} else {
					pstmt = con.prepareStatement(GET_NUM_STATES_LEFT);
					pstmt.setInt(1,  cust_offset);
				}
			}
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public int getNumStatesLeft(int cust_offset, int category_id) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			pstmt = con.prepareStatement(GET_NUM_PERSONS_LEFT);
			pstmt.setInt(1,  cust_offset);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		
		public List<AnalyticsModel> getAnalytics(int customer_or_state, int alpha_or_sales, int category_id, int product_offset, int customer_offset,  int total_offset) throws SQLException {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query = "";
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		    System.out.println("totaloffset in dao: " + total_offset);

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
					    
					    System.out.println("totaloffset in dao: " + total_offset);
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  category_id);
					    pstmt.setInt(2,  product_offset);
					    pstmt.setInt(3,  total_offset);
					    pstmt.setInt(4,  customer_offset);
					    pstmt.setInt(5,  total_offset);
					    System.out.println(pstmt);
					} else {
						// display all
						
						// get the sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  total_offset);
					    pstmt.setInt(3,  customer_offset);
					    pstmt.setInt(4, total_offset);
					    System.out.println(pstmt);

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
					    pstmt.setInt(3,  total_offset);
					    pstmt.setInt(4,  customer_offset);
					    pstmt.setInt(5,  total_offset);
					    System.out.println(pstmt);

					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_topk.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  total_offset);
					    pstmt.setInt(3,  customer_offset);
					    pstmt.setInt(4,  total_offset);
					    System.out.println(pstmt);

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
					    pstmt.setInt(3,  total_offset);
					    pstmt.setInt(4,  customer_offset);
					    pstmt.setInt(5,  total_offset);
					    System.out.println(pstmt);

					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  total_offset);
					    pstmt.setInt(3,  customer_offset);
					    pstmt.setInt(4,  total_offset);
					    System.out.println(pstmt);

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
					    pstmt.setInt(3,  total_offset);
					    pstmt.setInt(4,  customer_offset);
					    pstmt.setInt(5,  total_offset);
					    System.out.println(pstmt);

					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_topk.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					    pstmt.setInt(2,  total_offset);
					    pstmt.setInt(3,  customer_offset);
					    pstmt.setInt(4,  total_offset);
					    System.out.println(pstmt);

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
