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

		private static final String INSERT_PRECOMPUTED = "INSERT INTO precomputed_base (product_id, product_name, state_id, amount) VALUES (?, ?, ?, ?)";
		private static final String GET_TOTAL_SALE_FOR_PRODUCT = "SELECT product_id, sum FROM precomputed_pt WHERE product_id = ?";
		private static final String GET_PRODUCT_TOTALS = "SELECT * FROM precomputed_pt";
		private static final String GET_PRODUCT_TOTALS_CATEGORY = "SELECT pre.* FROM precomputed_pt pre, product prod WHERE prod.category_id = ? AND pre.product_id = prod.id";
		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}
		
		public ResultSet getProductTotals(int category_id) throws SQLException {
			ResultSet rs = null;
			if (category_id == -1) {
				Statement stmt = con.createStatement();
				rs = stmt.executeQuery(GET_PRODUCT_TOTALS);
			} else {
				PreparedStatement pstmt = con.prepareStatement(GET_PRODUCT_TOTALS_CATEGORY);
				pstmt.setInt(1,  category_id);
				rs = pstmt.executeQuery();
			}
			return rs;
		}
		
		public double getTotalSale(int product_id) throws SQLException{
			ResultSet rs = null;
			PreparedStatement pstmt = con.prepareStatement(GET_TOTAL_SALE_FOR_PRODUCT);
			pstmt.setInt(1,  product_id);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getDouble("sum");
		}
		
		public void insertPrecomputed(ArrayList<AnalyticsModel> log) throws SQLException {
			PreparedStatement pstmt = con.prepareStatement(INSERT_PRECOMPUTED);
			PreparedStatement pstmt2;
			
			for (int i = 0; i < log.size(); i++) {
				pstmt.setInt(1,  log.get(i).getProductId());
				pstmt.setString(2,  log.get(i).getProductName());
				pstmt.setInt(3, log.get(i).getStateId());
				pstmt.setDouble(4,  log.get(i).getSales());
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			pstmt2 = con.prepareStatement("DROP TABLE precomputed_2");
			pstmt2.executeUpdate();
			pstmt2 = con.prepareStatement("SELECT product_id, product_name, state_id, SUM(amount) as amount INTO precomputed_2 FROM precomputed_base GROUP BY product_id, product_name, state_id");
			pstmt2.executeUpdate();
			pstmt2 = con.prepareStatement("DROP TABLE precomputed_pt");
			pstmt2.executeUpdate();
			pstmt2 = con.prepareStatement("SELECT product_id, product_name, sum(amount) INTO precomputed_pt FROM precomputed_2 GROUP BY product_id, product_name");
			pstmt2.executeUpdate();
			con.commit();
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
								
				AnalyticsModel working = new AnalyticsModel(product_name, name, sales, product_total_sales);
				analytics.add(working);
			}
			return analytics;
		}
	}
