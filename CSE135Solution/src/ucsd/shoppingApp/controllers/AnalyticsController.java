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
import ucsd.shoppingApp.AnalyticsDAO;
import ucsd.shoppingApp.models.AnalyticsModel;

public class AnalyticsController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private AnalyticsDAO analyticsDAO = null;

	public void init() {
		con = ConnectionManager.getConnection();
		analyticsDAO = new AnalyticsDAO(con);
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
		ArrayList<AnalyticsModel> analytics;
		
		System.out.println("Customer_or_state: " + customer_or_state);
		System.out.println("alpha_or_sales: " + alpha_or_sales);
		System.out.println("category_id: " + category_id);
		
		// if we are to display by customer
		if (customer_or_state == 0) {
			
			// if we are to display alphabetically
			if (alpha_or_sales == 0) {
				// if we are to display by particular category
				if (category_id != -1) {
					// display by category
				} else {
					// display all
				}
			} else
				
			// if we are to display by top k
			if (alpha_or_sales == 1) {
				// if we are to display by particular category
				if (category_id != -1) {
					// display by category
				} else {
					// display all
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
				} else {
					// display all
				}
			} else
				
			// if we are to display by top k
			if (alpha_or_sales == 1) {
				// if we are to display by particular category
				if (category_id != -1) {
					// display by category
				} else {
					// display all
				}
			}
		}
	}

}
