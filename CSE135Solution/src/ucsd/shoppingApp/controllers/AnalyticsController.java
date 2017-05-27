package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.ProductDAO;
import ucsd.shoppingApp.models.ProductModel;
import ucsd.shoppingApp.models.ProductModelExtended;

public class AnalyticsController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private ProductDAO productDAO = null;

	public void init() {
		con = ConnectionManager.getConnection();
		productDAO = new ProductDAO(con);
	}

	public void destroy() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Same method can be used to retrieve products by id/by search/ by category
	// id/ etc..
	// we can switch based on the incoming parameters to the requests.
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		int customer_or_state = Integer.parseInt(request.getParameter("dd_cvs"));
		int alpha_or_sales = Integer.parseInt(request.getParameter("dd_avt"));
		int category_id = Integer.parseInt(request.getParameter("dd_cat"));
		System.out.println("Customer_or_state: " + customer_or_state);
		System.out.println("alpha_or_sales: " + alpha_or_sales);
		System.out.println("category_id: " + category_id);
		/*
		ArrayList<ProductModelExtended> products = new ArrayList<ProductModelExtended>();
		String match_prod_name = request.getParameter("match_prod_name");
		String cat_id_obj = request.getParameter("category_id");

		request.setAttribute("prod_id", product_id);
		request.setAttribute("category_id", cat_id_obj);
		request.setAttribute("match_prod_name", match_prod_name);

		request.getSession().setAttribute("sess_category_id", cat_id_obj);
		request.getSession().setAttribute("sess_match_prod_name", match_prod_name);

		try {
			if (cat_id_obj != null) {
				category_id = Integer.parseInt(cat_id_obj.toString());
			}

			if (request.getParameter("prod_id") != null && request.getParameter("prod_id").toString() != "") {
				product_id = Integer.parseInt(request.getParameter("prod_id"));
			}
			// switch on what is provided
			products = this.FilterProduct(category_id, match_prod_name);
			request.setAttribute("products", products);
		} catch (NumberFormatException e) {
			request.setAttribute("error", "Invalid Argument");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		request.getRequestDispatcher("./product.jsp").forward(request, response);*/
	}

	private ArrayList<ProductModelExtended> FilterProduct(int category_id, String match_prod_name) throws SQLException {
		ArrayList<ProductModelExtended> products = new ArrayList<ProductModelExtended>();
		if (category_id == -1 && match_prod_name != null && match_prod_name != "") {
			products = productDAO.filterProductAdmin(match_prod_name);
		} else if (category_id != -1 && (match_prod_name == null || match_prod_name == "")) {
			products = productDAO.filterProductAdmin(category_id);
		} else if (category_id != -1 && match_prod_name != null && match_prod_name != "") {
			products = productDAO.filterProductAdmin(match_prod_name, category_id);
		} else if (category_id == -1) {
			products = productDAO.filterProductAdmin("");
		}

		return products;
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		String prod_id = request.getParameter("prod_id");
		String sku_id = request.getParameter("sku_id");
		String prod_name = request.getParameter("prod_name");
		String price = request.getParameter("price");
		String category_id = request.getParameter("category_id");
		String modified_by = request.getParameter("modified_by");

		try {
			int updated_product_id = productDAO.updateProductById(Integer.parseInt(prod_id), sku_id, prod_name,
					Double.parseDouble(price), Integer.parseInt(category_id), modified_by);
			if (updated_product_id > 0) {
				ProductModel updated_product = productDAO.getProductById(updated_product_id).get(0);
				request.setAttribute("products", updated_product);
			} else {
				throw new ServletException("Could not add product. Retry.");
			}
		} catch (NumberFormatException e) {
			request.setAttribute("error", "Invalid Argument");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int product_id = Integer.parseInt(request.getParameter("prod_id"));
			request.setAttribute("prod_id", product_id);
			boolean done = productDAO.deleteProductById(product_id);
			if (done) {
				// some redirect
			} else {
				throw new ServletException("Could not delete product. Retry.");
			}
		} catch (NumberFormatException e) {
			request.setAttribute("error", "Invalid Argument");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}