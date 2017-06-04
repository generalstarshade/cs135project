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

		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}
		
		public List<AnalyticsModel> getAnalytics(int category_id, int total_offset) throws SQLException {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql_query = "";
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			if (category_id != -1) {
				// display by category
				
				// get sql query from filesystem
				InputStream is = classLoader.getResourceAsStream("analytics_state_topk_category.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";
			    
			    // prepare primary sql query
			    pstmt = con.prepareStatement(sql_query);
			    pstmt.setInt(1,  category_id);
			    System.out.println(pstmt);

			} else {
				// display all
				
				// get sql query from filesystem
				InputStream is = classLoader.getResourceAsStream("analytics_state_topk.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";
			    
			    // prepare primary sql query
			    pstmt = con.prepareStatement(sql_query);
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
