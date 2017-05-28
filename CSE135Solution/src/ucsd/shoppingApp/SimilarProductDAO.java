package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

import ucsd.shoppingApp.models.ProductModel;
import ucsd.shoppingApp.models.SimilarProductModel;

public class SimilarProductDAO {

		private static String DROP_SIMILAR_PRODUCT_TEMP_TABLES = "DROP TABLE proSales; DROP TABLE prodVecs; DROP TABLE crossed;";

		private static String GET_SIMILAR_PRODUCTS;
		private static String GET_SIMILAR_PRODUCTS_FINAL = 
				"SELECT product_a, product_b, (SUM(toadd)/(prod_a_sales + prod_b_sales)) AS cosine, prod_a_sales, prod_b_sales " + 
				"FROM crossed " +
				"GROUP BY product_a, product_b, prod_a_sales, prod_b_sales " +
				"ORDER BY cosine DESC " + 
				"LIMIT 100";
		private Connection con;

		public SimilarProductDAO(Connection con) {
			this.con = con;
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream is = classLoader.getResourceAsStream("SimilarProductsSQLQuery.sql");
			java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    this.GET_SIMILAR_PRODUCTS = s.hasNext() ? s.next() : "";

		}

		public List<SimilarProductModel> getSimilarProducts() {
			List<SimilarProductModel> simProducts = new ArrayList<SimilarProductModel>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(GET_SIMILAR_PRODUCTS);
				rs = stmt.executeQuery(GET_SIMILAR_PRODUCTS_FINAL);
				while (rs.next()) {
					String prod1_name = rs.getString("product_a");
					String prod2_name = rs.getString("product_b");
					ProductModel p1name = new ProductModel(prod1_name);
					ProductModel p2name = new ProductModel(prod2_name);
					SimilarProductModel working = new SimilarProductModel(p1name, p2name);
					simProducts.add(working);
				}
				stmt.executeUpdate(DROP_SIMILAR_PRODUCT_TEMP_TABLES);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (stmt != null) {
						stmt.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return simProducts;
		}
	}
