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

		private static final String UPDATE_PRECOMPUTED_ALL = "INSERT INTO precomputed_all (state_id, name, product_id, product_name, total, stotal, ptotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
		//private static final String UPDATE_PRECOMPUTED_ALL = "UPDATE precomputed_all SET ptotal = ptotal + ?, stotal = stotal + ? WHERE product_id = ? OR state_id = ?";
		private static final String UPDATE_PRECOMPUTED_50 = "UPDATE precomputed_50 SET ptotal = ptotal + ?, stotal = stotal + ? WHERE product_id = ? OR state_id = ?";
		private static final String UPDATE_PRECOMPUTED_ALL_STATE = "UPDATE precomputed_all SET stotal = stotal + ? WHERE state_id = ?";
		private static final String UPDATE_PRECOMPUTED_50_STATE = "UPDATE precomputed_50 SET stotal = stotal + ? WHERE state_id = ?";
		private static final String GET_TOTAL_SALE_FOR_PRODUCT = "SELECT DISTINCT product_id, ptotal FROM precomputed_all WHERE product_id = ?";
		
		private static final String DELETE_OLD_TOP_50_ROWS = "DELETE FROM precomputed_50 WHERE product_name = ?";
		private static final String SELECT_NEW_TOP_50_ROWS = "SELECT * FROM precomputed_all WHERE product_name = ?";
		private static final String ADD_NEW_TOP_50_ROWS = "INSERT INTO precomputed_50 (state_id, name, product_id, product_name, total, stotal, ptotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
			return rs.getDouble("ptotal");
		}
		
		public void removeOldTop50Rows(ArrayList<String> toRemove) throws SQLException {
			PreparedStatement pstmt = con.prepareStatement(DELETE_OLD_TOP_50_ROWS);
			System.out.println("Removing old top 50 from precomputed_50...");
			for (int i = 0; i < toRemove.size(); i++) {
				pstmt.setString(1,  toRemove.get(i));
				pstmt.addBatch();
				System.out.println(pstmt);
			}
			pstmt.executeUpdate();
		}
		
		public void addNewTop50Rows(ArrayList<String> toAdd) throws SQLException {
			ResultSet rs = null;
			PreparedStatement select = con.prepareStatement(SELECT_NEW_TOP_50_ROWS);
			PreparedStatement insert = con.prepareStatement(ADD_NEW_TOP_50_ROWS);
			System.out.println("Adding new top 50 to precomputed_50...");
			for (int i = 0; i < toAdd.size(); i++) {
				select.setString(1,  toAdd.get(i));
				rs = select.executeQuery();
				while (rs.next()) {
					int state_id = rs.getInt("state_id");
					int product_id = rs.getInt("product_id");
					double total = rs.getDouble("total");
					double stotal = rs.getDouble("stotal");
					double ptotal = rs.getDouble("ptotal");
					String name = rs.getString("name");
					String product_name = rs.getString("product_name");
					
					insert.setInt(1, state_id);
					insert.setString(2,  name);
					insert.setInt(3,  product_id);
					insert.setString(4,  product_name);
					insert.setDouble(5,  total);
					insert.setDouble(6,  stotal);
					insert.setDouble(7,  ptotal);
					insert.addBatch();
					System.out.println(insert);
				}
				insert.executeBatch();
			}
		}
		
		public void updatePrecomputed(ArrayList<AnalyticsModel> log) throws SQLException {
			PreparedStatement insert = con.prepareStatement(UPDATE_PRECOMPUTED_ALL);
			for (int i = 0; i < log.size(); i++) {
				insert.setInt(1, log.get(i).getStateId());
				insert.setString(2,  log.get(i).getName());
				insert.setInt(3,  log.get(i).getProductId());
				insert.setString(4,  log.get(i).getProductName());
				insert.setDouble(5,  log.get(i).getSales());
				insert.setDouble(6,  0);
				insert.setDouble(7,  0);
				/*pstmt.setDouble(1,  log.get(i).getSales());
				pstmt.setDouble(2,  log.get(i).getSales());
				pstmt.setInt(3,  log.get(i).getStateId());
				pstmt.setInt(4,  log.get(i).getProductId());
				pstmt.addBatch();*/
				insert.addBatch();
			}
			
			insert.executeBatch();
			//pstmt.executeBatch();
			/*pstmt = con.prepareStatement(UPDATE_PRECOMPUTED_50_PRODUCT);
			pstmt.setDouble(1,  added_sales);
			pstmt.setInt(2,  product_id);
			
			pstmt = con.prepareStatement(UPDATE_PRECOMPUTED_ALL_STATE);
			pstmt.setDouble(1, added_sales);
			pstmt.setInt(2,  state_id);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement(UPDATE_PRECOMPUTED_50_STATE);
			pstmt.setDouble(1, added_sales);
			pstmt.setInt(2,  state_id);
			pstmt.executeUpdate();*/
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
				/*InputStream is = classLoader.getResourceAsStream("analytics_state_topk_precomputed.sql");
				java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			    sql_query = s.hasNext() ? s.next() : "";*/
			    
			    // prepare primary sql query
			    pstmt = con.prepareStatement("SELECT * FROM precomputed_50 ORDER BY stotal DESC, ptotal DESC");
			}
			
			rs = pstmt.executeQuery();
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
