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

		private static String ANALYTICS_PERSON_ALPHABETICAL_FINAL = 
				"SELECT per.id AS cid, per.person_name AS name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total " +
				"FROM pro pp cross join person per " +
				"LEFT OUTER JOIN proSales " +
				"ON (pp.id = proSales.proid AND per.id = proSales.pid) " +
				"WHERE per.role_id = 2 " +
				"ORDER BY per.person_name, pp.product_name " +
				"OFFSET 200 * ? " +
				"FETCH NEXT 200 ROWS ONLY";
		
		private static String ANALYTICS_STATE_ALPHABETICAL_FINAL =
				"SELECT state_name AS name, product_name, SUM(total) AS total " +
				"FROM statesales " +
				"GROUP BY state_name, product_name " +
				"ORDER BY state_name, product_name " +
				"OFFSET 200 * ? " +
				"FETCH NEXT 200 ROWS ONLY";
		
		private static String ANALYTICS_PERSON_TOPK_FINAL =
				"SELECT o.cid, allSales.person_name AS name, allSales.pid, allSales.product_name, allSales.total, allSales.ptotal, o.sumTotal AS ctotal " +
				"FROM ordered o " +
				"LEFT OUTER JOIN allSales " +
				"ON (o.cid = allSales.cid) " +
				"ORDER BY ctotal DESC, ptotal DESC";
		
		private static String ANALYTICS_STATE_TOPK_FINAL =
				"SELECT o.state_id, allSales.state_name AS name, allSales.pid, allSales.product_name, allSales.salesstates AS total, allSales.ptotal, o.sTotal AS stotal " +
				"FROM ordered o " +
				"LEFT OUTER JOIN allSales " +
				"ON (o.state_id = allSales.state_id) " +
				"ORDER BY stotal DESC, ptotal DESC " +
				"OFFSET 200 * ? " +
				"FETCH NEXT 200 ROWS ONLY";

		private static String ANALYTICS_PERSON_ALPHABETICAL_CLEANUP = "DROP TABLE pro; DROP TABLE proSales";
		
		private static String ANALYTICS_STATE_ALPHABETICAL_CLEANUP = "DROP TABLE pro; DROP TABLE proSales; DROP TABLE stateSales";
		
		private static String ANALYTICS_PERSON_TOPK_CLEANUP = "DROP TABLE pro; DROP TABLE proSales; DROP TABLE allSales; DROP TABLE ordered";
		
		private static String ANALYTICS_STATE_TOPK_CLEANUP = "DROP TABLE pro; DROP TABLE proSales; DROP TABLE stateSales; DROP TABLE allSales; DROP TABLE ordered";
		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}

		public List<AnalyticsModel> getAnalytics(int customer_or_state, int alpha_or_sales, int category_id, int product_offset, int customer_offset) throws SQLException {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			PreparedStatement pstmt = null;
			PreparedStatement finalquery = null;
			Statement cleanup = con.createStatement();

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
					} else {
						// display all
						
						// get the sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_person_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					}
					
					// prepare final sql query
				    finalquery = con.prepareStatement(ANALYTICS_PERSON_ALPHABETICAL_FINAL);
				    finalquery.setInt(1,  customer_offset);
				    
				    // execute all queries
				    pstmt.executeUpdate(); // execute query to generate temp tables
				    rs = finalquery.executeQuery();
				    cleanup.executeUpdate(ANALYTICS_PERSON_ALPHABETICAL_CLEANUP); // clean up temp tables
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
					
					// prepare final sql query
				    finalquery = con.prepareStatement(ANALYTICS_PERSON_TOPK_FINAL);
				    
				    // execute all queries
				    pstmt.executeUpdate(); // execute query to generate temp tables
				    rs = finalquery.executeQuery();
				    cleanup.executeUpdate(ANALYTICS_PERSON_TOPK_CLEANUP);
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
					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_alphabetical.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					}
					
				    // prepare final sql query
				    finalquery = con.prepareStatement(ANALYTICS_STATE_ALPHABETICAL_FINAL);
				    finalquery.setInt(1, customer_offset);
				    
				    // execute all queries
				    pstmt.executeUpdate(); // execute query to generate temp tables
				    rs = finalquery.executeQuery(); // execute query to get results from temp tables
				    cleanup.executeUpdate(ANALYTICS_STATE_ALPHABETICAL_CLEANUP); // clean up temp tables
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
					} else {
						// display all
						
						// get sql query from filesystem
						InputStream is = classLoader.getResourceAsStream("analytics_state_topk.sql");
						java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					    sql_query = s.hasNext() ? s.next() : "";
					    
					    // prepare primary sql query
					    pstmt = con.prepareStatement(sql_query);
					    pstmt.setInt(1,  product_offset);
					}
				    
				    // prepare final sql query
				    finalquery = con.prepareStatement(ANALYTICS_STATE_TOPK_FINAL);
				    finalquery.setInt(1, customer_offset);
				    
				    // execute all queries
				    pstmt.executeUpdate(); // execute query to generate temp tables
				    rs = finalquery.executeQuery(); // execute query to get results from temp tables
				    cleanup.executeUpdate(ANALYTICS_STATE_TOPK_CLEANUP); // clean up temp tables
				}
			}
			
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
