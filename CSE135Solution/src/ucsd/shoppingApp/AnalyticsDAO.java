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

		private static final String UPDATE_PRECOMPUTED = "UPDATE precomputed_base SET amount = amount + ? WHERE product_id = ? AND state_id = ?";
		private static final String GET_TOTAL_SALE_FOR_PRODUCT = "SELECT DISTINCT product_id, amount FROM precomputed_base WHERE product_id = ?";
		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}
		
		public double getTotalSale(int product_id) throws SQLException{
			ResultSet rs = null;
			PreparedStatement pstmt = con.prepareStatement(GET_TOTAL_SALE_FOR_PRODUCT);
			pstmt.setInt(1,  product_id);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getDouble("amount");
		}
		
		public void updatePrecomputed(int product_id, int state_id, double added_sales) throws SQLException {
			PreparedStatement pstmt = con.prepareStatement(UPDATE_PRECOMPUTED);
			pstmt.setDouble(1,  added_sales);
			pstmt.setInt(2,  product_id);
			pstmt.setInt(3, state_id);
			pstmt.executeUpdate();
		}
		
		public List<AnalyticsModel> getAnalytics(int category_id) throws SQLException {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query = "";
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			if (category_id != -1) {
				// display by category
				
				// get sql query from filesystem
				InputStream is = classLoader.getResourceAsStream("analytics_state_topk_precomputed_category.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";
			    
			    // prepare primary sql query
			    pstmt = con.prepareStatement(sql_query);
			    pstmt.setInt(1,  category_id);
			} else {
				// display all
				
				// get sql query from filesystem
				InputStream is = classLoader.getResourceAsStream("analytics_state_topk_precomputed.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";
			    
			    // prepare primary sql query
			    pstmt = con.prepareStatement(sql_query);
			}
			
			rs = pstmt.executeQuery();
			System.out.println("Right before while loop");
			while (rs.next()) {
				String product_name = rs.getString("product_name");
				String name = rs.getString("name");
				double sales = rs.getDouble("total");
				double product_total_sales = rs.getDouble("ptotal");
				
				System.out.println(product_name);
				
				AnalyticsModel working = new AnalyticsModel(product_name, name, sales, product_total_sales);
				analytics.add(working);
			}
			return analytics;
		}
	}
