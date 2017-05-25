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


		private static String GET_SIMILAR_PRODUCTS;
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
				rs = stmt.executeQuery(GET_SIMILAR_PRODUCTS);
				while (rs.next()) {
					String prod1_name = rs.getString("product_a");
					String prod2_name = rs.getString("product_b");
					ProductModel p1name = new ProductModel(prod1_name);
					ProductModel p2name = new ProductModel(prod2_name);
					SimilarProductModel working = new SimilarProductModel(p1name, p2name);
					simProducts.add(working);
				}
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
