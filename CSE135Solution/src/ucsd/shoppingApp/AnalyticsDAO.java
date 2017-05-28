package ucsd.shoppingApp;

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
				"SELECT per.id AS cid, per.person_name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total " +
				"FROM pro pp cross join person per " +
				"LEFT OUTER JOIN proSales" +
				"ON (pp.id = proSales.proid AND per.id = proSales.pid) " +
				"WHERE per.role_id = 2 " +
				"ORDER BY per.person_name, pp.product_name " +
				"OFFSET 200 * ? " +
				"FETCH NEXT 200 ROWS ONLY";
		
		private static String ANALYTICS_STATE_ALPHABETICAL_FINAL =
				"SELECT state_name, product_name, SUM(total) " +
				"FROM statesales " +
				"GROUP BY state_name, product_name " +
				"ORDER BY state_name, product_name";
		
		private static String ANALYTICS_PERSON_TOPK_FINAL =
				"SELECT o.cid, allSales.person_name, allSales.pid, allSales.product_name, allSales.total, allSales.ptotal, o.sumTotal AS ctotal " +
				"FROM ordered o " +
				"LEFT OUTER JOIN allSales " +
				"ON (o.cid = allSales.cid) " +
				"ORDER BY ctotal DESC, ptotal DESC";
		
		private static String ANALYTICS_STATE_TOPK_FINAL =
				"-- SELECT o.state_id, allSales.state_name, allSales.pid, allSales.product_name, allSales.salesstates, allSales.ptotal, o.sTotal AS stotal " +
				"-- FROM ordered o " +
				"-- LEFT OUTER JOIN allSales " +
				"-- ON (o.state_id = allSales.state_id) " +
				"-- ORDER BY stotal DESC, ptotal DESC";

		private Connection con;

		public AnalyticsDAO(Connection con) {
			this.con = con;
		}

		public List<AnalyticsModel> getAnalytics() {
			List<AnalyticsModel> analytics = new ArrayList<AnalyticsModel>();
			Statement stmt = null;
			ResultSet rs = null;
			return analytics;
			/*try {
				stmt = con.createStatement();
				// rs = 
			} catch {
				
			} finally {
				
			}*/
		}
		
	}
